package com.shopflow.order.application.outbox;

import com.shopflow.shared.domain.DomainEvent;

import java.util.List;

public interface OutboxRepository {

    void saveEvents(List<DomainEvent> events);

}
