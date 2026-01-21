package edu.rutmiit.demo.auditservice.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class DlqEventListener {

    private static final Logger log = LoggerFactory.getLogger(DlqEventListener.class);

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "audit-queue.dlq", durable = "true"),
            exchange = @Exchange(name = "dlx-exchange", type = ExchangeTypes.TOPIC, durable = "true"),
            key = "dlq.audit"
    ))
    public void handleDlqMessages(Object failedMessage) {
        log.error("сообщение попал в DLQ: {}", failedMessage);
    }
}