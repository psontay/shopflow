package com.shopflow.identity.infrastructure.persistence;

import com.shopflow.identity.infrastructure.persistence.entity.RefreshTokenEntity;
import com.shopflow.identity.infrastructure.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaRefreshTokenRepository extends JpaRepository<RefreshTokenEntity, UUID> {

    Optional<RefreshTokenEntity> findByToken(String token);

    void deleteByUser(UserEntity userEntity);

}
