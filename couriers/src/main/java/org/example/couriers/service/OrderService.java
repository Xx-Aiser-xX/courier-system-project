package org.example.couriers.service;

import edu.rutmiit.demo.events.OrderCreatedEvent;
import edu.rutmiit.demo.events.OrderDeletedEvent;
import edu.rutmiit.demo.events.OrderStatusChangedEvent;
import org.example.couriers.config.RabbitMQConfig;
import org.example.couriers.entitys.Order;
import org.example.couriers.entitys.User;
import org.example.couriers.entitys.enums.OrderStatus;
import org.example.couriers.repo.OrderRepository;
import org.example.couriers.repo.UserRepository;
import org.example.courierscontract.dto.request.CreateOrderRequest;
import org.example.courierscontract.dto.response.OrderResponse;
import org.example.courierscontract.exception.ResourceNotFoundException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final RabbitTemplate rabbitTemplate;

    public OrderService(OrderRepository orderRepository, UserRepository userRepository, RabbitTemplate rabbitTemplate) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User", request.userId()));

        Order newOrder = new Order(
                user,
                request.senderAddress(),
                request.recipientAddress(),
                request.price()
        );
        Order savedOrder = orderRepository.save(newOrder);

        OrderCreatedEvent event = new OrderCreatedEvent(
                savedOrder.getId(),
                savedOrder.getUser().getId(),
                savedOrder.getSenderAddress(),
                savedOrder.getRecipientAddress(),
                savedOrder.getPrice()
        );
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.ROUTING_KEY_ORDER_CREATED, event);

        simulateOrderProcessing(savedOrder.getId());

        return mapToOrderResponse(savedOrder);
    }

    private void simulateOrderProcessing(UUID orderId) {
        CompletableFuture.runAsync(() -> {
            try {
                randomDelay();
                updateStatusAndNotify(orderId, OrderStatus.IN_TRANSIT, "Курьер забрал заказ и едет к вам");

                randomDelay();
                updateStatusAndNotify(orderId, OrderStatus.DELIVERED, "Заказ успешно доставлен. Приятного аппетита!");

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void updateStatusAndNotify(UUID orderId, OrderStatus newStatus, String msg) {
        orderRepository.findById(orderId).ifPresent(order -> {
            order.setStatus(newStatus);
            orderRepository.save(order);

            OrderStatusChangedEvent event = new OrderStatusChangedEvent(
                    orderId,
                    order.getUser().getId(),
                    newStatus.name(),
                    msg
            );
            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.ROUTING_KEY_ORDER_STATUS, event);
        });
    }

    private void randomDelay() throws InterruptedException {
        int delay = 5 + new Random().nextInt(5);
        TimeUnit.SECONDS.sleep(delay);
    }

    public OrderResponse getOrderById(UUID id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));
        return mapToOrderResponse(order);
    }

    @Transactional
    public void deleteOrder(UUID id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));
        order.setDeleted(true);
        orderRepository.save(order);

        OrderDeletedEvent event = new OrderDeletedEvent(order.getId());
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.ROUTING_KEY_ORDER_DELETED, event);
    }

    private OrderResponse mapToOrderResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getUser().getId(),
                order.getCourier() != null ? order.getCourier().getId() : null,
                order.getSenderAddress(),
                order.getRecipientAddress(),
                order.getStatus().name(),
                order.getPrice(),
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }
}