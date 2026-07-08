package com.shopflow.payment.infrastructure.messaging;

import com.shopflow.payment.infrastructure.persistence.entity.OutboxEntity;
import com.shopflow.payment.infrastructure.persistence.repository.JpaOutboxRepository;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
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
    @SchedulerLock(name = "payment_outbox_relay_lock",
            lockAtLeastFor = "50s",
            lockAtMostFor = "2m")
    public void processOutboxEvents() {
        List<OutboxEntity> events = jpaOutboxRepository.findAllByOrderByCreatedAtAsc();
        if (events.isEmpty()) {
            return;
        }
        log.info("[Outbox Relay] Start processing {} unsent events", events.size());
        for (OutboxEntity event : events) {
            try {
                kafkaTemplate.send("payment-events", event.getAggregateId(), event.getPayload());
                jpaOutboxRepository.delete(event);
                log.info("Deleted event [{}] success.", event.getId());
            } catch (Exception e) {
                log.error("Error when sent event [{}]. Skip, 10s later resent!", event.getId(), e);
            }
        }
    }

}
