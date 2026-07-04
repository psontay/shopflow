package com.shopflow.identity.application.commands;

public record VerifyRegisterCommand(
        String email, String otp
) {

    public VerifyRegisterCommand {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        if (otp == null || otp.isBlank()) {
            throw new IllegalArgumentException("OTP cannot be empty");
        }
    }

}
