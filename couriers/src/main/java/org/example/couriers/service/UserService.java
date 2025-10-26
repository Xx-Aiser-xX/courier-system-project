package org.example.couriers.service;

import org.example.couriers.entitys.User;
import org.example.couriers.repo.UserRepository;
import org.example.courierscontract.dto.request.CreateUserRequest;
import org.example.courierscontract.dto.response.UserResponse;
import org.example.courierscontract.exception.IncorrectDataException;
import org.example.courierscontract.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse createUser(CreateUserRequest request) {
        userRepository.findByEmailOrPhone(request.email(), request.phone())
                .ifPresent(u -> {throw new IncorrectDataException("почта или телефон уже существуют");});

        User user = new User(request.email(), request.phone(), request.name());
        User savedUser = userRepository.save(user);

        return new UserResponse(savedUser.getId(), savedUser.getEmail(), savedUser.getPhone(), savedUser.getName());
    }

    public UserResponse getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        return new UserResponse(user.getId(), user.getEmail(), user.getPhone(), user.getName());
    }
}