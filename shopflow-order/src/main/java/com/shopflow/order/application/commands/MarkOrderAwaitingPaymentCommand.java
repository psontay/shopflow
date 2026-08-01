package com.shopflow.order.application.commands;

import java.util.UUID;

public record MarkOrderAwaitingPaymentCommand(UUID orderId) {

}
