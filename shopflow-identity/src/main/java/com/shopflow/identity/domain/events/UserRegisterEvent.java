package com.shopflow.identity.domain.events;

import com.shopflow.shared.domain.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record UserRegisterEvent(UUID eventId, Instant occurredOn, UUID userId, String email, String eventType, String otp) implements DomainEvent {

    public UserRegisterEvent(UUID userId, String email, String otp) {
        this(UUID.randomUUID(), Instant.now(), userId, email, "UserRegisterEvent", otp);
    }

    @Override
    public String aggregateType() {
        return "USER";
    }

    @Override
    public String aggregateId() {
        return this.userId.toString();
    }

}
