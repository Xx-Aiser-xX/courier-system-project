package org.example.couriers.controller;

import org.example.couriers.assembler.OrderModelAssembler;
import org.example.couriers.service.DeliveryPriceService;
import org.example.couriers.service.OrderService;
import org.example.couriers.service.SecurityService;
import org.example.courierscontract.dto.request.CreateOrderRequest;
import org.example.courierscontract.dto.response.OrderResponse;
import org.example.courierscontract.endpoints.OrderApi;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
public class OrderController implements OrderApi {

    private final OrderService orderService;
    private final SecurityService securityService;
    private final OrderModelAssembler orderAssembler;
    private final DeliveryPriceService deliveryPriceService;

    public OrderController(OrderService orderService, SecurityService securityService, OrderModelAssembler orderAssembler, DeliveryPriceService deliveryPriceService) {
        this.orderService = orderService;
        this.securityService = securityService;
        this.orderAssembler = orderAssembler;
        this.deliveryPriceService = deliveryPriceService;
    }

    @Override
    public ResponseEntity<EntityModel<OrderResponse>> createOrder(CreateOrderRequest request) {
        UUID userId = securityService.getCurrentUserId();
        OrderResponse orderResponse = orderService.createOrder(userId, request);
        EntityModel<OrderResponse> entityModel = orderAssembler.toModel(orderResponse);

        return ResponseEntity
                .created(entityModel.getRequiredLink("self").toUri())
                .body(entityModel);
    }

    @Override
    public EntityModel<OrderResponse> getOrderById(UUID id) {
        OrderResponse orderResponse = orderService.getOrderById(id);
        return orderAssembler.toModel(orderResponse);
    }

    @Override
    public ResponseEntity<Void> deleteOrder(UUID id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public BigDecimal checkPrice(String from, String to, double weight, UUID userId) {
        return deliveryPriceService.calculateDeliveryPrice(from, to, weight, userId);
    }
}