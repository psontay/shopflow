package com.shopflow.identity.application.commands;

public record RegisterUserCommand(String username, String email, String rawPassword, String confirmPassword) {

}
