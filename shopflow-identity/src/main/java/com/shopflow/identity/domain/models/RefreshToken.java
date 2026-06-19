package com.shopflow.identity.domain.models;

import java.time.Instant;
import java.util.UUID;

public class RefreshToken {

    private UUID id;
    private User user;
    private String token;
    private Instant expiryDate;

    public RefreshToken(User user, String token, Instant expiryDate) {
        this.id = UUID.randomUUID();
        this.user = user;
        this.token = token;
        this.expiryDate = expiryDate;
    }

    public boolean isExpired() {
        return expiryDate.compareTo(Instant.now()) < 0;
    }

    public UUID getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getToken() {
        return token;
    }

    public Instant getExpiryDate() {
        return expiryDate;
    }

}
