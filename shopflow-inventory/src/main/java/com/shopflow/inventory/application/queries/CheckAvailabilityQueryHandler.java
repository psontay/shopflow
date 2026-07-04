package com.shopflow.inventory.application.queries;

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
public class CheckAvailabilityQueryHandler {

    private static final Logger log = LoggerFactory.getLogger(CheckAvailabilityQueryHandler.class);
    private final ProductRepository productRepository;

    private final DistributedCacheService cacheService;

    public CheckAvailabilityQueryHandler(ProductRepository productRepository, DistributedCacheService cacheService) {
        this.productRepository = productRepository;
        this.cacheService = cacheService;
    }

    @Transactional(readOnly = true)
    public ProductAvailabilityResponse handle(CheckAvailabilityQuery query) {
        log.debug("Checking stock quantity for Product ID: {}", query.productId());
        String cacheKey = "inventory-availability::" + query.productId();
        String lockKey = "lock:inventory-availability::" + query.productId();
        return cacheService.getWithDoubleCheckLock(cacheKey, lockKey, () -> {
            log.debug("Cache miss => Get product from database with ID: {}", query.productId());
            Product product = productRepository.findById(query.productId())
                                               .orElseThrow(() -> new InventoryDomainException(InventoryErrorCode.PRODUCT_NOT_FOUND));
            return new ProductAvailabilityResponse(product.getId(),
                                                   product.getName(),
                                                   product.getAvailableQuantity(),
                                                   product.getAvailableQuantity() > 0);
        }, 60);
    }

}
