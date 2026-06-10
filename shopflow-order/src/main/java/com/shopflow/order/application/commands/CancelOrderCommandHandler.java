package com.shopflow.order.application.commands;

import com.shopflow.order.domain.models.Order;
import com.shopflow.order.domain.repositories.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CancelOrderCommandHandler {

    private final OrderRepository orderRepository;

    public CancelOrderCommandHandler(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional
    public void handle(CancelOrderCommand command) {
        Optional<Order> order = orderRepository.findById(command.orderId());
        orderRepository.deleteById(order.get().getId());
    }

}
