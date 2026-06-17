package com.shopflow.order.domain.events;

import com.shopflow.shared.domain.DomainEvent;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderCreatedEvent(
        UUID eventId,
        UUID orderId,
        List<OrderItemSnapshot> items,
        Instant occurredOn
) implements DomainEvent {

    public OrderCreatedEvent(UUID orderId, List<OrderItemSnapshot> items) {
        this(UUID.randomUUID(), orderId, items, Instant.now());
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
