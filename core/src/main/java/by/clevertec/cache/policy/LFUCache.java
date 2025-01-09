package by.clevertec.cache.policy;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class LFUCache implements Cache {

    private final String name;
    private final int capacity;
    private final ConcurrentHashMap<Object, CacheValue> cache;
    private final ConcurrentHashMap<Object, Integer> frequencyMap;
    private final ReentrantLock lock = new ReentrantLock();

    public LFUCache(String name, int capacity) {
        this.name = name;
        this.capacity = capacity;
        this.cache = new ConcurrentHashMap<>();
        this.frequencyMap = new ConcurrentHashMap<>();
    }

    @NotNull
    @Override
    public String getName() {
        return name;
    }

    @NotNull
    @Override
    public Object getNativeCache() {
        return cache;
    }

    @Override
    public ValueWrapper get(@NotNull Object key) {
        if (!cache.containsKey(key)) {
            return null;
        }

        frequencyMap.merge(key, 1, Integer::sum);
        return new SimpleValueWrapper(cache.get(key).value);
    }

    @Override
    public <T> T get(@NotNull Object key, Class<T> type) {
        if (!cache.containsKey(key)) {
            return null;
        }

        Object value = cache.get(key).value;
        if (type.isInstance(value)) {
            frequencyMap.merge(key, 1, Integer::sum);
            return type.cast(value);
        }
        return null;
    }

    @Override
    public <T> T get(@NotNull Object key, @NotNull Callable<T> valueLoader) {
        if (cache.containsKey(key)) {
            frequencyMap.merge(key, 1, Integer::sum);
            return (T) cache.get(key).value;
        }

        try {
            T value = valueLoader.call();
            put(key, value);
            return value;
        } catch (Exception e) {
            log.error("{}{}", e.getMessage(), key);
            throw new RuntimeException("Error loading value for key: " + key, e);
        }
    }

    @Override
    public void put(@NotNull Object key, Object value) {
        lock.lock();
        try {
            if (cache.size() >= capacity) {
                evictLFU();
            }
            cache.put(key, new CacheValue(value));
            frequencyMap.put(key, 1);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void evict(@NotNull Object key) {
        lock.lock();
        try {
            cache.remove(key);
            frequencyMap.remove(key);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void clear() {
        lock.lock();
        try {
            cache.clear();
            frequencyMap.clear();
        } finally {
            lock.unlock();
        }
    }

    private void evictLFU() {
        lock.lock();
        try {
            Object leastUsedKey = null;
            int leastFrequency = Integer.MAX_VALUE;

            for (var entry : frequencyMap.entrySet()) {
                if (entry.getValue() < leastFrequency) {
                    leastUsedKey = entry.getKey();
                    leastFrequency = entry.getValue();
                }
            }

            if (leastUsedKey != null) {
                cache.remove(leastUsedKey);
                frequencyMap.remove(leastUsedKey);
                log.info("Evicted LFU key: {}", leastUsedKey);
            }
        } finally {
            lock.unlock();
        }
    }

    private record CacheValue(Object value) {
    }
}
