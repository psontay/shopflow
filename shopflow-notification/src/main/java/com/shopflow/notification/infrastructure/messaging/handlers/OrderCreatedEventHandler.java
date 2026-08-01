package com.shopflow.notification.infrastructure.messaging.handlers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopflow.notification.application.service.EmailService;
import com.shopflow.notification.infrastructure.persistence.repository.JpaProcessedEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class OrderCreatedEventHandler extends AbstractIdempotentEventHandler {

    private static final Logger log = LoggerFactory.getLogger(OrderCreatedEventHandler.class);
    private final ObjectMapper objectMapper;
    private final EmailService emailService;

    public OrderCreatedEventHandler(JpaProcessedEventRepository jpaProcessedEventRepository, ObjectMapper objectMapper,
                                    EmailService emailService) {
        super(jpaProcessedEventRepository);
        this.objectMapper = objectMapper;
        this.emailService = emailService;
    }

    @Override
    public boolean canHandle(String eventType) {
        return "OrderCreatedEvent".equals(eventType);
    }

    @Override
    protected void processBusinessLogic(String payload) throws Exception {
        JsonNode rootNode = objectMapper.readTree(payload);
        String email = rootNode.path("email")
                               .asText();
        String orderId = rootNode.path("orderId")
                                 .asText();
        String text = "Order Create with id: " + orderId;
        emailService.sendTextEmail(email, text);
        log.info("Email has sent for order with id: {}", orderId);
    }

}
