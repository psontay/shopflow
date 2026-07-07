package com.shopflow.inventory.application.queries;

import com.shopflow.inventory.domain.models.Product;
import com.shopflow.inventory.domain.repositories.ProductRepository;
import com.shopflow.shared.infrastructure.cache.DistributedCacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

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
    public boolean handle(CheckAvailabilityQuery query) {
        String cacheKey = "inventory-stock::" + query.productId();
        Integer cachedQuantity = cacheService.get(cacheKey, Integer.class);
        if (cachedQuantity != null) {
            if (cachedQuantity == - 1) {
                return false;
            }

            return cachedQuantity >= query.quantity();
        }
        Product product = productRepository.findById(query.productId())
                                           .orElse(null);

        if (product == null) {
            cacheService.set(cacheKey, - 1, 5, TimeUnit.MINUTES);
            return false;
        }

        cacheService.set(cacheKey, product.getAvailableQuantity(), 1, TimeUnit.HOURS);

        return product.getAvailableQuantity() >= query.quantity();
    }

}
