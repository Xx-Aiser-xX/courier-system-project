package org.example.courierscontract.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "ответ со статусом или ошибке")
public record StatusResponse(
        String status,
        String error
) {}
