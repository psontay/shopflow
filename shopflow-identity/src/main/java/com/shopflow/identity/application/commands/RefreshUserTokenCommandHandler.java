package com.shopflow.identity.application.commands;

import com.shopflow.identity.application.security.TokenProviderPort;
import com.shopflow.identity.application.token.RefreshTokenService;
import com.shopflow.identity.domain.models.RefreshToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RefreshUserTokenCommandHandler {

    private final RefreshTokenService refreshTokenService;
    private final TokenProviderPort tokenProviderPort;

    public RefreshUserTokenCommandHandler(RefreshTokenService refreshTokenService,
                                          TokenProviderPort tokenProviderPort) {
        this.refreshTokenService = refreshTokenService;
        this.tokenProviderPort = tokenProviderPort;
    }

    @Transactional
    public String handle(RefreshUserTokenCommand command) {
        RefreshToken refreshToken = refreshTokenService.findByToken(command.refreshToken());
        refreshTokenService.verifyExpiration(refreshToken);
        return tokenProviderPort.generateAccessToken(refreshToken.getUser());
    }

}
