package org.example.couriers.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    private static final Logger log = LoggerFactory.getLogger(RabbitMQConfig.class);

    public static final String EXCHANGE_NAME = "orders-exchange";
    public static final String ROUTING_KEY_ORDER_CREATED = "order.created";
    public static final String ROUTING_KEY_ORDER_DELETED = "order.deleted";
    public static final String ROUTING_KEY_ORDER_STATUS = "order.status_changed";
    public static final String FANOUT_EXCHANGE = "pricing-fanout";

    @Bean
    public TopicExchange ordersExchange() {
        return new TopicExchange(EXCHANGE_NAME, true, false);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter(new ObjectMapper().findAndRegisterModules());
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, Jackson2JsonMessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (!ack) {
                log.error("NACK: Message delivery to exchange failed! correlationData={}, cause={}", correlationData, cause);
            } else {
                log.info("ACK: Message delivered to exchange successfully. correlationData={}", correlationData);
            }
        });

        return rabbitTemplate;
    }

    @Bean
    public org.springframework.amqp.core.FanoutExchange pricingExchange() {
        return new org.springframework.amqp.core.FanoutExchange(FANOUT_EXCHANGE, true, false);
    }
}