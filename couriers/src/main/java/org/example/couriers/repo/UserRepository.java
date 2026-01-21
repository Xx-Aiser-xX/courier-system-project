package org.example.couriers.repo;

import org.example.couriers.entitys.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends ReaderRepository<User> {
    User save(User entity);

    Optional<User> findByIdOrEmailOrPhone(UUID id, String email, String phone);
}