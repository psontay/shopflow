package com.shopflow.inventory.application.queries;

import com.shopflow.inventory.domain.exceptions.InventoryDomainException;
import com.shopflow.inventory.domain.exceptions.InventoryErrorCode;

import java.util.UUID;

public record CheckAvailabilityQuery(
        UUID productId,
        int quantity
) {

    public CheckAvailabilityQuery {
        if (productId == null) {
            throw new InventoryDomainException(InventoryErrorCode.INVALID_ARGUMENT);
        }
        if (quantity < 1) {
            throw new InventoryDomainException(InventoryErrorCode.INVALID_STOCK_QUANTITY);
        }
    }

}
