package com.shopflow.order.domain.models;

import com.shopflow.order.domain.events.OrderCancelledEvent;
import com.shopflow.order.domain.events.OrderCreatedEvent;
import com.shopflow.order.domain.exceptions.OrderDomainException;
import com.shopflow.order.domain.exceptions.OrderErrorCode;
import com.shopflow.shared.domain.models.Money;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    @Test
    void testCreateOrder_Success() {
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        Order order = new Order(orderId, customerId, "123 Main St");

        assertEquals(orderId, order.getId());
        assertEquals(customerId, order.getCustomerId());
        assertEquals("123 Main St", order.getShippingAddress());
        assertEquals(OrderStatus.PENDING, order.getOrderStatus());
        assertEquals(PaymentStatus.PENDING, order.getPaymentStatus());
        assertTrue(order.getOrderItems().isEmpty());
    }

    @Test
    void testCreateOrder_NullIds_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> 
            new Order(null, UUID.randomUUID(), "Address"));
        assertThrows(IllegalArgumentException.class, () -> 
            new Order(UUID.randomUUID(), null, "Address"));
    }

    @Test
    void testAddItem_Success() {
        Order order = new Order(UUID.randomUUID(), UUID.randomUUID(), "Address");
        OrderItem item = new OrderItem(UUID.randomUUID(), UUID.randomUUID(), 2, Money.of(new BigDecimal("100000")));
        
        order.addItem(item);
        
        assertEquals(1, order.getOrderItems().size());
        assertEquals(2, order.getOrderItems().get(0).getQuantity());
    }

    @Test
    void testAddItem_ExistingProduct_UpdatesQuantity() {
        Order order = new Order(UUID.randomUUID(), UUID.randomUUID(), "Address");
        UUID productId = UUID.randomUUID();
        OrderItem item1 = new OrderItem(UUID.randomUUID(), productId, 2, Money.of(new BigDecimal("100000")));
        OrderItem item2 = new OrderItem(UUID.randomUUID(), productId, 3, Money.of(new BigDecimal("100000")));
        
        order.addItem(item1);
        order.addItem(item2); // Should merge with item1
        
        assertEquals(1, order.getOrderItems().size());
        assertEquals(5, order.getOrderItems().get(0).getQuantity());
    }

    @Test
    void testAddItem_InvalidQuantity_ThrowsException() {
        Order order = new Order(UUID.randomUUID(), UUID.randomUUID(), "Address");
        OrderItem item = new OrderItem(UUID.randomUUID(), UUID.randomUUID(), 100, Money.of(new BigDecimal("100000")));
        
        OrderDomainException ex = assertThrows(OrderDomainException.class, () -> order.addItem(item));
        assertEquals(OrderErrorCode.INSUFFICIENT_STOCK, ex.getErrorCode());
    }

    @Test
    void testRemoveItem_Success() {
        Order order = new Order(UUID.randomUUID(), UUID.randomUUID(), "Address");
        UUID productId = UUID.randomUUID();
        OrderItem item = new OrderItem(UUID.randomUUID(), productId, 2, Money.of(new BigDecimal("100000")));
        
        order.addItem(item);
        assertEquals(1, order.getOrderItems().size());
        
        order.removeItem(productId);
        assertTrue(order.getOrderItems().isEmpty());
    }

    @Test
    void testMarkAsPaid_Success() {
        Order order = new Order(UUID.randomUUID(), UUID.randomUUID(), "Address");
        order.markAsPaid(PaymentMethod.CREDIT_CARD);

        assertEquals(PaymentStatus.PAID, order.getPaymentStatus());
        assertEquals(OrderStatus.PENDING_PAYMENT, order.getOrderStatus()); // Notice OrderStatus changes to PENDING_PAYMENT
        assertEquals(PaymentMethod.CREDIT_CARD, order.getPaymentType());
    }

    @Test
    void testMarkAsPaid_AlreadyPaid_ThrowsException() {
        Order order = new Order(UUID.randomUUID(), UUID.randomUUID(), "Address");
        order.markAsPaid(PaymentMethod.CREDIT_CARD);
        
        OrderDomainException ex = assertThrows(OrderDomainException.class, () -> order.markAsPaid(PaymentMethod.PAYPAL));
        assertEquals(OrderErrorCode.INVALID_ORDER_STATE, ex.getErrorCode());
    }

    @Test
    void testSubmit_Success() {
        Order order = new Order(UUID.randomUUID(), UUID.randomUUID(), "Address");
        OrderItem item = new OrderItem(UUID.randomUUID(), UUID.randomUUID(), 2, Money.of(new BigDecimal("100000")));
        order.addItem(item);
        
        order.submit();
        
        assertEquals(1, order.getDomainEvents().size());
        assertTrue(order.getDomainEvents().get(0) instanceof OrderCreatedEvent);
    }

    @Test
    void testSubmit_EmptyOrder_ThrowsException() {
        Order order = new Order(UUID.randomUUID(), UUID.randomUUID(), "Address");
        
        OrderDomainException ex = assertThrows(OrderDomainException.class, order::submit);
        assertEquals(OrderErrorCode.INVALID_ORDER_STATE, ex.getErrorCode());
    }

    @Test
    void testCancel_Success() {
        Order order = new Order(UUID.randomUUID(), UUID.randomUUID(), "Address");
        order.cancel("Customer requested");

        assertEquals(OrderStatus.CANCELED, order.getOrderStatus());
        assertEquals(1, order.getDomainEvents().size());
        assertTrue(order.getDomainEvents().get(0) instanceof OrderCancelledEvent);
    }

    @Test
    void testCancel_AlreadyCancelled_ThrowsException() {
        Order order = new Order(UUID.randomUUID(), UUID.randomUUID(), "Address");
        order.cancel("First cancel");
        
        OrderDomainException ex = assertThrows(OrderDomainException.class, () -> order.cancel("Second cancel"));
        assertEquals(OrderErrorCode.INVALID_ORDER_STATE, ex.getErrorCode());
    }

    @Test
    void testGetTotalAmount_WithDiscount() {
        Order order = new Order(UUID.randomUUID(), UUID.randomUUID(), "Address");
        // 2 items * 100,000
        OrderItem item1 = new OrderItem(UUID.randomUUID(), UUID.randomUUID(), 2, Money.of(new BigDecimal("100000")));
        // 3 items * 50,000
        OrderItem item2 = new OrderItem(UUID.randomUUID(), UUID.randomUUID(), 3, Money.of(new BigDecimal("50000")));
        
        order.addItem(item1);
        order.addItem(item2);
        
        // Total should be 200,000 + 150,000 = 350,000
        assertEquals(0, new BigDecimal("350000").compareTo(order.getTotalAmount().amount()));
        
        // Apply 10% discount (multiplier 0.9)
        order.applyDiscount(0.9);
        
        // Total should be 350,000 * 0.9 = 315,000
        assertEquals(0, new BigDecimal("315000").compareTo(order.getTotalAmount().amount()));
    }
}
