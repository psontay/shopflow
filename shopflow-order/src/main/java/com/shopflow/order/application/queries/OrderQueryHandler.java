package com.shopflow.order.application.queries;

import com.shopflow.order.infrastructure.persistence.entity.OrderEntity;
import com.shopflow.order.infrastructure.persistence.repository.JpaOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class OrderQueryHandler {

    private final JpaOrderRepository jpaOrderRepository;

    public OrderQueryHandler(JpaOrderRepository jpaOrderRepository) {
        this.jpaOrderRepository = jpaOrderRepository;
    }

    public List<OrderSummaryResponse> getCustomerOrders(UUID customerId) {
        List<OrderEntity> orders = jpaOrderRepository.findByCustomerId(customerId);
        return orders.stream()
                     .map(this :: mapToSummaryResponse)
                     .collect(Collectors.toList());
    }

    private OrderSummaryResponse mapToSummaryResponse(OrderEntity orderEntity) {
        BigDecimal totalAmount =
                orderEntity.getItems()
                           .stream()
                           .map(item -> item.getUnitPriceAmount()
                                            .multiply(BigDecimal.valueOf(item.getQuantity())))
                           .reduce(BigDecimal.ZERO, BigDecimal :: add);
        String currency = orderEntity.getItems()
                                     .isEmpty() ? "VND" :
                orderEntity.getItems()
                           .getFirst()
                           .getUnitPriceCurrency();
        return new OrderSummaryResponse(
                orderEntity.getId(),
                orderEntity.getOrderStatus(),
                totalAmount,
                currency,
                orderEntity.getCreatedAt()
        );
    }

}
