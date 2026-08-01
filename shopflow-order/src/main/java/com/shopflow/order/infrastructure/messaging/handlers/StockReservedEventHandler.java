package com.shopflow.order.infrastructure.messaging.handlers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopflow.order.application.commands.MarkOrderAwaitingPaymentCommand;
import com.shopflow.order.application.commands.MarkOrderAwaitingPaymentCommandHandler;
import com.shopflow.order.infrastructure.persistence.repository.JpaProcessedEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class StockReservedEventHandler extends AbstractIdempotentEventHandler {

    private static final Logger log = LoggerFactory.getLogger(StockReservedEventHandler.class);
    private final ObjectMapper objectMapper;
    private final MarkOrderAwaitingPaymentCommandHandler markOrderAwaitingPaymentCommandHandler;

    public StockReservedEventHandler(
            JpaProcessedEventRepository jpaProcessedEventRepository,
            MarkOrderAwaitingPaymentCommandHandler markOrderAwaitingPaymentCommandHandler, ObjectMapper objectMapper) {
        super(jpaProcessedEventRepository);
        this.markOrderAwaitingPaymentCommandHandler = markOrderAwaitingPaymentCommandHandler;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean canHandle(String eventType) {
        return "StockReservedEvent".equals(eventType);
    }

    @Override
    protected void processBusinessLogic(String payload) throws Exception {
        JsonNode rootNode = objectMapper.readTree(payload);
        String orderIdStr = rootNode.path("aggregateId")
                                    .asText();
        UUID orderId = UUID.fromString(orderIdStr);
        MarkOrderAwaitingPaymentCommand command = new MarkOrderAwaitingPaymentCommand(orderId);
        markOrderAwaitingPaymentCommandHandler.handle(command);
        log.info("Update order {} to PENDING_PAYMENT success", orderId);
    }

}
