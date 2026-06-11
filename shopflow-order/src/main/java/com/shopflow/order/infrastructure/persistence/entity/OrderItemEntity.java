package com.shopflow.order.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemEntity {

    @Id
    private UUID id;
    @Column(nullable = false)
    private UUID productId;
    @Column(nullable = false)
    private int quantity;
    @Column(nullable = false,
            precision = 19,
            scale = 4)
    private BigDecimal unitPriceAmount;
    @Column(nullable = false,
            length = 3)
    private String unitPriceCurrency;

}
