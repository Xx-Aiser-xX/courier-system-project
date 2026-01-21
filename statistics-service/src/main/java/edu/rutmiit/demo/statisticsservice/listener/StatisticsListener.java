package edu.rutmiit.demo.statisticsservice.listener;

import com.rabbitmq.client.Channel;
import edu.rutmiit.demo.events.OrderCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class StatisticsListener {

    private static final Logger log = LoggerFactory.getLogger(StatisticsListener.class);
    private final Map<UUID, BigDecimal> ordersStatistics = new ConcurrentHashMap<>();

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "statistics-queue", durable = "true",
                    arguments = {
                            @Argument(name = "x-dead-letter-exchange", value = "dlx-exchange"),
                            @Argument(name = "x-dead-letter-routing-key", value = "dlq.stats")
                    }),
            exchange = @Exchange(name = "orders-exchange", type = ExchangeTypes.TOPIC, durable = "true"),
            key = "order.created"
    ))
    public void handleOrderCreatedEvent(OrderCreatedEvent event, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        log.info("получено OrderCreatedEvent: {}", event.orderId());
        ordersStatistics.put(event.orderId(), event.price());
        BigDecimal totalValue = ordersStatistics.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        log.info("кол-во заказов: {}, выручка: {}", ordersStatistics.size(), totalValue);
        channel.basicAck(deliveryTag, false);
    }
}