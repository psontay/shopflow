package com.shopflow.identity.application.commands;

public record RefreshUserTokenCommand(
        String refreshToken
) {

}
