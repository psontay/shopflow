package com.shopflow.order.application.queries;

import com.shopflow.order.domain.models.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record OrderSummaryResponse(
        UUID orderId,
        OrderStatus status,
        BigDecimal totalAmount,
        String currency,
        Instant createdAt
) {

}
