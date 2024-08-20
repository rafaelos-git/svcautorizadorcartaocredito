package br.com.caju.svcautorizadorcartaocredito.adapters.out;

import br.com.caju.svcautorizadorcartaocredito.application.ports.out.DistributedLockManagerOutputPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class DistributedLockManagerAdapter implements DistributedLockManagerOutputPort {
    private static final String LOCK_PREFIX = "lock:";

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public boolean tryLock(String key, long timeout) {
        String lockKey = LOCK_PREFIX + key;
        log.info("Attempting to acquire lock with key: {} and timeout: {} ms", lockKey, timeout);

        Boolean success = redisTemplate.opsForValue().setIfAbsent(lockKey, "LOCKED", timeout, TimeUnit.MILLISECONDS);

        if (success != null && success) {
            log.info("Lock acquired successfully for key: {}", lockKey);
            return true;
        } else {
            log.warn("Failed to acquire lock for key: {}", lockKey);
            return false;
        }
    }

    @Override
    public void releaseLock(String key) {
        String lockKey = LOCK_PREFIX + key;
        log.info("Releasing lock for key: {}", lockKey);

        redisTemplate.delete(lockKey);
        log.info("Lock released for key: {}", lockKey);
    }
}