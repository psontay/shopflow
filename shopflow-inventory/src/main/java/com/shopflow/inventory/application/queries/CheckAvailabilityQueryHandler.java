package com.shopflow.inventory.application.queries;

import com.github.benmanes.caffeine.cache.Cache;
import com.shopflow.inventory.domain.exceptions.InventoryDomainException;
import com.shopflow.inventory.domain.exceptions.InventoryErrorCode;
import com.shopflow.inventory.domain.repository.ProductRepository;
import com.shopflow.shared.infrastructure.cache.CacheResult;
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

    private final Cache<String, ProductAvailabilityResponse> localCache;

    public CheckAvailabilityQueryHandler(ProductRepository productRepository, DistributedCacheService cacheService,
                                         Cache<String, ProductAvailabilityResponse> localCache) {
        this.productRepository = productRepository;
        this.cacheService = cacheService;
        this.localCache = localCache;
    }

    @Transactional(readOnly = true)
    public ProductAvailabilityResponse handle(CheckAvailabilityQuery query) {
        String cacheKey = "inventory-availability::" + query.productId();
        ProductAvailabilityResponse localRes = localCache.getIfPresent(cacheKey);
        // l1 hit
        if (localRes != null) {
            log.info("L1 CACHE HIT: {}", query.productId());
            if (localRes.isNotFound()) {
                throw new InventoryDomainException(InventoryErrorCode.PRODUCT_NOT_FOUND);
            }
            return localRes;
        }
        // l1 miss => l2
        String lockKey = "lock:inventory-availability::" + query.productId();
        ProductAvailabilityResponse response = cacheService.getWithDoubleCheckLock(cacheKey, lockKey, () -> {
            log.debug("L2 MISS => Get from Database find product ID: {}", query.productId());
            return productRepository.findById(query.productId())
                                    .map(p -> CacheResult.ofRealData(new ProductAvailabilityResponse(p.getId(),
                                                                                                     p.getName(),
                                                                                                     p.getAvailableQuantity(),
                                                                                                     p.getAvailableQuantity() > 0,
                                                                                                     false), 60L))
                                    .orElseGet(() -> CacheResult.ofNullObject(ProductAvailabilityResponse.notFound(query.productId()),
                                                                              1L));
        });
        localCache.put(cacheKey, response);
        if (response.isNotFound()) {
            throw new InventoryDomainException(InventoryErrorCode.PRODUCT_NOT_FOUND);
        }
        return response;
    }

}
