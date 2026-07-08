package com.shopflow.payment.application.commands;

import com.shopflow.payment.application.outbox.OutboxRepository;
import com.shopflow.payment.domain.models.Payment;
import com.shopflow.payment.domain.models.PaymentStatus;
import com.shopflow.payment.domain.repositories.PaymentRepository;
import com.shopflow.payment.infrastructure.gateways.momo.HmacUtil;
import com.shopflow.payment.infrastructure.gateways.momo.MoMoConfig;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class ProcessMoMoIpnCommandHandler {

    private static final Logger log = LoggerFactory.getLogger(ProcessMoMoIpnCommandHandler.class);

    private final PaymentRepository paymentRepository;
    private final OutboxRepository outboxRepository;
    private final RedissonClient redissonClient;
    private final MoMoConfig moMoConfig;

    public ProcessMoMoIpnCommandHandler(PaymentRepository paymentRepository, OutboxRepository outboxRepository,
                                        RedissonClient redissonClient, MoMoConfig moMoConfig) {
        this.paymentRepository = paymentRepository;
        this.outboxRepository = outboxRepository;
        this.redissonClient = redissonClient;
        this.moMoConfig = moMoConfig;
    }

    @Transactional
    public void handle(ProcessMoMoIpnCommand command) {
        String rawData = "accessKey=" + moMoConfig.getAccessKey() +
                "&amount=" + command.getAmount() +
                "&extraData=" + command.getExtraData() +
                "&message=" + command.getMessage() +
                "&orderId=" + command.getOrderId() +
                "&orderInfo=" + command.getOrderInfo() +
                "&orderType=" + command.getOrderType() +
                "&partnerCode=" + command.getPartnerCode() +
                "&payType=" + command.getPayType() +
                "&requestId=" + command.getRequestId() +
                "&responseTime=" + command.getResponseTime() +
                "&resultCode=" + command.getResultCode() +
                "&transId=" + command.getTransId();

        String expectedSignature = HmacUtil.calculateHMac(rawData, moMoConfig.getSecretKey());
        if (! expectedSignature.equals(command.getSignature())) {
            log.error("Invalid MoMo IPN Signature. Expected: {}, Actual: {}",
                      expectedSignature,
                      command.getSignature());
            throw new RuntimeException("Invalid Signature");
        }

        String lockKey = "payment:lock:" + command.getOrderId();
        RLock lock = redissonClient.getLock(lockKey);
        try {
            boolean isLocked = lock.tryLock(5, 10, TimeUnit.SECONDS);
            if (! isLocked) {
                log.warn("Could not acquire lock for order: {}", command.getOrderId());
                return;
            }

            UUID paymentId = UUID.fromString(command.getRequestId());
            Payment payment = paymentRepository.findById(paymentId)
                                               .orElseThrow(() -> new RuntimeException("Payment not found"));

            if (payment.getPaymentStatus() != PaymentStatus.PENDING) {
                log.info("Payment {} is already processed. Status: {}", paymentId, payment.getPaymentStatus());
                return;
            }

            if (command.getResultCode() == 0) {
                payment.complete(String.valueOf(command.getTransId()));
            } else {
                payment.fail(String.valueOf(command.getTransId()), command.getMessage());
            }

            paymentRepository.save(payment);

            outboxRepository.saveEvents(payment.getDomainEvents());
            payment.clearDomainEvents();

            log.info("Successfully processed IPN for payment: {}", paymentId);

        } catch (InterruptedException e) {
            Thread.currentThread()
                  .interrupt();
            log.error("Interrupted while waiting for lock", e);
            throw new RuntimeException("Lock interrupted", e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

}
