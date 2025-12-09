package com.duong.lichvanien.horoscope.controller;

import com.duong.lichvanien.horoscope.service.HoroscopeCacheService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Admin controller for cache management operations.
 * This should be protected in production environments.
 */
@RestController
@RequestMapping("/api/v1/admin/horoscope/cache")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Horoscope Cache Admin", description = "Cache management operations for horoscope data")
public class HoroscopeCacheController {

    private final HoroscopeCacheService cacheService;

    @DeleteMapping("/all")
    @Operation(
            summary = "Clear all horoscope caches",
            description = "Clears all horoscope caches (daily, monthly, yearly, lifetime)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "All caches cleared successfully")
    })
    public ResponseEntity<Map<String, String>> clearAllCaches() {
        log.warn("Admin action: Clearing all horoscope caches");
        cacheService.clearAllCaches();
        return ResponseEntity.ok(Map.of("message", "All horoscope caches cleared"));
    }

    @DeleteMapping("/daily")
    @Operation(summary = "Clear daily horoscope cache")
    public ResponseEntity<Map<String, String>> clearDailyCache() {
        log.warn("Admin action: Clearing daily horoscope cache");
        cacheService.clearDailyCache();
        return ResponseEntity.ok(Map.of("message", "Daily horoscope cache cleared"));
    }

    @DeleteMapping("/monthly")
    @Operation(summary = "Clear monthly horoscope cache")
    public ResponseEntity<Map<String, String>> clearMonthlyCache() {
        log.warn("Admin action: Clearing monthly horoscope cache");
        cacheService.clearMonthlyCache();
        return ResponseEntity.ok(Map.of("message", "Monthly horoscope cache cleared"));
    }

    @DeleteMapping("/yearly")
    @Operation(summary = "Clear yearly horoscope cache")
    public ResponseEntity<Map<String, String>> clearYearlyCache() {
        log.warn("Admin action: Clearing yearly horoscope cache");
        cacheService.clearYearlyCache();
        return ResponseEntity.ok(Map.of("message", "Yearly horoscope cache cleared"));
    }

    @DeleteMapping("/lifetime")
    @Operation(summary = "Clear lifetime horoscope cache")
    public ResponseEntity<Map<String, String>> clearLifetimeCache() {
        log.warn("Admin action: Clearing lifetime horoscope cache");
        cacheService.clearLifetimeCache();
        return ResponseEntity.ok(Map.of("message", "Lifetime horoscope cache cleared"));
    }

    @DeleteMapping("/zodiac/{zodiacCode}")
    @Operation(
            summary = "Clear caches for a specific zodiac",
            description = "Clears all cache entries for a specific zodiac code"
    )
    public ResponseEntity<Map<String, String>> clearByZodiac(
            @Parameter(description = "Zodiac code (ti, suu, dan, etc.)", example = "ti")
            @PathVariable String zodiacCode
    ) {
        log.warn("Admin action: Clearing caches for zodiac: {}", zodiacCode);
        cacheService.clearByZodiac(zodiacCode);
        return ResponseEntity.ok(Map.of("message", "Caches cleared for zodiac: " + zodiacCode));
    }

    @DeleteMapping("/zodiac/id/{zodiacId}")
    @Operation(
            summary = "Clear caches for a specific zodiac ID",
            description = "Clears all cache entries for a specific zodiac ID"
    )
    public ResponseEntity<Map<String, String>> clearByZodiacId(
            @Parameter(description = "Zodiac ID (1-12)", example = "1")
            @PathVariable Long zodiacId
    ) {
        log.warn("Admin action: Clearing caches for zodiac ID: {}", zodiacId);
        cacheService.clearByZodiacId(zodiacId);
        return ResponseEntity.ok(Map.of("message", "Caches cleared for zodiac ID: " + zodiacId));
    }

    @GetMapping("/stats")
    @Operation(
            summary = "Get cache statistics",
            description = "Returns the count of cached entries for each horoscope type"
    )
    public ResponseEntity<HoroscopeCacheService.CacheStats> getCacheStats() {
        return ResponseEntity.ok(cacheService.getCacheStats());
    }
}

