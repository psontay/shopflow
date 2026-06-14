package com.shopflow.identity.domain.models;

import com.shopflow.identity.domain.events.UserRegisterEvent;
import com.shopflow.identity.domain.exceptions.UserDomainException;
import com.shopflow.identity.domain.exceptions.UserErrorCode;
import com.shopflow.shared.domain.BaseEntity;

import java.time.Instant;
import java.util.UUID;

public class User extends BaseEntity {

    private String username;

    private String email;

    private String hashedPassword;

    private UserStatus userStatus;

    public User(UUID userId, String username, String email,
                String password) {
        super(userId);
        if (this.getId() == null) {
            throw new IllegalArgumentException("User id cannot be null");
        }
        this.username = username;
        this.email = email;
        this.hashedPassword = password;
        this.userStatus = UserStatus.ACTIVE;
        this.registerEvent(new UserRegisterEvent(userId, email));
    }

    private User(UUID userId, String username, UserStatus userStatus, String email, String password, Instant createdAt,
                 Instant updatedAt) {
        super(userId, createdAt, updatedAt);
        this.username = username;
        this.userStatus = userStatus;
        this.email = email;
        this.hashedPassword = password;
    }

    public static User reconstruct(UUID userId, String username, UserStatus userStatus, String email, String password,
                                   Instant createdAt, Instant updatedAt) {
        return new User(userId, username, userStatus, email, password, createdAt, updatedAt);
    }

    public void changePassword(String newHashedPassword) {
        if (this.userStatus == UserStatus.INACTIVE) {
            throw new UserDomainException(UserErrorCode.INVALID_USER_STATE);
        }
        if (newHashedPassword == null || newHashedPassword.isBlank()) {
            throw new IllegalArgumentException("New password cannot be blank.");
        }
        this.hashedPassword = newHashedPassword;
        super.maskAsUpdated();
    }

    public void lock() {
        if (this.userStatus == UserStatus.INACTIVE) {
            throw new UserDomainException(UserErrorCode.INVALID_USER_STATE);
        }
        this.userStatus = UserStatus.ACTIVE;
        super.maskAsUpdated();
    }

    public void unlock() {
        if (this.userStatus == UserStatus.ACTIVE) {
            throw new UserDomainException(UserErrorCode.INVALID_USER_STATE);
        }
        this.userStatus = UserStatus.ACTIVE;
        super.maskAsUpdated();
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public UserStatus getUserStatus() {
        return userStatus;
    }

}
