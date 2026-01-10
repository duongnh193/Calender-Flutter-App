package com.duong.lichvanien.tuvi.dto.interpretation;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Interpretation for a single Tiểu Vận (Minor Cycle) year.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Detailed interpretation of a Tiểu Vận year")
public class TieuVanInterpretation {

    @Schema(description = "Age for this Tiểu Vận", example = "25")
    private int age;

    @Schema(description = "Year for this Tiểu Vận", example = "2025")
    private int year;

    @Schema(description = "Palace code for this year", example = "QUAN_LOC")
    private String palaceCode;

    @Schema(description = "Palace Vietnamese name", example = "Quan Lộc")
    private String palaceName;

    @Schema(description = "Palace Địa Chi", example = "Tý")
    private String palaceChi;

    @Schema(description = "Brief summary of this year")
    private String summary;

    @Schema(description = "Detailed interpretation text from AI")
    private String interpretation;

    @Schema(description = "Key events or themes for this year")
    private String keyEvents;
}

