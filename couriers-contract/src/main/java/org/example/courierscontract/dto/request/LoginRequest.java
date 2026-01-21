package org.example.courierscontract.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "Email не может быть пустым")
        String email,
        @NotBlank(message = "Пароль не может быть пустым")
        String password
){}