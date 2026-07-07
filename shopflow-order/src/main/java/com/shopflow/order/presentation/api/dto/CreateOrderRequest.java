package com.shopflow.order.presentation.api.dto;

import com.shopflow.order.application.commands.CreateOrderCommand;
import com.shopflow.shared.domain.models.Money;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public record CreateOrderRequest(
        @NotNull(message = "Customer id is required")
        UUID customerId,
        @NotBlank(message = "Shipping address cannot be empty")
        String shippingAddress,
        @NotEmpty(message = "Cart must be at least 1 item")
        @Valid
        List<OrderItemRequest> items

) {

    public CreateOrderCommand toCommand() {
        List<CreateOrderCommand.OrderItemCommand> itemCommands = items.stream()
                                                                      .map(i -> new CreateOrderCommand.OrderItemCommand(
                                                                              i.productId(),
                                                                              i.productName(),
                                                                              i.quantity(),
                                                                              Money.of(i.unitPrice()
                                                                                        .amount())
                                                                      ))
                                                                      .collect(Collectors.toList());
        return new CreateOrderCommand(customerId, shippingAddress, itemCommands);
    }

    public record OrderItemRequest(
            @NotNull UUID productId,
            @NotBlank String productName,
            @Min(value = 1,
                    message = "Quantity must be at least 1") int quantity,
            @NotNull @Valid MoneyRequest unitPrice
    ) {

    }

    public record MoneyRequest(@NotNull(message = "Amount cannot be null")
                               @Min(value = 0,
                                       message = "Amount must be positive") BigDecimal amount,
                               @NotBlank(message = "Currency is required") String currency) {

    }

}
