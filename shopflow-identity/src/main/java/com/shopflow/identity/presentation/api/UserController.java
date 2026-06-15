package com.shopflow.identity.presentation.api;

import com.shopflow.identity.application.commands.ChangePasswordCommand;
import com.shopflow.identity.application.commands.ChangePasswordCommandHandler;
import com.shopflow.identity.application.queries.GetUserProfileQueryHandler;
import com.shopflow.identity.application.queries.UserProfileResponse;
import com.shopflow.identity.presentation.api.dto.ChangePasswordRequest;
import com.shopflow.shared.presentation.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final GetUserProfileQueryHandler getUserProfileQueryHandler;
    private final ChangePasswordCommandHandler changePasswordCommandHandler;

    public UserController(GetUserProfileQueryHandler getUserProfileQueryHandler,
                          ChangePasswordCommandHandler changePasswordCommandHandler) {
        this.getUserProfileQueryHandler = getUserProfileQueryHandler;
        this.changePasswordCommandHandler = changePasswordCommandHandler;
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getMyProfile(Principal principal) {
        UUID userId = UUID.fromString(principal.getName());
        UserProfileResponse response = getUserProfileQueryHandler.handle(userId);
        return ResponseEntity.ok(ApiResponse.success(response, "Get profile successfully"));
    }

    @PostMapping("/me/password")

    public ResponseEntity<ApiResponse<Void>> changePassword(
            Principal principal,
            @Valid @RequestBody ChangePasswordRequest request) {
        UUID userId = UUID.fromString(principal.getName());
        ChangePasswordCommand command = new ChangePasswordCommand(
                userId,
                request.oldPassword(),
                request.newPassword()
        );
        changePasswordCommandHandler.handle(command);
        return ResponseEntity.ok(ApiResponse.success(null, "Password changed successfully"));
    }

}
