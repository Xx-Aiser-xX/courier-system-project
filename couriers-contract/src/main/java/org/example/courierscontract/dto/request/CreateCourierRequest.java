package org.example.courierscontract.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateCourierRequest(
        @NotBlank(message = "Email не может быть пустым")
        @Email(message = "Некорректный формат email")
        String email,

        @NotBlank(message = "Пароль не может быть пустым")
        @Size(min = 6, message = "Пароль должен быть не менее 6 символов")
        String password,

        @NotBlank(message = "Телефон не может быть пустым")
        @Pattern(regexp = "^\\d{11}$", message = "Телефон должен состоять из 11 цифр")
        String phone,

        @NotBlank(message = "Имя не может быть пустым")
        String name,

        @NotBlank(message = "Способ доставки не может быть пустым")
        String deliveryMethod
) {}