package com.shopflow.inventory.infrastructure.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.shopflow.inventory.application.queries.ProductAvailabilityResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {

    @Bean
    public Cache<String, ProductAvailabilityResponse> inventoryLocalCache() {
        return Caffeine.newBuilder()
                       .expireAfterWrite(30, TimeUnit.SECONDS)
                       .maximumSize(10_000)
                       .build();
    }

}
