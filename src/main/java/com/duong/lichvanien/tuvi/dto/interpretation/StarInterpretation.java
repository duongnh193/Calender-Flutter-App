package com.duong.lichvanien.tuvi.dto.interpretation;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Interpretation for a single star in a palace.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Detailed interpretation of a star in a palace")
public class StarInterpretation {

    @Schema(description = "Star code", example = "TU_VI")
    private String starCode;

    @Schema(description = "Star Vietnamese name", example = "Tử Vi")
    private String starName;

    @Schema(description = "Star type", example = "CHINH_TINH")
    private String starType;

    @Schema(description = "Star brightness level", example = "MIEU")
    private String brightness;

    @Schema(description = "Detailed interpretation for this star in this palace context")
    private String interpretation;

    @Schema(description = "Short summary of star's influence")
    private String summary;
}
