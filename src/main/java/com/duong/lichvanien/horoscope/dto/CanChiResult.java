package com.duong.lichvanien.horoscope.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Can-Chi calculation result from a birth date")
public class CanChiResult {

    @Schema(description = "Thiên Can of year", example = "Giáp")
    private String canYear;

    @Schema(description = "Địa Chi of year (zodiac)", example = "Tý")
    private String chiYear;

    @Schema(description = "Full Can-Chi year combination", example = "Giáp Tý")
    private String canChiYear;

    @Schema(description = "Zodiac ID (1-12)", example = "1")
    private Long zodiacId;

    @Schema(description = "Zodiac code", example = "ti")
    private String zodiacCode;

    @Schema(description = "Thiên Can of day (optional)")
    private String canDay;

    @Schema(description = "Địa Chi of day (optional)")
    private String chiDay;

    @Schema(description = "Full Can-Chi day combination (optional)")
    private String canChiDay;

    @Schema(description = "Thiên Can of month (optional)")
    private String canMonth;

    @Schema(description = "Địa Chi of month (optional)")
    private String chiMonth;

    @Schema(description = "Full Can-Chi month combination (optional)")
    private String canChiMonth;
}

