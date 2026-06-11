package com.shopflow.order.presentation.api.dto;

import com.shopflow.order.application.commands.CreateOrderCommand;
import com.shopflow.shared.domain.Money;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public record CreateOrderRequest(
        @NotNull(message = "Customer id is required")
        UUID customerId,
        @NotBlank(message = "Shipping address cannot be empty")
        String shippingAddress,
        @NotEmpty(message = "Cart must be at least 1 item")
        List<OrderItemRequest> items

) {

    public record OrderItemRequest(
            @NotNull UUID productId, String productName, int quantity, double unitPrice
    ) {

    }

    public CreateOrderCommand toCommand() {
        List<CreateOrderCommand.OrderItemCommand> itemCommands = items.stream()
                                                                      .map(i -> new CreateOrderCommand.OrderItemCommand(
                                                                              i.productId(),
                                                                              i.productName(),
                                                                              i.quantity(),
                                                                              Money.of(java.math.BigDecimal.valueOf(i.unitPrice()))
                                                                      ))
                                                                      .collect(Collectors.toList());
        return new CreateOrderCommand(customerId, shippingAddress, itemCommands);
    }

}
