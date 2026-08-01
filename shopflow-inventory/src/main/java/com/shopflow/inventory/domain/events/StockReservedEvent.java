package com.shopflow.inventory.domain.events;

import com.shopflow.shared.domain.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record StockReservedEvent(
        UUID eventId,
        Instant occurredOn,
        UUID orderId,
        String eventType
) implements DomainEvent {

    public StockReservedEvent(UUID orderId) {
        this(UUID.randomUUID(), Instant.now(), orderId, "StockReservedEvent");
    }

    @Override
    public String aggregateType() {
        return "INVENTORY";
    }

    @Override
    public String aggregateId() {
        return this.orderId.toString();
    }

}
