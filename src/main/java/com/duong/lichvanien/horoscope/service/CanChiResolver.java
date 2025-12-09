package com.duong.lichvanien.horoscope.service;

import com.duong.lichvanien.calendar.util.VietnameseLunarCalendar;
import com.duong.lichvanien.common.utils.LogSanitizer;
import com.duong.lichvanien.horoscope.dto.CanChiInfo;
import com.duong.lichvanien.zodiac.entity.ZodiacEntity;
import com.duong.lichvanien.zodiac.repository.ZodiacRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Service to resolve Can-Chi information from birth date and time.
 * 
 * Hour-to-Branch Mapping (Vietnamese 12 Canh):
 * - Tý (ti):   23:00 - 00:59
 * - Sửu (suu): 01:00 - 02:59
 * - Dần (dan): 03:00 - 04:59
 * - Mão (mao): 05:00 - 06:59
 * - Thìn (thin): 07:00 - 08:59
 * - Tỵ (ty):   09:00 - 10:59
 * - Ngọ (ngo): 11:00 - 12:59
 * - Mùi (mui): 13:00 - 14:59
 * - Thân (than): 15:00 - 16:59
 * - Dậu (dau): 17:00 - 18:59
 * - Tuất (tuat): 19:00 - 20:59
 * - Hợi (hoi): 21:00 - 22:59
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CanChiResolver {

    private final ZodiacRepository zodiacRepository;

    // Hour branch data: code, Vietnamese name
    private static final String[][] HOUR_BRANCHES = {
            {"ti", "Tý"},      // 23:00 - 00:59 (index 0)
            {"suu", "Sửu"},    // 01:00 - 02:59 (index 1)
            {"dan", "Dần"},    // 03:00 - 04:59 (index 2)
            {"mao", "Mão"},    // 05:00 - 06:59 (index 3)
            {"thin", "Thìn"},  // 07:00 - 08:59 (index 4)
            {"ty", "Tỵ"},      // 09:00 - 10:59 (index 5)
            {"ngo", "Ngọ"},    // 11:00 - 12:59 (index 6)
            {"mui", "Mùi"},    // 13:00 - 14:59 (index 7)
            {"than", "Thân"},  // 15:00 - 16:59 (index 8)
            {"dau", "Dậu"},    // 17:00 - 18:59 (index 9)
            {"tuat", "Tuất"},  // 19:00 - 20:59 (index 10)
            {"hoi", "Hợi"}     // 21:00 - 22:59 (index 11)
    };

    // Chi Vietnamese text to code mapping
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

    /**
     * Resolve complete Can-Chi information from birth data.
     *
     * @param date        Birth date (solar or lunar based on isLunar flag)
     * @param hour        Birth hour (0-23)
     * @param minute      Birth minute (0-59)
     * @param isLunar     Whether the input date is lunar calendar
     * @param isLeapMonth Whether it's a leap month (only relevant if isLunar=true)
     * @return Complete Can-Chi information
     */
    public CanChiInfo resolve(LocalDate date, int hour, int minute, boolean isLunar, boolean isLeapMonth) {
        // Mask DOB in logs to prevent PII leakage
        log.debug("Resolving Can-Chi for date={}, hour={}, minute={}, isLunar={}, isLeapMonth={}",
                LogSanitizer.maskDate(date.toString()), hour, minute, isLunar, isLeapMonth);

        // Step 1: Convert lunar to solar if needed
        LocalDate solarDate;
        VietnameseLunarCalendar.LunarDate lunarDateInfo;

        if (isLunar) {
            // Convert lunar date to solar date
            solarDate = VietnameseLunarCalendar.lunarToSolar(
                    date.getDayOfMonth(),
                    date.getMonthValue(),
                    date.getYear(),
                    isLeapMonth
            );
            lunarDateInfo = new VietnameseLunarCalendar.LunarDate(
                    date.getDayOfMonth(),
                    date.getMonthValue(),
                    date.getYear(),
                    isLeapMonth
            );
            log.debug("Converted lunar {} to solar {}", LogSanitizer.maskDate(date.toString()), LogSanitizer.maskDate(solarDate.toString()));
        } else {
            solarDate = date;
            // Convert solar to lunar for reference
            lunarDateInfo = VietnameseLunarCalendar.solarToLunar(solarDate);
        }

        // Step 2: Get Can-Chi from solar date
        VietnameseLunarCalendar.CanChi canChi = VietnameseLunarCalendar.canChiFromSolar(solarDate);

        // Step 3: Parse year Can-Chi
        String canChiYear = canChi.getYear();
        String[] yearParts = canChiYear.split(" ");
        String canYear = yearParts.length > 0 ? yearParts[0] : "";
        String chiYear = yearParts.length > 1 ? yearParts[1] : "";

        // Step 4: Parse day Can-Chi
        String canChiDay = canChi.getDay();
        String[] dayParts = canChiDay.split(" ");
        String canDay = dayParts.length > 0 ? dayParts[0] : "";
        String chiDay = dayParts.length > 1 ? dayParts[1] : "";

        // Step 5: Parse month Can-Chi
        String canChiMonth = canChi.getMonth();
        String[] monthParts = canChiMonth.split(" ");
        String canMonth = monthParts.length > 0 ? monthParts[0] : "";
        String chiMonth = monthParts.length > 1 ? monthParts[1] : "";

        // Step 6: Calculate hour branch
        int hourBranchIndex = getHourBranchIndex(hour, minute);
        String hourBranchCode = HOUR_BRANCHES[hourBranchIndex][0];
        String hourBranchName = HOUR_BRANCHES[hourBranchIndex][1];

        // Step 7: Get zodiac info from Chi (year)
        String zodiacCode = CHI_TEXT_TO_CODE.getOrDefault(chiYear, null);
        Long zodiacId = null;
        String zodiacName = chiYear;

        if (zodiacCode != null) {
            ZodiacEntity zodiac = zodiacRepository.findByCode(zodiacCode).orElse(null);
            if (zodiac != null) {
                zodiacId = zodiac.getId();
                zodiacName = zodiac.getNameVi();
            }
        }

        log.info("Resolved Can-Chi: canChiYear={}, zodiacCode={}, zodiacId={}, hourBranch={}",
                canChiYear, zodiacCode, zodiacId, hourBranchCode);

        return CanChiInfo.builder()
                // Year
                .canYear(canYear)
                .chiYear(chiYear)
                .canChiYear(canChiYear)
                // Zodiac
                .zodiacId(zodiacId)
                .zodiacCode(zodiacCode)
                .zodiacName(zodiacName)
                // Day
                .canDay(canDay)
                .chiDay(chiDay)
                .canChiDay(canChiDay)
                // Month
                .canMonth(canMonth)
                .chiMonth(chiMonth)
                .canChiMonth(canChiMonth)
                // Hour
                .hourBranchCode(hourBranchCode)
                .hourBranchName(hourBranchName)
                .hourBranchIndex(hourBranchIndex)
                // Source info
                .originalDate(date.format(DateTimeFormatter.ISO_DATE))
                .solarDate(solarDate.format(DateTimeFormatter.ISO_DATE))
                .wasLunar(isLunar)
                .lunarYear(lunarDateInfo.getYear())
                .lunarMonth(lunarDateInfo.getMonth())
                .lunarDay(lunarDateInfo.getDay())
                .wasLeapMonth(lunarDateInfo.isLeap())
                .build();
    }

    /**
     * Get the hour branch index (0-11) from hour and minute.
     * 
     * Mapping:
     * - Tý (0):   23:00 - 00:59
     * - Sửu (1):  01:00 - 02:59
     * - Dần (2):  03:00 - 04:59
     * - Mão (3):  05:00 - 06:59
     * - Thìn (4): 07:00 - 08:59
     * - Tỵ (5):   09:00 - 10:59
     * - Ngọ (6):  11:00 - 12:59
     * - Mùi (7):  13:00 - 14:59
     * - Thân (8): 15:00 - 16:59
     * - Dậu (9):  17:00 - 18:59
     * - Tuất (10): 19:00 - 20:59
     * - Hợi (11): 21:00 - 22:59
     */
    public int getHourBranchIndex(int hour, int minute) {
        // Special case: Tý spans 23:00 - 00:59
        if (hour == 23 || hour == 0) {
            return 0; // Tý
        }

        // For hours 1-22, calculate index
        // Hours 1-2 = Sửu (1), 3-4 = Dần (2), etc.
        return (hour + 1) / 2;
    }

    /**
     * Get hour branch code from hour and minute.
     */
    public String getHourBranchCode(int hour, int minute) {
        int index = getHourBranchIndex(hour, minute);
        return HOUR_BRANCHES[index][0];
    }

    /**
     * Get hour branch Vietnamese name from hour and minute.
     */
    public String getHourBranchName(int hour, int minute) {
        int index = getHourBranchIndex(hour, minute);
        return HOUR_BRANCHES[index][1];
    }

    /**
     * Get hour branch info as array [code, name] from hour and minute.
     */
    public String[] getHourBranch(int hour, int minute) {
        int index = getHourBranchIndex(hour, minute);
        return HOUR_BRANCHES[index];
    }

    /**
     * Normalize a Can-Chi string for database lookup.
     * Removes extra whitespace, trims, but preserves diacritics.
     */
    public String normalizeCanChi(String canChi) {
        if (canChi == null) {
            return null;
        }
        return canChi.trim().replaceAll("\\s+", " ");
    }

    /**
     * Normalize Can-Chi for cache key (lowercase, no spaces).
     */
    public String normalizeCanChiForKey(String canChi) {
        if (canChi == null) {
            return null;
        }
        return canChi.trim().toLowerCase().replaceAll("\\s+", "");
    }
}

