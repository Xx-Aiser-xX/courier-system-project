package org.example.couriers.assembler;

import org.example.couriers.controller.CourierController;
import org.example.courierscontract.dto.response.CourierResponse;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CourierModelAssembler implements RepresentationModelAssembler<CourierResponse, EntityModel<CourierResponse>> {
    @Override
    public EntityModel<CourierResponse> toModel(CourierResponse courier) {
        return EntityModel.of(courier,
                linkTo(methodOn(CourierController.class).getCourierById(courier.getId())).withSelfRel()
        );
    }
}