package org.example.couriers.service;

import org.example.couriers.entitys.enums.OrderStatus;
import org.example.courierscontract.dto.request.CreateOrderRequest;
import org.example.courierscontract.dto.response.OrderResponse;

import java.util.UUID;

public interface OrderService {
    OrderResponse createOrder(UUID userId, CreateOrderRequest request);

    void updateStatusAndNotify(UUID orderId, OrderStatus newStatus, String msg);

    OrderResponse getOrderById(UUID id);

    void deleteOrder(UUID id);
}