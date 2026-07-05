package com.shopflow.inventory.application.commands;

import com.shopflow.inventory.application.outbox.OutboxRepository;
import com.shopflow.inventory.domain.events.StockReservationFailedEvent;
import com.shopflow.inventory.domain.exceptions.InventoryDomainException;
import com.shopflow.inventory.domain.exceptions.InventoryErrorCode;
import com.shopflow.inventory.domain.models.Product;
import com.shopflow.inventory.domain.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReserveStockCommandHandler {

    private static final Logger log = LoggerFactory.getLogger(ReserveStockCommandHandler.class);

    private final ProductRepository productRepository;

    private final OutboxRepository outboxRepository;

    private final com.shopflow.shared.infrastructure.cache.DistributedCacheService cacheService;

    public ReserveStockCommandHandler(ProductRepository productRepository, OutboxRepository outboxRepository,
                                      com.shopflow.shared.infrastructure.cache.DistributedCacheService cacheService) {
        this.productRepository = productRepository;
        this.outboxRepository = outboxRepository;
        this.cacheService = cacheService;
    }

    @Transactional
    public void handle(ReserveStockCommand command) {
        log.info("Reserve stock processing. OrderId: {}, ProductID: {}, Quantity: {}",
                 command.orderId(),
                 command.productId(),
                 command.quantity());
        try {
            Product product = productRepository.findById(command.productId())
                                               .orElseThrow(() -> {
                                                   log.warn("Cannot found Product ID: {}", command.productId());
                                                   return new InventoryDomainException(InventoryErrorCode.PRODUCT_NOT_FOUND);
                                               });
            product.reserveStock(command.quantity());
            productRepository.save(product);
            cacheService.evictCacheAndNotify("inventory-availability::" + command.productId());
            log.info("Reserve stock successfully. ProductID: {}", command.productId());

        } catch (InventoryDomainException e) {
            log.warn("Saga Compensating: Out of stock, cancel for OrderID: {}", command.orderId());
            StockReservationFailedEvent failedEvent = new StockReservationFailedEvent(command.orderId(),
                                                                                      e.getMessage());
            outboxRepository.saveEvents(java.util.List.of(failedEvent));
        }
    }

}
