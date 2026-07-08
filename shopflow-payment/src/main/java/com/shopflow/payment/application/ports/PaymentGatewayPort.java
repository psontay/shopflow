package com.shopflow.payment.application.ports;

import com.shopflow.payment.domain.models.Payment;
import com.shopflow.payment.domain.models.PaymentMethod;

public interface PaymentGatewayPort {

    String generatePayUrl(Payment payment, String orderInfo);

    boolean verifySignature(String rawData, String signature);

    boolean supports(PaymentMethod method);

}
