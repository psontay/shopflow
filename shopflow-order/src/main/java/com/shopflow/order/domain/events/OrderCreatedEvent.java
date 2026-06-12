package com.shopflow.order.domain.events;

import com.shopflow.shared.domain.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record OrderCreatedEvent(
        UUID orderId,
        Instant occurredOn,
        UUID eventId
) implements DomainEvent {

    public OrderCreatedEvent(UUID orderId) {
        this(UUID.randomUUID(), Instant.now(), orderId);
    }

    @Override
    public String aggregateType() {
        return "ORDER";
    }

    @Override
    public String aggregateId() {
        return this.orderId.toString();
    }

}
