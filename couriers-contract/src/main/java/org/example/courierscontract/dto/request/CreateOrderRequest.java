package org.example.courierscontract.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateOrderRequest(
        @NotNull(message = "ID пользователя не может быть пустым")
        UUID userId,

        @NotBlank(message = "Адрес отправителя не может быть пустым")
        String senderAddress,

        @NotBlank(message = "Адрес получателя не может быть пустым")
        String recipientAddress,

        @NotNull(message = "Цена не может быть пустой")
        @DecimalMin(value = "0.0", inclusive = false, message = "Цена должна быть положительной")
        BigDecimal price
) {}
