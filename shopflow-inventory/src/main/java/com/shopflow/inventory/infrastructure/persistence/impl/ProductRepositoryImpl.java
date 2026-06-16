package com.shopflow.inventory.infrastructure.persistence.impl;

import com.shopflow.inventory.domain.models.Product;
import com.shopflow.inventory.domain.repository.ProductRepository;
import com.shopflow.inventory.infrastructure.persistence.JpaProductRepository;
import com.shopflow.inventory.infrastructure.persistence.entity.ProductEntity;
import com.shopflow.inventory.infrastructure.persistence.mapper.ProductPersistenceMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class ProductRepositoryImpl implements ProductRepository {

    private static final Logger log = LoggerFactory.getLogger(ProductRepositoryImpl.class);
    private final JpaProductRepository jpaProductRepository;
    private final ProductPersistenceMapper mapper;

    public ProductRepositoryImpl(JpaProductRepository jpaProductRepository, ProductPersistenceMapper mapper) {
        this.jpaProductRepository = jpaProductRepository;
        this.mapper = mapper;
    }

    @Override
    public void save(Product product) {
        ProductEntity entity = mapper.toEntity(product);
        jpaProductRepository.save(entity);
        log.debug("Save product id: {} success.", product.getId());
    }

    @Override
    public Optional<Product> findById(UUID id) {
        return jpaProductRepository.findById(id)
                                   .map(mapper :: toDomain);
    }

}
