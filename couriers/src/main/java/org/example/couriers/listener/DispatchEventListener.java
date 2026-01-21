package org.example.couriers.listener;

import edu.rutmiit.demo.events.OrderCreatedEvent;
import org.example.couriers.config.RabbitMQConfig;
import org.example.couriers.service.DispatchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class DispatchEventListener {

    private static final Logger log = LoggerFactory.getLogger(DispatchEventListener.class);
    private final DispatchService dispatchService;

    public DispatchEventListener(DispatchService dispatchService) {
        this.dispatchService = dispatchService;
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "q.dispatch.created", durable = "true"),
            exchange = @Exchange(name = RabbitMQConfig.EXCHANGE_NAME, type = "topic"),
            key = "order.created"
    ))
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("получено событие создания заказа {}", event.orderId());
        dispatchService.startDispatching(event.orderId());
    }
}