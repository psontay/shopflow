package com.shopflow.order.infrastructure.persistence.impl;

import com.shopflow.order.application.outbox.OutboxRepository;
import com.shopflow.order.infrastructure.persistence.JpaOutboxRepository;
import com.shopflow.order.infrastructure.persistence.entity.OutboxEntity;
import com.shopflow.order.infrastructure.persistence.mapper.OutboxPersistenceMapper;
import com.shopflow.shared.domain.DomainEvent;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class OutboxRepositoryImpl implements OutboxRepository {

    private final JpaOutboxRepository jpaOutboxRepository;
    private final OutboxPersistenceMapper mapper;

    public OutboxRepositoryImpl(JpaOutboxRepository jpaOutboxRepository, OutboxPersistenceMapper mapper) {
        this.jpaOutboxRepository = jpaOutboxRepository;
        this.mapper = mapper;
    }

    @Override
    public void saveEvents(List<DomainEvent> events) {
        List<OutboxEntity> entities = events.stream()
                                            .map(mapper :: toEntity)
                                            .collect(Collectors.toList());
        jpaOutboxRepository.saveAll(entities);
    }

}
