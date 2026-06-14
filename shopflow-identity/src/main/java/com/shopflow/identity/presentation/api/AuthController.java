package com.shopflow.identity.presentation.api;

import com.shopflow.identity.application.commands.RegisterUserCommand;
import com.shopflow.identity.application.commands.RegisterUserCommandHandler;
import com.shopflow.identity.presentation.api.dto.SignUpRequest;
import com.shopflow.identity.presentation.api.dto.SignUpResponse;
import com.shopflow.shared.presentation.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final RegisterUserCommandHandler registerUserCommandHandler;

    public AuthController(RegisterUserCommandHandler registerUserCommandHandler) {
        this.registerUserCommandHandler = registerUserCommandHandler;
    }

    public ResponseEntity<ApiResponse<SignUpResponse>> SignUp(@Valid @RequestBody SignUpRequest request) {
        RegisterUserCommand command = request.toCommand();
        UUID userId = registerUserCommandHandler.handle(command);
        SignUpResponse response = new SignUpResponse(userId, command.email(), command.username(), "Register success");
        ApiResponse<SignUpResponse> wrappedResponse = ApiResponse.created(response,
                                                                          "Register success with username: " + command.username());

        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(wrappedResponse);
    }

}
