package com.shopflow.order.application.commands;

import com.shopflow.order.application.outbox.OutboxService;
import com.shopflow.order.application.services.StockCheckerService;
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
    private final StockCheckerService stockCheckerService;

    public CreateOrderCommandHandler(OrderRepository orderRepository, OutboxService outboxService,
                                     StockCheckerService stockCheckerService) {
        this.orderRepository = orderRepository;
        this.outboxService = outboxService;
        this.stockCheckerService = stockCheckerService;
    }

    @Transactional
    public UUID handle(CreateOrderCommand command) {
        for (CreateOrderCommand.OrderItemCommand itemCmd : command.items()) {
            boolean isAvailable = stockCheckerService.checkStock(itemCmd.productId()
                                                                        .toString());
            if (! isAvailable) {
                throw new RuntimeException("Inventory System is unavailable or out of stock! Cancel order.");
            }
        }
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
        newOrder.submit();
        orderRepository.save(newOrder);
        outboxService.saveEvents(newOrder.getDomainEvents());
        return newOrder.getId();
    }

}
