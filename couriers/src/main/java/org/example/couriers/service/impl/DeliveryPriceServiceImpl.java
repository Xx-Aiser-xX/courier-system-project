package org.example.couriers.service.impl;

import edu.rutmiit.demo.events.PriceCalculatedEvent;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.example.couriers.config.RabbitMQConfig;
import org.example.couriers.service.DeliveryPriceService;
import org.example.courierscontract.exception.IncorrectDataException;
import org.example.grpc.CalculatePriceRequest;
import org.example.grpc.CalculatePriceResponse;
import org.example.grpc.Money;
import org.example.grpc.PricingServiceGrpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class DeliveryPriceServiceImpl implements DeliveryPriceService {
    private static final Logger log = LoggerFactory.getLogger(org.example.couriers.service.DeliveryPriceService.class);
    private final RabbitTemplate rabbitTemplate;

    @GrpcClient("pricing-service-stub")
    private PricingServiceGrpc.PricingServiceBlockingStub pricingStub;
    public DeliveryPriceServiceImpl(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }
    @Override
    public BigDecimal calculateDeliveryPrice(String from, String to, double weight, UUID userId) {
        try {
            CalculatePriceRequest request = CalculatePriceRequest.newBuilder()
                    .setSenderAddress(from)
                    .setRecipientAddress(to)
                    .setWeight(weight)
                    .build();

            CalculatePriceResponse response = pricingStub.calculatePrice(request);
            Money money = response.getPrice();

            BigDecimal price = BigDecimal.valueOf(money.getUnits())
                    .add(BigDecimal.valueOf(money.getNanos(), 9));

            PriceCalculatedEvent event = new PriceCalculatedEvent(
                    userId, price, money.getCurrencyCode(), from, to);

            rabbitTemplate.convertAndSend(RabbitMQConfig.FANOUT_EXCHANGE, "", event);
            log.info("Событие отправлено в Fanout: {}", event);
            return price;
        } catch (StatusRuntimeException e) {
            log.error("gRPC ошибка: {}", e.getStatus().getDescription());
            throw new IncorrectDataException("ошибка расчета цены: " + e.getStatus().getDescription());
        } catch (Exception e) {
            log.error("ошибка", e);
            throw new RuntimeException("сервис расчета недоступен");
        }
    }
}
