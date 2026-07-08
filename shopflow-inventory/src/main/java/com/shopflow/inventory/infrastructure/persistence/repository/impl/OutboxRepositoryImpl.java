package com.shopflow.inventory.infrastructure.persistence.repository.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopflow.inventory.application.outbox.OutboxRepository;
import com.shopflow.inventory.infrastructure.persistence.entity.OutboxEntity;
import com.shopflow.inventory.infrastructure.persistence.repository.JpaOutboxRepository;
import com.shopflow.shared.domain.DomainEvent;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public class OutboxRepositoryImpl implements OutboxRepository {

    private final JpaOutboxRepository jpaOutboxRepository;
    private final ObjectMapper objectMapper;

    public OutboxRepositoryImpl(JpaOutboxRepository jpaOutboxRepository, ObjectMapper objectMapper) {
        this.jpaOutboxRepository = jpaOutboxRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void saveEvents(List<DomainEvent> domainEvent) {
        for (DomainEvent event : domainEvent) {
            try {
                String jsonPayload = objectMapper.writeValueAsString(event);
                String eventType = event.getClass()
                                        .getSimpleName();
                OutboxEntity entity = OutboxEntity.builder()
                                                  .id(UUID.randomUUID())
                                                  .aggregateType(event.aggregateType())
                                                  .aggregateId(event.aggregateId())
                                                  .eventType(eventType)
                                                  .payload(jsonPayload)
                                                  .createdAt(Instant.now())
                                                  .build();
                jpaOutboxRepository.save(entity);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Error when parse JSON", e);
            }
        }
    }

}
