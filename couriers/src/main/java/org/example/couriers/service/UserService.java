package org.example.couriers.service;

import org.example.courierscontract.dto.request.CreateUserRequest;
import org.example.courierscontract.dto.response.UserResponse;

import java.util.UUID;

public interface UserService {
    UserResponse createUser(UUID keycloakId, CreateUserRequest request);

    UserResponse getUserById(UUID id);
}