package com.shopflow.payment.application.ports;

import com.shopflow.payment.domain.models.Payment;

public interface PaymentGatewayPort {

    String generatePayUrl(Payment payment);

    boolean verifySignature(String rawData, String signature);

}
