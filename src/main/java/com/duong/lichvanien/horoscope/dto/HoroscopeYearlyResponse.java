package com.duong.lichvanien.horoscope.dto;

import com.duong.lichvanien.zodiac.dto.ZodiacShortDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Yearly horoscope response")
public class HoroscopeYearlyResponse {

    @Schema(description = "Zodiac information")
    private ZodiacShortDto zodiac;

    @Schema(description = "Year", example = "2025")
    private int year;

    @Schema(description = "Yearly summary")
    private String summary;

    @Schema(description = "Love and relationships")
    private String love;

    @Schema(description = "Career predictions")
    private String career;

    @Schema(description = "Fortune and finance")
    private String fortune;

    @Schema(description = "Health predictions")
    private String health;

    @Schema(description = "Warnings and things to avoid")
    private String warnings;

    @Schema(description = "Additional metadata")
    private Map<String, Object> metadata;
}
