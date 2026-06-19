package com.shopflow.identity.application.token;

import com.shopflow.identity.domain.exceptions.IdentityDomainException;
import com.shopflow.identity.domain.exceptions.IdentityErrorCode;
import com.shopflow.identity.domain.models.RefreshToken;
import com.shopflow.identity.domain.models.User;
import com.shopflow.identity.domain.repository.RefreshTokenRepository;
import com.shopflow.identity.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @Value("${app.jwt.refreshExpirationMs:2592000000}")
    private Long refreshTokenDurationMs;

    public RefreshTokenServiceImpl(RefreshTokenRepository refreshTokenRepository, UserRepository userRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    @Override
    public RefreshToken findByToken(String refreshToken) {
        return refreshTokenRepository.findByToken(refreshToken)
                                     .orElseThrow(() -> new IdentityDomainException(IdentityErrorCode.INVALID_REFRESH_TOKEN));
    }

    @Override
    @Transactional
    public RefreshToken createRefreshToken(UUID userId) {
        User user = userRepository.findById(userId)
                                  .orElseThrow(() -> new IdentityDomainException(IdentityErrorCode.USER_NOT_FOUND));

        RefreshToken refreshToken = new RefreshToken(
                user,
                UUID.randomUUID()
                    .toString(),
                Instant.now()
                       .plus(refreshTokenDurationMs, ChronoUnit.MILLIS)
        );

        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    @Transactional
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.isExpired()) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token was expired. Please make a new signin request");
        }
        return token;
    }

    @Override
    @Transactional
    public void deleteByUserId(UUID userId) {
        User user = userRepository.findById(userId)
                                  .orElseThrow(() -> new IdentityDomainException(IdentityErrorCode.USER_NOT_FOUND));
        refreshTokenRepository.deleteByUser(user);
    }

}
