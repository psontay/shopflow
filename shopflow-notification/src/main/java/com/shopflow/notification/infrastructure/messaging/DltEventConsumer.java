package com.shopflow.notification.infrastructure.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class DltEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(DltEventConsumer.class);

    @KafkaListener(
            topics = {"identity-events.DLT"},
            groupId = "notification-dlt-group"
    )
    public void handleDeadLetterMessages(@Payload String messagePayload, Acknowledgment acknowledgment) {
        log.warn("Warning! Discovered new dead message in DLT: {}", messagePayload);
        acknowledgment.acknowledge();
    }

}
