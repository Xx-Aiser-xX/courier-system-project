package edu.rutmiit.demo.statisticsservice.listener;

import edu.rutmiit.demo.events.PriceCalculatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

@Component
public class PricingStatsListener {

    private static final Logger log = LoggerFactory.getLogger(PricingStatsListener.class);

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "q.statistics.pricing", durable = "true"),
            exchange = @Exchange(name = "pricing-fanout", type = "fanout")
    ))
    public void handlePriceEvent(PriceCalculatedEvent event) {
        log.info("+1 запрос, откуда: {}, куда: {}, цена: {}", event.fromAddress(), event.toAddress(), event.price());
        // какая-то логика
    }
}