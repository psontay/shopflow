package com.shopflow.order.domain.models;

import com.shopflow.shared.domain.BaseEntity;
import com.shopflow.shared.domain.Money;

import java.util.UUID;

public class OrderItem extends BaseEntity {

    private final String productId;
    private final String productName;
    private int quantity;
    private final Money unitPrice;

    public OrderItem(UUID id, String productId, String productName, int quantity, Money unitPrice) {
        super(id);
        if (productId == null || productId.isBlank()) {
            throw new IllegalArgumentException("ProductId cannot be null or blank");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        if (unitPrice == null) {
            throw new IllegalArgumentException("UnitPrice cannot be null");
        }
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public Money getSubTotal() {
        return null;
    }

    public void updateQuantity(int newQuantity) {
        if (newQuantity <= 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        this.quantity = newQuantity;
        this.maskAsUpdated();
    }

    public String getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public Money getUnitPrice() {
        return unitPrice;
    }

}
