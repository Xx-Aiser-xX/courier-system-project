package edu.rutmiit.demo.auditservice.listener;

import edu.rutmiit.demo.events.PriceCalculatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

@Component
public class PricingEventListener {
    private static final Logger log = LoggerFactory.getLogger(PricingEventListener.class);

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "q.audit.pricing", durable = "true"),
            exchange = @Exchange(name = "pricing-fanout", type = "fanout")
    ))
    public void handlePriceEvent(PriceCalculatedEvent event) {
        String userStr = (event.userId() != null) ? event.userId().toString() : "Anonymous";
        log.info("AUDIT: Расчет цены для {}. Маршрут: {} -> {}. Сумма: {} {}",
                userStr, event.fromAddress(), event.toAddress(), event.price(), event.currency());
    }
}