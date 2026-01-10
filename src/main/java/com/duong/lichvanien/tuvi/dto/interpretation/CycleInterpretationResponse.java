package com.duong.lichvanien.tuvi.dto.interpretation;

import com.duong.lichvanien.tuvi.dto.CycleInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Complete response for Đại hạn/Tiểu vận interpretation.
 * This is the FREE preview content available to all users.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Complete Đại hạn/Tiểu vận interpretation response")
public class CycleInterpretationResponse {

    @Schema(description = "Chart hash for reference")
    private String chartHash;

    @Schema(description = "Chart owner's name")
    private String name;

    @Schema(description = "Chart owner's gender", example = "male")
    private String gender;

    @Schema(description = "Birth date in ISO format", example = "1995-03-02")
    private String birthDate;

    @Schema(description = "Lunar year Can-Chi", example = "Ất Hợi")
    private String lunarYearCanChi;

    // === FACT Data ===
    @Schema(description = "Cycle FACT data (direction, start age, periods)")
    private CycleInfo cycleInfo;

    // === Interpretation Data ===
    @Schema(description = "Overall summary of life cycles")
    private String overallCycleSummary;

    @Schema(description = "Introduction about Đại hạn/Tiểu vận system")
    private String introduction;

    @Schema(description = "List of Đại Vận interpretations (12 periods)")
    private List<DaiVanInterpretation> daiVanInterpretations;

    @Schema(description = "List of Tiểu Vận interpretations (optional, for specific years)")
    private List<TieuVanInterpretation> tieuVanInterpretations;

    @Schema(description = "General advice for navigating life cycles")
    private String generalAdvice;

    // === Metadata ===
    @Schema(description = "Timestamp when interpretation was generated")
    private String generatedAt;

    @Schema(description = "AI model used for generation", example = "grok-beta")
    private String aiModel;

    @Schema(description = "Whether this is from cache")
    private boolean fromCache;
}

