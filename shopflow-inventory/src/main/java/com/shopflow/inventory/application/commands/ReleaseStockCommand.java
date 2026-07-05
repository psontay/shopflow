package com.shopflow.inventory.application.commands;

import java.util.UUID;

public record ReleaseStockCommand(UUID orderId, UUID productId, int quantity) {

}
