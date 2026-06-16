package com.shopflow.inventory.infrastructure.persistence.mapper;

import com.shopflow.inventory.domain.models.Product;
import com.shopflow.inventory.infrastructure.entity.ProductEntity;
import org.springframework.stereotype.Component;

@Component
public class ProductPersistenceMapper {

    public ProductEntity toEntity(Product product) {
        return ProductEntity.builder()
                            .id(product.getId())
                            .name(product.getName())
                            .reservedQuantity(product.getReservedQuantity())
                            .availableQuantity(
                                    product.getAvailableQuantity())
                            .createdAt(product.getCreatedAt())
                            .updatedAt(product.getUpdatedAt())
                            .build();
    }

    public Product toDomain(ProductEntity entity) {
        Product product = Product.reconstruct(
                entity.getId(),
                entity.getName(),
                entity.getAvailableQuantity(),
                entity.getReservedQuantity(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
                                             );
        product.clearDomainEvents();
        return product;
    }

}
