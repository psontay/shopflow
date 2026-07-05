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

    @Bean
    public org.springframework.data.redis.listener.RedisMessageListenerContainer redisContainer(
            org.springframework.data.redis.connection.RedisConnectionFactory connectionFactory,
            Cache<String, ProductAvailabilityResponse> localCache) {
        org.springframework.data.redis.listener.RedisMessageListenerContainer container = new org.springframework.data.redis.listener.RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener((message, pattern) -> {
            String cacheKey = new String(message.getBody()).replace("\"", "");
            System.out.println("Redis Listener - Delete L1 Cache for key: " + cacheKey);
            localCache.invalidate(cacheKey);
        }, new org.springframework.data.redis.listener.ChannelTopic("inventory-cache-invalidation"));
        return container;
    }

}
