package com.shopflow.identity.presentation.api.dto;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        String tokenType
) {

}
