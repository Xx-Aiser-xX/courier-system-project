package org.example.courierscontract.dto.response;

public record LoginPayload(
        String access_token,
        Integer expires_in,
        String refresh_token,
        String token_type,
        String scope
) {}