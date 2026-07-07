package com.shopflow.order.application.commands;

import com.shopflow.shared.domain.models.Money;

import java.util.List;
import java.util.UUID;

public record CreateOrderCommand(
        UUID customerId,
        String shippingAddress,
        List<OrderItemCommand> items
) {

    public record OrderItemCommand(
            UUID productId,
            String productName,
            int quantity,
            Money unitPrice
    ) {

    }

}
