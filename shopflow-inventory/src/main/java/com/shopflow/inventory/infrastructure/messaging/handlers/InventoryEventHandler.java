package com.shopflow.inventory.infrastructure.messaging.handlers;

public interface InventoryEventHandler {

    boolean canHandle(String eventType);

    void handle(String eventId, String payload) throws Exception;

}
