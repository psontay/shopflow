package com.shopflow.inventory.domain.events;

import com.shopflow.shared.domain.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record StockReservedEvent(
        UUID eventId,
        Instant occurredOn,
        UUID orderId
) implements DomainEvent {

    public StockReservedEvent(UUID orderId) {
        this(UUID.randomUUID(), Instant.now(), orderId);
    }

    @Override
    public String aggregateType() {
        return "Order";
    }

    @Override
    public String aggregateId() {
        return orderId.toString();
    }

    public String eventType() {
        return "StockReservedEvent";
    }

}
