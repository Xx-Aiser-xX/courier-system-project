package edu.rutmiit.demo.auditservice.listener;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import edu.rutmiit.demo.events.OrderCreatedEvent;
import edu.rutmiit.demo.events.OrderDeletedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class OrderEventListener {

    private static final Logger log = LoggerFactory.getLogger(OrderEventListener.class);
    private final Set<UUID> processedOrderCreations = ConcurrentHashMap.newKeySet();
    private static final String EXCHANGE_NAME = "orders-exchange";
    private static final String QUEUE_NAME = "audit-queue";

    private final ObjectMapper objectMapper;

    @Autowired
    public OrderEventListener(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = QUEUE_NAME, durable = "true",
                    arguments = {
                            @Argument(name = "x-dead-letter-exchange", value = "dlx-exchange"),
                            @Argument(name = "x-dead-letter-routing-key", value = "dlq.audit")
                    }),
            exchange = @Exchange(name = EXCHANGE_NAME, type = "topic", durable = "true"),
            key = "order.*"
    ))
    public void handleOrderEvents(@Payload Object payload, Channel channel,
                                  @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        try {
            if (payload instanceof Message message) {
                log.info("AUDIT: Получено сырое Message. Десериализуем вручную...");
                byte[] body = message.getBody();
                JsonNode rootNode = objectMapper.readTree(body);

                if (rootNode.has("senderAddress")) {
                    OrderCreatedEvent event = objectMapper.treeToValue(rootNode, OrderCreatedEvent.class);
                    processOrderCreated(event, channel, deliveryTag);
                } else if (rootNode.has("newStatus")) {
                    log.info("AUDIT: Пропускаем смену статуса (не настроено в этом листенере)");
                    channel.basicAck(deliveryTag, false);
                } else {
                    if (rootNode.has("orderId")) {
                        OrderDeletedEvent event = objectMapper.treeToValue(rootNode, OrderDeletedEvent.class);
                        log.info("AUDIT LOG: Received order deleted event: {}", event);
                        channel.basicAck(deliveryTag, false);
                    } else {
                        log.warn("AUDIT: Не удалось распознать JSON: {}", rootNode);
                        channel.basicAck(deliveryTag, false);
                    }
                }
            }
            else if (payload instanceof OrderCreatedEvent event) {
                processOrderCreated(event, channel, deliveryTag);
            } else if (payload instanceof OrderDeletedEvent event) {
                log.info("AUDIT LOG: Received order deleted event: {}", event);
                channel.basicAck(deliveryTag, false);
            } else {
                log.warn("Received unknown event type: {}", payload.getClass().getName());
                channel.basicAck(deliveryTag, false);
            }

        } catch (Exception e) {
            log.error("Failed to process event: {}. Sending to DLQ.", payload, e);
            channel.basicNack(deliveryTag, false, false);
        }
    }
    private void processOrderCreated(OrderCreatedEvent event, Channel channel, long deliveryTag) throws IOException {
        if (!processedOrderCreations.add(event.orderId())) {
            log.warn("Duplicate OrderCreatedEvent received for orderId: {}. Acknowledging and skipping.", event.orderId());
            channel.basicAck(deliveryTag, false);
            return;
        }
        log.info("AUDIT LOG: Received new order event: {}", event);
        if (event.senderAddress() != null && event.senderAddress().toLowerCase().contains("crash")) {
            throw new RuntimeException("Simulating processing error for DLQ test");
        }
        channel.basicAck(deliveryTag, false);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = QUEUE_NAME + ".dlq", durable = "true"),
            exchange = @Exchange(name = "dlx-exchange", type = "topic", durable = "true"),
            key = "dlq.audit"
    ))
    public void handleDlqMessages(Object failedMessage) {
        log.error("!!! Received message in DLQ: {}", failedMessage);
    }
}