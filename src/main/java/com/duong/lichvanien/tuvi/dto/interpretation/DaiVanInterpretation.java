package com.duong.lichvanien.tuvi.dto.interpretation;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Interpretation for a single Đại Vận (Major Cycle) period.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Detailed interpretation of a Đại Vận period")
public class DaiVanInterpretation {

    @Schema(description = "Start age of this Đại Vận period", example = "5")
    private int startAge;

    @Schema(description = "End age of this Đại Vận period", example = "14")
    private int endAge;

    @Schema(description = "Palace code for this period", example = "MENH")
    private String palaceCode;

    @Schema(description = "Palace Vietnamese name", example = "Mệnh")
    private String palaceName;

    @Schema(description = "Palace Địa Chi", example = "Hợi")
    private String palaceChi;

    @Schema(description = "Brief summary of this period (1-2 sentences)")
    private String summary;

    @Schema(description = "Detailed interpretation text from AI")
    private String interpretation;

    @Schema(description = "Key themes for this period")
    private String keyThemes;

    @Schema(description = "Advice and recommendations for this period")
    private String advice;

    @Schema(description = "Whether this period has Tuần marker")
    private boolean hasTuan;

    @Schema(description = "Whether this period has Triệt marker")
    private boolean hasTriet;
}

