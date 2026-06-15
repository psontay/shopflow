package com.shopflow.identity.application.queries;

public record AuthenticateUserQuery(
        String username,
        String rawPassword
) {

}
