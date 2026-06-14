package com.shopflow.identity.domain.events;

import com.shopflow.shared.domain.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record UserRegisterEvent(UUID eventId, Instant occurredOn, UUID userId, String email) implements DomainEvent {

    public UserRegisterEvent(UUID userId, String email) {
        this(UUID.randomUUID(), Instant.now(), userId, email);
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
