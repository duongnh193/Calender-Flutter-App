package com.duong.lichvanien.tuvi.calculator;

import com.duong.lichvanien.tuvi.enums.DiaChi;
import com.duong.lichvanien.tuvi.enums.NguHanh;
import com.duong.lichvanien.tuvi.enums.Star;
import lombok.Getter;
import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.Map;

/**
 * Calculator for Trường Sinh (Long Life) cycle - 12 stages of life.
 * The position depends on the Cục element and direction (thuận/nghịch).
 */
@UtilityClass
public class TruongSinhCalculator {

    /**
     * The 12 Trường Sinh stages in order.
     */
    @Getter
    public enum TruongSinhStage {
        TRUONG_SINH(0, "Trường Sinh", Star.TRUONG_SINH),
        MOC_DUC(1, "Mộc Dục", Star.MOC_DUC),
        QUAN_DAI(2, "Quán Đới", Star.QUAN_DAI),
        LAM_QUAN(3, "Lâm Quan", Star.LAM_QUAN),
        DE_VUONG(4, "Đế Vượng", Star.DE_VUONG),
        SUY(5, "Suy", Star.SUY),
        BENH(6, "Bệnh", Star.BENH),
        TU(7, "Tử", Star.TU),
        MO(8, "Mộ", Star.MO),
        TUYET(9, "Tuyệt", Star.TUYET),
        THAI(10, "Thai", Star.THAI),
        DUONG(11, "Dưỡng", Star.DUONG);

        private final int index;
        private final String text;
        private final Star star;

        TruongSinhStage(int index, String text, Star star) {
            this.index = index;
            this.text = text;
            this.star = star;
        }

        public static TruongSinhStage fromIndex(int index) {
            int normalizedIndex = Math.floorMod(index, 12);
            for (TruongSinhStage stage : values()) {
                if (stage.index == normalizedIndex) {
                    return stage;
                }
            }
            return TRUONG_SINH;
        }
    }

    /**
     * Starting position (Trường Sinh) for each element.
     * Thủy, Mộc start at Dần/Thân; Kim starts at Tỵ/Hợi; Hỏa, Thổ start at Dần/Thân
     */
    private static final Map<NguHanh, Integer> TRUONG_SINH_START = Map.of(
        NguHanh.THUY, 8,   // Thân
        NguHanh.MOC, 11,   // Hợi
        NguHanh.KIM, 5,    // Tỵ
        NguHanh.HOA, 2,    // Dần
        NguHanh.THO, 8     // Thân (same as Thủy)
    );

    /**
     * Calculate Trường Sinh positions for all 12 stages.
     * 
     * @param cucNguHanh The element of the Cục
     * @param isThuan Whether direction is forward (thuận) or backward (nghịch)
     * @return Map of DiaChi to Trường Sinh stage name
     */
    public static Map<DiaChi, String> calculateAllStages(NguHanh cucNguHanh, boolean isThuan) {
        Map<DiaChi, String> positions = new HashMap<>();
        
        int startPosition = TRUONG_SINH_START.getOrDefault(cucNguHanh, 2);
        int direction = isThuan ? 1 : -1;
        
        for (int i = 0; i < 12; i++) {
            int diaChiIndex = Math.floorMod(startPosition + (i * direction), 12);
            DiaChi chi = DiaChi.fromIndex(diaChiIndex);
            TruongSinhStage stage = TruongSinhStage.fromIndex(i);
            positions.put(chi, stage.getText());
        }
        
        return positions;
    }

    /**
     * Calculate Trường Sinh star positions (for placing as stars in palaces).
     * 
     * @param cucNguHanh The element of the Cục
     * @param isThuan Whether direction is forward (thuận) or backward (nghịch)
     * @return Map of Star to DiaChi position
     */
    public static Map<Star, DiaChi> calculateStarPositions(NguHanh cucNguHanh, boolean isThuan) {
        Map<Star, DiaChi> positions = new HashMap<>();
        
        int startPosition = TRUONG_SINH_START.getOrDefault(cucNguHanh, 2);
        int direction = isThuan ? 1 : -1;
        
        for (int i = 0; i < 12; i++) {
            int diaChiIndex = Math.floorMod(startPosition + (i * direction), 12);
            DiaChi chi = DiaChi.fromIndex(diaChiIndex);
            TruongSinhStage stage = TruongSinhStage.fromIndex(i);
            positions.put(stage.getStar(), chi);
        }
        
        return positions;
    }

    /**
     * Get the Trường Sinh stage at a specific DiaChi.
     */
    public static String getStageAtPosition(DiaChi chi, NguHanh cucNguHanh, boolean isThuan) {
        Map<DiaChi, String> allStages = calculateAllStages(cucNguHanh, isThuan);
        return allStages.get(chi);
    }
}
