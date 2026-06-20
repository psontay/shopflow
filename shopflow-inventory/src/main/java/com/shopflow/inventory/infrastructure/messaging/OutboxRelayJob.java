package com.shopflow.inventory.infrastructure.messaging;

import com.shopflow.inventory.infrastructure.persistence.JpaOutboxRepository;
import com.shopflow.inventory.infrastructure.persistence.entity.OutboxEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class OutboxRelayJob {

    private final JpaOutboxRepository jpaOutboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public OutboxRelayJob(JpaOutboxRepository jpaOutboxRepository, KafkaTemplate<String, String> kafkaTemplate) {
        this.jpaOutboxRepository = jpaOutboxRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedDelay = 10000)
    public void processOutboxEvents() {
        List<OutboxEntity> events = jpaOutboxRepository.findAllByOrderByCreatedAtAsc();
        if (events.isEmpty()) {
            return;
        }
        log.info("[Inventory Outbox Relay] Start processing {} unsent events", events.size());
        for (OutboxEntity event : events) {
            try {
                kafkaTemplate.send("inventory-events", event.getAggregateId(), event.getPayload());
                jpaOutboxRepository.delete(event);
                log.info("Deleted event [{}] success.", event.getId());
            } catch (Exception e) {
                log.error("Error when sent event [{}]. Skip, 10s later resent!", event.getId(), e);
            }
        }
    }

}
