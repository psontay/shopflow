package com.shopflow.order.domain.models;

import com.shopflow.shared.domain.BaseEntity;
import com.shopflow.shared.domain.Money;

import java.util.UUID;

public class OrderItem extends BaseEntity {

    private final UUID productId;
    private int quantity;
    private final Money unitPrice;

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
        return Money.of(this.unitPrice.amount().multiply(java.math.BigDecimal.valueOf(quantity)));
    }

    void updateQuantity(int newQuantity) {
        if (newQuantity <= 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        this.quantity = newQuantity;
        this.maskAsUpdated();
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

}
