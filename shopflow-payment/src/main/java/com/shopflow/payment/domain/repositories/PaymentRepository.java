package com.shopflow.payment.domain.repositories;

import com.shopflow.payment.domain.models.Payment;

import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository {

    void save(Payment payment);

    Optional<Payment> findById(UUID paymentId);

    Optional<Payment> findByOrderId(UUID orderId);

    Optional<Payment> findByProviderTransactionId(String providerTransactionId); // => idempotency

}
