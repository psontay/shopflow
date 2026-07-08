package com.shopflow.identity.infrastructure.messaging;

import com.shopflow.identity.infrastructure.persistence.entity.DeadLetterMessage;
import com.shopflow.identity.infrastructure.persistence.repository.DeadLetterMessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class DltEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(DltEventConsumer.class);

    private final DeadLetterMessageRepository deadLetterMessageRepository;

    public DltEventConsumer(DeadLetterMessageRepository deadLetterMessageRepository) {
        this.deadLetterMessageRepository = deadLetterMessageRepository;
    }

    @KafkaListener(
            topics = {"shopflow.identity.events.DLT", "shopflow.user.events.DLT"},
            groupId = "identity-dlt-group"
    )
    public void handleDeadLetterMessages(@Payload String messagePayload,
                                         @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                         Acknowledgment acknowledgment) {
        log.warn("Warning! Discovered new dead message in DLT: {}", messagePayload);
        DeadLetterMessage deadLetterMessage = DeadLetterMessage.builder()
                                                               .topic(topic)
                                                               .payload(messagePayload)
                                                               .reason("Failed after 3 retries in Identity Service")
                                                               .build();
        deadLetterMessageRepository.save(deadLetterMessage);

        acknowledgment.acknowledge();
    }

}
