package com.duong.lichvanien.tuvi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Center section information of Tu Vi chart.
 * Contains birth info, destiny calculations, and key metadata.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Center information of the Tu Vi chart")
public class CenterInfo {

    // Personal info (display only)
    @Schema(description = "Name (if provided)", example = "Thân Chủ 2026")
    private String name;

    @Schema(description = "Birthplace (if provided)", example = "Hà Nội")
    private String birthPlace;

    // Birth date/time info
    @Schema(description = "Solar birth date (yyyy-MM-dd)", example = "1995-03-02")
    private String solarDate;

    @Schema(description = "Lunar year Can-Chi", example = "Ất Hợi")
    private String lunarYearCanChi;

    @Schema(description = "Lunar year number", example = "1995")
    private int lunarYear;

    @Schema(description = "Lunar month number", example = "2")
    private int lunarMonth;

    @Schema(description = "Lunar month Can-Chi", example = "Kỷ Mão")
    private String lunarMonthCanChi;

    @Schema(description = "Whether lunar month is leap month")
    private boolean isLeapMonth;

    @Schema(description = "Lunar day number", example = "2")
    private int lunarDay;

    @Schema(description = "Lunar day Can-Chi", example = "Nhâm Thìn")
    private String lunarDayCanChi;

    @Schema(description = "Birth hour (0-23)", example = "8")
    private int birthHour;

    @Schema(description = "Birth minute (0-59)", example = "30")
    private int birthMinute;

    @Schema(description = "Birth hour Can-Chi", example = "Giáp Thìn")
    private String birthHourCanChi;

    @Schema(description = "Hour branch index (0-11 for 12 branches)", example = "4")
    private int hourBranchIndex;

    // Gender & Yin-Yang
    @Schema(description = "Gender", example = "female")
    private String gender;

    @Schema(description = "Âm/Dương classification", example = "Âm")
    private String amDuong;

    @Schema(description = "Direction of cycle (Thuận/Nghịch)", example = "Thuận lý")
    private String thuanNghich;

    // Destiny calculations
    @Schema(description = "Bản mệnh (Nạp Âm element)", example = "Sơn Đầu Hỏa")
    private String banMenh;

    @Schema(description = "Bản mệnh Ngũ Hành", example = "HOA")
    private String banMenhNguHanh;

    @Schema(description = "Bản mệnh description", example = "Lửa trên núi")
    private String banMenhDescription;

    @Schema(description = "Cục (Bureau/Destiny type)", example = "Thổ ngũ cục")
    private String cuc;

    @Schema(description = "Cục value (2-6)", example = "5")
    private int cucValue;

    @Schema(description = "Cục Ngũ Hành", example = "THO")
    private String cucNguHanh;

    @Schema(description = "Mệnh-Cục relationship", example = "Mệnh Hỏa sinh Cục Thổ")
    private String menhCucRelation;

    @Schema(description = "Chủ mệnh star", example = "Cự Môn")
    private String chuMenh;

    @Schema(description = "Whether Cung Mệnh has no Chính tinh (special case - Mệnh vô Chính tinh)", example = "false")
    private Boolean menhKhongChinhTinh;

    @Schema(description = "Chủ thân star", example = "Thiên Cơ")
    private String chuThan;

    @Schema(description = "Cân lượng (weight in lượng and chỉ)", example = "3 lượng 5 chỉ")
    private String canLuong;

    @Schema(description = "Lai nhân (origin palace)", example = "cung Phu Thê")
    private String laiNhan;

    @Schema(description = "Thân cư palace", example = "Tài Bạch")
    private String thanCu;

    @Schema(description = "Additional note", example = "Nguyệt vận tháng 10 & tiểu vận năm 2025 âm lịch")
    private String note;
}
