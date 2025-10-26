package org.example.courierscontract.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateCourierStatusRequest(
        @NotBlank(message = "Статус не может быть пустым")
        String status
) {}
