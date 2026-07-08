package com.shopflow.payment.infrastructure.persistence.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopflow.payment.infrastructure.persistence.entity.OutboxEntity;
import com.shopflow.shared.domain.DomainEvent;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class OutboxPersistenceMapper {

    private final ObjectMapper objectMapper;

    public OutboxPersistenceMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public OutboxEntity toEntity(DomainEvent domainEvent) {
        try {
            String jsonPayload = objectMapper.writeValueAsString(domainEvent);
            return OutboxEntity.builder()
                               .id(UUID.randomUUID())
                               .aggregateType("PAYMENT")
                               .aggregateId(domainEvent.aggregateId())
                               .eventType(domainEvent.getClass()
                                                     .getSimpleName())
                               .payload(jsonPayload)
                               .createdAt(domainEvent.occurredOn())
                               .build();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize Domain Event to JSON", e);
        }

    }

}
