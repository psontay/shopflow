package com.shopflow.notification.infrastructure.messaging.handlers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopflow.notification.application.service.EmailService;
import com.shopflow.notification.infrastructure.persistence.repository.JpaProcessedEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class UserRegisterEventHandler extends AbstractIdempotentEventHandler {

    private static final Logger log = LoggerFactory.getLogger(UserRegisterEventHandler.class);
    private final ObjectMapper objectMapper;
    private final EmailService emailService;

    public UserRegisterEventHandler(
            JpaProcessedEventRepository processedEventRepository,
            ObjectMapper objectMapper, EmailService emailService) {
        super(processedEventRepository);
        this.objectMapper = objectMapper;
        this.emailService = emailService;
    }

    @Override
    public boolean canHandle(String eventType) {
        return "UserRegisterEvent".equals(eventType);
    }

    @Override
    protected void processBusinessLogic(String payload) throws Exception {
        JsonNode rootNode = objectMapper.readTree(payload);
        String email = rootNode.path("email")
                               .asText();
        String otp = rootNode.path("otp")
                             .asText();
        emailService.sendOtpEmail(email, otp);
        log.info("Email has sent for register");
    }

}
