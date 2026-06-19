package com.shopflow.identity.application.queries;

public record AuthenticateResult(
        String accessToken,
        String refreshToken
) {

}
