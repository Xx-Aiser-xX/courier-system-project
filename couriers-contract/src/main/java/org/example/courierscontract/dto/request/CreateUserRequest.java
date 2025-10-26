package org.example.courierscontract.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CreateUserRequest(
        @NotBlank(message = "Email не может быть пустым")
        @Email(message = "Некорректный формат email")
        String email,

        @NotBlank(message = "Телефон не может быть пустым")
        @Pattern(regexp = "^\\d{11}$", message = "Телефон должен состоять из 11 цифр")
        String phone,

        @NotBlank(message = "Имя не может быть пустым")
        String name
) {}