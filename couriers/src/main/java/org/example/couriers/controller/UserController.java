package org.example.couriers.controller;

import org.example.couriers.assembler.UserModelAssembler;
import org.example.couriers.service.UserService;
import org.example.courierscontract.dto.request.CreateUserRequest;
import org.example.courierscontract.dto.response.UserResponse;
import org.example.courierscontract.endpoints.UserApi;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class UserController implements UserApi {

    private final UserService userService;
    private final UserModelAssembler userAssembler;

    public UserController(UserService userService, UserModelAssembler userAssembler) {
        this.userService = userService;
        this.userAssembler = userAssembler;
    }

    @Override
    public ResponseEntity<EntityModel<UserResponse>> createUser(CreateUserRequest request) {
        UserResponse userResponse = userService.createUser(request);
        EntityModel<UserResponse> entityModel = userAssembler.toModel(userResponse);

        return ResponseEntity
                .created(entityModel.getRequiredLink("self").toUri())
                .body(entityModel);
    }

    @Override
    public EntityModel<UserResponse> getUserById(UUID id) {
        UserResponse userResponse = userService.getUserById(id);
        return userAssembler.toModel(userResponse);
    }
}