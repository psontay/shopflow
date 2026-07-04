package com.shopflow.inventory.application.queries;

import java.util.UUID;

public record ProductAvailabilityResponse(
        UUID productId,
        String name,
        int availableQuantity,
        boolean isAvailable,
        boolean isNotFound
) {

    public static ProductAvailabilityResponse notFound(UUID id) {
        return new ProductAvailabilityResponse(id, "NOT_FOUND", 0, false, true);
    }

}
