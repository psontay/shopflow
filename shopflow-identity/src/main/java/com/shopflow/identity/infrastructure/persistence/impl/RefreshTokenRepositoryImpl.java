package com.shopflow.identity.infrastructure.persistence.impl;

import com.shopflow.identity.domain.models.RefreshToken;
import com.shopflow.identity.domain.models.User;
import com.shopflow.identity.domain.repository.RefreshTokenRepository;
import com.shopflow.identity.infrastructure.persistence.JpaRefreshTokenRepository;

import java.util.Optional;

public class RefreshTokenRepositoryImpl implements RefreshTokenRepository {

    private final JpaRefreshTokenRepository jpaRefreshTokenRepository;

    public RefreshTokenRepositoryImpl(JpaRefreshTokenRepository jpaRefreshTokenRepository) {
        this.jpaRefreshTokenRepository = jpaRefreshTokenRepository;
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return Optional.empty();
    }

    @Override
    public void deleteByUser(User user) {

    }

}
