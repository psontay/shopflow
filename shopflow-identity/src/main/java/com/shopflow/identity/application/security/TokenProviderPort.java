package com.shopflow.identity.application.security;

import com.shopflow.identity.domain.models.User;

public interface TokenProviderPort {

    String generateAccessToken(User user);

    boolean validateToken(String token);

}
