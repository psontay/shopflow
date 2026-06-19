package com.shopflow.identity.application.token;

import com.shopflow.identity.domain.models.RefreshToken;

import java.util.UUID;

public interface RefreshTokenService {

    RefreshToken findByToken(String refreshToken);

    RefreshToken createRefreshToken(UUID userId);

    RefreshToken verifyExpiration(RefreshToken token);

    void deleteByUserId(UUID userId);

}
