package com.shopflow.order.application.commands;

import java.util.UUID;

public record CancelOrderCommand(
        UUID orderId
) {

}
