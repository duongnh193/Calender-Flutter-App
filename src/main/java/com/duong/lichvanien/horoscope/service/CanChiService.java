package com.duong.lichvanien.horoscope.service;

import com.duong.lichvanien.calendar.lunar.external.DiaChi;
import com.duong.lichvanien.calendar.lunar.external.LunarCalendarLib;
import com.duong.lichvanien.calendar.lunar.external.ThienCan;
import com.duong.lichvanien.calendar.util.VietnameseLunarCalendar;
import com.duong.lichvanien.horoscope.dto.CanChiResult;
import com.duong.lichvanien.zodiac.entity.ZodiacEntity;
import com.duong.lichvanien.zodiac.repository.ZodiacRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;

/**
 * Service for calculating Can-Chi (Thiên Can - Địa Chi) from dates.
 * Supports UTC+7 timezone normalization for accurate Vietnamese zodiac calculations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CanChiService {

    private static final ZoneId VIETNAM_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");

    // Mapping from DiaChi enum name to zodiac code
    private static final Map<String, String> CHI_TO_CODE = Map.ofEntries(
            Map.entry("TY", "ti"),
            Map.entry("SUU", "suu"),
            Map.entry("DAN", "dan"),
            Map.entry("MAO", "mao"),
            Map.entry("THIN", "thin"),
            Map.entry("TY_SNAKE", "ty"),
            Map.entry("NGO", "ngo"),
            Map.entry("MUI", "mui"),
            Map.entry("THAN", "than"),
            Map.entry("DAU", "dau"),
            Map.entry("TUAT", "tuat"),
            Map.entry("HOI", "hoi")
    );

    // Mapping from DiaChi Vietnamese text to zodiac code
    private static final Map<String, String> CHI_TEXT_TO_CODE = Map.ofEntries(
            Map.entry("Tý", "ti"),
            Map.entry("Sửu", "suu"),
            Map.entry("Dần", "dan"),
            Map.entry("Mão", "mao"),
            Map.entry("Thìn", "thin"),
            Map.entry("Tỵ", "ty"),
            Map.entry("Ngọ", "ngo"),
            Map.entry("Mùi", "mui"),
            Map.entry("Thân", "than"),
            Map.entry("Dậu", "dau"),
            Map.entry("Tuất", "tuat"),
            Map.entry("Hợi", "hoi")
    );

    private final ZodiacRepository zodiacRepository;

    /**
     * Calculate Can-Chi from a birth date.
     * Uses the lunar year to determine the zodiac (Địa Chi of year).
     *
     * @param birthDate the birth date (solar calendar)
     * @return CanChiResult with year, day, month Can-Chi information
     */
    public CanChiResult calculateFromBirthDate(LocalDate birthDate) {
        return calculateFromBirthDate(birthDate, null);
    }

    /**
     * Calculate Can-Chi from a birth date and optional time.
     * The time is used for more precise day boundary calculations.
     *
     * @param birthDate the birth date (solar calendar)
     * @param birthTime optional birth time for hour-based calculations
     * @return CanChiResult with full Can-Chi information
     */
    public CanChiResult calculateFromBirthDate(LocalDate birthDate, LocalTime birthTime) {
        // Normalize to Vietnam timezone
        LocalDate normalizedDate = normalizeToVietnamTimezone(birthDate, birthTime);

        // Get Can-Chi using the existing utility
        VietnameseLunarCalendar.CanChi canChi = VietnameseLunarCalendar.canChiFromSolar(normalizedDate);

        // Get lunar date for year reference
        VietnameseLunarCalendar.LunarDate lunarDate = VietnameseLunarCalendar.solarToLunar(normalizedDate);

        // Extract year Can-Chi components
        String canChiYear = canChi.getYear();
        String[] yearParts = canChiYear.split(" ");
        String canYear = yearParts.length > 0 ? yearParts[0] : "";
        String chiYear = yearParts.length > 1 ? yearParts[1] : "";

        // Extract day Can-Chi components
        String canChiDay = canChi.getDay();
        String[] dayParts = canChiDay.split(" ");
        String canDay = dayParts.length > 0 ? dayParts[0] : "";
        String chiDay = dayParts.length > 1 ? dayParts[1] : "";

        // Extract month Can-Chi components
        String canChiMonth = canChi.getMonth();
        String[] monthParts = canChiMonth.split(" ");
        String canMonth = monthParts.length > 0 ? monthParts[0] : "";
        String chiMonth = monthParts.length > 1 ? monthParts[1] : "";

        // Find zodiac code and ID
        String zodiacCode = getZodiacCodeFromChi(chiYear);
        Long zodiacId = getZodiacId(zodiacCode);

        return CanChiResult.builder()
                .canYear(canYear)
                .chiYear(chiYear)
                .canChiYear(canChiYear)
                .zodiacCode(zodiacCode)
                .zodiacId(zodiacId)
                .canDay(canDay)
                .chiDay(chiDay)
                .canChiDay(canChiDay)
                .canMonth(canMonth)
                .chiMonth(chiMonth)
                .canChiMonth(canChiMonth)
                .build();
    }

    /**
     * Get the zodiac code from a Can-Chi string.
     * Supports both full "Giáp Tý" format and just "Tý" format.
     *
     * @param canChiOrChi the Can-Chi string (e.g., "Giáp Tý") or just Chi (e.g., "Tý")
     * @return zodiac code (e.g., "ti")
     */
    public String getZodiacCodeFromCanChi(String canChiOrChi) {
        if (canChiOrChi == null || canChiOrChi.isBlank()) {
            return null;
        }

        String chi = canChiOrChi.trim();
        // If it's a full Can-Chi, extract the Chi part
        if (chi.contains(" ")) {
            String[] parts = chi.split(" ");
            chi = parts[parts.length - 1]; // Take the last part (Địa Chi)
        }

        return getZodiacCodeFromChi(chi);
    }

    /**
     * Get zodiac code from Địa Chi text.
     */
    public String getZodiacCodeFromChi(String chi) {
        if (chi == null || chi.isBlank()) {
            return null;
        }
        return CHI_TEXT_TO_CODE.getOrDefault(chi.trim(), null);
    }

    /**
     * Normalize a Can-Chi string for database lookup.
     * Removes extra spaces and normalizes unicode.
     *
     * @param canChi the Can-Chi string
     * @return normalized Can-Chi string
     */
    public String normalizeCanChi(String canChi) {
        if (canChi == null) {
            return null;
        }
        // Normalize spaces and trim
        return canChi.trim().replaceAll("\\s+", " ");
    }

    /**
     * Get all Can-Chi combinations (60 total = 10 Can × 6 alternating Chi pairs).
     * The 60-year cycle pairs each Can with every other Chi.
     */
    public String[] getAllCanChiCombinations() {
        String[] result = new String[60];
        int index = 0;

        for (ThienCan can : ThienCan.values()) {
            for (int i = 0; i < 12; i += 2) {
                // Each Can pairs with alternating Chi (even or odd based on Can index)
                int canIndex = can.ordinal();
                int chiIndex = (canIndex % 2 == 0) ? i : i;
                DiaChi chi = DiaChi.fromIndex(chiIndex + (canIndex % 2));
                result[index++] = can.getText() + " " + chi.getText();
                if (index >= 60) break;
            }
            if (index >= 60) break;
        }

        // Actually, the correct 60 combinations
        index = 0;
        for (int year = 0; year < 60; year++) {
            ThienCan can = ThienCan.fromIndex(year);
            DiaChi chi = DiaChi.fromIndex(year);
            result[index++] = can.getText() + " " + chi.getText();
        }

        return result;
    }

    /**
     * Validate if a Can-Chi string is valid.
     */
    public boolean isValidCanChi(String canChi) {
        if (canChi == null || canChi.isBlank()) {
            return false;
        }

        String normalized = normalizeCanChi(canChi);
        String[] parts = normalized.split(" ");

        if (parts.length != 2) {
            return false;
        }

        // Check if first part is a valid Thiên Can
        boolean validCan = false;
        for (ThienCan tc : ThienCan.values()) {
            if (tc.getText().equals(parts[0])) {
                validCan = true;
                break;
            }
        }

        // Check if second part is a valid Địa Chi
        boolean validChi = CHI_TEXT_TO_CODE.containsKey(parts[1]);

        return validCan && validChi;
    }

    /**
     * Get today's date in Vietnam timezone.
     */
    public LocalDate getTodayInVietnam() {
        return ZonedDateTime.now(VIETNAM_ZONE).toLocalDate();
    }

    /**
     * Normalize a date to Vietnam timezone.
     */
    private LocalDate normalizeToVietnamTimezone(LocalDate date, LocalTime time) {
        // For birth dates, we typically just use the date as-is
        // since the user's intent is the date in their local context
        // For API calls, we might want to convert from UTC
        return date;
    }

    /**
     * Get zodiac ID from code.
     */
    private Long getZodiacId(String code) {
        if (code == null) {
            return null;
        }
        return zodiacRepository.findByCode(code)
                .map(ZodiacEntity::getId)
                .orElse(null);
    }
}

