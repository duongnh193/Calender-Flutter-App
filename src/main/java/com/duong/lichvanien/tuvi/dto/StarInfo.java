package com.duong.lichvanien.tuvi.dto;

import com.duong.lichvanien.tuvi.enums.NguHanh;
import com.duong.lichvanien.tuvi.enums.Star;
import com.duong.lichvanien.tuvi.enums.StarBrightness;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Information about a star placed in a palace.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Information about a star in a palace")
public class StarInfo {

    @Schema(description = "Star code (enum name)", example = "TU_VI")
    private String code;

    @Schema(description = "Star Vietnamese name", example = "Tử Vi")
    private String name;

    @Schema(description = "Star type", example = "CHINH_TINH")
    private String type;

    @Schema(description = "Star element (Ngũ Hành)", example = "THO")
    private String nguHanh;

    @Schema(description = "Star brightness in this palace", example = "MIEU")
    private String brightness;

    @Schema(description = "Short brightness code (M/V/Đ/B/H)", example = "M")
    private String brightnessCode;

    @Schema(description = "Whether this is a main star (chính tinh)")
    private boolean isMainStar;

    @Schema(description = "Whether this star is positive (cát tinh)")
    private Boolean isPositive;

    public static StarInfo from(Star star, StarBrightness brightness) {
        return StarInfo.builder()
                .code(star.name())
                .name(star.getText())
                .type(star.getType().name())
                .nguHanh(star.getNguHanh().name())
                .brightness(brightness != null ? brightness.name() : null)
                .brightnessCode(brightness != null ? brightness.getShortCode() : null)
                .isMainStar(star.getType() == Star.StarType.CHINH_TINH)
                .build();
    }
}
