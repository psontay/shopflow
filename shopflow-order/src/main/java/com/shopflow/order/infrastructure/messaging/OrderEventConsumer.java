package com.shopflow.order.infrastructure.messaging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopflow.order.infrastructure.messaging.handlers.OrderEventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(OrderEventConsumer.class);
    private final ObjectMapper objectMapper;
    private final List<OrderEventHandler> eventHandlers;

    public OrderEventConsumer(List<OrderEventHandler> eventHandlers, ObjectMapper objectMapper) {
        this.eventHandlers = eventHandlers;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = {"inventory-events", "payment-events"},
            groupId = "order-service-group")
    public void consume(@Payload String messagePayload, Acknowledgment acknowledgment) {
        log.info("RAW PAYLOAD received from Kafka: {}", messagePayload);
        try {
            JsonNode rootNode = objectMapper.readTree(messagePayload);
            String eventType = rootNode.path("eventType")
                                       .asText();
            String eventId = rootNode.path("eventId")
                                     .asText();

            boolean isHandled = false;
            for (OrderEventHandler handler : eventHandlers) {
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
