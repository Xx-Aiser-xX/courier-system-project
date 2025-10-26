package org.example.couriers.controller;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class RootController {

    @GetMapping
    public RepresentationModel<?> getRoot() {
        RepresentationModel<?> rootModel = new RepresentationModel<>();

        rootModel.add(linkTo(methodOn(CourierController.class).getAllCouriers(0, 10, null)).withRel("couriers"));
        rootModel.add(linkTo(OrderController.class).withRel("orders"));
        rootModel.add(linkTo(UserController.class).withRel("users"));
        rootModel.add(Link.of("/swagger-ui.html", "documentation"));

        return rootModel;
    }
}