package org.example.couriers.service.impl;

import org.example.couriers.entitys.User;
import org.example.couriers.mapper.UserMapper;
import org.example.couriers.repo.UserRepository;
import org.example.couriers.service.UserService;
import org.example.courierscontract.dto.request.CreateUserRequest;
import org.example.courierscontract.dto.response.UserResponse;
import org.example.courierscontract.exception.IncorrectDataException;
import org.example.courierscontract.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserResponse createUser(UUID keycloakId, CreateUserRequest request) {
        if (userRepository.findByIdOrEmailOrPhone(keycloakId, request.email(), request.phone()).isPresent())
            throw new IncorrectDataException("пользователь с такими данными уже существует");

        User user = userMapper.toEntity(request);
        user.setId(keycloakId);
        User savedUser = userRepository.save(user);

        return userMapper.toResponse(savedUser);
    }

    @Override
    public UserResponse getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        return userMapper.toResponse(user);
    }
}
