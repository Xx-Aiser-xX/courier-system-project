package org.example.couriers.repo;

import org.example.couriers.entitys.Courier;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CourierRepository {
    Optional<Courier> findById(UUID id);
    Courier save(Courier entity);
    List<Courier> getAll(boolean deleted);
    Page<Courier> getPageEntities(int page, int size, boolean deleted);
}
