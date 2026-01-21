package org.example.couriers.controller;

import org.example.couriers.service.AuthService;
import org.example.couriers.service.CourierService;
import org.example.couriers.service.KeycloakAdminService;
import org.example.couriers.service.UserService;
import org.example.courierscontract.dto.request.CreateCourierRequest;
import org.example.courierscontract.dto.request.CreateUserRequest;
import org.example.courierscontract.dto.request.LoginRequest;
import org.example.courierscontract.endpoints.AuthApi;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class AuthController implements AuthApi {

    private final KeycloakAdminService keycloakAdminService;
    private final UserService userService;
    private final CourierService courierService;
    private final AuthService authService;

    public AuthController(KeycloakAdminService keycloakAdminService, UserService userService, CourierService courierService, AuthService authService) {
        this.keycloakAdminService = keycloakAdminService;
        this.userService = userService;
        this.courierService = courierService;
        this.authService = authService;
    }

    public ResponseEntity<?> registerUser(CreateUserRequest request) {
        UUID keycloakId = keycloakAdminService.registerUserInKeycloak(
                request.email(), request.password(),"USER");

        userService.createUser(keycloakId, request);
        return ResponseEntity.ok("пользователь зарегистрирован, ID: " + keycloakId);
    }

    public ResponseEntity<?> registerCourier(CreateCourierRequest request) {
        UUID keycloakId = keycloakAdminService.registerUserInKeycloak(
                request.email(), request.password(),"COURIER");

        courierService.registerCourier(keycloakId, request);
        return ResponseEntity.ok("курьер зарегистрирован, ID: " + keycloakId);
    }

    @Override
    public ResponseEntity<?> login(LoginRequest request) {
        Object tokenResponse = authService.performLogin(request.email(), request.password());
        return ResponseEntity.ok(tokenResponse);
    }
}