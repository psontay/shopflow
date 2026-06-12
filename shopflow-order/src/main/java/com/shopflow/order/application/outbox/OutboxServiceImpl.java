package com.shopflow.order.application.outbox;

import com.shopflow.shared.domain.DomainEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class OutboxServiceImpl implements OutboxService {

    @Override
    public void saveEvents(List<DomainEvent> domainEvent) {
        log.info("save event to outbox success", domainEvent.size());
    }

}
