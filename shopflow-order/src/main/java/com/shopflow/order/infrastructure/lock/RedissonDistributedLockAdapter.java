package com.shopflow.order.infrastructure.lock;

import com.shopflow.order.application.ports.DistributedLockPort;
import com.shopflow.shared.domain.SystemDomainException;
import com.shopflow.shared.domain.SystemErrorCode;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedissonDistributedLockAdapter implements DistributedLockPort {

    private final RedissonClient redissonClient;

    public RedissonDistributedLockAdapter(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Override
    public void executeWithLock(String lockKey, Runnable task) {
        RLock lock = redissonClient.getLock(lockKey);
        try {
            boolean isLocked = lock.tryLock(5, 10, TimeUnit.SECONDS);
            if (isLocked) {
                try {
                    task.run();
                } finally {
                    lock.unlock();
                }
            } else {
                throw new SystemDomainException(SystemErrorCode.SYSTEM_BUSY);
            }
        } catch (InterruptedException e) {
            Thread.currentThread()
                  .interrupt();
            throw new RuntimeException("get lock failed", e);
        }

    }

}
