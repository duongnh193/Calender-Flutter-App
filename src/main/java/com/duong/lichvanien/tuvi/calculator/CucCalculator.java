package com.duong.lichvanien.tuvi.calculator;

import com.duong.lichvanien.tuvi.enums.DiaChi;
import com.duong.lichvanien.tuvi.enums.NgucGioi;
import com.duong.lichvanien.tuvi.enums.NguHanh;
import com.duong.lichvanien.tuvi.enums.ThienCan;
import lombok.experimental.UtilityClass;

/**
 * Calculator for determining Cục (Bureau/Destiny type).
 * The Cục determines the starting position of the Tử Vi star and cycle direction.
 * 
 * The Cục is derived from the Nạp Âm (element) of the Mệnh palace's Can-Chi combination.
 * - First, calculate the Thiên Can of the Mệnh palace based on Year Can and position
 * - Then, get the Nạp Âm of the (MệnhCan, MệnhChi) pair
 * - Map the Ngũ Hành of Nạp Âm to Cục value
 */
@UtilityClass
public class CucCalculator {

    /**
     * Calculate the Cục (Bureau) based on year Can and Mệnh palace position.
     * 
     * The correct algorithm:
     * 1. Calculate the Thiên Can of the Mệnh palace using An Cung rules
     * 2. Get the Nạp Âm of (Mệnh Can, Mệnh Chi)
     * 3. Map the Ngũ Hành to Cục value
     * 
     * @param yearCan The Thiên Can of the lunar year
     * @param menhPalaceChi The Địa Chi where Mệnh palace is located
     * @return The Cục (NgucGioi enum)
     */
    public static NgucGioi calculateCuc(ThienCan yearCan, DiaChi menhPalaceChi) {
        // Step 1: Calculate Mệnh palace's Thiên Can
        ThienCan menhCan = calculateMenhCan(yearCan, menhPalaceChi);
        
        // Step 2: Get Nạp Âm of the Mệnh palace
        NapAmCalculator.NapAm napAm = NapAmCalculator.getNapAm(menhCan, menhPalaceChi);
        
        // Step 3: Map Ngũ Hành to Cục value
        return nguHanhToCuc(napAm.getNguHanh());
    }

    /**
     * Get Cục value directly (2-6).
     */
    public static int getCucValue(ThienCan yearCan, DiaChi menhPalaceChi) {
        return calculateCuc(yearCan, menhPalaceChi).getValue();
    }

    /**
     * Calculate the Thiên Can of the Mệnh palace based on Year Can and Mệnh Chi position.
     * Uses the An Cung (安宫) method:
     * - Giáp/Kỷ year -> Dần position starts with Bính
     * - Ất/Canh year -> Dần position starts with Mậu
     * - Bính/Tân year -> Dần position starts with Canh
     * - Đinh/Nhâm year -> Dần position starts with Nhâm
     * - Mậu/Quý year -> Dần position starts with Giáp
     */
    private static ThienCan calculateMenhCan(ThienCan yearCan, DiaChi menhChi) {
        int yearCanGroup = yearCan.getIndex() % 5;
        int baseCanAtDan;
        
        switch (yearCanGroup) {
            case 0: // Giáp/Kỷ
                baseCanAtDan = 2; // Bính
                break;
            case 1: // Ất/Canh
                baseCanAtDan = 4; // Mậu
                break;
            case 2: // Bính/Tân
                baseCanAtDan = 6; // Canh
                break;
            case 3: // Đinh/Nhâm
                baseCanAtDan = 8; // Nhâm
                break;
            case 4: // Mậu/Quý
                baseCanAtDan = 0; // Giáp
                break;
            default:
                baseCanAtDan = 0;
        }
        
        // Calculate offset from Dần (index 2)
        int chiOffset = (menhChi.getIndex() - 2 + 12) % 12;
        
        return ThienCan.fromIndex(baseCanAtDan + chiOffset);
    }

    /**
     * Map Ngũ Hành to Cục value.
     * - Kim -> Kim tứ cục (4)
     * - Mộc -> Mộc tam cục (3)
     * - Thủy -> Thủy nhị cục (2)
     * - Hỏa -> Hỏa lục cục (6)
     * - Thổ -> Thổ ngũ cục (5)
     */
    private static NgucGioi nguHanhToCuc(NguHanh nguHanh) {
        switch (nguHanh) {
            case KIM: return NgucGioi.KIM_TU_CUC;
            case MOC: return NgucGioi.MOC_TAM_CUC;
            case THUY: return NgucGioi.THUY_NHI_CUC;
            case HOA: return NgucGioi.HOA_LUC_CUC;
            case THO: return NgucGioi.THO_NGU_CUC;
            default: return NgucGioi.THUY_NHI_CUC;
        }
    }
}
