package com.shopflow.order.presentation.api.dto;

import java.util.UUID;

public record CreateOrderResponse(
        UUID orderId,
        String message
) {

}
