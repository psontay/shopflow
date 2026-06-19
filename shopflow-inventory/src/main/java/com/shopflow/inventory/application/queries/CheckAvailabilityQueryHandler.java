package com.shopflow.inventory.application.queries;

import com.shopflow.inventory.domain.exceptions.InventoryDomainException;
import com.shopflow.inventory.domain.exceptions.InventoryErrorCode;
import com.shopflow.inventory.domain.models.Product;
import com.shopflow.inventory.domain.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CheckAvailabilityQueryHandler {

    private static final Logger log = LoggerFactory.getLogger(CheckAvailabilityQueryHandler.class);
    private final ProductRepository productRepository;

    public CheckAvailabilityQueryHandler(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Cacheable(value = "inventory-availability",
            key = "#query.productId()")
    @Transactional(readOnly = true)
    public ProductAvailabilityResponse handle(CheckAvailabilityQuery query) {
        log.debug("Checking stock quantity for Product ID: {}", query.productId());
        Product product = productRepository.findById(query.productId())
                                           .orElseThrow(() -> new InventoryDomainException(InventoryErrorCode.PRODUCT_NOT_FOUND));
        return new ProductAvailabilityResponse(
                product.getId(),
                product.getName(),
                product.getAvailableQuantity(),
                product.getAvailableQuantity() > 0
        );
    }

}
