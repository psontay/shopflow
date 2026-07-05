package com.shopflow.order.application.ports;

public interface DistributedLockPort {

    void executeWithLock(String lockKey, Runnable task);

}
