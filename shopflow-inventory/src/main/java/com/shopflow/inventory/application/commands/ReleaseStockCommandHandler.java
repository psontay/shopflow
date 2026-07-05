package com.shopflow.inventory.application.commands;

import com.shopflow.inventory.domain.exceptions.InventoryDomainException;
import com.shopflow.inventory.domain.exceptions.InventoryErrorCode;
import com.shopflow.inventory.domain.models.Product;
import com.shopflow.inventory.domain.repository.ProductRepository;
import com.shopflow.shared.infrastructure.cache.DistributedCacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReleaseStockCommandHandler {

    private static final Logger log = LoggerFactory.getLogger(ReserveStockCommandHandler.class);
    private final ProductRepository productRepository;
    private final DistributedCacheService cacheService;

    public ReleaseStockCommandHandler(ProductRepository productRepository, DistributedCacheService cacheService) {
        this.productRepository = productRepository;
        this.cacheService = cacheService;
    }

    @Transactional
    public void handle(ReleaseStockCommand command) {
        log.info("Release Stock - OrderId: {}, Product ID: {}, Quantity: {}",
                 command.orderId(),
                 command.productId(),
                 command.quantity());
        Product product = productRepository.findById(command.productId())
                                           .orElseThrow(() -> new InventoryDomainException(
                                                   InventoryErrorCode.PRODUCT_NOT_FOUND));
        product.releaseStock(command.quantity());
        productRepository.save(product);
        cacheService.evictCacheAndNotify("inventory-stock::" + command.productId());
        log.info("Release Stock Successfully - Clear cache for ProductID: {}", command.productId());
    }

}
