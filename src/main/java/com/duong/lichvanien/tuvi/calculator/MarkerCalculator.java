package com.duong.lichvanien.tuvi.calculator;

import com.duong.lichvanien.tuvi.dto.MarkerInfo;
import com.duong.lichvanien.tuvi.enums.DiaChi;
import com.duong.lichvanien.tuvi.enums.ThienCan;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.UtilityClass;

/**
 * Calculator for Tuần and Triệt markers.
 * These are "empty" or "cut-off" positions that affect stars in those palaces.
 */
@UtilityClass
public class MarkerCalculator {

    /**
     * Calculate Tuần (空亡 - Void) positions.
     * Tuần consists of 2 consecutive DiaChi positions determined by Year Can-Chi.
     * 
     * The 60 Can-Chi cycle is divided into 6 groups of 10 (one for each Can cycle).
     * Each group has 2 DiaChi positions that are "void".
     * 
     * @param yearCan Year Thiên Can
     * @param yearChi Year Địa Chi
     * @return Pair of DiaChi representing Tuần start and end
     */
    public static DiaChi[] calculateTuan(ThienCan yearCan, DiaChi yearChi) {
        // Tuần is calculated based on which 10-year cycle the year belongs to
        // In each 10-year cycle (e.g., Giáp Tý to Quý Dậu), the 2 remaining Chi (Tuất, Hợi) are Tuần
        
        // Find the starting Chi of the current 10-year cycle
        // The cycle starts at a Can=Giáp (index 0)
        int canIndex = yearCan.getIndex();
        int chiIndex = yearChi.getIndex();
        
        // Calculate the offset from the start of the cycle
        // The starting Chi of the cycle = chiIndex - canIndex (mod 12)
        int cycleStartChi = Math.floorMod(chiIndex - canIndex, 12);
        
        // Tuần starts at cycleStartChi + 10 (the 11th position, which is not covered in the 10-day cycle)
        int tuanStart = (cycleStartChi + 10) % 12;
        int tuanEnd = (cycleStartChi + 11) % 12;
        
        return new DiaChi[] { DiaChi.fromIndex(tuanStart), DiaChi.fromIndex(tuanEnd) };
    }

    /**
     * Calculate Triệt (截路 - Cutoff) positions.
     * Triệt consists of 2 consecutive DiaChi positions determined by Year Can.
     * 
     * @param yearCan Year Thiên Can
     * @return Pair of DiaChi representing Triệt start and end
     */
    public static DiaChi[] calculateTriet(ThienCan yearCan) {
        // Triệt positions based on Year Can
        // Each pair of Can has a specific Triệt position
        int canIndex = yearCan.getIndex();
        
        int trietStart;
        switch (canIndex % 5) {
            case 0: // Giáp, Kỷ -> Thân Dậu
                trietStart = 8;
                break;
            case 1: // Ất, Canh -> Ngọ Mùi
                trietStart = 6;
                break;
            case 2: // Bính, Tân -> Thìn Tỵ
                trietStart = 4;
                break;
            case 3: // Đinh, Nhâm -> Dần Mão
                trietStart = 2;
                break;
            case 4: // Mậu, Quý -> Tý Sửu
                trietStart = 0;
                break;
            default:
                trietStart = 0;
        }
        
        int trietEnd = (trietStart + 1) % 12;
        
        return new DiaChi[] { DiaChi.fromIndex(trietStart), DiaChi.fromIndex(trietEnd) };
    }

    /**
     * Build MarkerInfo with both Tuần and Triệt.
     */
    public static MarkerInfo buildMarkerInfo(ThienCan yearCan, DiaChi yearChi) {
        DiaChi[] tuan = calculateTuan(yearCan, yearChi);
        DiaChi[] triet = calculateTriet(yearCan);
        
        return MarkerInfo.builder()
                .tuanStart(tuan[0].name())
                .tuanEnd(tuan[1].name())
                .tuanText("Tuần " + tuan[0].getText() + "-" + tuan[1].getText())
                .trietStart(triet[0].name())
                .trietEnd(triet[1].name())
                .trietText("Triệt " + triet[0].getText() + "-" + triet[1].getText())
                .build();
    }

    /**
     * Check if a DiaChi is in Tuần position.
     */
    public static boolean isInTuan(DiaChi chi, ThienCan yearCan, DiaChi yearChi) {
        DiaChi[] tuan = calculateTuan(yearCan, yearChi);
        return chi == tuan[0] || chi == tuan[1];
    }

    /**
     * Check if a DiaChi is in Triệt position.
     */
    public static boolean isInTriet(DiaChi chi, ThienCan yearCan) {
        DiaChi[] triet = calculateTriet(yearCan);
        return chi == triet[0] || chi == triet[1];
    }
}
