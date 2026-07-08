package com.shopflow.payment.application.commands;

import com.shopflow.payment.application.ports.PaymentGatewayPort;
import com.shopflow.payment.domain.exceptions.PaymentDomainException;
import com.shopflow.payment.domain.exceptions.PaymentErrorCode;
import com.shopflow.payment.domain.models.Payment;
import com.shopflow.payment.domain.models.PaymentMethod;
import com.shopflow.payment.domain.repositories.PaymentRepository;
import com.shopflow.shared.domain.models.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.UUID;

@Service
public class CreatePaymentLinkCommandHandler {

    private static final Logger log = LoggerFactory.getLogger(CreatePaymentLinkCommandHandler.class);
    private final List<PaymentGatewayPort> paymentGateways;
    private final PaymentRepository paymentRepository;
    private final TransactionTemplate transactionTemplate;

    public CreatePaymentLinkCommandHandler(List<PaymentGatewayPort> paymentGateways,
                                           PaymentRepository paymentRepository,
                                           TransactionTemplate transactionTemplate) {
        this.paymentGateways = paymentGateways;
        this.paymentRepository = paymentRepository;
        this.transactionTemplate = transactionTemplate;
    }

    public String handle(CreatePaymentLinkCommand command) {
        log.info("Start generate payment link for order Id: {}", command.orderId());
        PaymentMethod method = PaymentMethod.valueOf(command.paymentMethod()
                                                            .toUpperCase());
        Money money = Money.of(command.amount());
        return transactionTemplate.execute(status -> {
            UUID newPaymentId = UUID.randomUUID();
            Payment payment = new Payment(newPaymentId, command.orderId(), money, method);
            PaymentGatewayPort selectedGateway =
                    paymentGateways.stream()
                                   .filter(gateway -> gateway.supports(method))
                                   .findFirst()
                                   .orElseThrow(() -> new PaymentDomainException(
                                           PaymentErrorCode.PAY_ERR_INVALID_METHOD));

            paymentRepository.save(payment);
            String payUrl = selectedGateway.generatePayUrl(payment, command.orderInfo());

            log.info("Tạo link thanh toán thành công: {}", payUrl);
            return payUrl;
        });
    }

}
