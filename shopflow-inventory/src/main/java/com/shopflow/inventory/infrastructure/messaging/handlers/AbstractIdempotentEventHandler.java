package com.shopflow.inventory.infrastructure.messaging.handlers;

import com.shopflow.inventory.infrastructure.persistence.entity.ProcessedEventEntity;
import com.shopflow.inventory.infrastructure.persistence.repository.JpaProcessedEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

public abstract class AbstractIdempotentEventHandler implements InventoryEventHandler {

    protected final Logger log = LoggerFactory.getLogger(getClass());
    private final JpaProcessedEventRepository processedEventRepository;

    protected AbstractIdempotentEventHandler(JpaProcessedEventRepository processedEventRepository) {
        this.processedEventRepository = processedEventRepository;
    }

    @Override
    @Transactional
    public void handle(String eventId, String payload) throws Exception {
        if (eventId == null || eventId.isBlank()) {
            log.warn("Event ID is empty, skipping processing.");
            return;
        }

        if (processedEventRepository.existsById(eventId)) {
            log.info("Event {} has already been processed. Skipping.", eventId);
            return;
        }

        processBusinessLogic(payload);

        processedEventRepository.save(new ProcessedEventEntity(eventId, Instant.now()));
        log.debug("Successfully processed and locked eventID: {}", eventId);
    }

    protected abstract void processBusinessLogic(String payload) throws Exception;

}
