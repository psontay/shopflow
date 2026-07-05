package com.shopflow.inventory.infrastructure.messaging.handlers;

import com.shopflow.inventory.infrastructure.persistence.JpaProcessedEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractIdempotentEventHandler implements InventoryEventHandler {

    protected final Logger log = LoggerFactory.getLogger(getClass());
    private final JpaProcessedEventRepository processedEventRepository;

    protected AbstractIdempotentEventHandler(JpaProcessedEventRepository processedEventRepository) {
        this.processedEventRepository = processedEventRepository;
    }

}
