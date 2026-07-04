package com.shopflow.identity.domain.models;

import com.shopflow.identity.domain.events.PasswordChangedEvent;
import com.shopflow.identity.domain.events.UserRegisterEvent;
import com.shopflow.identity.domain.exceptions.IdentityDomainException;
import com.shopflow.identity.domain.exceptions.IdentityErrorCode;
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

    private boolean deleted;

    private Role role;

    public User(UUID userId, String username, String email,
                String password) {
        super(userId);
        if (this.getId() == null) {
            throw new IllegalArgumentException("User id cannot be null");
        }
        this.username = username;
        this.email = email;
        this.hashedPassword = password;
        this.userStatus = UserStatus.PENDING_VERIFICATION;
        this.deleted = false;
        this.role = Role.USER;
        this.registerEvent(new UserRegisterEvent(userId, email));
    }

    private User(UUID userId, String username, UserStatus userStatus, Role role, boolean deleted, String email,
                 String password,
                 Instant createdAt,
                 Instant updatedAt) {
        super(userId, createdAt, updatedAt);
        this.username = username;
        this.userStatus = userStatus;
        this.role = role;
        this.deleted = deleted;
        this.email = email;
        this.hashedPassword = password;
    }

    public static User reconstruct(UUID userId, String username, UserStatus userStatus, Role role, boolean deleted,
                                   String email,
                                   String password,
                                   Instant createdAt, Instant updatedAt) {
        return new User(userId, username, userStatus, role, deleted, email, password, createdAt, updatedAt);
    }

    public void changePassword(String newHashedPassword) {
        if (this.userStatus == UserStatus.INACTIVE) {
            throw new UserDomainException(UserErrorCode.INVALID_USER_STATE);
        }
        if (newHashedPassword == null || newHashedPassword.isBlank()) {
            throw new IllegalArgumentException("New password cannot be blank.");
        }
        this.hashedPassword = newHashedPassword;
        this.registerEvent(new PasswordChangedEvent(this.getId()));
        super.markAsUpdated();
    }

    public void lock() {
        if (this.userStatus == UserStatus.INACTIVE) {
            throw new UserDomainException(UserErrorCode.INVALID_USER_STATE);
        }
        this.userStatus = UserStatus.INACTIVE;
        super.markAsUpdated();
    }

    public void unlock() {
        if (this.userStatus == UserStatus.ACTIVE) {
            throw new UserDomainException(UserErrorCode.INVALID_USER_STATE);
        }
        this.userStatus = UserStatus.ACTIVE;
        super.markAsUpdated();
    }

    public void softDelete() {
        this.deleted = true;
        super.markAsUpdated();
    }

    public void verify() {
        if (this.userStatus != UserStatus.PENDING_VERIFICATION) {
            throw new IdentityDomainException(IdentityErrorCode.INVALID_USER_STATE);
        }
        this.userStatus = UserStatus.ACTIVE;
        super.markAsUpdated();
    }

    public boolean isDeleted() {
        return deleted;
    }

    public Role getRole() {
        return role;
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
