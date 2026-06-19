package com.shopflow.identity.application.commands;

public record AuthenticateUserCommand(
        String username,
        String rawPassword
) {

}
