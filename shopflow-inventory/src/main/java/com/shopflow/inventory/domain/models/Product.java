package com.shopflow.inventory.domain.models;

import com.shopflow.inventory.domain.exceptions.InventoryDomainException;
import com.shopflow.inventory.domain.exceptions.InventoryErrorCode;
import com.shopflow.shared.domain.BaseEntity;

import java.time.Instant;
import java.util.UUID;

public class Product extends BaseEntity {

    private String name;
    private int availableQuantity;
    private int reservedQuantity;

    public Product(UUID productId, String name, int initialQuantity) {
        super(productId);
        if (initialQuantity < 0) {
            throw new InventoryDomainException(InventoryErrorCode.INVALID_STOCK_QUANTITY);
        }
        this.name = name;
        this.availableQuantity = initialQuantity;
        this.reservedQuantity = 0;
    }

    private Product(UUID id, String name, int availableQuantity, int reservedQuantity, Instant createdAt,
                    Instant updatedAt) {
        super(id, createdAt, updatedAt);
        this.name = name;
        this.availableQuantity = availableQuantity;
        this.reservedQuantity = reservedQuantity;
    }

    public static Product reconstruct(UUID id, String name, int availableQuantity, int reservedQuantity,
                                      Instant createdAt, Instant updatedAt) {
        return new Product(id, name, availableQuantity, reservedQuantity, createdAt, updatedAt);
    }

    // reverse stock when user book
    public void reserveStock(int quantity) {
        if (quantity <= 0) {
            throw new InventoryDomainException(InventoryErrorCode.INVALID_STOCK_QUANTITY);
        }
        if (this.availableQuantity < quantity) {
            throw new InventoryDomainException(InventoryErrorCode.INSUFFICIENT_STOCK);
        }

        this.availableQuantity -= quantity;
        this.reservedQuantity += quantity;
        super.maskAsUpdated();
    }

    // release when user cancel order
    public void releaseStock(int quantity) {
        if (quantity <= 0 || this.reservedQuantity < quantity) {
            throw new InventoryDomainException(InventoryErrorCode.INSUFFICIENT_STOCK);
        }
        this.availableQuantity += quantity;
        this.reservedQuantity -= quantity;
        super.maskAsUpdated();
    }

    // commit stock when order status == success
    public void commitStock(int quantity) {
        if (quantity <= 0 || this.reservedQuantity < quantity) {
            throw new InventoryDomainException(InventoryErrorCode.INSUFFICIENT_STOCK);
        }
        this.reservedQuantity -= quantity;
        super.maskAsUpdated();
    }

    public String getName() {
        return name;
    }

    public int getAvailableQuantity() {
        return availableQuantity;
    }

    public int getReservedQuantity() {
        return reservedQuantity;
    }

}
