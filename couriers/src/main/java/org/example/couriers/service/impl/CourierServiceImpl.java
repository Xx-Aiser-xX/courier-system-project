package org.example.couriers.service.impl;

import org.example.couriers.entitys.Courier;
import org.example.couriers.entitys.Order;
import org.example.couriers.entitys.enums.CourierStatus;
import org.example.couriers.entitys.enums.OrderStatus;
import org.example.couriers.mapper.CourierMapper;
import org.example.couriers.repo.CourierRepository;
import org.example.couriers.repo.OrderRepository;
import org.example.couriers.repo.UserRepository;
import org.example.couriers.service.CourierService;
import org.example.couriers.service.DispatchService;
import org.example.couriers.service.OrderService;
import org.example.courierscontract.dto.request.CreateCourierRequest;
import org.example.courierscontract.dto.request.UpdateCourierLocationRequest;
import org.example.courierscontract.dto.request.UpdateCourierStatusRequest;
import org.example.courierscontract.dto.response.CourierResponse;
import org.example.courierscontract.exception.IncorrectDataException;
import org.example.courierscontract.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CourierServiceImpl implements CourierService {

    private final CourierRepository courierRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final DispatchService dispatchService;
    private final CourierMapper courierMapper;

    public CourierServiceImpl(CourierRepository courierRepository, UserRepository userRepository, OrderRepository orderRepository, OrderService orderService, DispatchService dispatchService, CourierMapper courierMapper) {
        this.courierRepository = courierRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.orderService = orderService;
        this.dispatchService = dispatchService;
        this.courierMapper = courierMapper;
    }

    @Override
    public CourierResponse getCourierById(UUID id) {
        Courier courier = findCourierById(id);
        return courierMapper.toResponse(courier);
    }

    @Override
    public Page<CourierResponse> getAllCouriers(int page, int size) {
        Page<Courier> courierPage = courierRepository.getPageEntities(page, size, false);
        return courierPage.map(courierMapper::toResponse);
    }

    @Override
    @Transactional
    public CourierResponse registerCourier(UUID keycloakId, CreateCourierRequest request) {
        if (userRepository.findByIdOrEmailOrPhone(keycloakId, request.email(), request.phone()).isPresent())
            throw new IncorrectDataException("пользователь с такими данными уже существует");

        Courier courier = courierMapper.toEntity(request);
        courier.setId(keycloakId);
        courier.setStatus(CourierStatus.INACTIVE);

        Courier savedCourier = courierRepository.save(courier);
        return courierMapper.toResponse(savedCourier);
    }

    @Override
    @Transactional
    public CourierResponse updateCourierLocation(UUID id, UpdateCourierLocationRequest request) {
        Courier courier = findCourierById(id);
        courierMapper.updateLocation(request, courier);
        Courier updatedCourier = courierRepository.save(courier);
        return courierMapper.toResponse(updatedCourier);
    }

    @Override
    @Transactional
    public CourierResponse updateCourierStatus(UUID id, UpdateCourierStatusRequest request) {
        Courier courier = findCourierById(id);
        courierMapper.updateStatus(request, courier);
        Courier updatedCourier = courierRepository.save(courier);
        return courierMapper.toResponse(updatedCourier);
    }

    @Override
    public void acceptOrder(UUID courierId, UUID orderId) {
        dispatchService.acceptOrder(courierId, orderId);
    }

    @Override
    public void declineOrder(UUID courierId, UUID orderId) {
        dispatchService.declineOrder(courierId, orderId);
    }

    @Override
    @Transactional
    public void changeDeliveryStatus(UUID courierId, UUID orderId, String statusStr) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));

        if (order.getCourier() == null || !order.getCourier().getId().equals(courierId))
            throw new IncorrectDataException("этот заказ назначен другому курьеру");

        OrderStatus newStatus = OrderStatus.valueOf(statusStr.toUpperCase());
        String notificationMessage = "";

        if (newStatus == OrderStatus.IN_TRANSIT)
            notificationMessage = "курьер забрал ваш заказ";

        else if (newStatus == OrderStatus.DELIVERED) {
            notificationMessage = "заказ доставлен";

            Courier courier = findCourierById(courierId);
            courier.setStatus(CourierStatus.FREE);
            courierRepository.save(courier);
        }

        orderService.updateStatusAndNotify(orderId, newStatus, notificationMessage);
    }

    private Courier findCourierById(UUID id) {
        return courierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Courier", id));
    }
}