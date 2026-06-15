package com.shopflow.identity.application.commands;

import java.util.UUID;

public record ChangePasswordCommand(
        UUID userId,
        String oldPassword,
        String newPassword
) {

}
