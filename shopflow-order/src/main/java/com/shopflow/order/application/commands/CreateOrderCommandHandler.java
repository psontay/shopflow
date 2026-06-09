package com.shopflow.order.application.commands;

import com.shopflow.order.domain.models.Order;
import com.shopflow.order.domain.models.OrderItem;

import java.util.UUID;

public class CreateOrderCommandHandler {

    public UUID handle(CreateOrderCommand command) {
        UUID newOrderId = UUID.randomUUID();
        Order newOrder = new Order(
                newOrderId, command.customerId(), command.shippingAddress()
        );
        for (CreateOrderCommand.OrderItemCommand itemCmd : command.items()) {
            OrderItem orderItem = new OrderItem(UUID.randomUUID(), itemCmd.productId(), itemCmd.productName(), itemCmd.quantity(), itemCmd.unitPrice());
            newOrder.addItem(orderItem);
        }
        return newOrder.getId();
    }

}
