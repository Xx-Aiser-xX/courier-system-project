package org.example.courierscontract.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateOrderRequest(
        @NotBlank(message = "Адрес отправителя не может быть пустым")
        String senderAddress,

        @NotBlank(message = "Адрес получателя не может быть пустым")
        String recipientAddress,

        @NotNull(message = "Вес не может быть пустым")
        @DecimalMin(value = "0.0", inclusive = false, message = "Вес должен быть положительным")
        Double weight
) {}
