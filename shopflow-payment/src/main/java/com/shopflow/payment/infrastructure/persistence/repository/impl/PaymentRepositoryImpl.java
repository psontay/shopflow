package com.shopflow.payment.infrastructure.persistence.repository.impl;

import com.shopflow.payment.domain.models.Payment;
import com.shopflow.payment.domain.repositories.PaymentRepository;
import com.shopflow.payment.infrastructure.persistence.repository.JpaPaymentRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class PaymentRepositoryImpl implements PaymentRepository {

    private final JpaPaymentRepository jpaPaymentRepository;
    private final PaymentPersistenceMapper mapper;

    @Override
    public void save(Payment payment) {

    }

    @Override
    public Optional<Payment> findById(UUID paymentId) {
        return Optional.empty();
    }

    @Override
    public Optional<Payment> findByOrderId(UUID orderId) {
        return Optional.empty();
    }

    @Override
    public Optional<Payment> findByProviderTransactionId(String providerTransactionId) {
        return Optional.empty();
    }

}
