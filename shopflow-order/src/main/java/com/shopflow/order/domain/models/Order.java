package com.shopflow.order.domain.models;

import com.shopflow.shared.domain.BaseEntity;
import com.shopflow.shared.domain.Money;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Order extends BaseEntity {

    private UUID customerId;
    private OrderStatus orderStatus;

    private final List<OrderItem> orderItems;
    private String shippingAddress;
    private PaymentType paymentType;
    private PaymentStatus paymentStatus;
    private double discountMultiplier = 1.0;

    //Invariants Constructor
    public Order(UUID orderId, UUID customerId, String shippingAddress) {
        super(orderId);
        if (this.getId() == null || customerId == null) {
            throw new IllegalArgumentException("OrderId and CustomerId cannot be null.");
        }
        this.customerId = customerId;
        this.shippingAddress = shippingAddress;

        this.orderStatus = OrderStatus.PENDING;
        this.paymentStatus = PaymentStatus.PENDING;
        this.orderItems = new ArrayList<>();
    }

    public List<OrderItem> getOrderItem() {
        return Collections.unmodifiableList(orderItems);
    }

    public void addItem(OrderItem newItem) {
        if (newItem == null) {
            throw new IllegalArgumentException("Item cannot be null");
        }
        if (this.orderStatus != OrderStatus.PENDING) {
            throw new IllegalArgumentException("Cannot add an item to this order. Order status is " + this.orderStatus);
        }
        if (newItem.getQuantity() > 99) {
            throw new IllegalArgumentException("Maximum of 99 items per product type allowed.");
        }
        for (OrderItem existingItem : this.orderItems) {
            if (existingItem.getProductId().equals(newItem.getProductId())) {
                existingItem.updateQuantity(existingItem.getQuantity() + newItem.getQuantity());
                super.maskAsUpdated();
                return;
            }
        }
        if (this.orderItems.size() >= 50) {
            throw new IllegalStateException("Oversize of order items.");
        }
        this.orderItems.add(newItem);
        super.maskAsUpdated();
    }

    public void changeItemQuantity(UUID productId, int newQuantity) {
        if (this.orderStatus != OrderStatus.PENDING) {
            throw new IllegalStateException("Cannot update order item quantity when order status is processing.");
        }
        if (newQuantity <= 0) {
            throw new IllegalArgumentException("new quantity cannot be negative.");
        }
        for (OrderItem existingItem : this.orderItems) {
            if (existingItem.getProductId().equals(productId)) {
                existingItem.updateQuantity(existingItem.getQuantity() + newQuantity);
                super.maskAsUpdated();
                return;
            }
        }
        throw new IllegalArgumentException("Cannot find product in order");
    }

    public void removeItem(UUID productId) {
        if (this.orderStatus != OrderStatus.PENDING) {
            throw new IllegalStateException("Cart can only be modified when the order is pending.");
        }
        boolean removed = this.orderItems.removeIf(item -> item.getProductId().equals(productId));
        if (removed) {
            super.maskAsUpdated();
        }
    }

    public void markAsPaid(PaymentType type) {
        if (this.orderStatus == OrderStatus.CANCELED) {
            throw new IllegalStateException("Cannot checkout for canceled order!");
        }
        if (this.paymentStatus == PaymentStatus.SUCCESS) {
            throw new IllegalStateException("This order has already been paid.");
        }
        this.paymentType = type;
        this.paymentStatus = PaymentStatus.SUCCESS;
        this.orderStatus = OrderStatus.PENDING_PAYMENT;
        super.maskAsUpdated();
    }

    public void cancel() {
        boolean canNotCancel = (this.orderStatus == OrderStatus.CANCELED || this.orderStatus == OrderStatus.SHIPPED || this.orderStatus == OrderStatus.SUCCESS);
        if (canNotCancel) {
            throw new IllegalStateException("Cannot cancel the order in this status" + this.orderStatus);
        }
        this.orderStatus = OrderStatus.CANCELED;
        super.maskAsUpdated();
    }

    public Money getTotalAmount() {
        return this.orderItems.stream().map(OrderItem :: getSubTotal).reduce(Money.zero(), Money :: add).multiply(this.discountMultiplier);
    }

    public void applyDiscount(double multiplier) {
        if (multiplier <= 0 || multiplier > 1) {
            throw new IllegalArgumentException("Invalid multiplier discount");
        }
        this.discountMultiplier = multiplier;
        super.maskAsUpdated();
    }

    public long countWholesaleItems() {
        return this.orderItems.stream().filter(item -> item.getQuantity() >= 3).count();
    }

}
