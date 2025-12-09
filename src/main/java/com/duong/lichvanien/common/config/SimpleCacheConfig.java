package com.duong.lichvanien.common.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Simple in-memory cache configuration for when Redis is not available.
 * Uses ConcurrentMapCacheManager which stores cache entries in memory.
 * Note: Cache entries are lost on application restart and not shared between instances.
 */
@Configuration
@EnableCaching
@ConditionalOnProperty(name = "spring.cache.type", havingValue = "simple", matchIfMissing = true)
public class SimpleCacheConfig {

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(
                RedisConfig.CACHE_HOROSCOPE_DAILY,
                RedisConfig.CACHE_HOROSCOPE_MONTHLY,
                RedisConfig.CACHE_HOROSCOPE_YEARLY,
                RedisConfig.CACHE_HOROSCOPE_LIFETIME
        );
    }
}

