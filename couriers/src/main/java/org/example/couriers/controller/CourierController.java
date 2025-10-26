package org.example.couriers.controller;

import org.example.couriers.assembler.CourierModelAssembler;
import org.example.couriers.service.CourierService;
import org.example.courierscontract.dto.request.UpdateCourierLocationRequest;
import org.example.courierscontract.dto.request.UpdateCourierStatusRequest;
import org.example.courierscontract.dto.response.CourierResponse;
import org.example.courierscontract.endpoints.CourierApi;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.web.bind.annotation.RestController;


import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
public class CourierController implements CourierApi {

    private final CourierService courierService;
    private final CourierModelAssembler courierAssembler;

    public CourierController(CourierService courierService, CourierModelAssembler courierAssembler) {
        this.courierService = courierService;
        this.courierAssembler = courierAssembler;
    }

    @Override
    public PagedModel<EntityModel<CourierResponse>> getAllCouriers(int page, int size, PagedResourcesAssembler<CourierResponse> assembler) {
        Page<CourierResponse> courierPage = courierService.getAllCouriers(page, size);
        return assembler.toModel(courierPage, courierAssembler);
    }

    @Override
    public EntityModel<CourierResponse> getCourierById(UUID id) {
        return courierAssembler.toModel(courierService.getCourierById(id));
    }

    @Override
    public EntityModel<CourierResponse> updateCourierLocation(UUID id, UpdateCourierLocationRequest request) {
        return courierAssembler.toModel(courierService.updateCourierLocation(id, request));
    }

    @Override
    public EntityModel<CourierResponse> updateCourierStatus(UUID id, UpdateCourierStatusRequest request) {
        return courierAssembler.toModel(courierService.updateCourierStatus(id, request));
    }
}