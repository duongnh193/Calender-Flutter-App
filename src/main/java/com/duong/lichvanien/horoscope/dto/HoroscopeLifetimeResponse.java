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

    @Schema(description = "Additional metadata")
    private Map<String, Object> metadata;
}

