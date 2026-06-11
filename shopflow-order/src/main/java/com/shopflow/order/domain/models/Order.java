package com.shopflow.order.domain.models;

import com.shopflow.order.domain.events.OrderCreatedEvent;
import com.shopflow.order.domain.exceptions.OrderDomainException;
import com.shopflow.order.domain.exceptions.OrderErrorCode;
import com.shopflow.shared.domain.BaseEntity;
import com.shopflow.shared.domain.Money;

import java.time.Instant;
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
        this.registerEvent(new OrderCreatedEvent(orderId));
        this.orderItems = new ArrayList<>();
    }

    Order(UUID id, UUID customerId, OrderStatus orderStatus, String shippingAddress, PaymentType paymentType,
          PaymentStatus paymentStatus, double discountMultiplier, Instant createdAt, Instant updatedAt){
        super(id, createdAt, updatedAt);
        this.customerId = customerId;
        this.orderStatus = orderStatus;
        this.shippingAddress = shippingAddress;
        this.paymentType = paymentType;
        this.paymentStatus = paymentStatus;
        this.discountMultiplier = discountMultiplier;
        this.orderItems = new ArrayList<>();
    }

    public List<OrderItem> getOrderItem() {
        return Collections.unmodifiableList(orderItems);
    }

    public void addItem(OrderItem newItem) {
        if (newItem == null) {
            throw new OrderDomainException(OrderErrorCode.INSUFFICIENT_STOCK);
        }
        if (this.orderStatus != OrderStatus.PENDING) {
            throw new OrderDomainException(OrderErrorCode.INVALID_ORDER_STATE);
        }
        if (newItem.getQuantity() > 99) {
            throw new OrderDomainException(OrderErrorCode.INSUFFICIENT_STOCK);
        }
        for (OrderItem existingItem : this.orderItems) {
            if (existingItem.getProductId()
                            .equals(newItem.getProductId())) {
                existingItem.updateQuantity(existingItem.getQuantity() + newItem.getQuantity());
                super.maskAsUpdated();
                return;
            }
        }
        if (this.orderItems.size() >= 50) {
            throw new OrderDomainException(OrderErrorCode.INSUFFICIENT_STOCK);
        }
        this.orderItems.add(newItem);
        super.maskAsUpdated();
    }

    public void changeItemQuantity(UUID productId, int newQuantity) {
        if (this.orderStatus != OrderStatus.PENDING) {
            throw new OrderDomainException(OrderErrorCode.INVALID_ORDER_STATE);
        }
        if (newQuantity <= 0) {
            throw new IllegalArgumentException("new quantity cannot be negative.");
        }
        for (OrderItem existingItem : this.orderItems) {
            if (existingItem.getProductId()
                            .equals(productId)) {
                existingItem.updateQuantity(existingItem.getQuantity() + newQuantity);
                super.maskAsUpdated();
                return;
            }
        }
        throw new IllegalArgumentException("Cannot find product in order");
    }

    public void removeItem(UUID productId) {
        if (this.orderStatus != OrderStatus.PENDING) {
            throw new OrderDomainException(OrderErrorCode.INVALID_ORDER_STATE);
        }
        boolean removed = this.orderItems.removeIf(item -> item.getProductId()
                                                               .equals(productId));
        if (removed) {
            super.maskAsUpdated();
        }
    }

    public void markAsPaid(PaymentType type) {
        if (this.orderStatus == OrderStatus.CANCELED) {
            throw new OrderDomainException(OrderErrorCode.INVALID_ORDER_STATE);
        }
        if (this.paymentStatus == PaymentStatus.SUCCESS) {
            throw new OrderDomainException(OrderErrorCode.INVALID_ORDER_STATE);
        }
        this.paymentType = type;
        this.paymentStatus = PaymentStatus.SUCCESS;
        this.orderStatus = OrderStatus.PENDING_PAYMENT;
        super.maskAsUpdated();
    }

    public void cancel() {
        boolean canNotCancel = (this.orderStatus == OrderStatus.CANCELED || this.orderStatus == OrderStatus.SHIPPED || this.orderStatus == OrderStatus.SUCCESS);
        if (canNotCancel) {
            throw new OrderDomainException(OrderErrorCode.INVALID_ORDER_STATE);
        }
        this.orderStatus = OrderStatus.CANCELED;
        super.maskAsUpdated();
    }

    public Money getTotalAmount() {
        return this.orderItems.stream()
                              .map(OrderItem :: getSubTotal)
                              .reduce(Money.zero(), Money :: add)
                              .multiply(this.discountMultiplier);
    }

    public void applyDiscount(double multiplier) {
        if (multiplier <= 0 || multiplier > 1) {
            throw new IllegalArgumentException("Invalid multiplier discount");
        }
        this.discountMultiplier = multiplier;
        super.maskAsUpdated();
    }

    public long countWholesaleItems() {
        return this.orderItems.stream()
                              .filter(item -> item.getQuantity() >= 3)
                              .count();
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public double getDiscountMultiplier() {
        return discountMultiplier;
    }

}
