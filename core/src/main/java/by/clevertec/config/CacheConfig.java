package by.clevertec.config;

import by.clevertec.cache.policy.LFUCache;
import by.clevertec.cache.policy.LRUCache;
import by.clevertec.exception.CacheException;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static by.clevertec.constants.CacheConstant.LFU;
import static by.clevertec.constants.CacheConstant.LRU;

@Configuration
public class CacheConfig extends CachingConfigurerSupport {

    @Value("${cache.default.type:lfu}")
    private String cachePolicy;

    @Value("${cache.default.capacity:200}")
    private int capacity;

    @Override
    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager() {
            @Override
            @NotNull
            protected Cache createConcurrentMapCache(@NotNull String name) {
                return createCache(name);
            }
        };
    }

    private Cache createCache(String name) {
        return switch (cachePolicy.toLowerCase()) {
            case LFU -> new LFUCache(name, capacity);
            case LRU -> new LRUCache(name, capacity);
            default -> throw new CacheException();
        };
    }
}
