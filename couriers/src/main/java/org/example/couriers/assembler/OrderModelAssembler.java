package org.example.couriers.assembler;

import org.example.couriers.controller.CourierController;
import org.example.couriers.controller.OrderController;
import org.example.couriers.controller.UserController;
import org.example.couriers.entitys.enums.OrderStatus;
import org.example.courierscontract.dto.response.OrderResponse;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class OrderModelAssembler implements RepresentationModelAssembler<OrderResponse, EntityModel<OrderResponse>> {

    @Override
    public EntityModel<OrderResponse> toModel(OrderResponse order) {
        EntityModel<OrderResponse> orderModel = EntityModel.of(order,
                linkTo(methodOn(OrderController.class).getOrderById(order.getId())).withSelfRel(),
                linkTo(methodOn(UserController.class).getUserById(order.getUserId())).withRel("customer"));

        if (order.getCourierId() != null)
            orderModel.add(linkTo(methodOn(CourierController.class).getCourierById(order.getCourierId())).withRel("courier"));

        OrderStatus orderStatus = OrderStatus.valueOf(order.getStatus());

        if (OrderStatus.SEARCHING == orderStatus) {
            orderModel.add(linkTo(methodOn(CourierController.class).acceptOrder(order.getId())).withRel("accept"));
            orderModel.add(linkTo(methodOn(CourierController.class).declineOrder(order.getId())).withRel("decline"));
        }
        else if (OrderStatus.ASSIGNED == orderStatus || OrderStatus.IN_TRANSIT == orderStatus)
            orderModel.add(linkTo(methodOn(CourierController.class).changeDeliveryStatus(order.getId(), null)).withRel("changeStatus"));
        else if (OrderStatus.DELIVERED != orderStatus && OrderStatus.CANCELLED != orderStatus)
            orderModel.add(linkTo(methodOn(OrderController.class).deleteOrder(order.getId())).withRel("cancel"));

        return orderModel;
    }
}