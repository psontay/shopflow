package com.shopflow.order.application.commands;

import com.shopflow.order.application.outbox.OutboxService;
import com.shopflow.order.domain.models.Order;
import com.shopflow.order.domain.models.OrderItem;
import com.shopflow.order.domain.repositories.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CreateOrderCommandHandler {

    private final OrderRepository orderRepository;
    private final OutboxService outboxService;

    public CreateOrderCommandHandler(OrderRepository orderRepository, OutboxService outboxService) {
        this.orderRepository = orderRepository;
        this.outboxService = outboxService;
    }

    @Transactional
    public UUID handle(CreateOrderCommand command) {
        UUID newOrderId = UUID.randomUUID();
        Order newOrder = new Order(
                newOrderId, command.customerId(), command.shippingAddress()
        );
        for (CreateOrderCommand.OrderItemCommand itemCmd : command.items()) {
            OrderItem orderItem = new OrderItem(UUID.randomUUID(),
                                                itemCmd.productId(),
                                                itemCmd.quantity(),
                                                itemCmd.unitPrice());
            newOrder.addItem(orderItem);
        }
        orderRepository.save(newOrder);
        outboxService.saveEvents(newOrder.getDomainEvents());
        return newOrder.getId();
    }

}
