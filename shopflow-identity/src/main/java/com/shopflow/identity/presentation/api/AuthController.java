package com.shopflow.identity.presentation.api;

import com.shopflow.identity.application.commands.RegisterUserCommand;
import com.shopflow.identity.application.commands.RegisterUserCommandHandler;
import com.shopflow.identity.application.queries.AuthenticateUserQuery;
import com.shopflow.identity.application.queries.AuthenticateUserQueryHandler;
import com.shopflow.identity.presentation.api.dto.LoginRequest;
import com.shopflow.identity.presentation.api.dto.LoginResponse;
import com.shopflow.identity.presentation.api.dto.SignUpRequest;
import com.shopflow.identity.presentation.api.dto.SignUpResponse;
import com.shopflow.shared.presentation.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final RegisterUserCommandHandler registerUserCommandHandler;
    private final AuthenticateUserQueryHandler authenticateUserQueryHandler;

    public AuthController(RegisterUserCommandHandler registerUserCommandHandler,
                          AuthenticateUserQueryHandler authenticateUserQueryHandler) {
        this.registerUserCommandHandler = registerUserCommandHandler;
        this.authenticateUserQueryHandler = authenticateUserQueryHandler;
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignUpResponse>> SignUp(@Valid @RequestBody SignUpRequest request) {
        RegisterUserCommand command = request.toCommand();
        UUID userId = registerUserCommandHandler.handle(command);
        SignUpResponse response = new SignUpResponse(userId, command.email(), command.username(), "Register success");
        ApiResponse<SignUpResponse> wrappedResponse = ApiResponse.created(response,
                                                                          "Register success with username: " + command.username());

        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(wrappedResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> Login(@Valid @RequestBody LoginRequest request) {
        AuthenticateUserQuery query = new AuthenticateUserQuery(request.username(), request.password());
        String token = authenticateUserQueryHandler.handle(query);

        LoginResponse response = new LoginResponse(token, "Bearer");

        ApiResponse<LoginResponse> wrappedResponse = ApiResponse.success(response, "Login success");
        return ResponseEntity.ok(wrappedResponse);
    }

}
