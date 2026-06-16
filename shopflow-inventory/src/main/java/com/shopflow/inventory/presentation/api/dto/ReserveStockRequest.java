package com.shopflow.inventory.presentation.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ReserveStockRequest(
        @NotNull(message = "Product ID cannot be null")
        UUID productId,
        @Min(value = 1,
                message = "Quantity must be positive")
        int quantity
) {

}
