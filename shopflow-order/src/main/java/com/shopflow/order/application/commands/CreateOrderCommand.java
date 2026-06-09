package com.shopflow.order.application.commands;

import com.shopflow.shared.domain.Money;

import java.util.List;
import java.util.UUID;

public record CreateOrderCommand(
        UUID customerId,
        String shippingAddress,
        List<OrderItemCommand> items
) {

    public record OrderItemCommand(
            String productId,
            String productName,
            int quantity,
            Money unitPrice
    ) {

    }

}
