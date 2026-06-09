package com.shopflow.order.domain.models;

import com.shopflow.shared.domain.BaseEntity;
import com.shopflow.shared.domain.Money;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

enum OrderStatus {
    PENDING,
    PENDING_PAYMENT,
    SHIPPED,
    CANCELED,
    SUCCESS,
}

enum PaymentStatus {
    PENDING,
    SUCCESS,
}

enum PaymentType {
    CREDIT_CARD,
    PAYPAL,
    CASH,
}

public class Order extends BaseEntity {

    private UUID customerId;
    private OrderStatus orderStatus;

    private final List<OrderItem> orderItems;
    private String shippingAddress;
    private PaymentType paymentType;
    private PaymentStatus paymentStatus;

    //must use Value Object Money

    //Invariants Constructor
    public Order(UUID orderId, UUID customerId, String shippingAddress) {
        super(orderId);
        if (this.getId() == null || customerId == null) {
            throw new IllegalArgumentException("OrderId and CustomerId cannot be null");
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
        for (OrderItem existingItem : this.orderItems) {
            if (existingItem.getProductId().equals(newItem.getProductId())) {
                existingItem.updateQuantity(existingItem.getQuantity() + newItem.getQuantity());
                super.maskAsUpdated();
                return;
            }
        }
        this.orderItems.add(newItem);
        super.maskAsUpdated();
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

    public Money getTotalAmount() {
        Money total = Money.zero();
        for (OrderItem orderItem : this.orderItems) {
            total = total.add(orderItem.getSubTotal());
        }
        return total;
    }

}
