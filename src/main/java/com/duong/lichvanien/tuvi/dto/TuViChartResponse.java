package com.duong.lichvanien.tuvi.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Complete Tu Vi chart response with all palaces, stars, and metadata.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Complete Tu Vi (Purple Star Astrology) chart")
public class TuViChartResponse {

    @Schema(description = "Center section with birth info and destiny calculations")
    private CenterInfo center;

    @Schema(description = "12 palaces with stars")
    private List<PalaceInfo> palaces;

    @Schema(description = "Special markers (Tuần, Triệt)")
    private MarkerInfo markers;

    @Schema(description = "Đại Vận / Tiểu Vận cycle information")
    private CycleInfo cycles;

    @Schema(description = "Calculation timestamp (for caching)")
    private String calculatedAt;

    @Schema(description = "Unique hash identifying this chart (for payment/caching)")
    private String chartHash;

    @Schema(description = "Debug information (only in dev mode)")
    private Map<String, Object> debug;
}
