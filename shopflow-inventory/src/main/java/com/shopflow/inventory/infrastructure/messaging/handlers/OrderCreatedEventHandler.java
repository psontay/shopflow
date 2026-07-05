package com.shopflow.inventory.infrastructure.messaging.handlers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopflow.inventory.application.commands.ReserveStockCommand;
import com.shopflow.inventory.application.commands.ReserveStockCommandHandler;
import com.shopflow.inventory.infrastructure.persistence.JpaProcessedEventRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class OrderCreatedEventHandler extends AbstractIdempotentEventHandler {

    private final ReserveStockCommandHandler reserveStockCommandHandler;
    private final ObjectMapper objectMapper;

    public OrderCreatedEventHandler(JpaProcessedEventRepository processedEventRepository,
                                    ReserveStockCommandHandler reserveStockCommandHandler,
                                    ObjectMapper objectMapper) {
        super(processedEventRepository);
        this.reserveStockCommandHandler = reserveStockCommandHandler;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean canHandle(String eventType) {
        return "OrderCreatedEvent".equals(eventType);
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
                log.debug("Process reserve stock. ProductID: {}, Quantity: {}", productId, quantity);
                UUID orderId = UUID.fromString(orderIdStr);
                ReserveStockCommand command = new ReserveStockCommand(orderId, productId, quantity);
                reserveStockCommandHandler.handle(command);
            }
        }
    }

}
