package com.shopflow.identity.infrastructure.persistence.mapper;

import com.shopflow.identity.domain.models.RefreshToken;
import com.shopflow.identity.domain.models.User;
import com.shopflow.identity.infrastructure.persistence.entity.RefreshTokenEntity;
import com.shopflow.identity.infrastructure.persistence.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class RefreshTokenPersistenceMapper {

    public RefreshTokenEntity toEntity(RefreshToken refreshToken) {
        User userDomain = refreshToken.getUser();
        UserEntity userEntity = UserEntity.builder()
                                          .id(userDomain.getId())
                                          .username(userDomain.getUsername())
                                          .email(userDomain.getEmail())
                                          .password(userDomain.getHashedPassword())
                                          .userStatus(userDomain.getUserStatus())
                                          .deleted(userDomain.isDeleted())
                                          .role(userDomain.getRole())
                                          .createdAt(userDomain.getCreatedAt())
                                          .updatedAt(userDomain.getUpdatedAt())
                                          .build();
        return RefreshTokenEntity.builder()
                                 .id(refreshToken.getId())
                                 .userEntity(userEntity)
                                 .token(refreshToken.getToken())
                                 .expiryDate(refreshToken.getExpiryDate())
                                 .build();
    }

    public RefreshToken toDomain(RefreshTokenEntity refreshTokenEntity) {
        UserEntity userEntity = refreshTokenEntity.getUserEntity();
        User user = User.reconstruct(
                userEntity.getId(),
                userEntity.getUsername(),
                userEntity.getUserStatus(),
                userEntity.getRole(),
                userEntity.isDeleted(),
                userEntity.getEmail(),
                userEntity.getPassword(),
                userEntity.getCreatedAt(),
                userEntity.getUpdatedAt()
                                    );
        return new RefreshToken(user, refreshTokenEntity.getToken(), refreshTokenEntity.getExpiryDate());
    }

}
