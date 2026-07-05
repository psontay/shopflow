package com.shopflow.order.domain.events;

import com.shopflow.shared.domain.DomainEvent;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderCancelledEvent(
        UUID eventId,
        UUID orderId,
        String reason,
        List<OrderItemSnapshot> items,
        Instant occurredOn,
        String eventType
) implements DomainEvent {

    public OrderCancelledEvent(UUID orderId, String reason, List<OrderItemSnapshot> items) {
        this(UUID.randomUUID(), orderId, reason, items, Instant.now(), "OrderCancelledEvent");
    }

    @Override
    public String aggregateType() {
        return "Order";
    }

    @Override
    public String aggregateId() {
        return this.orderId.toString();
    }

}
