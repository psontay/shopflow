package com.shopflow.notification.infrastructure.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopflow.notification.application.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class OrderCreatedEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(OrderCreatedEventConsumer.class);
    private final EmailService emailService;
    private final ObjectMapper objectMapper;

    public OrderCreatedEventConsumer(EmailService emailService, ObjectMapper objectMapper) {
        this.emailService = emailService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "order-events",
            groupId = "notification-service-group")
    public void consume(@Payload String messagePayload, Acknowledgment acknowledgment) {
        log.info("Get Event from Order: {}", messagePayload);
        try {
            JsonNode rootNode = objectMapper.readTree(messagePayload);
            String eventType = rootNode.path("eventType")
                                       .asText();
            if ("OrderCreatedEvent".equals(eventType)) {
            }
        } catch (JsonProcessingException e) {
            log.error("Error when parse JSON: {}", e.getMessage());
            acknowledgment.acknowledge();
        }
    }

}
