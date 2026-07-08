package com.shopflow.order.application.commands;

import com.shopflow.order.application.outbox.OutboxRepository;
import com.shopflow.order.application.services.StockCheckerService;
import com.shopflow.order.domain.models.Order;
import com.shopflow.order.domain.models.OrderItem;
import com.shopflow.order.domain.repositories.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.UUID;

@Service
public class CreateOrderCommandHandler {

    private final OrderRepository orderRepository;
    private final OutboxRepository outboxRepository;
    private final StockCheckerService stockCheckerService;
    private final TransactionTemplate transactionTemplate;

    public CreateOrderCommandHandler(OrderRepository orderRepository, OutboxRepository outboxRepository,
                                     StockCheckerService stockCheckerService, TransactionTemplate transactionTemplate) {
        this.orderRepository = orderRepository;
        this.outboxRepository = outboxRepository;
        this.stockCheckerService = stockCheckerService;
        this.transactionTemplate = transactionTemplate;
    }

    public UUID handle(CreateOrderCommand command) {
        for (CreateOrderCommand.OrderItemCommand itemCmd : command.items()) {
            boolean isAvailable = stockCheckerService.checkStock(itemCmd.productId()
                                                                        .toString(), itemCmd.quantity());
            if (! isAvailable) {
                throw new RuntimeException("Inventory System is unavailable or out of stock! Cancel order.");
            }
        }
        return transactionTemplate.execute(status -> {
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
            outboxRepository.saveEvents(newOrder.getDomainEvents());
            return newOrder.getId();
        });
    }

}
