package com.duong.lichvanien.tuvi.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for Tu Vi interpretation caching.
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "tuvi.cache")
public class TuViCacheConfig {

    /**
     * Whether caching is enabled
     */
    private boolean enabled = true;

    /**
     * Time-to-live in hours
     */
    private int ttlHours = 720; // 30 days default

    /**
     * Cache key prefix
     */
    private String prefix = "tuvi:interpretation:";

    /**
     * Get TTL in seconds
     */
    public long getTtlSeconds() {
        return ttlHours * 3600L;
    }
}
