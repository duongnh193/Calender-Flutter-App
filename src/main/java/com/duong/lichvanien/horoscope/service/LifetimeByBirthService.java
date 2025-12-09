package com.duong.lichvanien.horoscope.service;

import com.duong.lichvanien.common.config.RedisConfig;
import com.duong.lichvanien.common.exception.BadRequestException;
import com.duong.lichvanien.horoscope.dto.CanChiInfo;
import com.duong.lichvanien.horoscope.dto.LifetimeByBirthRequest;
import com.duong.lichvanien.horoscope.dto.LifetimeByBirthResponse;
import com.duong.lichvanien.horoscope.entity.HoroscopeLifetimeEntity;
import com.duong.lichvanien.horoscope.repository.HoroscopeLifetimeRepository;
import com.duong.lichvanien.zodiac.entity.ZodiacEntity;
import com.duong.lichvanien.zodiac.repository.ZodiacRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service to get lifetime horoscope by birth data.
 * Computes Can-Chi from birth date/time, looks up in database,
 * and returns the horoscope with fallback support.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class LifetimeByBirthService {

    private final CanChiResolver canChiResolver;
    private final HoroscopeLifetimeRepository lifetimeRepository;
    private final ZodiacRepository zodiacRepository;
    private final MeterRegistry meterRegistry;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private Counter requestCounter;
    private Counter cacheHitCounter;
    private Counter cacheMissCounter;
    private Counter fallbackCounter;

    @PostConstruct
    void initMetrics() {
        requestCounter = Counter.builder("horoscope_lifetime_requests_total")
                .description("Total lifetime horoscope by birth requests")
                .register(meterRegistry);
        cacheHitCounter = Counter.builder("horoscope_lifetime_cache_hits_total")
                .description("Cache hits for lifetime horoscope")
                .register(meterRegistry);
        cacheMissCounter = Counter.builder("horoscope_lifetime_cache_misses_total")
                .description("Cache misses for lifetime horoscope")
                .register(meterRegistry);
        fallbackCounter = Counter.builder("horoscope_lifetime_fallbacks_total")
                .description("Fallback responses for lifetime horoscope")
                .register(meterRegistry);
    }

    /**
     * Get lifetime horoscope by birth data.
     *
     * @param request Birth data request
     * @return Lifetime horoscope response (exact match or fallback)
     */
    @Timed(value = "horoscope_lifetime_latency_ms", description = "Lifetime horoscope by birth latency")
    public LifetimeByBirthResponse getLifetimeByBirth(LifetimeByBirthRequest request) {
        requestCounter.increment();
        long startTime = System.currentTimeMillis();

        // Parse and validate date
        LocalDate birthDate;
        try {
            birthDate = LocalDate.parse(request.getDate());
        } catch (DateTimeParseException e) {
            throw new BadRequestException("INVALID_DATE", "Invalid date format: " + request.getDate());
        }

        // Resolve Can-Chi
        CanChiInfo canChiInfo = canChiResolver.resolve(
                birthDate,
                request.getHour(),
                request.getMinute(),
                request.getIsLunar(),
                request.getIsLeapMonth()
        );

        // Parse gender
        HoroscopeLifetimeEntity.Gender gender;
        try {
            gender = HoroscopeLifetimeEntity.Gender.valueOf(request.getGender().toLowerCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("INVALID_GENDER", "Gender must be 'male' or 'female'");
        }

        // Try to get from cache first
        String cacheKey = buildCacheKey(canChiInfo.getCanChiYear(), request.getGender());

        // Lookup in database
        LifetimeByBirthResponse response = lookupLifetime(canChiInfo, gender, cacheKey);

        long duration = System.currentTimeMillis() - startTime;
        log.info("Lifetime by birth lookup completed in {}ms - canChi={}, zodiac={}, gender={}, isFallback={}",
                duration, canChiInfo.getCanChiYear(), canChiInfo.getZodiacCode(), request.getGender(),
                response.getIsFallback());

        return response;
    }

    /**
     * Cacheable lookup for lifetime horoscope.
     */
    @Cacheable(
            value = RedisConfig.CACHE_HOROSCOPE_LIFETIME,
            key = "#cacheKey",
            unless = "#result == null || #result.isFallback"
    )
    public LifetimeByBirthResponse lookupLifetime(CanChiInfo canChiInfo, HoroscopeLifetimeEntity.Gender gender, String cacheKey) {
        String canChiYear = canChiInfo.getCanChiYear();
        String normalizedCanChi = canChiResolver.normalizeCanChi(canChiYear);

        // Try exact match first
        Optional<HoroscopeLifetimeEntity> exactMatch = findExactMatch(normalizedCanChi, gender);

        if (exactMatch.isPresent()) {
            log.debug("Exact match found for canChi={}, gender={}", canChiYear, gender);
            return mapToResponse(exactMatch.get(), canChiInfo, false);
        }

        // Try fallback by zodiac_id + gender
        log.debug("No exact match, trying fallback for zodiacId={}, gender={}", canChiInfo.getZodiacId(), gender);
        fallbackCounter.increment();

        Optional<HoroscopeLifetimeEntity> fallback = findFallbackByZodiac(canChiInfo.getZodiacId(), gender);

        if (fallback.isPresent()) {
            return mapToFallbackResponse(fallback.get(), canChiInfo, gender);
        }

        // Return empty fallback response
        return buildEmptyFallbackResponse(canChiInfo, gender);
    }

    private Optional<HoroscopeLifetimeEntity> findExactMatch(String canChi, HoroscopeLifetimeEntity.Gender gender) {
        // Try normalized search first
        List<HoroscopeLifetimeEntity> results = lifetimeRepository.findAllByNormalizedCanChiAndGender(canChi, gender);
        if (!results.isEmpty()) {
            return Optional.of(results.get(0));
        }

        // Try exact match
        return lifetimeRepository.findByCanChiAndGender(canChi, gender);
    }

    private Optional<HoroscopeLifetimeEntity> findFallbackByZodiac(Long zodiacId, HoroscopeLifetimeEntity.Gender gender) {
        if (zodiacId == null) {
            return Optional.empty();
        }

        // Find first lifetime record for this zodiac + gender
        return lifetimeRepository.findFirstByZodiacIdAndGender(zodiacId, gender);
    }

    private LifetimeByBirthResponse mapToResponse(HoroscopeLifetimeEntity entity, CanChiInfo canChiInfo, boolean isFallback) {
        Map<String, Object> metadata = parseMetadata(entity.getMetadata());
        if (metadata == null) {
            metadata = new HashMap<>();
        }
        metadata.put("computed", true);
        metadata.put("hourBranch", canChiInfo.getHourBranchCode());
        metadata.put("lunarYear", canChiInfo.getLunarYear());
        metadata.put("solarDate", canChiInfo.getSolarDate());

        return LifetimeByBirthResponse.builder()
                .zodiacId(entity.getZodiac().getId())
                .zodiacCode(entity.getZodiac().getCode())
                .zodiacName(entity.getZodiac().getNameVi())
                .canChi(entity.getCanChi())
                .gender(entity.getGender().name())
                .hourBranch(canChiInfo.getHourBranchCode())
                .hourBranchName(canChiInfo.getHourBranchName())
                .computed(true)
                .isFallback(isFallback)
                .overview(entity.getOverview())
                .career(entity.getCareer())
                .love(entity.getLove())
                .health(entity.getHealth())
                .family(entity.getFamily())
                .fortune(entity.getFortune())
                .unlucky(entity.getUnlucky())
                .advice(entity.getAdvice())
                .metadata(metadata)
                .build();
    }

    private LifetimeByBirthResponse mapToFallbackResponse(HoroscopeLifetimeEntity entity, CanChiInfo canChiInfo, HoroscopeLifetimeEntity.Gender gender) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("computed", true);
        metadata.put("fallback", true);
        metadata.put("originalCanChi", canChiInfo.getCanChiYear());
        metadata.put("hourBranch", canChiInfo.getHourBranchCode());

        return LifetimeByBirthResponse.builder()
                .zodiacId(canChiInfo.getZodiacId())
                .zodiacCode(canChiInfo.getZodiacCode())
                .zodiacName(canChiInfo.getZodiacName())
                .canChi(null) // null indicates fallback
                .gender(gender.name())
                .hourBranch(canChiInfo.getHourBranchCode())
                .hourBranchName(canChiInfo.getHourBranchName())
                .message("Lifetime data not found for computed Can-Chi; returning zodiac-level default.")
                .computed(true)
                .isFallback(true)
                .overview(entity.getOverview())
                .career(entity.getCareer())
                .love(entity.getLove())
                .health(entity.getHealth())
                .family(entity.getFamily())
                .fortune(entity.getFortune())
                .unlucky(entity.getUnlucky())
                .advice(entity.getAdvice())
                .metadata(metadata)
                .build();
    }

    private LifetimeByBirthResponse buildEmptyFallbackResponse(CanChiInfo canChiInfo, HoroscopeLifetimeEntity.Gender gender) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("computed", true);
        metadata.put("fallback", true);
        metadata.put("noData", true);
        metadata.put("originalCanChi", canChiInfo.getCanChiYear());
        metadata.put("hourBranch", canChiInfo.getHourBranchCode());

        // Get zodiac info
        String zodiacName = canChiInfo.getZodiacName();
        if (zodiacName == null && canChiInfo.getZodiacCode() != null) {
            zodiacRepository.findByCode(canChiInfo.getZodiacCode())
                    .ifPresent(z -> metadata.put("zodiacName", z.getNameVi()));
        }

        return LifetimeByBirthResponse.builder()
                .zodiacId(canChiInfo.getZodiacId())
                .zodiacCode(canChiInfo.getZodiacCode())
                .zodiacName(zodiacName)
                .canChi(null)
                .gender(gender.name())
                .hourBranch(canChiInfo.getHourBranchCode())
                .hourBranchName(canChiInfo.getHourBranchName())
                .message("Lifetime horoscope data not available for this Can-Chi combination. " +
                        "Computed: " + canChiInfo.getCanChiYear() + " (" + gender.name() + ")")
                .computed(true)
                .isFallback(true)
                .metadata(metadata)
                .build();
    }

    private String buildCacheKey(String canChi, String gender) {
        String normalizedCanChi = canChiResolver.normalizeCanChiForKey(canChi);
        return normalizedCanChi + ":" + gender.toLowerCase();
    }

    private Map<String, Object> parseMetadata(String metadata) {
        if (metadata == null || metadata.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readValue(metadata, new TypeReference<>() {});
        } catch (Exception e) {
            log.warn("Failed to parse metadata: {}", e.getMessage());
            return null;
        }
    }
}

