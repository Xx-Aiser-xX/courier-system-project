package org.example.couriers.repo;

import org.example.couriers.entitys.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface Test extends JpaRepository<User, UUID> {
}
