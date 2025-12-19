package com.duong.lichvanien.tuvi.dto;

import com.duong.lichvanien.tuvi.enums.CungName;
import com.duong.lichvanien.tuvi.enums.DiaChi;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Information about a palace (cung) in the Tu Vi chart.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Information about a palace in the Tu Vi chart")
public class PalaceInfo {

    @Schema(description = "Palace position index (0-11)", example = "0")
    private int index;

    @Schema(description = "Palace name code", example = "MENH")
    private String nameCode;

    @Schema(description = "Palace Vietnamese name", example = "Mệnh")
    private String name;

    @Schema(description = "Địa Chi of this palace", example = "HOI")
    private String diaChiCode;

    @Schema(description = "Địa Chi Vietnamese text", example = "Hợi")
    private String diaChi;

    @Schema(description = "Can-Chi prefix (e.g., D.Hợi)", example = "D.Hợi")
    private String canChiPrefix;

    @Schema(description = "List of stars in this palace")
    private List<StarInfo> stars;

    @Schema(description = "Đại vận start age for this palace", example = "5")
    private Integer daiVanStartAge;

    @Schema(description = "Đại vận label (e.g., 'Th.1' for position 1)", example = "Th.1")
    private String daiVanLabel;

    @Schema(description = "Whether Tuần (void) is in this palace")
    private boolean hasTuan;

    @Schema(description = "Whether Triệt (cutoff) is in this palace")
    private boolean hasTriet;

    @Schema(description = "Trường sinh stage name if applicable", example = "Trường sinh")
    private String truongSinhStage;

    @Schema(description = "Whether this palace contains Thân (body)")
    private boolean isThanCu;

    public static PalaceInfo create(int index, CungName cungName, DiaChi diaChi, String canChiPrefix) {
        return PalaceInfo.builder()
                .index(index)
                .nameCode(cungName.name())
                .name(cungName.getText())
                .diaChiCode(diaChi.name())
                .diaChi(diaChi.getText())
                .canChiPrefix(canChiPrefix)
                .build();
    }
}
