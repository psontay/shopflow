package com.shopflow.inventory.application.queries;

import java.util.UUID;

public record ProductAvailabilityResponse(
        UUID productId,
        String name,
        int availableQuantity,
        boolean isAvailable
) {

}
