package com.shopflow.inventory.domain.repositories;

import com.shopflow.inventory.domain.models.Product;

import java.util.Optional;
import java.util.UUID;

public interface ProductRepository {

    void save(Product product);

    Optional<Product> findById(UUID id);

}
