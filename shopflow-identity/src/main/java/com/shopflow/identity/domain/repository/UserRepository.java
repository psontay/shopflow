package com.shopflow.identity.domain.repository;

import com.shopflow.identity.domain.models.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    void save(User user);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<User> findByUsername(String username);

    Optional<User> findById(UUID id);

    Optional<User> findByEmail(String email);

}
