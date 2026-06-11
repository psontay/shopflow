package com.shopflow.order.infrastructure.persistence;

import com.shopflow.order.domain.models.Order;
import com.shopflow.order.domain.models.OrderItem;
import com.shopflow.order.infrastructure.persistence.entity.OrderEntity;
import com.shopflow.order.infrastructure.persistence.entity.OrderItemEntity;
import com.shopflow.shared.domain.Money;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderPersistenceMapper {

    public OrderEntity toEntity(Order order) {
        List<OrderItemEntity> itemEntities = order.getOrderItem()
                                                  .stream()
                                                  .map(item -> OrderItemEntity.builder()
                                                                              .id(item.getId())
                                                                              .productId(item.getProductId())
                                                                              .unitPriceAmount(item.getUnitPrice()
                                                                                                   .amount())
                                                                              .unitPriceCurrency(item.getUnitPrice()
                                                                                                     .currency()
                                                                                                     .getCurrencyCode())
                                                                              .quantity(item.getQuantity())
                                                                              .build())
                                                  .collect(Collectors.toList());
        return OrderEntity.builder()
                          .id(order.getId())
                          .customerId(order.getCustomerId())
                          .orderStatus(order.getOrderStatus())
                          .shippingAddress(order.getShippingAddress())
                          .paymentType(order.getPaymentType())
                          .paymentStatus(order.getPaymentStatus())
                          .discountMultiplier(order.getDiscountMultiplier())
                          .items(itemEntities)
                          .createdAt(order.getCreatedAt())
                          .updatedAt(order.getUpdatedAt())
                          .build();
    }

    public Order toDomain(OrderEntity entity) {
        Order order = new Order(entity.getId(), entity.getCustomerId(), entity.getShippingAddress());
        entity.getItems()
              .forEach(itemEntity -> {
                  OrderItem item = new OrderItem(
                          itemEntity.getId(),
                          itemEntity.getProductId(),
                          itemEntity.getQuantity(),
                          new Money(itemEntity.getUnitPriceAmount(),
                                    java.util.Currency.getInstance(itemEntity.getUnitPriceCurrency()))
                  );
                  order.addItem(item);
              });

        return order;
    }

}
