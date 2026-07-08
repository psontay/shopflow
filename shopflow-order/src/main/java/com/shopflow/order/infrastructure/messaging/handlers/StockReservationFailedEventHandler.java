package com.shopflow.order.infrastructure.messaging.handlers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopflow.order.application.commands.CancelOrderCommand;
import com.shopflow.order.application.commands.CancelOrderCommandHandler;
import com.shopflow.order.infrastructure.persistence.repository.JpaProcessedEventRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class StockReservationFailedEventHandler extends AbstractIdempotentEventHandler {

    private final CancelOrderCommandHandler cancelOrderCommandHandler;
    private final ObjectMapper objectMapper;

    public StockReservationFailedEventHandler(JpaProcessedEventRepository jpaProcessedEventRepository,
                                              CancelOrderCommandHandler cancelOrderCommandHandler,
                                              ObjectMapper objectMapper) {
        super(jpaProcessedEventRepository);
        this.cancelOrderCommandHandler = cancelOrderCommandHandler;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean canHandle(String eventType) {
        return "StockReservationFailedEvent".equals(eventType);
    }

    @Override
    protected void processBusinessLogic(String payload) throws Exception {
        JsonNode rootNode = objectMapper.readTree(payload);
        String orderIdStr = rootNode.path("aggregateId")
                                    .asText();
        String reason = rootNode.path("reason")
                                .asText();
        UUID orderId = UUID.fromString(orderIdStr);
        CancelOrderCommand command = new CancelOrderCommand(orderId, reason);
        cancelOrderCommandHandler.handle(command);
    }

}
