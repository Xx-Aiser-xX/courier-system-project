package org.example.couriers.service.impl;

import edu.rutmiit.demo.events.OrderCreatedEvent;
import edu.rutmiit.demo.events.OrderDeletedEvent;
import edu.rutmiit.demo.events.OrderStatusChangedEvent;
import org.example.couriers.config.RabbitMQConfig;
import org.example.couriers.entitys.Order;
import org.example.couriers.entitys.User;
import org.example.couriers.entitys.enums.OrderStatus;
import org.example.couriers.mapper.OrderMapper;
import org.example.couriers.repo.OrderRepository;
import org.example.couriers.repo.UserRepository;
import org.example.couriers.service.DeliveryPriceService;
import org.example.couriers.service.OrderService;
import org.example.courierscontract.dto.request.CreateOrderRequest;
import org.example.courierscontract.dto.response.OrderResponse;
import org.example.courierscontract.exception.ResourceNotFoundException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final RabbitTemplate rabbitTemplate;
    private final DeliveryPriceService deliveryPriceService;
    private final OrderMapper orderMapper;


    public OrderServiceImpl(OrderRepository orderRepository, UserRepository userRepository, RabbitTemplate rabbitTemplate, DeliveryPriceService deliveryPriceService, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.deliveryPriceService = deliveryPriceService;
        this.orderMapper = orderMapper;
    }

    @Override
    @Transactional
    public OrderResponse createOrder(UUID userId, CreateOrderRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        BigDecimal calculatedPrice = deliveryPriceService.calculateDeliveryPrice(
                request.senderAddress(),
                request.recipientAddress(),
                request.weight(),
                userId
        );

        Order newOrder = orderMapper.toEntity(request, user, calculatedPrice);
        Order savedOrder = orderRepository.save(newOrder);
        OrderCreatedEvent event = orderMapper.toCreatedEvent(savedOrder);

        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.ROUTING_KEY_ORDER_CREATED, event);
        return orderMapper.toResponse(savedOrder);
    }

    @Override
    public void updateStatusAndNotify(UUID orderId, OrderStatus newStatus, String msg) {
        orderRepository.findById(orderId).ifPresent(order -> {
            order.setStatus(newStatus);
            orderRepository.save(order);
            OrderStatusChangedEvent event = orderMapper.toStatusChangedEvent(order, newStatus, msg);

            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.ROUTING_KEY_ORDER_STATUS, event);
        });
    }

    @Override
    public OrderResponse getOrderById(UUID id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));
        return orderMapper.toResponse(order);
    }

    @Override
    @Transactional
    public void deleteOrder(UUID id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));
        order.setDeleted(true);
        orderRepository.save(order);

        OrderDeletedEvent event = new OrderDeletedEvent(order.getId());
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.ROUTING_KEY_ORDER_DELETED, event);
    }
}