package com.duong.lichvanien.tuvi.calculator;

import com.duong.lichvanien.tuvi.enums.DiaChi;
import com.duong.lichvanien.tuvi.enums.Star;
import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.Map;

/**
 * Calculator for placing Tử Vi star group (6 stars).
 * The Tử Vi star position depends on the Cục and lunar day.
 */
@UtilityClass
public class TuViStarCalculator {

    /**
     * Tử Vi position lookup table.
     * Format: [Cục value - 2][Day - 1] = DiaChi index
     * Cục values: 2 (Thủy), 3 (Mộc), 4 (Kim), 5 (Thổ), 6 (Hỏa)
     */
    private static final int[][] TU_VI_TABLE = {
        // Thủy nhị cục (value 2)
        {  2,  2,  3,  3,  4,  4,  5,  5,  6,  6,  7,  7,  8,  8,  9,  9, 10, 10, 11, 11,  0,  0,  1,  1,  2,  2,  3,  3,  4,  4 },
        // Mộc tam cục (value 3)
        {  2,  3,  3,  4,  4,  5,  5,  6,  6,  7,  7,  8,  8,  9,  9, 10, 10, 11, 11,  0,  0,  1,  1,  2,  2,  3,  3,  4,  4,  5 },
        // Kim tứ cục (value 4)
        {  2,  3,  4,  4,  5,  5,  6,  6,  7,  7,  8,  8,  9,  9, 10, 10, 11, 11,  0,  0,  1,  1,  2,  2,  3,  3,  4,  4,  5,  5 },
        // Thổ ngũ cục (value 5)
        {  2,  3,  4,  5,  5,  6,  6,  7,  7,  8,  8,  9,  9, 10, 10, 11, 11,  0,  0,  1,  1,  2,  2,  3,  3,  4,  4,  5,  5,  6 },
        // Hỏa lục cục (value 6)
        {  2,  3,  4,  5,  6,  6,  7,  7,  8,  8,  9,  9, 10, 10, 11, 11,  0,  0,  1,  1,  2,  2,  3,  3,  4,  4,  5,  5,  6,  6 }
    };

    /**
     * Calculate the position of Tử Vi star.
     * 
     * @param cucValue The Cục value (2-6)
     * @param lunarDay The lunar day (1-30)
     * @return DiaChi where Tử Vi is placed
     */
    public static DiaChi calculateTuViPosition(int cucValue, int lunarDay) {
        if (cucValue < 2 || cucValue > 6) {
            throw new IllegalArgumentException("Cục value must be between 2 and 6");
        }
        if (lunarDay < 1 || lunarDay > 30) {
            throw new IllegalArgumentException("Lunar day must be between 1 and 30");
        }
        
        int tableIndex = cucValue - 2;
        int dayIndex = lunarDay - 1;
        int diaChiIndex = TU_VI_TABLE[tableIndex][dayIndex];
        
        return DiaChi.fromIndex(diaChiIndex);
    }

    /**
     * Place all Tử Vi group stars.
     * The 6 stars follow Tử Vi in a specific pattern.
     * 
     * @param cucValue The Cục value (2-6)
     * @param lunarDay The lunar day (1-30)
     * @param isThuan Whether the direction is thuận (forward)
     * @return Map of Star to DiaChi position
     */
    public static Map<Star, DiaChi> placeAllStars(int cucValue, int lunarDay, boolean isThuan) {
        Map<Star, DiaChi> positions = new HashMap<>();
        
        DiaChi tuViChi = calculateTuViPosition(cucValue, lunarDay);
        positions.put(Star.TU_VI, tuViChi);
        
        // Stars following Tử Vi (always forward from Tử Vi)
        // Liêm Trinh: Tử Vi - 4
        positions.put(Star.LIEM_TRINH, tuViChi.offset(-4));
        
        // Thiên Đồng: Tử Vi - 3
        positions.put(Star.THIEN_DONG, tuViChi.offset(-3));
        
        // Vũ Khúc: Tử Vi - 2
        positions.put(Star.VU_KHUC, tuViChi.offset(-2));
        
        // Thái Dương: Tử Vi - 1
        positions.put(Star.THAI_DUONG, tuViChi.offset(-1));
        
        // Thiên Cơ: Tử Vi + 1
        positions.put(Star.THIEN_CO, tuViChi.offset(1));
        
        return positions;
    }
}
