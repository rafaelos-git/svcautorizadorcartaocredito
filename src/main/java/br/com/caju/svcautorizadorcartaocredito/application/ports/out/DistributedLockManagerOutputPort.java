package br.com.caju.svcautorizadorcartaocredito.application.ports.out;

public interface DistributedLockManagerOutputPort {
    boolean tryLock(String key, long timeout);
    void releaseLock(String key);
}
