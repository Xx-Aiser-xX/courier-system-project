package org.example.couriers.repo;

import org.example.couriers.entitys.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    Optional<User> findById(UUID id);
    User save(User entity);

    Optional<User> findByEmailOrPhone(String email, String phone);
}