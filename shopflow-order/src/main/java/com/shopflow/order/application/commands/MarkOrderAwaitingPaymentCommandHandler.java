package com.shopflow.order.application.commands;

import com.shopflow.order.application.outbox.OutboxRepository;
import com.shopflow.order.application.ports.DistributedLockPort;
import com.shopflow.order.domain.exceptions.OrderDomainException;
import com.shopflow.order.domain.exceptions.OrderErrorCode;
import com.shopflow.order.domain.models.Order;
import com.shopflow.order.domain.repositories.OrderRepository;
import org.springframework.stereotype.Service;

@Service
public class MarkOrderAwaitingPaymentCommandHandler {

    private final OrderRepository orderRepository;
    private final OutboxRepository outboxRepository;
    private final DistributedLockPort distributedLockPort;

    public MarkOrderAwaitingPaymentCommandHandler(OrderRepository orderRepository, OutboxRepository outboxRepository,
                                                  DistributedLockPort distributedLockPort) {
        this.orderRepository = orderRepository;
        this.outboxRepository = outboxRepository;
        this.distributedLockPort = distributedLockPort;
    }

    public void handle(MarkOrderAwaitingPaymentCommand command) {
        String lockKey = "lock:order" + command.orderId();
        distributedLockPort.executeWithLock(lockKey, () -> {
            Order order = orderRepository.findById(command.orderId())
                                         .orElseThrow(() -> new OrderDomainException(
                                                 OrderErrorCode.ORDER_NOT_FOUND));
            order.markAsAwaitingPayment();
            orderRepository.save(order);
            outboxRepository.saveEvents(order.getDomainEvents());
            order.clearDomainEvents();
        });
    }

}
