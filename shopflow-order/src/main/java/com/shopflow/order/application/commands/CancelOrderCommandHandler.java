package com.shopflow.order.application.commands;

import com.shopflow.order.application.outbox.OutboxRepository;
import com.shopflow.order.application.ports.DistributedLockPort;
import com.shopflow.order.domain.exceptions.OrderDomainException;
import com.shopflow.order.domain.exceptions.OrderErrorCode;
import com.shopflow.order.domain.models.Order;
import com.shopflow.order.domain.repositories.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

@Service
public class CancelOrderCommandHandler {

    private final OrderRepository orderRepository;
    private final OutboxRepository outboxRepository;
    private final DistributedLockPort distributedLockPort;
    private final TransactionTemplate transactionTemplate;

    public CancelOrderCommandHandler(OrderRepository orderRepository, OutboxRepository outboxRepository,
                                     DistributedLockPort distributedLockPort, TransactionTemplate transactionTemplate) {
        this.orderRepository = orderRepository;
        this.outboxRepository = outboxRepository;
        this.distributedLockPort = distributedLockPort;
        this.transactionTemplate = transactionTemplate;
    }

    public void handle(CancelOrderCommand command) {
        String lockKey = "lock:order:" + command.orderId();
        distributedLockPort.executeWithLock(lockKey, () -> {
            transactionTemplate.executeWithoutResult(status -> {
                Order order =
                        orderRepository.findById(command.orderId())
                                       .orElseThrow(() -> new OrderDomainException(
                                               OrderErrorCode.ORDER_NOT_FOUND));
                order.cancel(command.reason());
                orderRepository.save(order);
                outboxRepository.saveEvents(order.getDomainEvents());
                order.clearDomainEvents();
            });
        });
    }

}
