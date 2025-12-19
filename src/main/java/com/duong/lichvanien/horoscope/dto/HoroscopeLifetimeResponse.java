package com.duong.lichvanien.horoscope.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Lifetime horoscope response based on Can-Chi and gender")
public class HoroscopeLifetimeResponse {

    @Schema(description = "Zodiac ID", example = "1")
    private Long zodiacId;

    @Schema(description = "Zodiac code", example = "ti")
    private String zodiacCode;

    @Schema(description = "Zodiac name in Vietnamese", example = "Tý")
    private String zodiacName;

    @Schema(description = "Can-Chi combination", example = "Giáp Tý")
    private String canChi;

    @Schema(description = "Gender", example = "male")
    private String gender;

    @Schema(description = "Overview of lifetime fortune")
    private String overview;

    @Schema(description = "Career and profession analysis")
    private String career;

    @Schema(description = "Love and marriage analysis")
    private String love;

    @Schema(description = "Health analysis")
    private String health;

    @Schema(description = "Family relationships analysis")
    private String family;

    @Schema(description = "Fortune and wealth analysis")
    private String fortune;

    @Schema(description = "Unlucky periods and things to avoid")
    private String unlucky;

    @Schema(description = "Advice and recommendations")
    private String advice;

    // New fields from V4 migration
    @Schema(description = "Love by month group 1 (months 5, 6, 9)")
    private String loveByMonthGroup1;

    @Schema(description = "Love by month group 2 (months 1, 2, 7, 10, 11, 12)")
    private String loveByMonthGroup2;

    @Schema(description = "Love by month group 3 (months 3, 4, 8)")
    private String loveByMonthGroup3;

    @Schema(description = "Compatible ages for business (JSON array)")
    private String compatibleAges;

    @Schema(description = "Difficult years (JSON array)")
    private String difficultYears;

    @Schema(description = "Incompatible ages (JSON array)")
    private String incompatibleAges;

    @Schema(description = "Yearly progression (JSON object)")
    private String yearlyProgression;

    @Schema(description = "Ritual guidance for star worship")
    private String ritualGuidance;

    @Schema(description = "Additional metadata")
    private Map<String, Object> metadata;
}

