package com.shopflow.order.domain.models;

import com.shopflow.order.domain.events.OrderCancelledEvent;
import com.shopflow.order.domain.events.OrderCreatedEvent;
import com.shopflow.order.domain.events.OrderItemSnapshot;
import com.shopflow.order.domain.exceptions.OrderDomainException;
import com.shopflow.order.domain.exceptions.OrderErrorCode;
import com.shopflow.shared.domain.models.BaseEntity;
import com.shopflow.shared.domain.models.Money;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Order extends BaseEntity {

    private final List<OrderItem> orderItems;
    private UUID customerId;
    private OrderStatus orderStatus;
    private String shippingAddress;
    private PaymentMethod paymentMethod;
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

    private Order(UUID orderId, UUID customerId, OrderStatus orderStatus, String shippingAddress,
                  PaymentMethod paymentMethod,
                  PaymentStatus paymentStatus, double discountMultiplier, Instant createdAt, Instant updatedAt) {
        super(orderId, createdAt, updatedAt);
        this.customerId = customerId;
        this.orderStatus = orderStatus;
        this.shippingAddress = shippingAddress;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.discountMultiplier = discountMultiplier;
        this.orderItems = new ArrayList<>();
    }

    public static Order reconstruct(UUID id, UUID customerId, OrderStatus orderStatus,
                                    String shippingAddress, PaymentMethod paymentMethod,
                                    PaymentStatus paymentStatus, double discountMultiplier,
                                    Instant createdAt, Instant updatedAt) {
        return new Order(id, customerId, orderStatus, shippingAddress,
                         paymentMethod, paymentStatus, discountMultiplier,
                         createdAt, updatedAt);
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
                super.markAsUpdated();
                return;
            }
        }
        if (this.orderItems.size() >= 50) {
            throw new OrderDomainException(OrderErrorCode.INSUFFICIENT_STOCK);
        }
        this.orderItems.add(newItem);
        super.markAsUpdated();
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
                super.markAsUpdated();
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
            super.markAsUpdated();
        }
    }

    public void markAsPaid(PaymentMethod type) {
        if (this.orderStatus == OrderStatus.CANCELED) {
            throw new OrderDomainException(OrderErrorCode.INVALID_ORDER_STATE);
        }
        if (this.paymentStatus == PaymentStatus.SUCCESS) {
            throw new OrderDomainException(OrderErrorCode.INVALID_ORDER_STATE);
        }
        this.paymentMethod = type;
        this.paymentStatus = PaymentStatus.SUCCESS;
        this.orderStatus = OrderStatus.PENDING_PAYMENT;
        super.markAsUpdated();
    }

    public void submit() {
        if (this.orderItems.isEmpty()) {
            throw new OrderDomainException(OrderErrorCode.INVALID_ORDER_STATE);
        }
        List<OrderItemSnapshot> itemSnapshots =
                orderItems.stream()
                          .map(item -> new OrderItemSnapshot(item.getProductId(), item.getQuantity()))
                          .toList();
        this.registerEvent(new OrderCreatedEvent(this.getId(), itemSnapshots));
        super.markAsUpdated();
    }

    public void cancel(String reason) {
        boolean canNotCancel = (this.orderStatus == OrderStatus.CANCELED || this.orderStatus == OrderStatus.SHIPPED || this.orderStatus == OrderStatus.SUCCESS);
        if (canNotCancel) {
            throw new OrderDomainException(OrderErrorCode.INVALID_ORDER_STATE);
        }
        this.orderStatus = OrderStatus.CANCELED;
        super.markAsUpdated();
        List<OrderItemSnapshot> itemSnapshots =
                orderItems.stream()
                          .map(item -> new OrderItemSnapshot(item.getProductId(), item.getQuantity()))
                          .toList();
        this.registerEvent(new OrderCancelledEvent(this.getId(), reason, itemSnapshots));
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
        super.markAsUpdated();
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

    public PaymentMethod getPaymentType() {
        return paymentMethod;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public double getDiscountMultiplier() {
        return discountMultiplier;
    }

}
