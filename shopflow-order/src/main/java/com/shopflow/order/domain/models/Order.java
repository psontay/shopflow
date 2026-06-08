package com.shopflow.order.domain.models;

import com.shopflow.shared.domain.BaseEntity;

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

    private String customerId;
    private OrderStatus orderStatus;

    private final List<OrderItem> orderItem;
    private String shippingAddress;
    private PaymentType paymentType;
    private PaymentStatus paymentStatus;

    //must use Value Object Money

    //Invariants Constructor
    public Order(UUID orderId, String customerId, String shippingAddress) {
        super(orderId);
        if (this.getId() == null || customerId == null) {
            throw new IllegalArgumentException("OrderId and CustomerId cannot be null");
        }
        this.customerId = customerId;
        this.shippingAddress = shippingAddress;

        this.orderStatus = OrderStatus.PENDING;
        this.orderItem = new ArrayList<>();
    }

    public List<OrderItem> getOrderItem() {
        return Collections.unmodifiableList(orderItem);
    }

    public void addItem(OrderItem item) {
        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null");
        }
        if (this.orderStatus != OrderStatus.PENDING) {
            throw new IllegalArgumentException("Cannot add an item to this order. Order status is " + this.orderStatus);
        }
        if (this.getOrderItem().contains(item)) {
            item.updateQuantity(item.getQuantity() + 1);
            return;
        }
        this.orderItem.add(item);
    }

}
