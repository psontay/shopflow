package com.shopflow.identity.domain.repositories;

import com.shopflow.identity.domain.models.User;

import java.util.Optional;

public interface UserRepository {

    void save(User user);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<User> findByUsername(String username);

}
