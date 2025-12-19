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

    // New fields from V4 migration
    @Schema(description = "Cung Mệnh with stars and interpretation")
    private String cungMenh;

    @Schema(description = "Cung Xung Chiếu")
    private String cungXungChieu;

    @Schema(description = "Cung Tam Hợp")
    private String cungTamHop;

    @Schema(description = "Cung Nhị Hợp")
    private String cungNhiHop;

    @Schema(description = "Vận hạn details (JSON)")
    private String vanHan;

    @Schema(description = "Tứ trụ (JSON)")
    private String tuTru;

    @Schema(description = "Phong thủy may mắn (JSON)")
    private String phongThuy;

    @Schema(description = "Q&A section (JSON array)")
    private String qaSection;

    @Schema(description = "Conclusion")
    private String conclusion;

    @Schema(description = "Monthly breakdown (JSON object)")
    private String monthlyBreakdown;

    @Schema(description = "Additional metadata")
    private Map<String, Object> metadata;
}
