package com.shopflow.identity.domain.repository;

import com.shopflow.identity.domain.models.RefreshToken;
import com.shopflow.identity.domain.models.User;

import java.util.Optional;

public interface RefreshTokenRepository {

    Optional<RefreshToken> findByToken(String token);

    void deleteByUser(User user);

}
