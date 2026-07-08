package com.shopflow.payment.infrastructure.persistence.repository.impl;

import com.shopflow.payment.domain.models.Payment;
import com.shopflow.payment.domain.repositories.PaymentRepository;
import com.shopflow.payment.infrastructure.persistence.entity.PaymentEntity;
import com.shopflow.payment.infrastructure.persistence.mapper.PaymentPersistenceMapper;
import com.shopflow.payment.infrastructure.persistence.repository.JpaPaymentRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class PaymentRepositoryImpl implements PaymentRepository {

    private final JpaPaymentRepository jpaPaymentRepository;
    private final PaymentPersistenceMapper mapper;

    public PaymentRepositoryImpl(JpaPaymentRepository jpaPaymentRepository, PaymentPersistenceMapper mapper) {
        this.jpaPaymentRepository = jpaPaymentRepository;
        this.mapper = mapper;
    }

    @Override
    public void save(Payment payment) {
        PaymentEntity entity = mapper.toEntity(payment);
        jpaPaymentRepository.save(entity);
    }

    @Override
    public Optional<Payment> findById(UUID paymentId) {
        return jpaPaymentRepository.findById(paymentId)
                                   .map(mapper :: toDomain);
    }

    @Override
    public Optional<Payment> findByOrderId(UUID orderId) {
        return jpaPaymentRepository.findByOrderId(orderId)
                                   .map(mapper :: toDomain);
    }

    @Override
    public Optional<Payment> findByProviderTransactionId(String providerTransactionId) {
        return jpaPaymentRepository.findByProviderTransactionId(providerTransactionId)
                                   .map(mapper :: toDomain);
    }

}
