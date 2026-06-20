package com.shopflow.inventory.application.commands;

import com.shopflow.inventory.domain.exceptions.InventoryDomainException;
import com.shopflow.inventory.domain.exceptions.InventoryErrorCode;

import java.util.UUID;

public record ReserveStockCommand(
        UUID orderId,
        UUID productId,
        int quantity
) {

    public ReserveStockCommand {
        if (orderId == null) {
            throw new InventoryDomainException(InventoryErrorCode.INVALID_ARGUMENT);
        }
        if (productId == null) {
            throw new InventoryDomainException(InventoryErrorCode.INVALID_ARGUMENT);
        }
        if (quantity <= 0) {
            throw new InventoryDomainException(InventoryErrorCode.INVALID_STOCK_QUANTITY);
        }
    }

}
