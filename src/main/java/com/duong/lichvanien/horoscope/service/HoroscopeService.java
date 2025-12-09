package com.duong.lichvanien.horoscope.service;

import com.duong.lichvanien.common.config.RedisConfig;
import com.duong.lichvanien.common.exception.BadRequestException;
import com.duong.lichvanien.common.exception.NotFoundException;
import com.duong.lichvanien.horoscope.dto.HoroscopeDailyResponse;
import com.duong.lichvanien.horoscope.dto.HoroscopeLifetimeResponse;
import com.duong.lichvanien.horoscope.dto.HoroscopeMonthlyResponse;
import com.duong.lichvanien.horoscope.dto.HoroscopeYearlyResponse;
import com.duong.lichvanien.horoscope.entity.HoroscopeDailyEntity;
import com.duong.lichvanien.horoscope.entity.HoroscopeLifetimeEntity;
import com.duong.lichvanien.horoscope.entity.HoroscopeMonthlyEntity;
import com.duong.lichvanien.horoscope.entity.HoroscopeYearlyEntity;
import com.duong.lichvanien.horoscope.repository.HoroscopeDailyRepository;
import com.duong.lichvanien.horoscope.repository.HoroscopeLifetimeRepository;
import com.duong.lichvanien.horoscope.repository.HoroscopeMonthlyRepository;
import com.duong.lichvanien.horoscope.repository.HoroscopeYearlyRepository;
import com.duong.lichvanien.zodiac.dto.ZodiacShortDto;
import com.duong.lichvanien.zodiac.entity.ZodiacEntity;
import com.duong.lichvanien.zodiac.service.ZodiacService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class HoroscopeService {

    private static final ZoneId VIETNAM_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final HoroscopeYearlyRepository yearlyRepository;
    private final HoroscopeDailyRepository dailyRepository;
    private final HoroscopeMonthlyRepository monthlyRepository;
    private final HoroscopeLifetimeRepository lifetimeRepository;
    private final ZodiacService zodiacService;
    private final CanChiService canChiService;

    // ==================== LIFETIME HOROSCOPE ====================

    @Cacheable(
            value = RedisConfig.CACHE_HOROSCOPE_LIFETIME,
            key = "#canChi.toLowerCase().replace(' ', '') + ':' + #gender.toLowerCase()",
            unless = "#result == null"
    )
    @Timed(value = "horoscope.lifetime.get", description = "Time to get lifetime horoscope")
    public HoroscopeLifetimeResponse getLifetime(String canChi, String gender) {
        log.info("Fetching lifetime horoscope for canChi={}, gender={}", canChi, gender);

        // Validate and normalize parameters
        String normalizedCanChi = canChiService.normalizeCanChi(canChi);
        if (!canChiService.isValidCanChi(normalizedCanChi)) {
            throw new BadRequestException("INVALID_CAN_CHI", "Invalid Can-Chi format: " + canChi);
        }

        HoroscopeLifetimeEntity.Gender genderEnum;
        try {
            genderEnum = HoroscopeLifetimeEntity.Gender.valueOf(gender.toLowerCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("INVALID_GENDER", "Gender must be 'male' or 'female'");
        }

        HoroscopeLifetimeEntity entity = lifetimeRepository
                .findByNormalizedCanChiAndGender(normalizedCanChi, genderEnum)
                .or(() -> lifetimeRepository.findByCanChiAndGender(normalizedCanChi, genderEnum))
                .orElseThrow(() -> new NotFoundException("NOT_FOUND",
                        "Lifetime horoscope not found for " + canChi + " / " + gender));

        return mapLifetimeToResponse(entity);
    }

    // ==================== YEARLY HOROSCOPE ====================

    @Cacheable(
            value = RedisConfig.CACHE_HOROSCOPE_YEARLY,
            key = "#zodiacId != null ? #zodiacId + ':' + #year : #zodiacCode + ':' + #year",
            unless = "#result == null"
    )
    @Timed(value = "horoscope.yearly.get", description = "Time to get yearly horoscope")
    public HoroscopeYearlyResponse getYearly(Long zodiacId, String zodiacCode, int year) {
        log.info("Fetching yearly horoscope for zodiacId={}, zodiacCode={}, year={}", zodiacId, zodiacCode, year);

        ZodiacEntity zodiac = resolveZodiac(zodiacId, zodiacCode);

        HoroscopeYearlyEntity entity = yearlyRepository.findByZodiacAndYear(zodiac, year)
                .orElseThrow(() -> new NotFoundException("NOT_FOUND",
                        "Yearly horoscope not found for zodiac " + zodiac.getCode() + " year " + year));

        return mapYearlyToResponse(entity, zodiac);
    }

    /**
     * Legacy method for backward compatibility.
     */
    public HoroscopeYearlyResponse getYearly(String zodiacCode, int year) {
        return getYearly(null, zodiacCode, year);
    }

    // ==================== MONTHLY HOROSCOPE ====================

    @Cacheable(
            value = RedisConfig.CACHE_HOROSCOPE_MONTHLY,
            key = "#zodiacId != null ? #zodiacId + ':' + #year + '-' + #month : #zodiacCode + ':' + #year + '-' + #month",
            unless = "#result == null"
    )
    @Timed(value = "horoscope.monthly.get", description = "Time to get monthly horoscope")
    public HoroscopeMonthlyResponse getMonthly(Long zodiacId, String zodiacCode, int year, int month) {
        log.info("Fetching monthly horoscope for zodiacId={}, zodiacCode={}, year={}, month={}",
                zodiacId, zodiacCode, year, month);

        if (month < 1 || month > 12) {
            throw new BadRequestException("INVALID_MONTH", "Month must be between 1 and 12");
        }

        ZodiacEntity zodiac = resolveZodiac(zodiacId, zodiacCode);

        HoroscopeMonthlyEntity entity = monthlyRepository.findByZodiacAndYearAndMonth(zodiac, year, month)
                .orElseThrow(() -> new NotFoundException("NOT_FOUND",
                        "Monthly horoscope not found for zodiac " + zodiac.getCode() + " " + year + "/" + month));

        return mapMonthlyToResponse(entity, zodiac);
    }

    // ==================== DAILY HOROSCOPE ====================

    @Cacheable(
            value = RedisConfig.CACHE_HOROSCOPE_DAILY,
            key = "#zodiacId != null ? #zodiacId + ':' + #date : #zodiacCode + ':' + #date",
            unless = "#result == null"
    )
    @Timed(value = "horoscope.daily.get", description = "Time to get daily horoscope")
    public HoroscopeDailyResponse getDaily(Long zodiacId, String zodiacCode, LocalDate date) {
        // Default to today in Vietnam timezone if date not provided
        LocalDate effectiveDate = date != null ? date : getTodayInVietnam();

        log.info("Fetching daily horoscope for zodiacId={}, zodiacCode={}, date={}",
                zodiacId, zodiacCode, effectiveDate);

        ZodiacEntity zodiac = resolveZodiac(zodiacId, zodiacCode);

        HoroscopeDailyEntity entity = dailyRepository.findByZodiacAndSolarDate(zodiac, effectiveDate)
                .orElseThrow(() -> new NotFoundException("NOT_FOUND",
                        "Daily horoscope not found for zodiac " + zodiac.getCode() + " date " + effectiveDate));

        return mapDailyToResponse(entity, zodiac);
    }

    /**
     * Legacy method for backward compatibility.
     */
    public HoroscopeDailyResponse getDaily(String zodiacCode, LocalDate date) {
        return getDaily(null, zodiacCode, date);
    }

    // ==================== HELPER METHODS ====================

    private ZodiacEntity resolveZodiac(Long zodiacId, String zodiacCode) {
        if (zodiacId != null) {
            return zodiacService.getById(zodiacId);
        } else if (zodiacCode != null && !zodiacCode.isBlank()) {
            return zodiacService.getByCode(zodiacCode);
        } else {
            throw new BadRequestException("INVALID_PARAMS", "Either zodiacId or zodiacCode must be provided");
        }
    }

    private LocalDate getTodayInVietnam() {
        return ZonedDateTime.now(VIETNAM_ZONE).toLocalDate();
    }

    private ZodiacShortDto toShort(ZodiacEntity entity) {
        return ZodiacShortDto.builder()
                .code(entity.getCode())
                .nameVi(entity.getNameVi())
                .build();
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

    // ==================== MAPPERS ====================

    private HoroscopeLifetimeResponse mapLifetimeToResponse(HoroscopeLifetimeEntity entity) {
        ZodiacEntity zodiac = entity.getZodiac();
        return HoroscopeLifetimeResponse.builder()
                .zodiacId(zodiac.getId())
                .zodiacCode(zodiac.getCode())
                .zodiacName(zodiac.getNameVi())
                .canChi(entity.getCanChi())
                .gender(entity.getGender().name())
                .overview(entity.getOverview())
                .career(entity.getCareer())
                .love(entity.getLove())
                .health(entity.getHealth())
                .family(entity.getFamily())
                .fortune(entity.getFortune())
                .unlucky(entity.getUnlucky())
                .advice(entity.getAdvice())
                .metadata(parseMetadata(entity.getMetadata()))
                .build();
    }

    private HoroscopeYearlyResponse mapYearlyToResponse(HoroscopeYearlyEntity entity, ZodiacEntity zodiac) {
        return HoroscopeYearlyResponse.builder()
                .zodiac(toShort(zodiac))
                .year(entity.getYear())
                .summary(entity.getSummary())
                .love(entity.getLove())
                .career(entity.getCareer())
                .fortune(entity.getFortune())
                .health(entity.getHealth())
                .warnings(entity.getWarnings())
                .metadata(parseMetadata(entity.getMetadata()))
                .build();
    }

    private HoroscopeMonthlyResponse mapMonthlyToResponse(HoroscopeMonthlyEntity entity, ZodiacEntity zodiac) {
        return HoroscopeMonthlyResponse.builder()
                .zodiac(toShort(zodiac))
                .year(entity.getYear())
                .month(entity.getMonth())
                .summary(entity.getSummary())
                .career(entity.getCareer())
                .love(entity.getLove())
                .health(entity.getHealth())
                .fortune(entity.getFortune())
                .metadata(parseMetadata(entity.getMetadata()))
                .build();
    }

    private HoroscopeDailyResponse mapDailyToResponse(HoroscopeDailyEntity entity, ZodiacEntity zodiac) {
        return HoroscopeDailyResponse.builder()
                .zodiac(toShort(zodiac))
                .date(entity.getSolarDate().toString())
                .summary(entity.getSummary())
                .love(entity.getLove())
                .career(entity.getCareer())
                .fortune(entity.getFortune())
                .health(entity.getHealth())
                .luckyColor(entity.getLuckyColor())
                .luckyNumber(entity.getLuckyNumber())
                .metadata(parseMetadata(entity.getMetadata()))
                .build();
    }
}
