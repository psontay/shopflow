package com.shopflow.order.application.commands;

import com.shopflow.order.application.outbox.OutboxService;
import com.shopflow.order.domain.exceptions.OrderDomainException;
import com.shopflow.order.domain.exceptions.OrderErrorCode;
import com.shopflow.order.domain.models.Order;
import com.shopflow.order.domain.repositories.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CancelOrderCommandHandler {

    private final OrderRepository orderRepository;
    private final OutboxService outboxService;

    public CancelOrderCommandHandler(OrderRepository orderRepository, OutboxService outboxService) {
        this.orderRepository = orderRepository;
        this.outboxService = outboxService;
    }

    @Transactional
    public void handle(CancelOrderCommand command) {
        Order order = orderRepository.findById(command.orderId())
                                     .orElseThrow(() -> new OrderDomainException(OrderErrorCode.ORDER_NOT_FOUND));
        order.cancel();
        orderRepository.save(order);
        outboxService.saveEvents(order.getDomainEvents());
        order.clearDomainEvents();
    }

}
