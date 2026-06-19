package com.shopflow.inventory.application.queries;

import java.io.Serializable;
import java.util.UUID;

public record ProductAvailabilityResponse(
        UUID productId,
        String name,
        int availableQuantity,
        boolean isAvailable
) implements Serializable {

}
