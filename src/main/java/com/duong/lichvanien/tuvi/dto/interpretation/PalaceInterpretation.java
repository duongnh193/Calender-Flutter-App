package com.duong.lichvanien.tuvi.dto.interpretation;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Interpretation for a single palace in the Tu Vi chart.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Detailed interpretation of a palace")
public class PalaceInterpretation {

    @Schema(description = "Palace code", example = "MENH")
    private String palaceCode;

    @Schema(description = "Palace Vietnamese name", example = "Mệnh")
    private String palaceName;

    @Schema(description = "Palace Địa Chi", example = "Hợi")
    private String palaceChi;

    @Schema(description = "Palace Can-Chi prefix", example = "D.Hợi")
    private String canChiPrefix;

    @Schema(description = "Summary of this palace interpretation (2-3 sentences)")
    private String summary;

    @Schema(description = "Introduction about this palace's meaning and significance")
    private String introduction;

    @Schema(description = "Detailed analysis of the palace based on stars and position")
    private String detailedAnalysis;

    @Schema(description = "Analysis based on gender")
    private String genderAnalysis;

    @Schema(description = "List of star interpretations in this palace")
    private List<StarInterpretation> starAnalyses;

    @Schema(description = "Whether Tuần (void) affects this palace")
    private boolean hasTuan;

    @Schema(description = "Whether Triệt (cutoff) affects this palace")
    private boolean hasTriet;

    @Schema(description = "Analysis of Tuần/Triệt effects if present")
    private String tuanTrietEffect;

    @Schema(description = "Advice and recommendations for this aspect of life")
    private String adviceSection;

    @Schema(description = "Conclusion paragraph")
    private String conclusion;
}
