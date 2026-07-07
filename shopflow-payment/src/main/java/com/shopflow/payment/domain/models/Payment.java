package com.shopflow.payment.domain.models;

import com.shopflow.shared.domain.models.BaseEntity;
import com.shopflow.shared.domain.models.Money;

import java.time.Instant;
import java.util.UUID;

public class Payment extends BaseEntity {

    private final UUID orderId;
    private final Money amount;
    private final PaymentMethod paymentMethod;
    private final PaymentStatus paymentStatus;
    private final String providerTransactionId;

    //Invariants Constructor

    public Payment(UUID paymentId, UUID orderId, Money amount, PaymentMethod paymentMethod) {
        super(paymentId);
        this.orderId = orderId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = PaymentStatus.PENDING;
        this.providerTransactionId = null;
    }

    private Payment(UUID paymentId, UUID orderId, Money amount, PaymentMethod paymentMethod,
                    PaymentStatus paymentStatus, String providerTransactionId, Instant createdAt, Instant updatedAt) {
        super(paymentId, createdAt, updatedAt);
        this.orderId = orderId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.providerTransactionId = providerTransactionId;
    }

}
