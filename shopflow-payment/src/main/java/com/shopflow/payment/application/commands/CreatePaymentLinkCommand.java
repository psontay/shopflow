package com.shopflow.payment.application.commands;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record CreatePaymentLinkCommand(
        @NotNull(message = "OrderId cannot be null")
        UUID orderId,
        @NotNull(message = "Amount cannot be null")
        @DecimalMin(value = "1000",
                message = "Amount must be greater than 1000 VND")
        BigDecimal amount,

        @NotBlank(message = "Currency must not be null or blank")
        String currency,

        @NotBlank(message = "Payment method cannot be null")
        String paymentMethod,

        @NotBlank(message = "Payment content cannot be blank")
        String orderInfo
) {

}
