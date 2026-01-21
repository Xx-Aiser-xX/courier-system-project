package org.example.couriers.graphql;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.InputArgument;
import org.example.couriers.service.AuthService;
import org.example.couriers.service.CourierService;
import org.example.couriers.service.KeycloakAdminService;
import org.example.couriers.service.UserService;
import org.example.courierscontract.dto.request.CreateCourierRequest;
import org.example.courierscontract.dto.request.CreateUserRequest;
import org.example.courierscontract.dto.request.LoginRequest;
import org.example.courierscontract.dto.response.CourierResponse;
import org.example.courierscontract.dto.response.LoginPayload;
import org.example.courierscontract.dto.response.UserResponse;

import java.util.Map;
import java.util.UUID;

@DgsComponent
public class AuthDataFetcher {

    private final KeycloakAdminService keycloakAdminService;
    private final UserService userService;
    private final CourierService courierService;
    private final AuthService authService;
    private final ObjectMapper objectMapper;

    public AuthDataFetcher(KeycloakAdminService keycloakAdminService,
                           UserService userService,
                           CourierService courierService, AuthService authService,
                           ObjectMapper objectMapper) {
        this.keycloakAdminService = keycloakAdminService;
        this.userService = userService;
        this.courierService = courierService;
        this.authService = authService;
        this.objectMapper = objectMapper;
    }

    @DgsMutation
    public UserResponse registerUser(@InputArgument("input") Map<String, Object> input) {
        CreateUserRequest request = objectMapper.convertValue(input, CreateUserRequest.class);
        UUID keycloakId = keycloakAdminService.registerUserInKeycloak(
                request.email(), request.password(),"USER");
        return userService.createUser(keycloakId, request);
    }

    @DgsMutation
    public CourierResponse registerCourier(@InputArgument("input") Map<String, Object> input) {
        CreateCourierRequest request = objectMapper.convertValue(input, CreateCourierRequest.class);
        UUID keycloakId = keycloakAdminService.registerUserInKeycloak(
                request.email(), request.password(),"COURIER");
        return courierService.registerCourier(keycloakId, request);
    }

    @DgsMutation
    public LoginPayload login(@InputArgument("input") LoginRequest request) {
        Object rawResponse = authService.performLogin(request.email(), request.password());
        return objectMapper.convertValue(rawResponse, LoginPayload.class);
    }
}