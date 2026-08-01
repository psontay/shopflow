package com.shopflow.notification.infrastructure.messaging.handlers;

public interface NotificationEventHandler {

    boolean canHandle(String eventType);

    void handle(String eventId, String payload) throws Exception;

}
