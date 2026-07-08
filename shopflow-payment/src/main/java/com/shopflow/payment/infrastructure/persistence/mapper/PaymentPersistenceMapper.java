package com.shopflow.payment.infrastructure.persistence.mapper;

import com.shopflow.payment.domain.models.Payment;
import com.shopflow.payment.infrastructure.persistence.entity.PaymentEntity;
import com.shopflow.shared.domain.models.Money;
import org.springframework.stereotype.Component;

@Component
public class PaymentPersistenceMapper {

    public PaymentEntity toEntity(Payment payment) {
        return PaymentEntity.builder()
                            .id(payment.getId())
                            .orderId(payment.getOrderId())
                            .paymentStatus(payment.getPaymentStatus())
                            .paymentMethod(payment.getPaymentMethod())
                            .amount(payment.getAmount()
                                           .amount())
                            .providerTransactionId(payment.getProviderTransactionId())
                            .build();
    }

    public Payment toDomain(PaymentEntity entity) {

        return Payment.reconstruct(entity.getId(),
                                   entity.getOrderId(),
                                   Money.of(entity.getAmount()),
                                   entity.getPaymentMethod(),
                                   entity.getPaymentStatus(),
                                   entity.getProviderTransactionId(),
                                   null,
                                   null);
    }

}
