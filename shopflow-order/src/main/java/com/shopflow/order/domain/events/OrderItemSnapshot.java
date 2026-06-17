package com.shopflow.order.domain.events;

import java.util.UUID;

public record OrderItemSnapshot(
        UUID productId,
        int quantity
) {

}
