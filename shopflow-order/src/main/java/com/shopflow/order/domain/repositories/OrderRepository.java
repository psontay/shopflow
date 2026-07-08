package com.shopflow.order.domain.repositories;

import com.shopflow.order.domain.models.Order;
import com.shopflow.order.domain.models.OrderStatus;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {

    void save(Order order);

    Optional<Order> findById(UUID id);

    void deleteById(UUID orderId);

    List<Order> findByOrderStatusAndCreatedAtBefore(OrderStatus status, Instant timeThreshold);

}
