package com.shopflow.inventory.infrastructure.persistence.impl;

import com.shopflow.inventory.domain.repository.ProductRepository;
import com.shopflow.inventory.infrastructure.persistence.JpaProductRepository;
import org.springframework.stereotype.Repository;

@Repository
public class ProductRepositoryImpl implements ProductRepository {
    private final JpaProductRepository jpaProductRepository;
    private final
}
