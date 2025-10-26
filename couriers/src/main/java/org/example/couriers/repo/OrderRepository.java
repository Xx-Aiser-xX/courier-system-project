package org.example.couriers.repo;

import org.example.couriers.entitys.Order;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {
    Optional<Order> findById(UUID id);
    Order save(Order entity);
}
