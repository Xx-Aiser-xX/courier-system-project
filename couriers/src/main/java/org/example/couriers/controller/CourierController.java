package org.example.couriers.controller;

import org.example.couriers.assembler.CourierModelAssembler;
import org.example.couriers.service.CourierService;
import org.example.couriers.service.SecurityService;
import org.example.courierscontract.dto.request.UpdateCourierLocationRequest;
import org.example.courierscontract.dto.request.UpdateCourierStatusRequest;
import org.example.courierscontract.dto.response.CourierResponse;
import org.example.courierscontract.endpoints.CourierApi;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;


import java.util.UUID;


@RestController
public class CourierController implements CourierApi {

    private final CourierService courierService;
    private final SecurityService securityService;
    private final CourierModelAssembler courierAssembler;

    public CourierController(CourierService courierService, SecurityService securityService, CourierModelAssembler courierAssembler) {
        this.courierService = courierService;
        this.securityService = securityService;
        this.courierAssembler = courierAssembler;
    }

    @Override
    public EntityModel<CourierResponse> getCourierById(UUID id) {
        return courierAssembler.toModel(courierService.getCourierById(id));
    }

    @Override
    public ResponseEntity<Void> acceptOrder(UUID orderId) {
        UUID courierId = securityService.getCurrentUserId();
        courierService.acceptOrder(courierId, orderId);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> declineOrder(UUID orderId) {
        UUID courierId = securityService.getCurrentUserId();
        courierService.declineOrder(courierId, orderId);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> changeDeliveryStatus(UUID orderId, String newStatus) {
        UUID courierId = securityService.getCurrentUserId();
        courierService.changeDeliveryStatus(courierId, orderId, newStatus);
        return ResponseEntity.ok().build();
    }

    @Override
    public EntityModel<CourierResponse> updateCourierLocation(UUID id, UpdateCourierLocationRequest request) {
        return courierAssembler.toModel(courierService.updateCourierLocation(id, request));
    }

    @Override
    public EntityModel<CourierResponse> updateCourierStatus(UpdateCourierStatusRequest request) {
        UUID currentCourierId = securityService.getCurrentUserId();
        return courierAssembler.toModel(courierService.updateCourierStatus(currentCourierId, request));
    }
}