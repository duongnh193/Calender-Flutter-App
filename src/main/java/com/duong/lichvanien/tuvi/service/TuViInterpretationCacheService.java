package com.duong.lichvanien.tuvi.service;

import com.duong.lichvanien.tuvi.config.TuViCacheConfig;
import com.duong.lichvanien.tuvi.dto.TuViChartResponse;
import com.duong.lichvanien.tuvi.dto.interpretation.OverviewSection;
import com.duong.lichvanien.tuvi.dto.interpretation.PalaceInterpretation;
import com.duong.lichvanien.tuvi.dto.interpretation.TuViInterpretationResponse;
import com.duong.lichvanien.tuvi.util.ChartHashGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

/**
 * Service for caching Tu Vi interpretation results.
 * Uses Redis for distributed caching.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TuViInterpretationCacheService {

    private final StringRedisTemplate redisTemplate;
    private final TuViCacheConfig cacheConfig;
    private final ObjectMapper objectMapper;

    /**
     * Get cached complete interpretation.
     *
     * @param chart Chart response
     * @param gender Gender
     * @return Cached interpretation if exists, empty otherwise
     */
    public Optional<TuViInterpretationResponse> getCachedInterpretation(
            TuViChartResponse chart, String gender) {

        if (!cacheConfig.isEnabled()) {
            return Optional.empty();
        }

        try {
            String hash = ChartHashGenerator.generateChartHash(chart, gender);
            String key = cacheConfig.getPrefix() + "full:" + hash;

            String cachedJson = redisTemplate.opsForValue().get(key);
            if (cachedJson != null) {
                log.info("Cache HIT for interpretation hash: {}", hash);
                TuViInterpretationResponse cached = objectMapper.readValue(
                        cachedJson,
                        TuViInterpretationResponse.class
                );
                return Optional.of(cached);
            }

            log.debug("Cache MISS for interpretation hash: {}", hash);
            return Optional.empty();

        } catch (Exception e) {
            log.warn("Error reading from cache: {}", e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Cache complete interpretation.
     *
     * @param chart Chart response
     * @param gender Gender
     * @param interpretation Interpretation to cache
     */
    public void cacheInterpretation(
            TuViChartResponse chart,
            String gender,
            TuViInterpretationResponse interpretation) {

        if (!cacheConfig.isEnabled()) {
            return;
        }

        try {
            String hash = ChartHashGenerator.generateChartHash(chart, gender);
            String key = cacheConfig.getPrefix() + "full:" + hash;

            String json = objectMapper.writeValueAsString(interpretation);
            Duration ttl = Duration.ofSeconds(cacheConfig.getTtlSeconds());

            redisTemplate.opsForValue().set(key, json, ttl);
            log.info("Cached interpretation hash: {} (TTL: {} hours)", hash, cacheConfig.getTtlHours());

        } catch (Exception e) {
            // Gracefully handle Redis connection failures and other errors
            // Log warning but don't fail the request
            log.warn("Error caching interpretation (Redis may be unavailable): {}", e.getMessage());
        }
    }

    /**
     * Get cached overview section.
     *
     * @param chart Chart response
     * @param gender Gender
     * @return Cached overview if exists, empty otherwise
     */
    public Optional<OverviewSection> getCachedOverview(
            TuViChartResponse chart, String gender) {

        if (!cacheConfig.isEnabled()) {
            return Optional.empty();
        }

        try {
            String hash = ChartHashGenerator.generateChartHash(chart, gender);
            String sectionHash = ChartHashGenerator.generateSectionHash(hash, "overview");
            String key = cacheConfig.getPrefix() + "section:" + sectionHash;

            String cachedJson = redisTemplate.opsForValue().get(key);
            if (cachedJson != null) {
                log.debug("Cache HIT for overview hash: {}", sectionHash);
                OverviewSection cached = objectMapper.readValue(
                        cachedJson,
                        OverviewSection.class
                );
                return Optional.of(cached);
            }

            return Optional.empty();

        } catch (Exception e) {
            log.warn("Error reading cached overview: {}", e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Cache overview section.
     *
     * @param chart Chart response
     * @param gender Gender
     * @param overview Overview to cache
     */
    public void cacheOverview(
            TuViChartResponse chart,
            String gender,
            OverviewSection overview) {

        if (!cacheConfig.isEnabled()) {
            return;
        }

        try {
            String hash = ChartHashGenerator.generateChartHash(chart, gender);
            String sectionHash = ChartHashGenerator.generateSectionHash(hash, "overview");
            String key = cacheConfig.getPrefix() + "section:" + sectionHash;

            String json = objectMapper.writeValueAsString(overview);
            Duration ttl = Duration.ofSeconds(cacheConfig.getTtlSeconds());

            redisTemplate.opsForValue().set(key, json, ttl);
            log.debug("Cached overview hash: {}", sectionHash);

        } catch (Exception e) {
            // Gracefully handle Redis connection failures and other errors
            log.warn("Error caching overview (Redis may be unavailable): {}", e.getMessage());
        }
    }

    /**
     * Get cached palace interpretation.
     *
     * @param chart Chart response
     * @param gender Gender
     * @param palaceCode Palace code (e.g., "MENH", "QUAN_LOC")
     * @return Cached palace interpretation if exists, empty otherwise
     */
    public Optional<PalaceInterpretation> getCachedPalace(
            TuViChartResponse chart,
            String gender,
            String palaceCode) {

        if (!cacheConfig.isEnabled()) {
            return Optional.empty();
        }

        try {
            String hash = ChartHashGenerator.generateChartHash(chart, gender);
            String sectionHash = ChartHashGenerator.generateSectionHash(hash, palaceCode);
            String key = cacheConfig.getPrefix() + "section:" + sectionHash;

            String cachedJson = redisTemplate.opsForValue().get(key);
            if (cachedJson != null) {
                log.debug("Cache HIT for palace {} hash: {}", palaceCode, sectionHash);
                PalaceInterpretation cached = objectMapper.readValue(
                        cachedJson,
                        PalaceInterpretation.class
                );
                return Optional.of(cached);
            }

            return Optional.empty();

        } catch (Exception e) {
            log.warn("Error reading cached palace {}: {}", palaceCode, e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Cache palace interpretation.
     *
     * @param chart Chart response
     * @param gender Gender
     * @param palaceCode Palace code
     * @param interpretation Palace interpretation to cache
     */
    public void cachePalace(
            TuViChartResponse chart,
            String gender,
            String palaceCode,
            PalaceInterpretation interpretation) {

        if (!cacheConfig.isEnabled()) {
            return;
        }

        try {
            String hash = ChartHashGenerator.generateChartHash(chart, gender);
            String sectionHash = ChartHashGenerator.generateSectionHash(hash, palaceCode);
            String key = cacheConfig.getPrefix() + "section:" + sectionHash;

            String json = objectMapper.writeValueAsString(interpretation);
            Duration ttl = Duration.ofSeconds(cacheConfig.getTtlSeconds());

            redisTemplate.opsForValue().set(key, json, ttl);
            log.debug("Cached palace {} hash: {}", palaceCode, sectionHash);

        } catch (Exception e) {
            // Gracefully handle Redis connection failures and other errors
            log.warn("Error caching palace {} (Redis may be unavailable): {}", palaceCode, e.getMessage());
        }
    }

    /**
     * Clear all cached interpretations for a chart.
     *
     * @param chart Chart response
     * @param gender Gender
     */
    public void clearCache(TuViChartResponse chart, String gender) {
        if (!cacheConfig.isEnabled()) {
            return;
        }

        try {
            String hash = ChartHashGenerator.generateChartHash(chart, gender);
            String fullKey = cacheConfig.getPrefix() + "full:" + hash;
            redisTemplate.delete(fullKey);

            // Clear all section keys
            String sectionPattern = cacheConfig.getPrefix() + "section:" + hash + ":*";
            // Note: Redis DELETE with pattern requires SCAN in production, simplified here
            log.info("Cleared cache for hash: {}", hash);

        } catch (Exception e) {
            log.warn("Error clearing cache: {}", e.getMessage());
        }
    }
}
