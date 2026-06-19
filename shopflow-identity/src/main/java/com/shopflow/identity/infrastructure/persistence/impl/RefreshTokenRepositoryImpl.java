package com.shopflow.identity.infrastructure.persistence.impl;

import com.shopflow.identity.domain.models.RefreshToken;
import com.shopflow.identity.domain.models.User;
import com.shopflow.identity.domain.repository.RefreshTokenRepository;
import com.shopflow.identity.infrastructure.persistence.JpaRefreshTokenRepository;
import com.shopflow.identity.infrastructure.persistence.entity.UserEntity;
import com.shopflow.identity.infrastructure.persistence.mapper.RefreshTokenPersistenceMapper;
import com.shopflow.identity.infrastructure.persistence.mapper.UserPersistenceMapper;

import java.util.Optional;

public class RefreshTokenRepositoryImpl implements RefreshTokenRepository {

    private final JpaRefreshTokenRepository jpaRefreshTokenRepository;
    private final RefreshTokenPersistenceMapper refreshTokenPersistenceMapper;
    private final UserPersistenceMapper userPersistenceMapper;

    public RefreshTokenRepositoryImpl(JpaRefreshTokenRepository jpaRefreshTokenRepository,
                                      RefreshTokenPersistenceMapper refreshTokenPersistenceMapper,
                                      UserPersistenceMapper userPersistenceMapper) {
        this.jpaRefreshTokenRepository = jpaRefreshTokenRepository;
        this.refreshTokenPersistenceMapper = refreshTokenPersistenceMapper;
        this.userPersistenceMapper = userPersistenceMapper;
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return jpaRefreshTokenRepository.findByToken(token)
                                        .map(refreshTokenPersistenceMapper :: toDomain);
    }

    @Override
    public void deleteByUser(User user) {
        UserEntity userEntity = userPersistenceMapper.toEntity(user);
        jpaRefreshTokenRepository.deleteByUser(userEntity);
    }

}
