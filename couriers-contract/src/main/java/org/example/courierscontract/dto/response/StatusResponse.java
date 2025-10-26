package org.example.courierscontract.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Стандартный ответ с сообщением о статусе или ошибке")
public record StatusResponse(
        String status,
        String error
) {}
