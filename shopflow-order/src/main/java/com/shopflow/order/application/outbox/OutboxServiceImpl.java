package com.shopflow.order.application.outbox;

import com.shopflow.order.infrastructure.persistence.JpaOutboxRepository;
import com.shopflow.order.infrastructure.persistence.entity.OutboxEntity;
import com.shopflow.order.infrastructure.persistence.mapper.OutboxPersistenceMapper;
import com.shopflow.shared.domain.DomainEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class OutboxServiceImpl implements OutboxService {

    private final JpaOutboxRepository jpaOutboxRepository;
    private final OutboxPersistenceMapper mapper;

    public OutboxServiceImpl(JpaOutboxRepository jpaOutboxRepository, OutboxPersistenceMapper mapper) {
        this.jpaOutboxRepository = jpaOutboxRepository;
        this.mapper = mapper;
    }

    @Override
    public void saveEvents(List<DomainEvent> domainEvent) {
        List<OutboxEntity> entities = new ArrayList<>();
        for (DomainEvent de : domainEvent) {
            OutboxEntity outboxEntity = mapper.toEntity(de);
            entities.add(outboxEntity);
        }
        jpaOutboxRepository.saveAll(entities);
    }

}
