package com.shopflow.inventory.domain.models;

import com.shopflow.inventory.domain.exceptions.InventoryDomainException;
import com.shopflow.inventory.domain.exceptions.InventoryErrorCode;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    @Test
    void testCreateProduct_Success() {
        UUID productId = UUID.randomUUID();
        Product product = new Product(productId, "Laptop", 100);

        assertEquals(productId, product.getId());
        assertEquals("Laptop", product.getName());
        assertEquals(100, product.getAvailableQuantity());
        assertEquals(0, product.getReservedQuantity());
    }

    @Test
    void testCreateProduct_NegativeQuantity_ThrowsException() {
        InventoryDomainException ex = assertThrows(InventoryDomainException.class, 
            () -> new Product(UUID.randomUUID(), "Laptop", -10));
        assertEquals(InventoryErrorCode.INVALID_STOCK_QUANTITY, ex.getErrorCode());
    }

    @Test
    void testReserveStock_Success() {
        Product product = new Product(UUID.randomUUID(), "Laptop", 100);
        product.reserveStock(20);

        assertEquals(80, product.getAvailableQuantity());
        assertEquals(20, product.getReservedQuantity());
    }

    @Test
    void testReserveStock_InvalidQuantity_ThrowsException() {
        Product product = new Product(UUID.randomUUID(), "Laptop", 100);
        
        InventoryDomainException ex = assertThrows(InventoryDomainException.class, () -> product.reserveStock(-5));
        assertEquals(InventoryErrorCode.INVALID_STOCK_QUANTITY, ex.getErrorCode());
        
        ex = assertThrows(InventoryDomainException.class, () -> product.reserveStock(0));
        assertEquals(InventoryErrorCode.INVALID_STOCK_QUANTITY, ex.getErrorCode());
    }

    @Test
    void testReserveStock_InsufficientStock_ThrowsException() {
        Product product = new Product(UUID.randomUUID(), "Laptop", 100);
        
        InventoryDomainException ex = assertThrows(InventoryDomainException.class, () -> product.reserveStock(150));
        assertEquals(InventoryErrorCode.INSUFFICIENT_STOCK, ex.getErrorCode());
    }

    @Test
    void testReleaseStock_Success() {
        Product product = new Product(UUID.randomUUID(), "Laptop", 100);
        product.reserveStock(30); // Available: 70, Reserved: 30
        
        product.releaseStock(10); // Cancel 10

        assertEquals(80, product.getAvailableQuantity());
        assertEquals(20, product.getReservedQuantity());
    }

    @Test
    void testReleaseStock_InvalidOrInsufficient_ThrowsException() {
        Product product = new Product(UUID.randomUUID(), "Laptop", 100);
        product.reserveStock(30);

        // Negative quantity
        InventoryDomainException ex = assertThrows(InventoryDomainException.class, () -> product.releaseStock(-5));
        assertEquals(InventoryErrorCode.INSUFFICIENT_STOCK, ex.getErrorCode());

        // Releasing more than reserved
        ex = assertThrows(InventoryDomainException.class, () -> product.releaseStock(40));
        assertEquals(InventoryErrorCode.INSUFFICIENT_STOCK, ex.getErrorCode());
    }

    @Test
    void testCommitStock_Success() {
        Product product = new Product(UUID.randomUUID(), "Laptop", 100);
        product.reserveStock(30); // Available: 70, Reserved: 30
        
        product.commitStock(25); // Order success for 25 items

        assertEquals(70, product.getAvailableQuantity());
        assertEquals(5, product.getReservedQuantity()); // 5 items still reserved for other orders
    }

    @Test
    void testCommitStock_InvalidOrInsufficient_ThrowsException() {
        Product product = new Product(UUID.randomUUID(), "Laptop", 100);
        product.reserveStock(30);

        // Negative quantity
        InventoryDomainException ex = assertThrows(InventoryDomainException.class, () -> product.commitStock(-5));
        assertEquals(InventoryErrorCode.INSUFFICIENT_STOCK, ex.getErrorCode());

        // Committing more than reserved
        ex = assertThrows(InventoryDomainException.class, () -> product.commitStock(40));
        assertEquals(InventoryErrorCode.INSUFFICIENT_STOCK, ex.getErrorCode());
    }
}
