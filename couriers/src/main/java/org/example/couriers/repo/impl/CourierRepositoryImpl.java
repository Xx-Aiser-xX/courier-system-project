package org.example.couriers.repo.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.example.couriers.entitys.Courier;
import org.example.couriers.repo.CourierRepository;
import org.springframework.stereotype.Repository;

@Repository
public class CourierRepositoryImpl extends BaseRepository<Courier> implements CourierRepository {
    @PersistenceContext
    EntityManager em;

    public CourierRepositoryImpl() {
        super(Courier.class);
    }
}
