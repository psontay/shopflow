package com.shopflow.payment.domain.models;

import com.shopflow.payment.domain.events.PaymentCompletedEvent;
import com.shopflow.payment.domain.exceptions.PaymentDomainException;
import com.shopflow.payment.domain.exceptions.PaymentErrorCode;
import com.shopflow.shared.domain.models.BaseEntity;
import com.shopflow.shared.domain.models.Money;

import java.time.Instant;
import java.util.UUID;

public class Payment extends BaseEntity {

    private final UUID orderId;
    private final Money amount;
    private final PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private String providerTransactionId;

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

    public static Payment reconstruct(UUID paymentId, UUID orderId, Money amount, PaymentMethod paymentMethod,
                                      PaymentStatus paymentStatus,
                                      String providerTransactionId, Instant createdAt, Instant updatedAt) {
        return new Payment(paymentId,
                           orderId,
                           amount,
                           paymentMethod,
                           paymentStatus,
                           providerTransactionId,
                           createdAt,
                           updatedAt);

    }

    public void complete(String providerTransactionId) {
        if (this.paymentStatus != PaymentStatus.PENDING) {
            throw new PaymentDomainException(PaymentErrorCode.PAY_ERR_INVALID_STATUS);
        }
        if (providerTransactionId == null || providerTransactionId.isBlank()) {
            throw new PaymentDomainException(PaymentErrorCode.PAY_ERR_INVALID_PROVIDER_TRANSACTION_ID);
        }
        this.paymentStatus = PaymentStatus.SUCCESS;
        this.providerTransactionId = providerTransactionId;
        this.registerEvent(new PaymentCompletedEvent(this.getId(), this.orderId, this.amount));
    }

}
