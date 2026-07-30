package com.shopflow.identity.domain.models;

import com.shopflow.identity.domain.events.PasswordChangedEvent;
import com.shopflow.identity.domain.events.UserRegisterEvent;
import com.shopflow.identity.domain.exceptions.IdentityDomainException;
import com.shopflow.identity.domain.exceptions.IdentityErrorCode;
import com.shopflow.identity.domain.exceptions.UserDomainException;
import com.shopflow.identity.domain.exceptions.UserErrorCode;
import com.shopflow.shared.domain.DomainEvent;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testCreateUser_Success() {
        UUID userId = UUID.randomUUID();
        User user = new User(userId, "john.doe", "john@example.com", "hashedPassword123", "123456");

        assertEquals(userId, user.getId());
        assertEquals("john.doe", user.getUsername());
        assertEquals("john@example.com", user.getEmail());
        assertEquals("hashedPassword123", user.getHashedPassword());
        assertEquals(UserStatus.PENDING_VERIFICATION, user.getUserStatus());
        assertEquals(Role.USER, user.getRole());
        assertFalse(user.isDeleted());

        List<DomainEvent> events = user.getDomainEvents();
        assertEquals(1, events.size());
        assertTrue(events.get(0) instanceof UserRegisterEvent);
        UserRegisterEvent event = (UserRegisterEvent) events.get(0);
        assertEquals(userId.toString(), event.aggregateId());
    }

    @Test
    void testCreateUser_NullId_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> 
            new User(null, "john.doe", "john@example.com", "password", "123456")
        );
    }

    @Test
    void testReconstructUser() {
        UUID userId = UUID.randomUUID();
        Instant now = Instant.now();
        User user = User.reconstruct(userId, "admin", UserStatus.ACTIVE, Role.ADMIN, false, "admin@example.com", "pass", now, now);

        assertEquals(userId, user.getId());
        assertEquals("admin", user.getUsername());
        assertEquals(UserStatus.ACTIVE, user.getUserStatus());
        assertEquals(Role.ADMIN, user.getRole());
        assertEquals(0, user.getDomainEvents().size());
    }

    @Test
    void testChangePassword_Success() {
        User user = new User(UUID.randomUUID(), "john", "john@example.com", "oldPass", "123456");
        user.verify(); // Move to ACTIVE state
        user.clearDomainEvents(); // Clear creation and verify events

        user.changePassword("newPass");

        assertEquals("newPass", user.getHashedPassword());
        assertEquals(1, user.getDomainEvents().size());
        assertTrue(user.getDomainEvents().get(0) instanceof PasswordChangedEvent);
    }

    @Test
    void testChangePassword_WhenInactive_ThrowsException() {
        User user = new User(UUID.randomUUID(), "john", "john@example.com", "oldPass", "123456");
        user.verify(); // ACTIVE
        user.lock(); // INACTIVE

        UserDomainException ex = assertThrows(UserDomainException.class, () -> user.changePassword("newPass"));
        assertEquals(UserErrorCode.INVALID_USER_STATE, ex.getErrorCode());
    }

    @Test
    void testChangePassword_BlankPassword_ThrowsException() {
        User user = new User(UUID.randomUUID(), "john", "john@example.com", "oldPass", "123456");
        assertThrows(IllegalArgumentException.class, () -> user.changePassword(""));
        assertThrows(IllegalArgumentException.class, () -> user.changePassword(null));
    }

    @Test
    void testLock_Success() {
        User user = new User(UUID.randomUUID(), "john", "john@example.com", "oldPass", "123456");
        user.verify();
        assertEquals(UserStatus.ACTIVE, user.getUserStatus());

        user.lock();
        assertEquals(UserStatus.INACTIVE, user.getUserStatus());
    }

    @Test
    void testLock_WhenAlreadyInactive_ThrowsException() {
        User user = new User(UUID.randomUUID(), "john", "john@example.com", "oldPass", "123456");
        user.verify();
        user.lock(); // Now INACTIVE

        UserDomainException ex = assertThrows(UserDomainException.class, user::lock);
        assertEquals(UserErrorCode.INVALID_USER_STATE, ex.getErrorCode());
    }

    @Test
    void testUnlock_Success() {
        User user = new User(UUID.randomUUID(), "john", "john@example.com", "oldPass", "123456");
        user.verify();
        user.lock(); // INACTIVE
        
        user.unlock(); // ACTIVE
        assertEquals(UserStatus.ACTIVE, user.getUserStatus());
    }

    @Test
    void testUnlock_WhenAlreadyActive_ThrowsException() {
        User user = new User(UUID.randomUUID(), "john", "john@example.com", "oldPass", "123456");
        user.verify(); // ACTIVE

        UserDomainException ex = assertThrows(UserDomainException.class, user::unlock);
        assertEquals(UserErrorCode.INVALID_USER_STATE, ex.getErrorCode());
    }

    @Test
    void testSoftDelete() {
        User user = new User(UUID.randomUUID(), "john", "john@example.com", "oldPass", "123456");
        assertFalse(user.isDeleted());

        user.softDelete();
        assertTrue(user.isDeleted());
    }

    @Test
    void testVerify_Success() {
        User user = new User(UUID.randomUUID(), "john", "john@example.com", "oldPass", "123456");
        assertEquals(UserStatus.PENDING_VERIFICATION, user.getUserStatus());

        user.verify();
        assertEquals(UserStatus.ACTIVE, user.getUserStatus());
    }

    @Test
    void testVerify_WhenNotPending_ThrowsException() {
        User user = new User(UUID.randomUUID(), "john", "john@example.com", "oldPass", "123456");
        user.verify(); // Status becomes ACTIVE

        IdentityDomainException ex = assertThrows(IdentityDomainException.class, user::verify);
        assertEquals(IdentityErrorCode.INVALID_USER_STATE, ex.getErrorCode());
    }
}
