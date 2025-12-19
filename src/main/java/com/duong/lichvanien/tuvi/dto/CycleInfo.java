package com.duong.lichvanien.tuvi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Information about Đại Vận (Major Cycle) and Tiểu Vận (Minor Cycle).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Cycle information (Đại Vận / Tiểu Vận)")
public class CycleInfo {

    @Schema(description = "Direction of cycles", example = "THUAN")
    private String direction;

    @Schema(description = "Direction text", example = "Thuận")
    private String directionText;

    @Schema(description = "Starting age for Đại Vận", example = "5")
    private int daiVanStartAge;

    @Schema(description = "Cycle period in years", example = "10")
    private int cyclePeriod;

    @Schema(description = "List of Đại Vận entries")
    private List<DaiVanEntry> daiVanList;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Individual Đại Vận entry")
    public static class DaiVanEntry {

        @Schema(description = "Palace index (0-11)", example = "0")
        private int palaceIndex;

        @Schema(description = "Palace name", example = "Mệnh")
        private String palaceName;

        @Schema(description = "Start age", example = "5")
        private int startAge;

        @Schema(description = "End age", example = "14")
        private int endAge;

        @Schema(description = "Label shown on chart", example = "5")
        private String label;
    }
}
