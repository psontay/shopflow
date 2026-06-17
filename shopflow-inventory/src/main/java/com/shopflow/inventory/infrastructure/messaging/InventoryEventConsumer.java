package com.shopflow.inventory.infrastructure.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopflow.inventory.application.commands.ReserveStockCommand;
import com.shopflow.inventory.application.commands.ReserveStockCommandHandler;
import com.shopflow.inventory.infrastructure.persistence.JpaProcessedEventRepository;
import com.shopflow.inventory.infrastructure.persistence.entity.ProcessedEventEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Component
public class InventoryEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(InventoryEventConsumer.class);

    private final ReserveStockCommandHandler reserveStockCommandHandler;
    private final ObjectMapper objectMapper;
    private final JpaProcessedEventRepository processedEventRepository;

    public InventoryEventConsumer(ReserveStockCommandHandler reserveStockCommandHandler, ObjectMapper objectMapper,
                                  JpaProcessedEventRepository processedEventRepository) {
        this.reserveStockCommandHandler = reserveStockCommandHandler;
        this.objectMapper = objectMapper;
        this.processedEventRepository = processedEventRepository;
    }

    @Transactional
    @KafkaListener(topics = "order-events",
            groupId = "inventory-service-group")
    public void handleOrderCreateEvent(@Payload String messagePayload, Acknowledgment acknowledgment) {
        log.info("Get event from Kafka. Processing...");
        try {
            JsonNode rootNode = objectMapper.readTree(messagePayload);
            String eventType = rootNode.path("eventType")
                                       .asText();
            String eventId = rootNode.path("eventId")
                                     .asText();
            if ("OrderCreatedEvent".equals(eventType) && ! eventId.isBlank()) {
                try {
                    processedEventRepository.saveAndFlush(new ProcessedEventEntity(eventId, Instant.now()));
                    log.debug("Lock eventID: {}", eventId);
                    JsonNode itemsNode = rootNode.path("items");
                    if (itemsNode.isArray()) {
                        for (JsonNode item : itemsNode) {
                            UUID productId = UUID.fromString(item.path("productId")
                                                                 .asText());
                            int quantity = item.path("quantity")
                                               .asInt();
                            log.debug("Process reserve stock. ProductID: {}, Quantity: {}", productId, quantity);
                            ReserveStockCommand command = new ReserveStockCommand(productId, quantity);
                            reserveStockCommandHandler.handle(command);
                        }
                    }
                } catch (DataIntegrityViolationException e) {
                    log.warn("Warning duplicate event: {}", eventId);
                }

            }
            acknowledgment.acknowledge();
            log.info("Processing event success and Commit Acknowledgment");
        } catch (JsonProcessingException e) {

            acknowledgment.acknowledge();
        }
    }

}
