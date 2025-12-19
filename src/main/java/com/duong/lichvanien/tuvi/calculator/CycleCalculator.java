package com.duong.lichvanien.tuvi.calculator;

import com.duong.lichvanien.tuvi.dto.CycleInfo;
import com.duong.lichvanien.tuvi.dto.PalaceInfo;
import com.duong.lichvanien.tuvi.enums.CungName;
import com.duong.lichvanien.tuvi.enums.DiaChi;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;

/**
 * Calculator for Đại Vận (Major Cycle) and related cycle information.
 * 
 * Đại Vận represents 10-year life periods, with each period governed by a different palace.
 * The starting age and direction depend on the Cục and Âm Dương/gender combination.
 */
@UtilityClass
public class CycleCalculator {

    /**
     * Calculate the starting age for Đại Vận based on Cục value.
     * 
     * Each Cục has a specific starting age:
     * - Thủy nhị cục (2): starts at age 2
     * - Mộc tam cục (3): starts at age 3
     * - Kim tứ cục (4): starts at age 4
     * - Thổ ngũ cục (5): starts at age 5
     * - Hỏa lục cục (6): starts at age 6
     * 
     * @param cucValue The Cục value (2-6)
     * @return The starting age for Đại Vận
     */
    public static int calculateDaiVanStartAge(int cucValue) {
        return cucValue;
    }

    /**
     * Calculate Đại Vận cycle for all 12 palaces.
     * 
     * @param cucValue The Cục value (2-6)
     * @param isThuan Whether the direction is forward (thuận) or backward (nghịch)
     * @param menhChi The DiaChi where Mệnh palace is located
     * @param palaces List of palace info (to update with Đại Vận labels)
     * @return CycleInfo containing all cycle information
     */
    public static CycleInfo calculateDaiVan(
            int cucValue, 
            boolean isThuan, 
            DiaChi menhChi,
            List<PalaceInfo> palaces) {
        
        int startAge = calculateDaiVanStartAge(cucValue);
        int cyclePeriod = 10; // Each Đại Vận lasts 10 years
        
        List<CycleInfo.DaiVanEntry> daiVanList = new ArrayList<>();
        
        // Direction: thuận means clockwise (increasing DiaChi index)
        // nghịch means counter-clockwise (decreasing DiaChi index)
        int direction = isThuan ? 1 : -1;
        
        // Start from Mệnh palace
        int currentPalaceIndex = 0; // Mệnh is always at index 0 in the palace list
        
        for (int i = 0; i < 12; i++) {
            int age = startAge + (i * cyclePeriod);
            
            // Find the palace for this cycle
            // For thuận: Mệnh -> Phụ Mẫu -> Phúc Đức... (palace index increases)
            // For nghịch: Mệnh -> Huynh Đệ -> Phu Thê... (palace index decreases, wraps around)
            int palaceIndexForCycle;
            if (isThuan) {
                // Thuận: go through palaces in order (but on chart, they're counter-clockwise)
                // Actually, for Đại Vận:
                // - Thuận goes along the DiaChi direction (forward in DiaChi)
                // - Nghịch goes opposite
                palaceIndexForCycle = Math.floorMod(i * direction, 12);
            } else {
                palaceIndexForCycle = Math.floorMod(-i, 12);
            }
            
            PalaceInfo palace = palaces.get(palaceIndexForCycle);
            
            // Create entry
            CycleInfo.DaiVanEntry entry = CycleInfo.DaiVanEntry.builder()
                    .palaceIndex(palaceIndexForCycle)
                    .palaceName(palace.getName())
                    .startAge(age)
                    .endAge(age + cyclePeriod - 1)
                    .label(String.valueOf(age))
                    .build();
            
            daiVanList.add(entry);
            
            // Update the palace with Đại Vận info
            palace.setDaiVanStartAge(age);
            palace.setDaiVanLabel(String.valueOf(age));
        }
        
        return CycleInfo.builder()
                .direction(isThuan ? "THUAN" : "NGHICH")
                .directionText(isThuan ? "Thuận" : "Nghịch")
                .daiVanStartAge(startAge)
                .cyclePeriod(cyclePeriod)
                .daiVanList(daiVanList)
                .build();
    }

    /**
     * Calculate which Đại Vận period a specific age falls into.
     * 
     * @param age The current age
     * @param cucValue The Cục value (2-6)
     * @return The index of the Đại Vận period (0-11)
     */
    public static int getDaiVanPeriodForAge(int age, int cucValue) {
        int startAge = calculateDaiVanStartAge(cucValue);
        if (age < startAge) {
            return 0; // Before first Đại Vận
        }
        return Math.min((age - startAge) / 10, 11);
    }

    /**
     * Calculate Tiểu Vận (Minor Cycle) for a specific year.
     * Tiểu Vận is a yearly cycle that goes through the 12 palaces.
     * 
     * @param age The current age
     * @param isThuan Whether the direction is forward
     * @param birthHourChi The DiaChi of the birth hour
     * @return The DiaChi representing the Tiểu Vận palace for that year
     */
    public static DiaChi calculateTieuVan(int age, boolean isThuan, DiaChi birthHourChi) {
        // Tiểu Vận starts from the hour branch and moves in the thuận/nghịch direction
        int direction = isThuan ? 1 : -1;
        int offset = (age - 1) * direction; // Age 1 starts at birth hour
        return birthHourChi.offset(offset);
    }

    /**
     * Calculate Lưu Niên (Yearly) cycle position.
     * Lưu Niên follows the year's DiaChi, starting from the birth year.
     * 
     * @param currentYear The current year
     * @param birthYear The birth year
     * @param birthYearChi The DiaChi of the birth year
     * @return The DiaChi representing the Lưu Niên position for the current year
     */
    public static DiaChi calculateLuuNien(int currentYear, int birthYear, DiaChi birthYearChi) {
        int yearDiff = currentYear - birthYear;
        return birthYearChi.offset(yearDiff);
    }
}
