package com.shopflow.inventory.infrastructure.messaging.handlers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopflow.inventory.application.commands.ReleaseStockCommand;
import com.shopflow.inventory.application.commands.ReleaseStockCommandHandler;
import com.shopflow.inventory.infrastructure.persistence.JpaProcessedEventRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class OrderCancelledEventHandler extends AbstractIdempotentEventHandler {

    private final ReleaseStockCommandHandler releaseStockCommandHandler;
    private final ObjectMapper objectMapper;

    public OrderCancelledEventHandler(JpaProcessedEventRepository jpaProcessedEventRepository,
                                      ReleaseStockCommandHandler releaseStockCommandHandler,
                                      ObjectMapper objectMapper) {
        super(jpaProcessedEventRepository);
        this.releaseStockCommandHandler = releaseStockCommandHandler;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean canHandle(String eventType) {
        return "OrderCancelledEvent".equals(eventType);
    }

    @Override
    protected void processBusinessLogic(String payload) throws Exception {
        JsonNode rootNode = objectMapper.readTree(payload);
        String orderIdStr = rootNode.path("aggregateId")
                                    .asText();
        JsonNode itemsNode = rootNode.path("items");
        if (itemsNode.isArray()) {
            for (JsonNode item : itemsNode) {
                UUID productId = UUID.fromString(item.path("productId")
                                                     .asText());
                int quantity = item.path("quantity")
                                   .asInt();
                log.debug("Process release stock. ProductID: {}, Quantity: {}", productId, quantity);
                UUID orderId = UUID.fromString(orderIdStr);
                ReleaseStockCommand command = new ReleaseStockCommand(orderId, productId, quantity);
                releaseStockCommandHandler.handle(command);
            }
        }
    }

}
