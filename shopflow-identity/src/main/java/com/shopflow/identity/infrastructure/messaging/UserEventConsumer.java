package com.shopflow.identity.infrastructure.messaging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserEventConsumer {

    @KafkaListener(topics = "shopflow.user.events")
    public void listenUserEvents(String messagePayload) {
        log.info("\n=============================");
        log.info("[Kafka-Consumer] Got a new message: {}", messagePayload);
    }

}
