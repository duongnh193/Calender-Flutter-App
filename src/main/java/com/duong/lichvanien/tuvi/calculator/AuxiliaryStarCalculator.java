package com.duong.lichvanien.tuvi.calculator;

import com.duong.lichvanien.tuvi.enums.DiaChi;
import com.duong.lichvanien.tuvi.enums.Star;
import com.duong.lichvanien.tuvi.enums.ThienCan;
import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.Map;

/**
 * Calculator for placing auxiliary stars (Phụ tinh):
 * - Lục sát (6 malefic): Kình Dương, Đà La, Hỏa Tinh, Linh Tinh, Địa Kiếp, Địa Không
 * - Lục cát (6 benefic): Văn Xương, Văn Khúc, Tả Phù, Hữu Bật, Thiên Khôi, Thiên Việt
 * - Lộc Tồn group
 */
@UtilityClass
public class AuxiliaryStarCalculator {

    // ===== Lộc Tồn, Kình Dương, Đà La based on Year Can =====
    
    /**
     * Lộc Tồn position by Year Can.
     * Format: ThienCan index -> DiaChi index
     */
    private static final int[] LOC_TON_TABLE = {
        // Giáp  Ất   Bính  Đinh  Mậu   Kỷ   Canh  Tân   Nhâm  Quý
             2,   3,    5,    6,   5,   6,    8,    9,   11,    0
    };

    /**
     * Calculate Lộc Tồn, Kình Dương, Đà La positions.
     */
    public static Map<Star, DiaChi> calculateLocTonGroup(ThienCan yearCan) {
        Map<Star, DiaChi> positions = new HashMap<>();
        
        int locTonIndex = LOC_TON_TABLE[yearCan.getIndex()];
        DiaChi locTonChi = DiaChi.fromIndex(locTonIndex);
        
        positions.put(Star.LOC_TON, locTonChi);
        positions.put(Star.KINH_DUONG, locTonChi.offset(1)); // Kình Dương at Lộc Tồn + 1
        positions.put(Star.DA_LA, locTonChi.offset(-1));     // Đà La at Lộc Tồn - 1
        
        return positions;
    }

    // ===== Văn Xương, Văn Khúc based on Hour =====
    
    /**
     * Văn Xương position by Hour branch.
     * Format: Hour branch index -> DiaChi index
     */
    private static final int[] VAN_XUONG_TABLE = {
        // Tý  Sửu  Dần  Mão  Thìn  Tỵ   Ngọ  Mùi  Thân  Dậu  Tuất  Hợi
           9,   8,   7,   6,    5,   4,    3,   2,    1,   0,   11,   10
    };

    /**
     * Văn Khúc position by Hour branch.
     * Format: Hour branch index -> DiaChi index
     */
    private static final int[] VAN_KHUC_TABLE = {
        // Tý  Sửu  Dần  Mão  Thìn  Tỵ   Ngọ  Mùi  Thân  Dậu  Tuất  Hợi
           5,   6,   7,   8,    9,  10,   11,   0,    1,   2,    3,    4
    };

    /**
     * Calculate Văn Xương, Văn Khúc positions.
     */
    public static Map<Star, DiaChi> calculateVanXuongVanKhuc(int hourBranchIndex) {
        Map<Star, DiaChi> positions = new HashMap<>();
        
        positions.put(Star.VAN_XUONG, DiaChi.fromIndex(VAN_XUONG_TABLE[hourBranchIndex]));
        positions.put(Star.VAN_KHUC, DiaChi.fromIndex(VAN_KHUC_TABLE[hourBranchIndex]));
        
        return positions;
    }

    // ===== Tả Phù, Hữu Bật based on Lunar Month =====
    
    /**
     * Calculate Tả Phù, Hữu Bật positions.
     * Tả Phù: starts at Thìn, moves forward with month
     * Hữu Bật: starts at Tuất, moves backward with month
     */
    public static Map<Star, DiaChi> calculateTaPhuHuuBat(int lunarMonth) {
        Map<Star, DiaChi> positions = new HashMap<>();
        
        // Tả Phù: Thìn (4) + (month - 1)
        int taPhuIndex = (4 + (lunarMonth - 1)) % 12;
        positions.put(Star.TA_PHU, DiaChi.fromIndex(taPhuIndex));
        
        // Hữu Bật: Tuất (10) - (month - 1)
        int huuBatIndex = (10 - (lunarMonth - 1) + 12) % 12;
        positions.put(Star.HUU_BAT, DiaChi.fromIndex(huuBatIndex));
        
        return positions;
    }

    // ===== Thiên Khôi, Thiên Việt based on Year Can =====
    
    /**
     * Thiên Khôi position by Year Can.
     */
    private static final int[] THIEN_KHOI_TABLE = {
        // Giáp  Ất   Bính  Đinh  Mậu   Kỷ   Canh  Tân   Nhâm  Quý
             1,   0,   11,   11,   1,   0,    7,    6,    3,    3
    };

    /**
     * Thiên Việt position by Year Can.
     */
    private static final int[] THIEN_VIET_TABLE = {
        // Giáp  Ất   Bính  Đinh  Mậu   Kỷ   Canh  Tân   Nhâm  Quý
             7,   8,    5,    6,   5,   6,    1,    2,    5,    5
    };

    /**
     * Calculate Thiên Khôi, Thiên Việt positions.
     */
    public static Map<Star, DiaChi> calculateThienKhoiViet(ThienCan yearCan) {
        Map<Star, DiaChi> positions = new HashMap<>();
        
        positions.put(Star.THIEN_KHOI, DiaChi.fromIndex(THIEN_KHOI_TABLE[yearCan.getIndex()]));
        positions.put(Star.THIEN_VIET, DiaChi.fromIndex(THIEN_VIET_TABLE[yearCan.getIndex()]));
        
        return positions;
    }

    // ===== Hỏa Tinh, Linh Tinh based on Year Chi and Hour =====
    
    /**
     * Calculate Hỏa Tinh position.
     * Depends on year chi (grouped) and hour branch.
     */
    public static DiaChi calculateHoaTinh(DiaChi yearChi, int hourBranchIndex) {
        // Year groups: Dần-Ngọ-Tuất, Thân-Tý-Thìn, Tỵ-Dậu-Sửu, Hợi-Mão-Mùi
        int yearGroup = getYearGroup(yearChi);
        
        int[] startingPositions = { 2, 2, 1, 10 }; // Dần, Dần, Sửu, Tuất
        int baseIndex = startingPositions[yearGroup];
        
        return DiaChi.fromIndex((baseIndex + hourBranchIndex) % 12);
    }

    /**
     * Calculate Linh Tinh position.
     */
    public static DiaChi calculateLinhTinh(DiaChi yearChi, int hourBranchIndex) {
        int yearGroup = getYearGroup(yearChi);
        
        int[] startingPositions = { 3, 10, 3, 10 }; // Mão, Tuất, Mão, Tuất
        int baseIndex = startingPositions[yearGroup];
        
        return DiaChi.fromIndex((baseIndex + hourBranchIndex) % 12);
    }

    private static int getYearGroup(DiaChi yearChi) {
        // Group 0: Dần-Ngọ-Tuất (2, 6, 10)
        // Group 1: Thân-Tý-Thìn (8, 0, 4)
        // Group 2: Tỵ-Dậu-Sửu (5, 9, 1)
        // Group 3: Hợi-Mão-Mùi (11, 3, 7)
        int index = yearChi.getIndex();
        if (index == 2 || index == 6 || index == 10) return 0;
        if (index == 8 || index == 0 || index == 4) return 1;
        if (index == 5 || index == 9 || index == 1) return 2;
        return 3; // Hợi, Mão, Mùi
    }

    // ===== Địa Kiếp, Địa Không based on Hour =====
    
    /**
     * Calculate Địa Kiếp, Địa Không positions.
     */
    public static Map<Star, DiaChi> calculateDiaKiepKhong(int hourBranchIndex) {
        Map<Star, DiaChi> positions = new HashMap<>();
        
        // Địa Kiếp: Hợi + hourBranchIndex
        positions.put(Star.DIA_KIEP, DiaChi.fromIndex((11 + hourBranchIndex) % 12));
        
        // Địa Không: Hợi - hourBranchIndex
        positions.put(Star.DIA_KHONG, DiaChi.fromIndex((11 - hourBranchIndex + 12) % 12));
        
        return positions;
    }

    /**
     * Place all auxiliary stars.
     */
    public static Map<Star, DiaChi> placeAllStars(
            ThienCan yearCan,
            DiaChi yearChi,
            int lunarMonth,
            int hourBranchIndex) {
        
        Map<Star, DiaChi> positions = new HashMap<>();
        
        positions.putAll(calculateLocTonGroup(yearCan));
        positions.putAll(calculateVanXuongVanKhuc(hourBranchIndex));
        positions.putAll(calculateTaPhuHuuBat(lunarMonth));
        positions.putAll(calculateThienKhoiViet(yearCan));
        positions.put(Star.HOA_TINH, calculateHoaTinh(yearChi, hourBranchIndex));
        positions.put(Star.LINH_TINH, calculateLinhTinh(yearChi, hourBranchIndex));
        positions.putAll(calculateDiaKiepKhong(hourBranchIndex));
        
        return positions;
    }
}
