package com.shopflow.shared.infrastructure.cache;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Service
public class DistributedCacheService {

    private static final Logger logger = LoggerFactory.getLogger(DistributedCacheService.class);

    private final RedissonClient redissonClient;
    private final RedisTemplate<String, Object> redisTemplate;

    public DistributedCacheService(RedissonClient redissonClient, RedisTemplate<String, Object> redisTemplate) {
        this.redissonClient = redissonClient;
        this.redisTemplate = redisTemplate;
    }

    public <T> T getWithDoubleCheckLock(String cacheKey, String lockKey, Supplier<T> dbQuery, long cacheTtlMinutes) {
        // check cache
        T cachedData = (T) redisTemplate.opsForValue()
                                        .get(cacheKey);
        if (cachedData != null) { // cache hit
            return cachedData;
        }

        // if cache miss, try to get lock
        RLock lock = redissonClient.getLock(lockKey);
        try {
            boolean isLocked = lock.tryLock(3, 15, TimeUnit.SECONDS);

            if (isLocked) {
                try {
                    // check 2 time
                    cachedData = (T) redisTemplate.opsForValue()
                                                  .get(cacheKey);
                    if (cachedData != null) {
                        return cachedData;
                    }
                    // still miss => hit db
                    logger.info("Cache miss for key: {}. Going to Database...", cacheKey);
                    T dbData = dbQuery.get();

                    // set cache
                    if (dbData != null) {
                        redisTemplate.opsForValue()
                                     .set(cacheKey, dbData, cacheTtlMinutes, TimeUnit.MINUTES);
                    }
                    return dbData;

                } finally {
                    // always unlock
                    lock.unlock();
                }
            } else {
                logger.warn("Timeout waiting for lock {}. Double-checking cache for fallback.", lockKey);
                cachedData = (T) redisTemplate.opsForValue()
                                              .get(cacheKey);
                if (cachedData != null) {
                    return cachedData;
                }
                throw new IllegalStateException("System busy, try again later!");
            }
        } catch (InterruptedException e) {
            Thread.currentThread()
                  .interrupt();
            throw new RuntimeException("get lock failed", e);
        }
    }

}
