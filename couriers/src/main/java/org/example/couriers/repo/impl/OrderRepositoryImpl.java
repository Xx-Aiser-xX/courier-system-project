package org.example.couriers.repo.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.example.couriers.entitys.Order;
import org.example.couriers.repo.OrderRepository;
import org.springframework.stereotype.Repository;


@Repository
public class OrderRepositoryImpl extends BaseRepository<Order> implements OrderRepository {
    @PersistenceContext
    EntityManager em;

    public OrderRepositoryImpl() {
        super(Order.class);
    }

}