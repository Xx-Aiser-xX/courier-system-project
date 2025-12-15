package org.example.couriers.service;

import edu.rutmiit.demo.events.PriceCalculatedEvent;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.example.couriers.config.RabbitMQConfig;
import org.example.grpc.CalculatePriceRequest;
import org.example.grpc.CalculatePriceResponse;
import org.example.grpc.PricingServiceGrpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class DeliveryPriceService {

    private static final Logger log = LoggerFactory.getLogger(DeliveryPriceService.class);

    private final RabbitTemplate rabbitTemplate;

    @GrpcClient("pricing-service-stub")
    private PricingServiceGrpc.PricingServiceBlockingStub pricingStub;

    public DeliveryPriceService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public BigDecimal calculateDeliveryPrice(String from, String to, double weight, UUID userId) {
        try {
            CalculatePriceRequest request = CalculatePriceRequest.newBuilder()
                    .setSenderAddress(from)
                    .setRecipientAddress(to)
                    .setWeight(weight)
                    .build();

            CalculatePriceResponse response = pricingStub.calculatePrice(request);
            BigDecimal price = BigDecimal.valueOf(response.getPrice());

            PriceCalculatedEvent event = new PriceCalculatedEvent(
                    userId, price, response.getCurrency(), from, to);

            rabbitTemplate.convertAndSend(RabbitMQConfig.FANOUT_EXCHANGE, "", event);
            log.info("Event sent to Fanout: {}", event);

            return price;

        } catch (StatusRuntimeException e) {
            log.error("gRPC Error: {}", e.getStatus().getDescription());
            return BigDecimal.valueOf(-1);
        } catch (Exception e) {
            log.error("Unexpected error", e);
            return BigDecimal.valueOf(-1);
        }
    }
}

