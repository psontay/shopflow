package com.shopflow.identity.infrastructure.persistence.impl;

import com.shopflow.identity.domain.models.User;
import com.shopflow.identity.domain.repository.UserRepository;
import com.shopflow.identity.infrastructure.persistence.JpaUserRepository;
import com.shopflow.identity.infrastructure.persistence.entity.UserEntity;
import com.shopflow.identity.infrastructure.persistence.mapper.UserPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final JpaUserRepository jpaUserRepository;
    private final UserPersistenceMapper mapper;

    public UserRepositoryImpl(JpaUserRepository jpaUserRepository, UserPersistenceMapper mapper) {
        this.jpaUserRepository = jpaUserRepository;
        this.mapper = mapper;
    }

    @Override
    public void save(User user) {
        UserEntity userEntity = mapper.toEntity(user);
        jpaUserRepository.save(userEntity);
    }

    @Override
    public boolean existsByUsername(String username) {
        return jpaUserRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaUserRepository.existsByEmail(email);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return jpaUserRepository.findByUsername(username)
                                .map(mapper :: toDomain);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return jpaUserRepository.findById(id)
                                .map(mapper :: toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaUserRepository.findByEmail(email)
                                .map(mapper :: toDomain);
    }

}
