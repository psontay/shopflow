package com.shopflow.order.application.commands;

import java.util.UUID;

public record PayOrderCommand(UUID orderId, String paymentMethod) {

}
