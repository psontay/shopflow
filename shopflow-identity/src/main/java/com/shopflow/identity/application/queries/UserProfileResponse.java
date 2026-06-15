package com.shopflow.identity.application.queries;

import com.shopflow.identity.domain.models.Role;
import com.shopflow.identity.domain.models.UserStatus;

import java.util.UUID;

public record UserProfileResponse(
        UUID id,
        String username,
        String email,
        Role role,
        UserStatus status
) {

}
