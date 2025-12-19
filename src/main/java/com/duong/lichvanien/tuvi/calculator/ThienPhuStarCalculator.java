package com.duong.lichvanien.tuvi.calculator;

import com.duong.lichvanien.tuvi.enums.DiaChi;
import com.duong.lichvanien.tuvi.enums.Star;
import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.Map;

/**
 * Calculator for placing Thiên Phủ star group (8 stars).
 * Thiên Phủ position is determined by Tử Vi position (mirrored).
 */
@UtilityClass
public class ThienPhuStarCalculator {

    /**
     * Thiên Phủ mirror table.
     * Thiên Phủ is always at a position that mirrors Tử Vi across the Dần-Thân axis.
     * Format: TuViDiaChi index -> ThienPhuDiaChi index
     */
    private static final int[] THIEN_PHU_MIRROR = {
        // Tử Vi at: Tý  Sửu  Dần  Mão  Thìn  Tỵ  Ngọ  Mùi  Thân  Dậu  Tuất  Hợi
        //                0    1    2    3     4   5    6    7     8    9    10   11
                         4,   3,   2,   1,    0,  11,  10,   9,    8,   7,    6,   5
    };

    /**
     * Calculate the position of Thiên Phủ star based on Tử Vi position.
     * 
     * @param tuViChi The DiaChi where Tử Vi is placed
     * @return DiaChi where Thiên Phủ is placed
     */
    public static DiaChi calculateThienPhuPosition(DiaChi tuViChi) {
        int thienPhuIndex = THIEN_PHU_MIRROR[tuViChi.getIndex()];
        return DiaChi.fromIndex(thienPhuIndex);
    }

    /**
     * Place all Thiên Phủ group stars.
     * The 8 stars follow Thiên Phủ in a specific pattern (always forward/clockwise).
     * 
     * @param tuViChi The DiaChi where Tử Vi is placed
     * @return Map of Star to DiaChi position
     */
    public static Map<Star, DiaChi> placeAllStars(DiaChi tuViChi) {
        Map<Star, DiaChi> positions = new HashMap<>();
        
        DiaChi thienPhuChi = calculateThienPhuPosition(tuViChi);
        positions.put(Star.THIEN_PHU, thienPhuChi);
        
        // Stars following Thiên Phủ (always clockwise/forward from Thiên Phủ)
        // Thái Âm: Thiên Phủ + 1
        positions.put(Star.THAI_AM, thienPhuChi.offset(1));
        
        // Tham Lang: Thiên Phủ + 2
        positions.put(Star.THAM_LANG, thienPhuChi.offset(2));
        
        // Cự Môn: Thiên Phủ + 3
        positions.put(Star.CU_MON, thienPhuChi.offset(3));
        
        // Thiên Tướng: Thiên Phủ + 4
        positions.put(Star.THIEN_TUONG, thienPhuChi.offset(4));
        
        // Thiên Lương: Thiên Phủ + 5
        positions.put(Star.THIEN_LUONG, thienPhuChi.offset(5));
        
        // Thất Sát: Thiên Phủ + 6
        positions.put(Star.THAT_SAT, thienPhuChi.offset(6));
        
        // Phá Quân: Thiên Phủ + 10 (or Tử Vi - 2 = Tử Vi directly opposite)
        positions.put(Star.PHA_QUAN, thienPhuChi.offset(10));
        
        return positions;
    }
}
