package com.shopflow.identity.presentation.api.dto;

import java.util.UUID;

public record SignUpResponse(
        UUID id,
        String email,
        String username,
        String message
) {

}
