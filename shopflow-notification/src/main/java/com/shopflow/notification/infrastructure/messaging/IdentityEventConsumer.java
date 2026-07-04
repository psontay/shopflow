package com.shopflow.notification.infrastructure.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopflow.notification.application.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class IdentityEventConsumer {

    private final EmailService emailService;
    private final ObjectMapper objectMapper;

    public IdentityEventConsumer(EmailService emailService, ObjectMapper objectMapper) {
        this.emailService = emailService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "identity-events", groupId = "notification-group")
    public void handleIdentityEvents(@Payload String messagePayload, Acknowledgment acknowledgment) {
        log.info("Nhận sự kiện từ Identity: {}", messagePayload);
        try {
            JsonNode rootNode = objectMapper.readTree(messagePayload);
            String eventType = rootNode.path("eventType").asText();
            
            if ("UserRegisterEvent".equals(eventType)) {
                String email = rootNode.path("email").asText();
                String otp = rootNode.path("otp").asText();
                
                if (email != null && !email.isBlank() && otp != null && !otp.isBlank()) {
                    emailService.sendOtpEmail(email, otp);
                } else {
                    log.warn("Bỏ qua gửi mail vì thiếu thông tin. Email: {}, OTP: {}", email, otp);
                }
            }
            acknowledgment.acknowledge();
        } catch (JsonProcessingException e) {
            log.error("Lỗi parse JSON: {}", e.getMessage());
            acknowledgment.acknowledge(); // Nuốt lỗi parse để không bị kẹt queue
        }
    }
}
