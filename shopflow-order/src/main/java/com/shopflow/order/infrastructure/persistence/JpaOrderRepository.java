package com.shopflow.order.infrastructure.persistence;

import com.shopflow.order.infrastructure.persistence.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public class JpaOrderRepository extends JpaRepository<OrderEntity, UUID> {

}
