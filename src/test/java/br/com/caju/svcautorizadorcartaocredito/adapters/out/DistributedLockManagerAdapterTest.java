package br.com.caju.svcautorizadorcartaocredito.adapters.out;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DistributedLockManagerAdapterTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private DistributedLockManagerAdapter lockManager;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    public void testTryLockSuccess() throws NoSuchFieldException, IllegalAccessException {
        String key = "testKey";
        String lockKey = getLockKey(key);

        when(valueOperations.setIfAbsent(lockKey, "LOCKED", 100, TimeUnit.MILLISECONDS)).thenReturn(true);

        boolean result = lockManager.tryLock(key, 100);

        assertTrue(result);
        verify(valueOperations).setIfAbsent(lockKey, "LOCKED", 100, TimeUnit.MILLISECONDS);
    }

    @Test
    public void testTryLockFailure() throws NoSuchFieldException, IllegalAccessException {
        String key = "testKey";
        String lockKey = getLockKey(key);

        when(valueOperations.setIfAbsent(lockKey, "LOCKED", 100, TimeUnit.MILLISECONDS)).thenReturn(false);

        boolean result = lockManager.tryLock(key, 100);

        assertFalse(result);
        verify(valueOperations).setIfAbsent(lockKey, "LOCKED", 100, TimeUnit.MILLISECONDS);
    }

    @Test
    public void testTryLockNullResponse() throws NoSuchFieldException, IllegalAccessException {
        String key = "testKey";
        String lockKey = getLockKey(key);

        when(valueOperations.setIfAbsent(lockKey, "LOCKED", 100, TimeUnit.MILLISECONDS)).thenReturn(null);

        boolean result = lockManager.tryLock(key, 100);

        assertFalse(result);
        verify(valueOperations).setIfAbsent(lockKey, "LOCKED", 100, TimeUnit.MILLISECONDS);
    }

    @Test
    public void testReleaseLock() throws NoSuchFieldException, IllegalAccessException {
        String key = "testKey";
        String lockKey = getLockKey(key);

        lockManager.releaseLock(key);

        verify(redisTemplate).delete(lockKey);
    }

    private String getLockKey(String key) throws NoSuchFieldException, IllegalAccessException {
        Field lockPrefixField = DistributedLockManagerAdapter.class.getDeclaredField("LOCK_PREFIX");
        lockPrefixField.setAccessible(true);
        String lockPrefix = (String) lockPrefixField.get(null);
        return lockPrefix + key;
    }
}