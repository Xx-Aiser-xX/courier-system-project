package edu.rutmiit.demo.auditservice.listener;

import edu.rutmiit.demo.events.OrderCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class OrderEventListener {

    private static final Logger log = LoggerFactory.getLogger(OrderEventListener.class);

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "audit-queue", durable = "true"),
            exchange = @Exchange(name = "orders-exchange", type = "topic"),
            key = "order.created"))
    public void handleOrderCreatedEvent(OrderCreatedEvent event) {
        log.info("Received new order event: {}", event);
    }
}
