package com.shopflow.inventory.domain.events;

import com.shopflow.shared.domain.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record StockReservationFailedEvent(
        UUID eventId,
        Instant occurredOn,
        UUID orderId,
        String reason
) implements DomainEvent {

    public StockReservationFailedEvent(UUID orderId, String reason) {
        this(UUID.randomUUID(), Instant.now(), orderId, reason);
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
        return "StockReservationFailedEvent";
    }
}
