package com.shopflow.identity.infrastructure.persistence.mapper;

import com.shopflow.identity.domain.models.User;
import com.shopflow.identity.infrastructure.persistence.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserPersistenceMapper {

    public UserEntity toEntity(User user) {
        return UserEntity.builder()
                         .id(user.getId())
                         .email(user.getEmail())
                         .role(user.getRole())
                         .password(user.getHashedPassword())
                         .userStatus(user.getUserStatus())
                         .deleted(user.isDeleted())
                         .createdAt(user.getCreatedAt())
                         .updatedAt(user.getUpdatedAt())
                         .build();
    }

    public User toDomain(UserEntity userEntity) {
        User user = User.reconstruct(userEntity.getId(),
                                     userEntity.getUsername(),
                                     userEntity.getUserStatus(),
                                     userEntity.getRole(),
                                     userEntity.isDeleted()
                ,
                                     userEntity.getEmail(),
                                     userEntity.getPassword(),
                                     userEntity.getCreatedAt(),
                                     userEntity.getUpdatedAt());
        user.clearDomainEvents();
        return user;
    }

}
