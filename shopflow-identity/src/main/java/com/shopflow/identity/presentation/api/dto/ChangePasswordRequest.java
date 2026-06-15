package com.shopflow.identity.presentation.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(
        @NotBlank(message = "Old password is required")
        String oldPassword,
        @NotBlank(message = "New password is required")
        @Size(min = 6,
                max = 40,
                message = "New password must be between 6 and 40 chars")
        String newPassword
) {

}
