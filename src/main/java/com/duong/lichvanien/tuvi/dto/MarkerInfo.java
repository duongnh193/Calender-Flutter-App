package com.duong.lichvanien.tuvi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Information about special markers (Tuần, Triệt, etc.).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Special markers on the chart (Tuần, Triệt)")
public class MarkerInfo {

    @Schema(description = "Tuần (void) first palace Địa Chi", example = "TY")
    private String tuanStart;

    @Schema(description = "Tuần second palace Địa Chi", example = "SUU")
    private String tuanEnd;

    @Schema(description = "Tuần text", example = "Tuần Tý-Sửu")
    private String tuanText;

    @Schema(description = "Triệt (cutoff) first palace Địa Chi", example = "THIN")
    private String trietStart;

    @Schema(description = "Triệt second palace Địa Chi", example = "TI")
    private String trietEnd;

    @Schema(description = "Triệt text", example = "Triệt Thìn-Tỵ")
    private String trietText;
}
