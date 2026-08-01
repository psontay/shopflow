package com.shopflow.order.application.commands;

import com.shopflow.order.application.outbox.OutboxRepository;
import com.shopflow.order.application.ports.DistributedLockPort;
import com.shopflow.order.domain.repositories.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

@Service
public class MarkOrderAwaitingPaymentCommandHandler {

    private final OrderRepository orderRepository;
    private final OutboxRepository outboxRepository;
    private final DistributedLockPort distributedLockPort;
    private final TransactionTemplate transactionTemplate;

    public MarkOrderAwaitingPaymentCommandHandler(OrderRepository orderRepository, OutboxRepository outboxRepository,
                                                  DistributedLockPort distributedLockPort,
                                                  TransactionTemplate transactionTemplate) {
        this.orderRepository = orderRepository;
        this.outboxRepository = outboxRepository;
        this.distributedLockPort = distributedLockPort;
        this.transactionTemplate = transactionTemplate;
    }

}
