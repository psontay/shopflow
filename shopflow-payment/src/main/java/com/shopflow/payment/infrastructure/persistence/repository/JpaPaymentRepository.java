package com.shopflow.payment.infrastructure.persistence.repository;

import com.shopflow.payment.domain.models.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public class JpaPaymentRepository implements JpaRepository<Payment, UUID> {

}
