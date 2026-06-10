package com.shopflow.order.infrastructure.persistence;

import com.shopflow.order.domain.models.Order;
import com.shopflow.order.domain.repositories.OrderRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class OrderRepositoryImpl implements OrderRepository {

    @Override
    public void save(Order order) {
        System.out.println("Save to database by Hibernate");
    }

    @Override
    public Optional<Order> findById(UUID id) {
        return Optional.empty();
    }

    @Override
    public void deleteById(UUID orderId) {
        System.out.println("Delete order");
    }

}
