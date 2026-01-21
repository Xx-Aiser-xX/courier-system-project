package org.example.couriers.service.impl;

import edu.rutmiit.demo.events.NotificationEvent;
import org.example.couriers.config.RabbitMQConfig;
import org.example.couriers.entitys.Courier;
import org.example.couriers.entitys.Order;
import org.example.couriers.entitys.enums.CourierStatus;
import org.example.couriers.entitys.enums.OrderStatus;
import org.example.couriers.repo.CourierRepository;
import org.example.couriers.repo.OrderRepository;
import org.example.couriers.service.DispatchService;
import org.example.courierscontract.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Service
public class DispatchServiceImpl implements DispatchService {

    private static final Logger log = LoggerFactory.getLogger(DispatchService.class);
    private static final int OFFER_TIMEOUT_SECONDS = 30;

    private static class DispatchSession {
        final UUID orderId;
        final List<UUID> candidateIds;
        int currentIndex = 0;
        ScheduledFuture<?> timeoutTask;

        public DispatchSession(UUID orderId, List<UUID> candidateIds) {
            this.orderId = orderId;
            this.candidateIds = candidateIds;
        }
    }

    private final Map<UUID, DispatchSession> activeSessions = new ConcurrentHashMap<>();

    private final TaskScheduler taskScheduler;
    private final CourierRepository courierRepository;
    private final OrderRepository orderRepository;
    private final RabbitTemplate rabbitTemplate;

    public DispatchServiceImpl(CourierRepository courierRepository, OrderRepository orderRepository, RabbitTemplate rabbitTemplate) {
        this.courierRepository = courierRepository;
        this.orderRepository = orderRepository;
        this.rabbitTemplate = rabbitTemplate;

        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(5);
        scheduler.initialize();
        this.taskScheduler = scheduler;
    }

    @Override
    @Transactional
    public void startDispatching(UUID orderId) {
        List<Courier> freeCouriers = courierRepository.findAllByStatus(CourierStatus.FREE);

        if (freeCouriers.isEmpty()) {
            notifyUser(getErrorOrderUser(orderId), "нет свободных курьеров, попробуйте позже");
            return;
        }

        Order order = orderRepository.findById(orderId).orElseThrow();
        order.setStatus(OrderStatus.SEARCHING);
        orderRepository.save(order);

        List<UUID> candidateIds = freeCouriers.stream().map(Courier::getId).toList();
        DispatchSession session = new DispatchSession(orderId, candidateIds);
        activeSessions.put(orderId, session);

        offerOrderToCourier(session);
    }

    private void offerOrderToCourier(DispatchSession session) {
        if (session.currentIndex >= session.candidateIds.size()) {
            activeSessions.remove(session.orderId);
            updateOrderStatus(session.orderId, OrderStatus.CREATED);
            notifyUser(getErrorOrderUser(session.orderId), "никто не взял ваш заказ");
            return;
        }
        UUID candidateId = session.candidateIds.get(session.currentIndex);
        sendNotification(candidateId, "OFFER", "новый заказ, У вас " + OFFER_TIMEOUT_SECONDS + " секунд", session.orderId);

        session.timeoutTask = taskScheduler.schedule(
                () -> onCourierTimeout(session.orderId, candidateId),
                Instant.now().plusSeconds(OFFER_TIMEOUT_SECONDS)
        );
    }

    private void onCourierTimeout(UUID orderId, UUID courierId) {
        DispatchSession session = activeSessions.get(orderId);
        if (session == null)
            return;
        log.info("курьер {} промолчал (Timeout)", courierId);
        session.currentIndex++;
        offerOrderToCourier(session);
    }

    @Override
    @Transactional
    public void acceptOrder(UUID courierId, UUID orderId) {
        DispatchSession session = activeSessions.get(orderId);
        if (session == null)
            throw new RuntimeException("предложение уже недействительно");

        UUID currentCandidate = session.candidateIds.get(session.currentIndex);
        if (!currentCandidate.equals(courierId))
            throw new RuntimeException("этот заказ сейчас предложен другому");

        if (session.timeoutTask != null)
            session.timeoutTask.cancel(false);

        Courier courier = courierRepository.findById(courierId)
                .orElseThrow(() -> new ResourceNotFoundException("Courier", courierId));
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));

        courier.setStatus(CourierStatus.BUSY);
        order.setCourier(courier);
        order.setStatus(OrderStatus.ASSIGNED);

        courierRepository.save(courier);
        orderRepository.save(order);

        activeSessions.remove(orderId);
        log.info("заказ {} назначен курьеру {}", orderId, courierId);

        sendNotification(courierId, "ASSIGNED", "вы назначены на заказ", orderId);
        notifyUser(order.getUser().getId(), "курьер найден, К вам едет " + courier.getName());
    }

    @Override
    public void declineOrder(UUID courierId, UUID orderId) {
        DispatchSession session = activeSessions.get(orderId);
        if (session == null)
            return;

        UUID currentCandidate = session.candidateIds.get(session.currentIndex);
        if (!currentCandidate.equals(courierId))
            return;

        if (session.timeoutTask != null)
            session.timeoutTask.cancel(false);

        log.info("курьер {} отказался", courierId);

        session.currentIndex++;
        offerOrderToCourier(session);
    }

    private void sendNotification(UUID targetId, String type, String text, UUID orderId) {
        if (targetId == null)
            return;

        NotificationEvent event = new NotificationEvent(targetId, type, text, orderId);
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, "order.dispatch", event);
    }

    private void notifyUser(UUID userId, String text) {
        sendNotification(userId, "INFO", text, null);
    }

    private UUID getErrorOrderUser(UUID orderId) {
        return orderRepository.findById(orderId).map(o -> o.getUser().getId()).orElse(null);
    }

    private void updateOrderStatus(UUID orderId, OrderStatus status) {
        orderRepository.findById(orderId).ifPresent(o -> {
            o.setStatus(status);
            orderRepository.save(o);
        });
    }
}
