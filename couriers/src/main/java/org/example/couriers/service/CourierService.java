package org.example.couriers.service;

import org.example.courierscontract.dto.request.CreateCourierRequest;
import org.example.courierscontract.dto.request.UpdateCourierLocationRequest;
import org.example.courierscontract.dto.request.UpdateCourierStatusRequest;
import org.example.courierscontract.dto.response.CourierResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public interface CourierService {
    CourierResponse getCourierById(UUID id);

    Page<CourierResponse> getAllCouriers(int page, int size);

    CourierResponse registerCourier(UUID keycloakId, CreateCourierRequest request);

    CourierResponse updateCourierLocation(UUID id, UpdateCourierLocationRequest request);

    CourierResponse updateCourierStatus(UUID id, UpdateCourierStatusRequest request);

    void acceptOrder(UUID courierId, UUID orderId);

    void declineOrder(UUID courierId, UUID orderId);

    void changeDeliveryStatus(UUID courierId, UUID orderId, String statusStr);
}