package com.shopflow.shared.infrastructure.cache;

public record CacheResult<T>(T data, long ttl) {

    public static <T> CacheResult<T> ofRealData(T data, long ttl) {
        return new CacheResult<>(data, ttl);
    }

    public static <T> CacheResult<T> ofNullObject(T data, long ttl) {
        return new CacheResult<>(data, ttl);
    }

}
