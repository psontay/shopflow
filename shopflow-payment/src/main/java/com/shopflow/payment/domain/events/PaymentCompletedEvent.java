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
        String paymentMethod,
        Instant occurredOn,
        String eventType
) implements DomainEvent {

    public PaymentCompletedEvent(UUID paymentId, UUID orderId, Money amount, String paymentMethod) {
        this(UUID.randomUUID(), paymentId, orderId, amount, String paymentMethod,Instant.now(), "PaymentCompletedEvent");
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
