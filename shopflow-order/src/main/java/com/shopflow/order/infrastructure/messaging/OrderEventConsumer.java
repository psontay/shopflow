package com.shopflow.order.infrastructure.messaging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderEventConsumer {

    @KafkaListener(topics = "shopflow.order.events")
    public void listenOrderEvents(String messagePayload) {
        log.info("\n=============================");
        log.info("[Kafka-Consumer] Got a new message: {}", messagePayload);

    }

}
