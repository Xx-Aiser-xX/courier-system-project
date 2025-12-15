package org.example.pricingservice.service;

import org.example.grpc.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class PricingServiceImpl extends PricingServiceGrpc.PricingServiceImplBase {

    @Override
    public void calculatePrice(CalculatePriceRequest request, StreamObserver<CalculatePriceResponse> responseObserver) {
        System.out.println("PRICING: Calculating for " + request.getSenderAddress() + " -> " + request.getRecipientAddress());

        if (request.getRecipientAddress().toUpperCase().contains("MARS")) {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription("Delivery to Mars is not supported yet")
                    .asRuntimeException());
            return;
        }

        double distanceFactor = (request.getSenderAddress().length() + request.getRecipientAddress().length()) * 2.5;
        double weightFactor = request.getWeight() * 50.0;

        double finalPrice = 100.0 + distanceFactor + weightFactor;

        CalculatePriceResponse response = CalculatePriceResponse.newBuilder()
                .setPrice(finalPrice)
                .setCurrency("RUB")
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
