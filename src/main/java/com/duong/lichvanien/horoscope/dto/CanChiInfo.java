package com.duong.lichvanien.horoscope.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * Complete Can-Chi information resolved from birth date and time.
 * Used internally by the resolver service.
 */
@Data
@Builder
@Schema(description = "Complete Can-Chi information from birth data")
public class CanChiInfo {

    // Year Can-Chi
    @Schema(description = "Thiên Can of year", example = "Canh")
    private String canYear;

    @Schema(description = "Địa Chi of year", example = "Tuất")
    private String chiYear;

    @Schema(description = "Full Can-Chi year combination", example = "Canh Tuất")
    private String canChiYear;

    // Zodiac info
    @Schema(description = "Zodiac ID (1-12)", example = "11")
    private Long zodiacId;

    @Schema(description = "Zodiac code", example = "tuat")
    private String zodiacCode;

    @Schema(description = "Zodiac Vietnamese name", example = "Tuất")
    private String zodiacName;

    // Day Can-Chi
    @Schema(description = "Thiên Can of day", example = "Giáp")
    private String canDay;

    @Schema(description = "Địa Chi of day", example = "Tý")
    private String chiDay;

    @Schema(description = "Full Can-Chi day combination", example = "Giáp Tý")
    private String canChiDay;

    // Month Can-Chi
    @Schema(description = "Thiên Can of month", example = "Mậu")
    private String canMonth;

    @Schema(description = "Địa Chi of month", example = "Dần")
    private String chiMonth;

    @Schema(description = "Full Can-Chi month combination", example = "Mậu Dần")
    private String canChiMonth;

    // Hour branch
    @Schema(description = "Hour branch code", example = "ti")
    private String hourBranchCode;

    @Schema(description = "Hour branch Vietnamese name", example = "Tý")
    private String hourBranchName;

    @Schema(description = "Hour branch index (0-11)", example = "0")
    private Integer hourBranchIndex;

    // Source info
    @Schema(description = "Original date (solar or lunar)", example = "1990-02-15")
    private String originalDate;

    @Schema(description = "Converted solar date (if input was lunar)", example = "1990-03-12")
    private String solarDate;

    @Schema(description = "Whether the original date was lunar", example = "false")
    private Boolean wasLunar;

    @Schema(description = "Lunar year", example = "1990")
    private Integer lunarYear;

    @Schema(description = "Lunar month", example = "1")
    private Integer lunarMonth;

    @Schema(description = "Lunar day", example = "20")
    private Integer lunarDay;

    @Schema(description = "Whether it was a leap month", example = "false")
    private Boolean wasLeapMonth;
}

