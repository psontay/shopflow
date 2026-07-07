package com.shopflow.payment.domain.events;

import com.shopflow.shared.domain.DomainEvent;
import com.shopflow.shared.domain.models.Money;

import java.time.Instant;
import java.util.UUID;

public record PaymentCompletedEvent(
        UUID eventId,
        UUID paymentId,
        UUID orderId,
        Money amount,
        Instant occurredOn,
        String eventType
) implements DomainEvent {

    public PaymentCompletedEvent(UUID paymentId, UUID orderId, Money amount) {
        this(UUID.randomUUID(), paymentId, orderId, amount, Instant.now(), "PaymentCompletedEvent");
    }

    @Override
    public String aggregateType() {
        return "PAYMENT";
    }

    @Override
    public String aggregateId() {
        return this.orderId.toString();
    }

}
