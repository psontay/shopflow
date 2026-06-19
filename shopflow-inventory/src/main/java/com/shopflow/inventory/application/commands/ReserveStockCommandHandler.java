package com.shopflow.inventory.application.commands;

import com.shopflow.inventory.domain.exceptions.InventoryDomainException;
import com.shopflow.inventory.domain.exceptions.InventoryErrorCode;
import com.shopflow.inventory.domain.models.Product;
import com.shopflow.inventory.domain.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReserveStockCommandHandler {

    private static final Logger log = LoggerFactory.getLogger(ReserveStockCommandHandler.class);

    private final ProductRepository productRepository;

    public ReserveStockCommandHandler(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    @CacheEvict(value = "inventory-availability",
            key = "#p0.productId()")
    public void handle(ReserveStockCommand command) {
        log.info("Reserve stock processing. ProductID: {}, Quantity: {}", command.productId(), command.quantity());
        Product product = productRepository.findById(command.productId())
                                           .orElseThrow(() -> {
                                               log.warn("Cannot found Product ID: {}", command.productId());
                                               return new InventoryDomainException(InventoryErrorCode.PRODUCT_NOT_FOUND);
                                           });

        product.reserveStock(command.quantity());
        productRepository.save(product);
        log.info("Reserve stock successfully. ProductID: {}", command.productId());
    }

}
