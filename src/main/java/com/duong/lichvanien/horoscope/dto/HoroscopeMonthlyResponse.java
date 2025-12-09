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
@Schema(description = "Monthly horoscope response")
public class HoroscopeMonthlyResponse {

    @Schema(description = "Zodiac information")
    private ZodiacShortDto zodiac;

    @Schema(description = "Year", example = "2025")
    private int year;

    @Schema(description = "Month (1-12)", example = "12")
    private int month;

    @Schema(description = "Monthly summary")
    private String summary;

    @Schema(description = "Career predictions")
    private String career;

    @Schema(description = "Love and relationships")
    private String love;

    @Schema(description = "Health predictions")
    private String health;

    @Schema(description = "Fortune and finance")
    private String fortune;

    @Schema(description = "Additional metadata")
    private Map<String, Object> metadata;
}

