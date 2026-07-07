package com.shopflow.payment.domain.events;

import com.shopflow.shared.domain.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record PaymentFailedEvent(
        UUID eventId,
        UUID paymentId,
        UUID orderId,
        String failureReason,
        Instant occurredOn,
        String eventType
) implements DomainEvent {

    public PaymentFailedEvent(UUID paymentId, UUID orderId, String failureReason) {
        this(UUID.randomUUID(), paymentId, orderId, failureReason, Instant.now(), "PaymentFailedEvent");
    }

    @Override
    public String aggregateType() {
        return "PAYMENT";
    }

    @Override
    public String aggregateId() {
        return this.paymentId.toString();
    }

}
