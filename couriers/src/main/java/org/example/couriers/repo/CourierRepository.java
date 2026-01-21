package org.example.couriers.repo;

import org.example.couriers.entitys.Courier;
import org.example.couriers.entitys.enums.CourierStatus;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface CourierRepository extends ReaderRepository<Courier> {
    Courier save(Courier entity);

    List<Courier> findAllByStatus(CourierStatus status);
}
