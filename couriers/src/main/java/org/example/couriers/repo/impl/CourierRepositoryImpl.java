package org.example.couriers.repo.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.example.couriers.entitys.Courier;
import org.example.couriers.entitys.enums.CourierStatus;
import org.example.couriers.repo.CourierRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CourierRepositoryImpl extends BaseRepository<Courier> implements CourierRepository {
    @PersistenceContext
    EntityManager em;

    public CourierRepositoryImpl() {
        super(Courier.class);
    }

    @Override
    public List<Courier> findAllByStatus(CourierStatus status) {
        return em.createQuery(
                "SELECT c FROM Courier c " +
                        "WHERE c.status = :status AND c.deleted = false", Courier.class)
                .setParameter("status", status)
                .getResultList();
    }
}
