package com.shopflow.order.infrastructure.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopflow.order.application.commands.CancelOrderCommand;
import com.shopflow.order.application.commands.CancelOrderCommandHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class OrderEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(OrderEventConsumer.class);
    private final CancelOrderCommandHandler cancelOrderCommandHandler;
    private final ObjectMapper objectMapper;

    public OrderEventConsumer(CancelOrderCommandHandler cancelOrderCommandHandler, ObjectMapper objectMapper) {
        this.cancelOrderCommandHandler = cancelOrderCommandHandler;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "inventory-events",
            groupId = "order-saga-group")
    public void handleInventoryEvents(@Payload String messagePayload, Acknowledgment acknowledgment) {
        log.info("Get events res from Inventory. Processing...");
        try {
            JsonNode rootNode = objectMapper.readTree(messagePayload);
            String eventType = rootNode.path("eventType")
                                       .asText();
            if ("StockReservationFailedEvent".equals(eventType)) {
                String orderIdStr = rootNode.path("orderId")
                                            .asText();
                if (orderIdStr != null && ! orderIdStr.isBlank()) {
                    UUID orderId = UUID.fromString(orderIdStr);
                    log.warn("Start to cancel Order ID {} cause run out of stock.", orderId);
                    CancelOrderCommand command = new CancelOrderCommand(orderId);
                    cancelOrderCommandHandler.handle(command);
                    log.info("Cancel Order ID {} successfully", orderId);
                } else {
                    log.error("Cannot find order id in payload {}", messagePayload);
                }
            }
            acknowledgment.acknowledge();
        } catch (JsonProcessingException e) {
            log.error("Parse JSON error from Kafka: {}", messagePayload, e);
            acknowledgment.acknowledge();
        } catch (IllegalArgumentException e) {
            log.error("{}", messagePayload, e);
            acknowledgment.acknowledge();
        }
    }

}
