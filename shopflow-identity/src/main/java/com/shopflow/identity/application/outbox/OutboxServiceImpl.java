package com.shopflow.identity.application.outbox;

import com.shopflow.shared.domain.DomainEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class OutboxServiceImpl implements OutboxService {

    private final OutboxRepository outboxRepository;

    public OutboxServiceImpl(OutboxRepository outboxRepository) {
        this.outboxRepository = outboxRepository;
    }

    @Override
    public void saveEvents(List<DomainEvent> domainEvent) {
        outboxRepository.saveEvents(domainEvent);
    }

}
