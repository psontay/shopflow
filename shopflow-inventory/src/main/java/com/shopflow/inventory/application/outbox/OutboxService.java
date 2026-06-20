package com.shopflow.inventory.application.outbox;

import com.shopflow.shared.domain.DomainEvent;

import java.util.List;

public interface OutboxService {
    void saveEvents(List<DomainEvent> domainEvent);
}
