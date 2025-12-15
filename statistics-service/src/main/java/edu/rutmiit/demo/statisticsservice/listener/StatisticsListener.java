package edu.rutmiit.demo.statisticsservice.listener;

import com.rabbitmq.client.Channel;
import edu.rutmiit.demo.events.OrderCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
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
            value = @Queue(name = "statistics-queue", durable = "true"),
            exchange = @Exchange(name = "orders-exchange", type = "topic", durable = "true"),
            key = "order.created"
    ))
    public void handleOrderCreatedEvent(OrderCreatedEvent event, Channel channel,
                                        @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        try {
            log.info("STATISTICS: Received OrderCreatedEvent: {}", event.orderId());
            ordersStatistics.put(event.orderId(), event.price());
            BigDecimal totalValue = ordersStatistics.values().stream()
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            log.info("STATISTICS UPDATE: Total orders processed: {}. Total value: {}",
                    ordersStatistics.size(), totalValue);
            channel.basicAck(deliveryTag, false);

        } catch (Exception e) {
            log.error("STATISTICS: Failed to process event {}. Rejecting message.", event, e);
            channel.basicNack(deliveryTag, false, false);
        }
    }
}
