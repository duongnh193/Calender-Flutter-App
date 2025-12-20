package com.duong.lichvanien.tuvi.dto.interpretation;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Overview section containing general interpretations about the chart owner.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "General overview interpretation of the Tu Vi chart")
public class OverviewSection {

    @Schema(description = "Introduction paragraph about the overall chart")
    private String introduction;

    // === Bản mệnh ===
    @Schema(description = "Nạp Âm element name", example = "Sơn Đầu Hỏa")
    private String banMenhName;

    @Schema(description = "Nạp Âm element category", example = "HOA")
    private String banMenhNguHanh;

    @Schema(description = "Detailed interpretation of Bản mệnh (Nạp Âm)")
    private String banMenhInterpretation;

    // === Cục mệnh ===
    @Schema(description = "Cục name", example = "Thổ ngũ cục")
    private String cucName;

    @Schema(description = "Cục value", example = "5")
    private int cucValue;

    @Schema(description = "Relationship between Bản mệnh and Cục", example = "Cục Kim khắc Mệnh Mộc")
    private String menhCucRelation;

    @Schema(description = "Detailed interpretation of Cục mệnh")
    private String cucInterpretation;

    // === Chủ mệnh ===
    @Schema(description = "Main star governing Mệnh palace", example = "Tử Vi")
    private String chuMenh;

    @Schema(description = "Detailed interpretation of Chủ mệnh star")
    private String chuMenhInterpretation;

    // === Chủ thân ===
    @Schema(description = "Main star governing Thân palace", example = "Thiên Tướng")
    private String chuThan;

    @Schema(description = "Detailed interpretation of Chủ thân star")
    private String chuThanInterpretation;

    // === Lai nhân ===
    @Schema(description = "Palace where Thân resides", example = "cung Huynh Đệ")
    private String thanCu;

    @Schema(description = "Detailed interpretation of Lai nhân (Thân cư)")
    private String laiNhanInterpretation;

    // === Cân lượng ===
    @Schema(description = "Bone weight calculation value", example = "3 lượng 3 chỉ")
    private String canLuong;

    @Schema(description = "Detailed interpretation of Cân lượng (bone weight)")
    private String canLuongInterpretation;

    // === Thân cư analysis ===
    @Schema(description = "Whether Thân is in the same palace as Mệnh")
    private boolean thanMenhDongCung;

    @Schema(description = "Detailed interpretation of Thân cư relationship with Mệnh")
    private String thanCuInterpretation;

    // === Âm Dương / Thuận Nghịch ===
    @Schema(description = "Âm/Dương and Thuận/Nghịch direction", example = "Âm nam - Nghịch lý")
    private String thuanNghich;

    @Schema(description = "Interpretation of Thuận/Nghịch direction")
    private String thuanNghichInterpretation;

    // === Overall summary ===
    @Schema(description = "Overall summary and general advice")
    private String overallSummary;
}
