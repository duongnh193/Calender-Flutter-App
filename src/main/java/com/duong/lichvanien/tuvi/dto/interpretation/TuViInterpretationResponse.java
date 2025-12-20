package com.duong.lichvanien.tuvi.dto.interpretation;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Complete interpretation response for a Tu Vi chart.
 * Contains detailed analysis for all 12 palaces plus overview section.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Complete Tu Vi chart interpretation with all palace analyses")
public class TuViInterpretationResponse {

    @Schema(description = "Chart owner's name")
    private String name;

    @Schema(description = "Chart owner's gender", example = "male")
    private String gender;

    @Schema(description = "Birth date in ISO format", example = "1995-03-02")
    private String birthDate;

    @Schema(description = "Birth hour", example = "8")
    private int birthHour;

    @Schema(description = "Lunar year Can-Chi", example = "Ất Hợi")
    private String lunarYearCanChi;

    // === Section 1: Overview ===
    @Schema(description = "Section 1: General overview interpretation")
    private OverviewSection overview;

    // === Section 2: Mệnh Palace (Most detailed) ===
    @Schema(description = "Section 2: Mệnh palace (Destiny) - most detailed interpretation")
    private PalaceInterpretation menhInterpretation;

    // === Section 3: Quan Lộc Palace ===
    @Schema(description = "Section 3: Quan Lộc palace (Career) interpretation")
    private PalaceInterpretation quanLocInterpretation;

    // === Section 4: Tài Bạch Palace ===
    @Schema(description = "Section 4: Tài Bạch palace (Wealth) interpretation")
    private PalaceInterpretation taiBachInterpretation;

    // === Section 5: Phu Thê Palace ===
    @Schema(description = "Section 5: Phu Thê palace (Spouse/Love) interpretation")
    private PalaceInterpretation phuTheInterpretation;

    // === Section 6: Tật Ách Palace ===
    @Schema(description = "Section 6: Tật Ách palace (Health) interpretation")
    private PalaceInterpretation tatAchInterpretation;

    // === Section 7: Tử Tức Palace ===
    @Schema(description = "Section 7: Tử Tức palace (Children) interpretation")
    private PalaceInterpretation tuTucInterpretation;

    // === Section 8: Điền Trạch Palace ===
    @Schema(description = "Section 8: Điền Trạch palace (Property) interpretation")
    private PalaceInterpretation dienTrachInterpretation;

    // === Section 9: Phụ Mẫu Palace ===
    @Schema(description = "Section 9: Phụ Mẫu palace (Parents) interpretation")
    private PalaceInterpretation phuMauInterpretation;

    // === Section 10: Huynh Đệ Palace ===
    @Schema(description = "Section 10: Huynh Đệ palace (Siblings) interpretation")
    private PalaceInterpretation huynhDeInterpretation;

    // === Section 11: Phúc Đức Palace ===
    @Schema(description = "Section 11: Phúc Đức palace (Fortune/Ancestors) interpretation")
    private PalaceInterpretation phucDucInterpretation;

    // === Section 12: Nô Bộc Palace ===
    @Schema(description = "Section 12: Nô Bộc palace (Servants/Employees) interpretation")
    private PalaceInterpretation noBocInterpretation;

    // === Section 13: Thiên Di Palace ===
    @Schema(description = "Section 13: Thiên Di palace (Travel/Movement) interpretation")
    private PalaceInterpretation thienDiInterpretation;

    // === Metadata ===
    @Schema(description = "Timestamp when interpretation was generated")
    private String generatedAt;

    @Schema(description = "AI model used for generation", example = "gpt-4")
    private String aiModel;
}
