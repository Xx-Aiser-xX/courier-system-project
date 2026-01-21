package org.example.courierscontract.endpoints;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.courierscontract.dto.request.CreateCourierRequest;
import org.example.courierscontract.dto.request.CreateUserRequest;
import org.example.courierscontract.dto.request.LoginRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


@Tag(name = "аутентификация", description = "регистрация пользователей и курьеров")
@RequestMapping("/api/auth")
public interface AuthApi {

    @Operation(summary = "регистрация нового клиента")
    @PostMapping("/register/user")
    ResponseEntity<?> registerUser(@RequestBody CreateUserRequest request);

    @Operation(summary = "регистрация нового курьера")
    @PostMapping("/register/courier")
    ResponseEntity<?> registerCourier(@RequestBody CreateCourierRequest request);

    @Operation(summary = "вход в систему (получение токена)")
    @PostMapping("/login")
    ResponseEntity<?> login(@RequestBody LoginRequest request);
}
