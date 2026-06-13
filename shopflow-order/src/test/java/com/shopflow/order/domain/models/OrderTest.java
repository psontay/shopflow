package com.shopflow.order.domain.models;

import com.shopflow.order.domain.exceptions.OrderDomainException;
import com.shopflow.order.domain.exceptions.OrderErrorCode;
import com.shopflow.shared.domain.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderTest {

    @Test
    @DisplayName("Add item when valid item and status is pending should success")
    void addItem_WhenValidItemAndStatusPending_ShouldSuccess() {
        UUID customerId = UUID.randomUUID();
        Order order = new Order(UUID.randomUUID(), customerId, "123 Street");

        OrderItem newItem = new OrderItem(
                UUID.randomUUID(),
                UUID.randomUUID(),
                2,
                Money.of(BigDecimal.valueOf(100000))
        );

        order.addItem(newItem);

        assertEquals(1,
                     order.getOrderItems()
                          .size(),
                     "Order list size must equal to 1");
        assertEquals(2,
                     order.getOrderItems()
                          .getFirst()
                          .getQuantity(),
                     "Quantity of first item must 2");
    }

    @Test
    @DisplayName("Add item with quantity > 99 must throw out INSUFFICIENT_STOCK")
    void addItem_WhenQuantityGreaterThan99_ShouldThrowException() {
        Order order = new Order(UUID.randomUUID(), UUID.randomUUID(), "123 Street");
        OrderItem tooManyItem = new OrderItem(
                UUID.randomUUID(),
                UUID.randomUUID(),
                100,
                Money.of(BigDecimal.valueOf(100000))
        );

        OrderDomainException exception = assertThrows(OrderDomainException.class, () -> {
            order.addItem(tooManyItem);
        });

        assertEquals(OrderErrorCode.INSUFFICIENT_STOCK, exception.getErrorCode());
    }

    @Test
    @DisplayName("Cancel pending order should change to canceled")
    void cancel_WhenStatusIsPending_ShouldChangeToCanceled() {
        Order order = new Order(UUID.randomUUID(), UUID.randomUUID(), "123 Street");
        assertEquals(OrderStatus.PENDING, order.getOrderStatus());

        order.cancel();

        assertEquals(OrderStatus.CANCELED, order.getOrderStatus(), "Order status must CANCELED");
    }

}