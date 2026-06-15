package com.shopflow.identity.infrastructure.messaging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class IdentityEventConsumer {

    @KafkaListener(topics = "shopflow.identity.events")
    public void listenIdentityEvents(String messagePayload) {
        log.info("\n=============================");
        log.info("[Identity Kafka-Consumer] Got a new message: {}", messagePayload);
        if (messagePayload.contains("UserRegisterEvent")) {
            log.info("=> Sending welcome email for new user..");
        } else if (messagePayload.contains("PasswordChangedEvent")) {
            log.info("=> Password has changed, revoke old token");
        }
    }

}
