package org.example.couriers.assembler;

import org.example.couriers.controller.CourierController;
import org.example.couriers.controller.OrderController;
import org.example.couriers.controller.UserController;
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
                linkTo(methodOn(UserController.class).getUserById(order.getUserId())).withRel("user"));
        if (order.getCourierId() != null) {
            orderModel.add(linkTo(methodOn(CourierController.class).getCourierById(order.getCourierId())).withRel("courier"));
        }
        return orderModel;
    }
}