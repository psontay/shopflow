package com.shopflow.order.infrastructure.messaging.handlers;

public interface OrderEventHandler {

    boolean canHandle(String eventType);

    void handle(String eventId, String payload) throws Exception;

}
