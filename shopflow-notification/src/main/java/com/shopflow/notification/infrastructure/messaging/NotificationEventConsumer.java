package com.shopflow.notification.infrastructure.messaging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopflow.notification.infrastructure.messaging.handlers.NotificationEventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NotificationEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(NotificationEventConsumer.class);

    private final ObjectMapper objectMapper;
    private final List<NotificationEventHandler> eventHandlers;

    public NotificationEventConsumer(ObjectMapper objectMapper, List<NotificationEventHandler> eventHandlers) {
        this.objectMapper = objectMapper;
        this.eventHandlers = eventHandlers;
    }

    @KafkaListener(topics = {"identity-events", "order-events"},
            groupId = "notification-service-group")
    public void consume(@Payload String messagePayload, Acknowledgment acknowledgment) {
        log.info("RAW PAYLOAD received from Kafka: {}", messagePayload);
        try {
            JsonNode rootNode = objectMapper.readTree(messagePayload);
            String eventType = rootNode.path("eventType")
                                       .asText();
            String eventId = rootNode.path("eventId")
                                     .asText();

            boolean isHandled = false;
            for (NotificationEventHandler handler : eventHandlers) {
                if (handler.canHandle(eventType)) {
                    handler.handle(eventId, messagePayload);
                    isHandled = true;
                    break;
                }
            }
            if (! isHandled) {
                log.warn("No handler found for eventType: {}", eventType);
            }
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("Error processing message payload: {}", messagePayload, e);
            acknowledgment.acknowledge();
        }
    }

}
