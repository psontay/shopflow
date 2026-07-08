package com.shopflow.order.application.commands;

import com.shopflow.order.application.outbox.OutboxRepository;
import com.shopflow.order.domain.models.Order;
import com.shopflow.order.domain.models.PaymentMethod;
import com.shopflow.order.domain.repositories.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PayOrderCommandHandler {

    private static final Logger log = LoggerFactory.getLogger(PayOrderCommandHandler.class);

    private final OrderRepository orderRepository;
    private final OutboxRepository outboxRepository;

    public PayOrderCommandHandler(OrderRepository orderRepository, OutboxRepository outboxRepository) {
        this.orderRepository = orderRepository;
        this.outboxRepository = outboxRepository;
    }

    @Transactional
    public void handle(PayOrderCommand command) {
        log.info("Processing update order status payment: {}", command.orderId());

        Order order = orderRepository.findById(command.orderId())
                                     .orElseThrow(() -> new RuntimeException("Cannot find order" + command.orderId()));
        PaymentMethod type = PaymentMethod.valueOf(command.paymentMethod());
        order.markAsPaid(type);

        orderRepository.save(order);
        outboxRepository.saveEvents(order.getDomainEvents());

        log.info("Update order {} to PAID success", command.orderId());
    }

}

