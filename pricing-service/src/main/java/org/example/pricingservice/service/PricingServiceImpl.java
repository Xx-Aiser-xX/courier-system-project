package org.example.pricingservice.service;

import io.grpc.Status;
import org.example.grpc.*;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.math.BigDecimal;

@GrpcService
public class PricingServiceImpl extends PricingServiceGrpc.PricingServiceImplBase {

    @Override
    public void calculatePrice(CalculatePriceRequest request, StreamObserver<CalculatePriceResponse> responseObserver) {
        if (request.getWeight() <= 0) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("вес должен быть больше нуля")
                    .asRuntimeException());
            return;
        }

        if (request.getSenderAddress().isEmpty() || request.getRecipientAddress().isEmpty()) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("адреса не могут быть пустыми")
                    .asRuntimeException());
            return;
        }
        BigDecimal finalPrice = BigDecimal.valueOf(100.0)
                .add(BigDecimal.valueOf((request.getSenderAddress().length() + request.getRecipientAddress().length()) * 2.5))
                .add(BigDecimal.valueOf(request.getWeight() * 50.0));

        long unit = finalPrice.longValue();
        int nanos = finalPrice.remainder(BigDecimal.ONE).movePointRight(9).intValue();

        Money money = Money.newBuilder()
                .setCurrencyCode("RUB")
                .setUnits(unit)
                .setNanos(nanos)
                .build();

        CalculatePriceResponse response = CalculatePriceResponse.newBuilder()
                .setPrice(money).build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
