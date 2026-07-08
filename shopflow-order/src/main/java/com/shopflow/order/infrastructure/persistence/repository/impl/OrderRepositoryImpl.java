package com.shopflow.order.infrastructure.persistence.repository.impl;

import com.shopflow.order.domain.models.Order;
import com.shopflow.order.domain.repositories.OrderRepository;
import com.shopflow.order.infrastructure.persistence.entity.OrderEntity;
import com.shopflow.order.infrastructure.persistence.mapper.OrderPersistenceMapper;
import com.shopflow.order.infrastructure.persistence.repository.JpaOrderRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class OrderRepositoryImpl implements OrderRepository {

    private final JpaOrderRepository jpaOrderRepository;
    private final OrderPersistenceMapper mapper;

    public OrderRepositoryImpl(JpaOrderRepository jpaOrderRepository, OrderPersistenceMapper mapper) {
        this.jpaOrderRepository = jpaOrderRepository;
        this.mapper = mapper;
    }

    @Override
    public void save(Order order) {
        OrderEntity entity = mapper.toEntity(order);
        jpaOrderRepository.save(entity);
    }

    @Override
    public Optional<Order> findById(UUID id) {
        return jpaOrderRepository.findById(id)
                                 .map(mapper :: toDomain);
    }

    @Override
    public void deleteById(UUID orderId) {
        jpaOrderRepository.deleteById(orderId);
    }

}
