package com.shopflow.identity.application.commands;

import com.shopflow.identity.application.queries.AuthenticateResult;
import com.shopflow.identity.application.security.TokenProviderPort;
import com.shopflow.identity.application.token.RefreshTokenService;
import com.shopflow.identity.domain.exceptions.UserDomainException;
import com.shopflow.identity.domain.exceptions.UserErrorCode;
import com.shopflow.identity.domain.models.RefreshToken;
import com.shopflow.identity.domain.models.User;
import com.shopflow.identity.domain.models.UserStatus;
import com.shopflow.identity.domain.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthenticateUserCommandHandler {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProviderPort tokenProviderPort;
    private final RefreshTokenService refreshTokenService;

    public AuthenticateUserCommandHandler(UserRepository userRepository, PasswordEncoder passwordEncoder,
                                          TokenProviderPort tokenProviderPort,
                                          RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProviderPort = tokenProviderPort;
        this.refreshTokenService = refreshTokenService;
    }

    @Transactional
    public AuthenticateResult handle(AuthenticateUserCommand query) {
        User user = userRepository.findByUsername(query.username())
                                  .orElseThrow(() -> new UserDomainException(
                                          UserErrorCode.USER_NOT_FOUND));

        if (user.getUserStatus() != UserStatus.ACTIVE) {
            throw new UserDomainException(UserErrorCode.INVALID_USER_STATE);
        }

        boolean isPasswordMatch = passwordEncoder.matches(query.rawPassword(), user.getHashedPassword());

        if (! isPasswordMatch) {
            throw new UserDomainException(UserErrorCode.INVALID_CREDENTIALS);
        }
        String accessToken = tokenProviderPort.generateAccessToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());
        return new AuthenticateResult(accessToken, refreshToken.getToken());
    }

}
