package org.example.couriers.service;

import org.example.couriers.entitys.Courier;
import org.example.couriers.entitys.enums.CourierStatus;
import org.example.couriers.repo.CourierRepository;
import org.example.courierscontract.dto.request.UpdateCourierLocationRequest;
import org.example.courierscontract.dto.request.UpdateCourierStatusRequest;
import org.example.courierscontract.dto.response.CourierResponse;
import org.example.courierscontract.exception.IncorrectDataException;
import org.example.courierscontract.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CourierService {

    private final CourierRepository courierRepository;

    public CourierService(CourierRepository courierRepository) {
        this.courierRepository = courierRepository;
    }

    public CourierResponse getCourierById(UUID id) {
        Courier courier = findCourierById(id);
        return mapToCourierResponse(courier);
    }

    public Page<CourierResponse> getAllCouriers(int page, int size) {
        Page<Courier> courierPage = courierRepository.getPageEntities(page, size, false);
        return courierPage.map(this::mapToCourierResponse);
    }

    @Transactional
    public CourierResponse updateCourierLocation(UUID id, UpdateCourierLocationRequest request) {
        Courier courier = findCourierById(id);
        courier.setLatitude(request.latitude());
        courier.setLongitude(request.longitude());
        Courier updatedCourier = courierRepository.save(courier);
        return mapToCourierResponse(updatedCourier);
    }

    @Transactional
    public CourierResponse updateCourierStatus(UUID id, UpdateCourierStatusRequest request) {
        Courier courier = findCourierById(id);
        try {
            CourierStatus newStatus = CourierStatus.valueOf(request.status().toUpperCase());
            courier.setStatus(newStatus);
        } catch (IllegalArgumentException e) {
            throw new IncorrectDataException("Invalid status value: " + request.status());
        }
        Courier updatedCourier = courierRepository.save(courier);
        return mapToCourierResponse(updatedCourier);
    }

    private Courier findCourierById(UUID id) {
        return courierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Courier", id));
    }

    private CourierResponse mapToCourierResponse(Courier courier) {
        return new CourierResponse(
                courier.getId(),
                courier.getName(),
                courier.getPhone(),
                courier.getDeliveryMethod(),
                courier.getStatus().name(),
                courier.getLatitude(),
                courier.getLongitude()
        );
    }
}