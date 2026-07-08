package com.shopflow.order.infrastructure.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class DltEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(DltEventConsumer.class);

    private final com.shopflow.order.infrastructure.persistence.repository.DeadLetterMessageRepository deadLetterMessageRepository;

    public DltEventConsumer(
            com.shopflow.order.infrastructure.persistence.repository.DeadLetterMessageRepository deadLetterMessageRepository) {
        this.deadLetterMessageRepository = deadLetterMessageRepository;
    }

    @KafkaListener(
            topics = {"payment-events.DLT", "inventory-events.DLT"},
            groupId = "order-dlt-group"
    )
    public void handleDeadLetterMessages(@Payload String messagePayload,
                                         @org.springframework.messaging.handler.annotation.Header(org.springframework.kafka.support.KafkaHeaders.RECEIVED_TOPIC) String topic,
                                         Acknowledgment acknowledgment) {
        log.warn("Warning! Discovered new dead message in DLT: {}", messagePayload);

        com.shopflow.order.infrastructure.persistence.entity.DeadLetterMessage deadLetterMessage = com.shopflow.order.infrastructure.persistence.entity.DeadLetterMessage.builder()
                                                                                                                                                                         .topic(topic)
                                                                                                                                                                         .payload(
                                                                                                                                                                                 messagePayload)
                                                                                                                                                                         .reason("Failed after 3 retries in Order Service")
                                                                                                                                                                         .build();
        deadLetterMessageRepository.save(deadLetterMessage);

        acknowledgment.acknowledge();
    }

}
