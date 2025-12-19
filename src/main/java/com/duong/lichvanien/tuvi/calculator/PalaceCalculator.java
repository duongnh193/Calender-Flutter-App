package com.duong.lichvanien.tuvi.calculator;

import com.duong.lichvanien.tuvi.dto.PalaceInfo;
import com.duong.lichvanien.tuvi.enums.AmDuong;
import com.duong.lichvanien.tuvi.enums.CungName;
import com.duong.lichvanien.tuvi.enums.DiaChi;
import com.duong.lichvanien.tuvi.enums.NgucGioi;
import com.duong.lichvanien.tuvi.enums.ThienCan;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Calculator for palace (cung) positions in Tu Vi chart.
 * Determines Mệnh, Thân, and all 12 palaces based on birth data.
 */
public class PalaceCalculator {

    /**
     * Result containing all palace information.
     */
    @Data
    @Builder
    public static class PalaceLayout {
        private DiaChi menhChi;           // Địa Chi of Mệnh palace
        private DiaChi thanChi;           // Địa Chi of Thân palace
        private int thanPalaceIndex;      // Palace name index where Thân resides
        private CungName thanCungName;    // Palace name where Thân resides
        private NgucGioi cuc;             // The Cục (Bureau)
        private boolean isThuan;          // Direction: true = thuận (forward), false = nghịch (backward)
        private List<PalaceInfo> palaces; // All 12 palaces
    }

    /**
     * Calculate all palace positions.
     * 
     * @param lunarMonth Lunar month (1-12)
     * @param hourBranchIndex Hour branch index (0-11)
     * @param yearCan Thiên Can of lunar year
     * @param isMale Whether the person is male
     * @param yearAmDuong Whether the year is Âm or Dương
     * @return PalaceLayout containing all palace information
     */
    public static PalaceLayout calculate(
            int lunarMonth, 
            int hourBranchIndex, 
            ThienCan yearCan, 
            boolean isMale,
            AmDuong yearAmDuong) {
        
        // Step 1: Calculate Mệnh palace position
        // Formula: Start from Dần (index 2), add month, subtract hour index
        // Mệnh = Dần + (month - 1) - hourBranchIndex
        int menhIndex = (2 + (lunarMonth - 1) - hourBranchIndex + 24) % 12;
        DiaChi menhChi = DiaChi.fromIndex(menhIndex);

        // Step 2: Calculate Thân palace position
        // Formula: Start from Dần (index 2), add month, add hour index
        // Thân = Dần + (month - 1) + hourBranchIndex
        int thanIndex = (2 + (lunarMonth - 1) + hourBranchIndex) % 12;
        DiaChi thanChi = DiaChi.fromIndex(thanIndex);

        // Step 3: Determine Cục (Bureau)
        NgucGioi cuc = CucCalculator.calculateCuc(yearCan, menhChi);

        // Step 4: Determine direction (Thuận/Nghịch)
        // Dương nam, Âm nữ => Thuận
        // Âm nam, Dương nữ => Nghịch
        boolean isDuongYear = yearAmDuong == AmDuong.DUONG;
        boolean isThuan = (isDuongYear && isMale) || (!isDuongYear && !isMale);

        // Step 5: Generate all 12 palaces
        List<PalaceInfo> palaces = generatePalaces(menhChi, yearCan, isThuan);

        // Step 6: Find which palace name Thân resides in
        int thanPalaceIndex = -1;
        CungName thanCungName = null;
        for (int i = 0; i < palaces.size(); i++) {
            if (palaces.get(i).getDiaChiCode().equals(thanChi.name())) {
                thanPalaceIndex = i;
                thanCungName = CungName.fromIndex(i);
                palaces.get(i).setThanCu(true);
                break;
            }
        }

        return PalaceLayout.builder()
                .menhChi(menhChi)
                .thanChi(thanChi)
                .thanPalaceIndex(thanPalaceIndex)
                .thanCungName(thanCungName)
                .cuc(cuc)
                .isThuan(isThuan)
                .palaces(palaces)
                .build();
    }

    /**
     * Generate all 12 palaces starting from Mệnh position.
     * Palaces are placed in counter-clockwise order (nghịch direction on chart).
     */
    private static List<PalaceInfo> generatePalaces(DiaChi menhChi, ThienCan yearCan, boolean isThuan) {
        List<PalaceInfo> palaces = new ArrayList<>(12);
        
        for (int i = 0; i < 12; i++) {
            CungName cungName = CungName.fromIndex(i);
            
            // Palaces are placed counter-clockwise from Mệnh
            // So Mệnh is at index menhChi, Phụ Mẫu is at menhChi-1, etc.
            DiaChi palaceChi = menhChi.offset(-i);
            
            // Calculate the Can-Chi prefix for the palace (e.g., "D.Hợi")
            String canChiPrefix = getCanChiPrefix(yearCan, palaceChi);
            
            PalaceInfo palace = PalaceInfo.create(i, cungName, palaceChi, canChiPrefix);
            palace.setStars(new ArrayList<>());
            palaces.add(palace);
        }
        
        return palaces;
    }

    /**
     * Get the Can-Chi prefix for a palace position.
     * Based on year Can and palace DiaChi.
     */
    private static String getCanChiPrefix(ThienCan yearCan, DiaChi palaceChi) {
        // Calculate the Thiên Can for this palace position
        // Using the "An thập nhị cung" method
        ThienCan palaceCan = calculatePalaceCan(yearCan, palaceChi);
        return palaceCan.getText().substring(0, 1) + "." + palaceChi.getText();
    }

    /**
     * Calculate Thiên Can for a palace based on year Can.
     * Following the An thập nhị cung pattern.
     */
    private static ThienCan calculatePalaceCan(ThienCan yearCan, DiaChi palaceChi) {
        // The pattern depends on year Can group:
        // Giáp/Kỷ => starts from Bính at Dần
        // Ất/Canh => starts from Mậu at Dần
        // Bính/Tân => starts from Canh at Dần
        // Đinh/Nhâm => starts from Nhâm at Dần
        // Mậu/Quý => starts from Giáp at Dần
        
        int yearCanIndex = yearCan.getIndex();
        int baseCanIndex;
        
        switch (yearCanIndex % 5) {
            case 0: // Giáp, Kỷ
                baseCanIndex = 2; // Bính
                break;
            case 1: // Ất, Canh
                baseCanIndex = 4; // Mậu
                break;
            case 2: // Bính, Tân
                baseCanIndex = 6; // Canh
                break;
            case 3: // Đinh, Nhâm
                baseCanIndex = 8; // Nhâm
                break;
            case 4: // Mậu, Quý
                baseCanIndex = 0; // Giáp
                break;
            default:
                baseCanIndex = 0;
        }
        
        // Dần is index 2, so calculate offset from Dần
        int chiOffset = (palaceChi.getIndex() - 2 + 12) % 12;
        
        return ThienCan.fromIndex(baseCanIndex + chiOffset);
    }

    /**
     * Get the palace containing a specific DiaChi.
     */
    public static PalaceInfo getPalaceByDiaChi(List<PalaceInfo> palaces, DiaChi chi) {
        for (PalaceInfo palace : palaces) {
            if (palace.getDiaChiCode().equals(chi.name())) {
                return palace;
            }
        }
        return null;
    }

    /**
     * Get the palace by its name.
     */
    public static PalaceInfo getPalaceByName(List<PalaceInfo> palaces, CungName name) {
        for (PalaceInfo palace : palaces) {
            if (palace.getNameCode().equals(name.name())) {
                return palace;
            }
        }
        return null;
    }
}
