package org.example.notificationservice.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.rutmiit.demo.events.NotificationEvent;
import edu.rutmiit.demo.events.OrderCreatedEvent;
import edu.rutmiit.demo.events.OrderStatusChangedEvent;
import org.example.notificationservice.websocket.NotificationHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RabbitListener(bindings = @QueueBinding(
        value = @Queue(name = "q.notifications.orders", durable = "true"),
        exchange = @Exchange(name = "orders-exchange", type = "topic"),
        key = "order.#"
))
public class RabbitEventListener {
    private static final Logger log = LoggerFactory.getLogger(RabbitEventListener.class);
    private final NotificationHandler notificationHandler;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RabbitEventListener(NotificationHandler notificationHandler) {
        this.notificationHandler = notificationHandler;
    }
    @RabbitHandler
    public void handleOrderCreated(OrderCreatedEvent event) {
        Map<String, Object> message = new HashMap<>();
        log.info("заказ создан: {}", event);
        message.put("type", "ORDER_CREATED");
        message.put("text", "Новый заказ #" + event.orderId() + " создан");
        message.put("orderId", event.orderId());
        message.put("userId", event.userId());
        message.put("newStatus", "CREATED");

        sendNotification(event.userId().toString(), message);
    }
    @RabbitHandler
    public void handleStatusChanged(OrderStatusChangedEvent event) {
        Map<String, Object> message = new HashMap<>();
        log.info("обновление статуса заказа: {}", event);
        message.put("type", "STATUS_CHANGED");
        message.put("text", event.message());
        message.put("newStatus", event.newStatus());
        message.put("orderId", event.orderId());
        message.put("userId", event.userId());
        sendNotification(event.userId().toString(), message);
    }

    @RabbitHandler
    public void handleGenericNotification(NotificationEvent event) {
        Map<String, Object> message = new HashMap<>();
        log.info("обработка универсальных уведомлений: {}", event);
        message.put("type", event.type());
        message.put("text", event.message());
        message.put("orderId", event.orderId());
        sendNotification(event.targetUserId().toString(), message);
    }

    private void sendNotification(String userId, Map<String, Object> messageMap) {
        try {
            String jsonResponse = objectMapper.writeValueAsString(messageMap);
            notificationHandler.sendToUser(userId, jsonResponse);
        } catch (Exception e) {
            log.error("ошибка отправки уведомления пользователю {}", userId, e);
        }
    }
}