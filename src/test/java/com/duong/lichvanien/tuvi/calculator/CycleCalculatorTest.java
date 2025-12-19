package com.duong.lichvanien.tuvi.calculator;

import com.duong.lichvanien.tuvi.dto.CycleInfo;
import com.duong.lichvanien.tuvi.dto.PalaceInfo;
import com.duong.lichvanien.tuvi.enums.AmDuong;
import com.duong.lichvanien.tuvi.enums.DiaChi;
import com.duong.lichvanien.tuvi.enums.ThienCan;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for CycleCalculator.
 */
class CycleCalculatorTest {

    @Nested
    @DisplayName("Đại Vận Start Age Tests")
    class DaiVanStartAgeTests {

        @ParameterizedTest
        @CsvSource({
            "2, 2",  // Thủy nhị cục starts at age 2
            "3, 3",  // Mộc tam cục starts at age 3
            "4, 4",  // Kim tứ cục starts at age 4
            "5, 5",  // Thổ ngũ cục starts at age 5
            "6, 6"   // Hỏa lục cục starts at age 6
        })
        @DisplayName("Starting age matches Cục value")
        void testStartAgeMatchesCuc(int cucValue, int expectedStartAge) {
            assertEquals(expectedStartAge, CycleCalculator.calculateDaiVanStartAge(cucValue));
        }
    }

    @Nested
    @DisplayName("Đại Vận Period Tests")
    class DaiVanPeriodTests {

        @Test
        @DisplayName("Get correct period for age 25 with Thổ ngũ cục")
        void testGetPeriodAge25ThoNguCuc() {
            // Thổ ngũ cục starts at age 5
            // Period 0: 5-14, Period 1: 15-24, Period 2: 25-34
            int period = CycleCalculator.getDaiVanPeriodForAge(25, 5);
            assertEquals(2, period);
        }

        @Test
        @DisplayName("Get correct period for age before first Đại Vận")
        void testGetPeriodBeforeFirst() {
            // Age 3 with Thổ ngũ cục (starts at 5)
            int period = CycleCalculator.getDaiVanPeriodForAge(3, 5);
            assertEquals(0, period);
        }

        @Test
        @DisplayName("Get correct period for very old age")
        void testGetPeriodOldAge() {
            // Age 150 should still return max period 11
            int period = CycleCalculator.getDaiVanPeriodForAge(150, 5);
            assertEquals(11, period);
        }
    }

    @Nested
    @DisplayName("Đại Vận Calculation Tests")
    class DaiVanCalculationTests {

        @Test
        @DisplayName("Calculate full Đại Vận cycle")
        void testFullDaiVanCycle() {
            // Create test palaces
            var layout = PalaceCalculator.calculate(
                2, 4, ThienCan.AT, false, AmDuong.AM
            );
            
            CycleInfo cycleInfo = CycleCalculator.calculateDaiVan(
                5,      // Thổ ngũ cục
                true,   // Thuận
                DiaChi.HOI,
                layout.getPalaces()
            );
            
            assertEquals("THUAN", cycleInfo.getDirection());
            assertEquals("Thuận", cycleInfo.getDirectionText());
            assertEquals(5, cycleInfo.getDaiVanStartAge());
            assertEquals(10, cycleInfo.getCyclePeriod());
            assertEquals(12, cycleInfo.getDaiVanList().size());
            
            // First entry should start at age 5
            assertEquals(5, cycleInfo.getDaiVanList().get(0).getStartAge());
            assertEquals(14, cycleInfo.getDaiVanList().get(0).getEndAge());
            
            // Second entry should start at age 15
            assertEquals(15, cycleInfo.getDaiVanList().get(1).getStartAge());
        }
    }

    @Nested
    @DisplayName("Tiểu Vận Tests")
    class TieuVanTests {

        @Test
        @DisplayName("Tiểu Vận at birth hour for age 1")
        void testTieuVanAge1() {
            DiaChi tieuVan = CycleCalculator.calculateTieuVan(1, true, DiaChi.THIN);
            assertEquals(DiaChi.THIN, tieuVan);
        }

        @Test
        @DisplayName("Tiểu Vận moves forward for thuận")
        void testTieuVanThuan() {
            DiaChi tieuVan = CycleCalculator.calculateTieuVan(3, true, DiaChi.THIN);
            assertEquals(DiaChi.NGO, tieuVan); // Thìn + 2
        }

        @Test
        @DisplayName("Tiểu Vận moves backward for nghịch")
        void testTieuVanNghich() {
            DiaChi tieuVan = CycleCalculator.calculateTieuVan(3, false, DiaChi.THIN);
            assertEquals(DiaChi.DAN, tieuVan); // Thìn - 2
        }
    }

    @Nested
    @DisplayName("Lưu Niên Tests")
    class LuuNienTests {

        @Test
        @DisplayName("Lưu Niên at birth year")
        void testLuuNienBirthYear() {
            DiaChi luuNien = CycleCalculator.calculateLuuNien(1995, 1995, DiaChi.HOI);
            assertEquals(DiaChi.HOI, luuNien);
        }

        @Test
        @DisplayName("Lưu Niên advances each year")
        void testLuuNienAdvances() {
            // 2025 is 30 years after 1995
            DiaChi luuNien = CycleCalculator.calculateLuuNien(2025, 1995, DiaChi.HOI);
            // 30 mod 12 = 6, so Hợi + 6 = Tỵ
            assertEquals(DiaChi.TI, luuNien);
        }
    }
}
