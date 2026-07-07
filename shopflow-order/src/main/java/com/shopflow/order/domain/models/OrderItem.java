package com.shopflow.order.domain.models;

import com.shopflow.shared.domain.models.BaseEntity;
import com.shopflow.shared.domain.models.Money;

import java.util.UUID;

public class OrderItem extends BaseEntity {

    private final UUID productId;
    private final Money unitPrice;
    private int quantity;

    public OrderItem(UUID id, UUID productId, int quantity, Money unitPrice) {
        super(id);
        if (productId == null) {
            throw new IllegalArgumentException("ProductId cannot be null or blank");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        if (unitPrice == null) {
            throw new IllegalArgumentException("UnitPrice cannot be null");
        }
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public Money getSubTotal() {
        return Money.of(this.unitPrice.amount()
                                      .multiply(java.math.BigDecimal.valueOf(quantity)));
    }

    public UUID getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public Money getUnitPrice() {
        return unitPrice;
    }

    void updateQuantity(int newQuantity) {
        if (newQuantity <= 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        this.quantity = newQuantity;
        this.markAsUpdated();
    }

}
