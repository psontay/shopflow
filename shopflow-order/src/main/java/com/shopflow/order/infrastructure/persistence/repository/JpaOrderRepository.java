package com.shopflow.order.infrastructure.persistence.repository;

import com.shopflow.order.domain.models.OrderStatus;
import com.shopflow.order.infrastructure.persistence.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface JpaOrderRepository extends JpaRepository<OrderEntity, UUID> {

    List<OrderEntity> findByCustomerId(UUID customerId);

    List<OrderEntity> findByOrderStatusAndCreatedAtBefore(OrderStatus status, Instant timeThreshold);

}
