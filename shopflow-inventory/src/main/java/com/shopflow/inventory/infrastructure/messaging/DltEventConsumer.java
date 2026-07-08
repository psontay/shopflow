package com.shopflow.inventory.infrastructure.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class DltEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(DltEventConsumer.class);
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final com.shopflow.inventory.infrastructure.persistence.repository.DeadLetterMessageRepository deadLetterMessageRepository;

    public DltEventConsumer(ObjectMapper objectMapper, KafkaTemplate<String, String> kafkaTemplate, com.shopflow.inventory.infrastructure.persistence.repository.DeadLetterMessageRepository deadLetterMessageRepository) {
        this.objectMapper = objectMapper;
        this.kafkaTemplate = kafkaTemplate;
        this.deadLetterMessageRepository = deadLetterMessageRepository;
    }

    @KafkaListener(topics = "order-events.DLT",
            groupId = "inventory-dlt-group")
    public void handleDltMessages(@Payload String messagePayload, @org.springframework.messaging.handler.annotation.Header(org.springframework.kafka.support.KafkaHeaders.RECEIVED_TOPIC) String topic, Acknowledgment acknowledgment) {
        log.warn("Warning! Discovered new dead message in DLT: {}", messagePayload);
        
        com.shopflow.inventory.infrastructure.persistence.entity.DeadLetterMessage deadLetterMessage = com.shopflow.inventory.infrastructure.persistence.entity.DeadLetterMessage.builder()
                .topic(topic)
                .payload(messagePayload)
                .reason("Failed after 3 retries in Inventory Service")
                .build();
        deadLetterMessageRepository.save(deadLetterMessage);

        try {
            JsonNode rootNode = objectMapper.readTree(messagePayload);
            String eventType = rootNode.path("eventType")
                                       .asText();
            String orderId = rootNode.path("orderId")
                                     .asText();
            if ("OrderCreatedEvent".equals(eventType) && orderId != null && ! orderId.isBlank()) {
                log.info("Start compensating transaction for Order ID: {}", orderId);
                String failedEventJson = String.format(
                        "{\"eventType\":\"StockReservationFailedEvent\", \"orderId\":\"%s\", \"reason\":\"Out of stock\"}",
                        orderId
                                                      );
                kafkaTemplate.send("inventory-events", orderId, failedEventJson);
            }
            acknowledgment.acknowledge();
        } catch (JsonProcessingException e) {
            log.error("Fucking JSON, next.... Payload: {}", messagePayload, e);
            acknowledgment.acknowledge();
        }
    }

}
