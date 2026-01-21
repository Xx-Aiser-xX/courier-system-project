package org.example.couriers.mapper;

import edu.rutmiit.demo.events.OrderCreatedEvent;
import edu.rutmiit.demo.events.OrderStatusChangedEvent;
import org.example.couriers.entitys.Order;
import org.example.couriers.entitys.User;
import org.example.couriers.entitys.enums.OrderStatus;
import org.example.courierscontract.dto.request.CreateOrderRequest;
import org.example.courierscontract.dto.response.OrderResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Mapper(componentModel = "spring", imports = {OrderStatus.class, LocalDateTime.class})
public interface OrderMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "courier.id", target = "courierId")
    OrderResponse toResponse(Order order);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "courier", ignore = true)
    @Mapping(target = "notifications", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(source = "request.senderAddress", target = "senderAddress")
    @Mapping(source = "request.recipientAddress", target = "recipientAddress")
    @Mapping(source = "user", target = "user")
    @Mapping(source = "price", target = "price")
    @Mapping(target = "status", expression = "java(OrderStatus.CREATED)")
    @Mapping(target = "createdAt", expression = "java(LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(LocalDateTime.now())")
    Order toEntity(CreateOrderRequest request, User user, BigDecimal price);

    @Mapping(source = "id", target = "orderId")
    @Mapping(source = "user.id", target = "userId")
    OrderCreatedEvent toCreatedEvent(Order order);

    @Mapping(source = "order.id", target = "orderId")
    @Mapping(source = "order.user.id", target = "userId")
    @Mapping(source = "newStatus", target = "newStatus")
    @Mapping(source = "message", target = "message")
    OrderStatusChangedEvent toStatusChangedEvent(Order order, OrderStatus newStatus, String message);
}