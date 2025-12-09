package com.duong.lichvanien.horoscope.service;

import com.duong.lichvanien.common.config.RedisConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;

/**
 * Service for managing horoscope cache operations including invalidation.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Profile("!test")
public class HoroscopeCacheService {

    private final CacheManager cacheManager;
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * Clear all horoscope caches.
     */
    public void clearAllCaches() {
        log.info("Clearing all horoscope caches");
        clearCache(RedisConfig.CACHE_HOROSCOPE_DAILY);
        clearCache(RedisConfig.CACHE_HOROSCOPE_MONTHLY);
        clearCache(RedisConfig.CACHE_HOROSCOPE_YEARLY);
        clearCache(RedisConfig.CACHE_HOROSCOPE_LIFETIME);
    }

    /**
     * Clear daily horoscope cache.
     */
    public void clearDailyCache() {
        log.info("Clearing daily horoscope cache");
        clearCache(RedisConfig.CACHE_HOROSCOPE_DAILY);
    }

    /**
     * Clear monthly horoscope cache.
     */
    public void clearMonthlyCache() {
        log.info("Clearing monthly horoscope cache");
        clearCache(RedisConfig.CACHE_HOROSCOPE_MONTHLY);
    }

    /**
     * Clear yearly horoscope cache.
     */
    public void clearYearlyCache() {
        log.info("Clearing yearly horoscope cache");
        clearCache(RedisConfig.CACHE_HOROSCOPE_YEARLY);
    }

    /**
     * Clear lifetime horoscope cache.
     */
    public void clearLifetimeCache() {
        log.info("Clearing lifetime horoscope cache");
        clearCache(RedisConfig.CACHE_HOROSCOPE_LIFETIME);
    }

    /**
     * Clear cache entries for a specific zodiac.
     */
    public void clearByZodiac(String zodiacCode) {
        log.info("Clearing caches for zodiac: {}", zodiacCode);
        clearKeysByPattern(RedisConfig.CACHE_HOROSCOPE_DAILY + "::" + zodiacCode + "*");
        clearKeysByPattern(RedisConfig.CACHE_HOROSCOPE_MONTHLY + "::" + zodiacCode + "*");
        clearKeysByPattern(RedisConfig.CACHE_HOROSCOPE_YEARLY + "::" + zodiacCode + "*");
    }

    /**
     * Clear cache entries for a specific zodiac ID.
     */
    public void clearByZodiacId(Long zodiacId) {
        log.info("Clearing caches for zodiac ID: {}", zodiacId);
        clearKeysByPattern(RedisConfig.CACHE_HOROSCOPE_DAILY + "::" + zodiacId + "*");
        clearKeysByPattern(RedisConfig.CACHE_HOROSCOPE_MONTHLY + "::" + zodiacId + "*");
        clearKeysByPattern(RedisConfig.CACHE_HOROSCOPE_YEARLY + "::" + zodiacId + "*");
    }

    /**
     * Clear daily cache for a specific date range.
     */
    public void clearDailyByDateRange(String startDate, String endDate) {
        log.info("Clearing daily caches for date range: {} to {}", startDate, endDate);
        clearKeysByPattern(RedisConfig.CACHE_HOROSCOPE_DAILY + "::*:" + startDate + "*");
        // Note: For precise date range clearing, a more sophisticated approach would be needed
    }

    /**
     * Get cache statistics.
     */
    public CacheStats getCacheStats() {
        long dailyCount = countKeys(RedisConfig.CACHE_HOROSCOPE_DAILY + "::*");
        long monthlyCount = countKeys(RedisConfig.CACHE_HOROSCOPE_MONTHLY + "::*");
        long yearlyCount = countKeys(RedisConfig.CACHE_HOROSCOPE_YEARLY + "::*");
        long lifetimeCount = countKeys(RedisConfig.CACHE_HOROSCOPE_LIFETIME + "::*");

        return new CacheStats(dailyCount, monthlyCount, yearlyCount, lifetimeCount);
    }

    private void clearCache(String cacheName) {
        var cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.clear();
            log.debug("Cache {} cleared", cacheName);
        }
    }

    private void clearKeysByPattern(String pattern) {
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
            log.debug("Deleted {} keys matching pattern: {}", keys.size(), pattern);
        }
    }

    private long countKeys(String pattern) {
        Set<String> keys = redisTemplate.keys(pattern);
        return keys != null ? keys.size() : 0;
    }

    public record CacheStats(long dailyEntries, long monthlyEntries, long yearlyEntries, long lifetimeEntries) {
        public long totalEntries() {
            return dailyEntries + monthlyEntries + yearlyEntries + lifetimeEntries;
        }
    }
}

