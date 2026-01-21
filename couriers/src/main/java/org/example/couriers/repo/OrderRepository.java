package org.example.couriers.repo;

import org.example.couriers.entitys.Order;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends ReaderRepository<Order> {
    Order save(Order entity);
}
