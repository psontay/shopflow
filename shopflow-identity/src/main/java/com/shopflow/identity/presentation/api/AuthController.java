package com.shopflow.identity.presentation.api;

import com.shopflow.identity.application.commands.AuthenticateUserCommand;
import com.shopflow.identity.application.commands.AuthenticateUserCommandHandler;
import com.shopflow.identity.application.commands.RefreshUserTokenCommand;
import com.shopflow.identity.application.commands.RefreshUserTokenCommandHandler;
import com.shopflow.identity.application.commands.RegisterUserCommand;
import com.shopflow.identity.application.commands.RegisterUserCommandHandler;
import com.shopflow.identity.application.queries.AuthenticateResult;
import com.shopflow.identity.application.security.TokenProviderPort;
import com.shopflow.identity.application.token.RefreshTokenService;
import com.shopflow.identity.presentation.api.dto.LoginRequest;
import com.shopflow.identity.presentation.api.dto.LoginResponse;
import com.shopflow.identity.presentation.api.dto.RefreshTokenRequest;
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
    private final AuthenticateUserCommandHandler authenticateUserQueryHandler;
    private final RefreshTokenService refreshTokenService;
    private final TokenProviderPort tokenProviderPort;
    private final RefreshUserTokenCommandHandler refreshUserTokenCommandHandler;

    public AuthController(RegisterUserCommandHandler registerUserCommandHandler,
                          AuthenticateUserCommandHandler authenticateUserQueryHandler,
                          RefreshTokenService refreshTokenService, TokenProviderPort tokenProviderPort,
                          RefreshUserTokenCommandHandler refreshUserTokenCommandHandler) {
        this.registerUserCommandHandler = registerUserCommandHandler;
        this.authenticateUserQueryHandler = authenticateUserQueryHandler;
        this.refreshTokenService = refreshTokenService;
        this.tokenProviderPort = tokenProviderPort;
        this.refreshUserTokenCommandHandler = refreshUserTokenCommandHandler;
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignUpResponse>> signup(@Valid @RequestBody SignUpRequest request) {
        RegisterUserCommand command = request.toCommand();
        UUID userId = registerUserCommandHandler.handle(command);
        SignUpResponse response = new SignUpResponse(userId, command.email(), command.username(), "Register success");
        ApiResponse<SignUpResponse> wrappedResponse = ApiResponse.created(response,
                                                                          "Register success with username: " + command.username());

        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(wrappedResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthenticateResult authenticateResult = authenticateUserQueryHandler.handle(new AuthenticateUserCommand(request.username(),
                                                                                                                request.password()));

        LoginResponse response = new LoginResponse(authenticateResult.accessToken(),
                                                   authenticateResult.refreshToken(),
                                                   "Bearer");

        ApiResponse<LoginResponse> wrappedResponse = ApiResponse.success(response, "Login success");
        return ResponseEntity.ok(wrappedResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> refreshToken(@RequestBody RefreshTokenRequest request) {
        RefreshUserTokenCommand command = new RefreshUserTokenCommand(request.refreshToken());
        String newAccessToken = refreshUserTokenCommandHandler.handle(command);
        LoginResponse response = new LoginResponse(newAccessToken, request.refreshToken(), "Bearer");
        return ResponseEntity.ok(ApiResponse.success(response, "Token refreshed successfully"));
    }

}
