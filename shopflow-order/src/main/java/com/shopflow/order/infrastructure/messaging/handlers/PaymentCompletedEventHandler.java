package com.shopflow.order.infrastructure.messaging.handlers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopflow.order.application.commands.PayOrderCommand;
import com.shopflow.order.application.commands.PayOrderCommandHandler;
import com.shopflow.order.infrastructure.persistence.repository.JpaProcessedEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PaymentCompletedEventHandler extends AbstractIdempotentEventHandler {

    private static final Logger log = LoggerFactory.getLogger(PaymentCompletedEventHandler.class);

    private final ObjectMapper objectMapper;
    private final PayOrderCommandHandler payOrderCommandHandler;

    public PaymentCompletedEventHandler(
            JpaProcessedEventRepository processedEventRepository,
            ObjectMapper objectMapper, PayOrderCommandHandler payOrderCommandHandler) {
        super(processedEventRepository);
        this.objectMapper = objectMapper;
        this.payOrderCommandHandler = payOrderCommandHandler;
    }

    @Override
    public boolean canHandle(String eventType) {
        return "PaymentCompletedEvent".equals(eventType);
    }

    @Override
    protected void processBusinessLogic(String payload) throws Exception {
        JsonNode rootNode = objectMapper.readTree(payload);
        String orderIdStr = rootNode.path("orderId")
                                    .asText();
        UUID orderId = UUID.fromString(orderIdStr);
        String paymentMethod = rootNode.path("paymentMethod")
                                       .asText();
        PayOrderCommand command = new PayOrderCommand(orderId, paymentMethod);
        payOrderCommandHandler.handle(command);
    }

}
