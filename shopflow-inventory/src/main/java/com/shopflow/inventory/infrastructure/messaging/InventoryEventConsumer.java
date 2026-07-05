package com.shopflow.inventory.infrastructure.messaging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopflow.inventory.infrastructure.messaging.handlers.InventoryEventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InventoryEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(InventoryEventConsumer.class);

    private final List<InventoryEventHandler> eventHandlers;
    private final ObjectMapper objectMapper;

    public InventoryEventConsumer(List<InventoryEventHandler> eventHandlers, ObjectMapper objectMapper) {
        this.eventHandlers = eventHandlers;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "order-events",
            groupId = "inventory-service-group")
    public void consume(@Payload String messagePayload, Acknowledgment acknowledgment) {
        log.info("RAW PAYLOAD received from Kafka");

        try {
            JsonNode rootNode = objectMapper.readTree(messagePayload);
            String eventType = rootNode.path("eventType")
                                       .asText();
            String eventId = rootNode.path("eventId")
                                     .asText();

            boolean isHandled = false;
            for (InventoryEventHandler handler : eventHandlers) {
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