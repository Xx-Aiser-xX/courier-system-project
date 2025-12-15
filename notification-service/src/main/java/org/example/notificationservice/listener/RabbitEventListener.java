package org.example.notificationservice.listener;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.rutmiit.demo.events.OrderCreatedEvent;
import edu.rutmiit.demo.events.OrderStatusChangedEvent;
import org.example.notificationservice.websocket.NotificationHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class RabbitEventListener {

    private static final Logger log = LoggerFactory.getLogger(RabbitEventListener.class);
    private final NotificationHandler notificationHandler;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RabbitEventListener(NotificationHandler notificationHandler) {
        this.notificationHandler = notificationHandler;
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "q.notifications.orders", durable = "true"),
            exchange = @Exchange(name = "orders-exchange", type = "topic"),
            key = "order.#"
    ))
    public void handleEvents(Object event) {
        try {
            Map<String, Object> outputMessage = new HashMap<>();

            if (event instanceof Message message) {
                log.info("DEBUG: Пришло сырое Message. Разбираем байты вручную...");

                byte[] body = message.getBody();
                JsonNode rootNode = objectMapper.readTree(body);

                if (rootNode.has("newStatus")) {
                    OrderStatusChangedEvent e = objectMapper.treeToValue(rootNode, OrderStatusChangedEvent.class);
                    fillStatusMessage(outputMessage, e);
                } else {
                    OrderCreatedEvent e = objectMapper.treeToValue(rootNode, OrderCreatedEvent.class);
                    fillCreatedMessage(outputMessage, e);
                }
            }
            else if (event instanceof OrderCreatedEvent e) {
                fillCreatedMessage(outputMessage, e);
            }
            else if (event instanceof OrderStatusChangedEvent e) {
                fillStatusMessage(outputMessage, e);
            }
            else {
                log.warn("Пропущено неизвестное событие типа: {}", event.getClass().getName());
                return;
            }

            if (outputMessage.containsKey("userId") && outputMessage.get("userId") != null) {
                String targetUserId = outputMessage.get("userId").toString();
                String jsonResponse = objectMapper.writeValueAsString(outputMessage);

                notificationHandler.sendToUser(targetUserId, jsonResponse);
            } else {
                log.warn("Событие обработано, но userId не найден. Уведомление не отправлено.");
            }

        } catch (Exception e) {
            log.error("Ошибка при обработке сообщения RabbitMQ", e);
        }
    }

    private void fillCreatedMessage(Map<String, Object> message, OrderCreatedEvent e) {
        message.put("type", "ORDER_CREATED");
        message.put("text", "Новый заказ #" + e.orderId() + " успешно создан!");
        message.put("orderId", e.orderId());
        message.put("userId", e.userId());

    }

    private void fillStatusMessage(Map<String, Object> message, OrderStatusChangedEvent e) {
        message.put("type", "STATUS_CHANGED");
        message.put("text", e.message());
        message.put("newStatus", e.newStatus());
        message.put("orderId", e.orderId());
        message.put("userId", e.userId());
    }
}