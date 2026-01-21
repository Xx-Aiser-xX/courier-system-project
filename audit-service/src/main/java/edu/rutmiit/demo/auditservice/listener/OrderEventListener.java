package edu.rutmiit.demo.auditservice.listener;

import com.rabbitmq.client.Channel;
import edu.rutmiit.demo.events.OrderCreatedEvent;
import edu.rutmiit.demo.events.OrderDeletedEvent;
import edu.rutmiit.demo.events.OrderStatusChangedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RabbitListener(bindings = @QueueBinding(
        value = @Queue(name = "audit-queue", durable = "true",
                arguments = {
                        @Argument(name = "x-dead-letter-exchange", value = "dlx-exchange"),
                        @Argument(name = "x-dead-letter-routing-key", value = "dlq.audit")
                }),
        exchange = @Exchange(name = "orders-exchange", type = ExchangeTypes.TOPIC, durable = "true"),
        key = "order.*"
))
public class OrderEventListener {

    private static final Logger log = LoggerFactory.getLogger(OrderEventListener.class);
    private final Set<UUID> processedOrderCreations = ConcurrentHashMap.newKeySet();

    @RabbitHandler
    public void handleOrderCreated(OrderCreatedEvent event, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        if (!processedOrderCreations.add(event.orderId())) {
            log.warn("дубликат заказа {}", event.orderId());
            channel.basicAck(deliveryTag, false);
            return;
        }
        log.info("новый заказ: {}", event);
        channel.basicAck(deliveryTag, false);
    }

    @RabbitHandler
    public void handleOrderDeleted(OrderDeletedEvent event, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        log.info("заказ удален: {}", event.orderId());
        channel.basicAck(deliveryTag, false);
    }

    @RabbitHandler
    public void handleStatusChanged(OrderStatusChangedEvent event, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        log.info("статус обновлён: {}", event.newStatus());
        channel.basicAck(deliveryTag, false);
    }
}