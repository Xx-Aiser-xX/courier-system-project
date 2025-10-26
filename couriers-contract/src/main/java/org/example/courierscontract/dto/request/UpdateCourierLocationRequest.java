package org.example.courierscontract.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UpdateCourierLocationRequest(
        @NotNull(message = "Широта не может быть пустой")
        @Min(value = -90, message = "Широта должна быть от -90 до 90")
        @Max(value = 90, message = "Широта должна быть от -90 до 90")
        Double latitude,

        @NotNull(message = "Долгота не может быть пустой")
        @Min(value = -180, message = "Долгота должна быть от -180 до 180")
        @Max(value = 180, message = "Долгота должна быть от -180 до 180")
        Double longitude
) {}