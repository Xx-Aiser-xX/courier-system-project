package org.example.couriers.repo;

import org.example.couriers.entitys.Courier;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface ReaderRepository<T> {
    Optional<T> findById(UUID id);
    List<T> getAll(boolean deleted);
    Page<T> getPageEntities(int page, int size, boolean deleted);
}
