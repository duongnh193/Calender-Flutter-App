package com.duong.lichvanien.horoscope.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Lifetime horoscope response computed from birth data")
public class LifetimeByBirthResponse {

    @Schema(description = "Zodiac ID (1-12)", example = "11")
    private Long zodiacId;

    @Schema(description = "Zodiac code", example = "tuat")
    private String zodiacCode;

    @Schema(description = "Zodiac name in Vietnamese", example = "Tuất")
    private String zodiacName;

    @Schema(description = "Computed Can-Chi combination (null if fallback)", example = "Canh Tuất")
    private String canChi;

    @Schema(description = "Gender", example = "male")
    private String gender;

    @Schema(description = "Hour branch (canh giờ)", example = "ti")
    private String hourBranch;

    @Schema(description = "Hour branch Vietnamese name", example = "Tý")
    private String hourBranchName;

    @Schema(description = "Fallback message when exact match not found")
    private String message;

    @Schema(description = "Whether this is a computed result", example = "true")
    private Boolean computed;

    @Schema(description = "Whether this is a fallback result", example = "false")
    private Boolean isFallback;

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

    @Schema(description = "Additional metadata including computation details")
    private Map<String, Object> metadata;
}

