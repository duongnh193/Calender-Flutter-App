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
@Schema(description = "Daily horoscope response")
public class HoroscopeDailyResponse {

    @Schema(description = "Zodiac information")
    private ZodiacShortDto zodiac;

    @Schema(description = "Date in ISO format (YYYY-MM-DD)", example = "2025-12-09")
    private String date;

    @Schema(description = "Daily summary")
    private String summary;

    @Schema(description = "Love and relationships")
    private String love;

    @Schema(description = "Career predictions")
    private String career;

    @Schema(description = "Fortune and finance")
    private String fortune;

    @Schema(description = "Health predictions")
    private String health;

    @Schema(description = "Lucky color for the day", example = "Đỏ")
    private String luckyColor;

    @Schema(description = "Lucky numbers", example = "3,7")
    private String luckyNumber;

    @Schema(description = "Additional metadata")
    private Map<String, Object> metadata;
}
