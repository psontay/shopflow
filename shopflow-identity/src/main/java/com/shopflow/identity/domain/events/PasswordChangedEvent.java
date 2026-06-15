package com.shopflow.identity.domain.events;

import com.shopflow.shared.domain.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record PasswordChangedEvent(
        UUID eventId,
        Instant occurredOn,
        UUID userId
) implements DomainEvent {

    public PasswordChangedEvent(UUID userId) {
        this(UUID.randomUUID(), Instant.now(), userId);
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
