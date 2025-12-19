package com.duong.lichvanien.tuvi.calculator;

import com.duong.lichvanien.tuvi.enums.DiaChi;
import com.duong.lichvanien.tuvi.enums.ThienCan;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for LunarDateCalculator.
 * Test cases based on tracuutuvi.com reference data.
 */
class LunarDateCalculatorTest {

    @Nested
    @DisplayName("Solar to Lunar Conversion Tests")
    class SolarToLunarTests {

        @Test
        @DisplayName("Convert 1995-03-02 (from tracuutuvi example) to lunar")
        void testConvert1995_03_02() {
            // From image: 1995 - Ất Hợi, Tháng 2 âm (Kỷ Mão), Ngày 2 - Nhâm Thìn
            var result = LunarDateCalculator.convertToLunar(1995, 3, 2, 8, 30);
            
            assertEquals(1995, result.getYear());
            assertEquals(2, result.getMonth());
            assertEquals(2, result.getDay());
            assertFalse(result.isLeapMonth());
            
            // Year: Ất Hợi (1995)
            assertEquals(ThienCan.AT, result.getCanYear());
            assertEquals(DiaChi.HOI, result.getChiYear());
            assertEquals("Ất Hợi", result.getCanChiYear());
            
            // Month: Kỷ Mão
            assertEquals(ThienCan.KY, result.getCanMonth());
            assertEquals(DiaChi.MAO, result.getChiMonth());
            
            // Day: Nhâm Thìn
            assertEquals(ThienCan.NHAM, result.getCanDay());
            assertEquals(DiaChi.THIN, result.getChiDay());
        }

        @Test
        @DisplayName("Convert date near lunar new year boundary")
        void testLunarNewYearBoundary() {
            // 2024-02-09 is the last day of lunar year 2023
            // 2024-02-10 is lunar new year 2024 (Giáp Thìn)
            var before = LunarDateCalculator.convertToLunar(2024, 2, 9, 12, 0);
            var after = LunarDateCalculator.convertToLunar(2024, 2, 10, 12, 0);
            
            assertEquals(2023, before.getYear());
            assertEquals(2024, after.getYear());
            assertEquals(1, after.getMonth());
            assertEquals(1, after.getDay());
        }

        @Test
        @DisplayName("Hour branch calculation - midnight")
        void testHourBranchMidnight() {
            // 23:00 should be Tý (index 0)
            var result = LunarDateCalculator.convertToLunar(2025, 1, 1, 23, 0);
            assertEquals(0, result.getHourBranchIndex());
            assertEquals(DiaChi.TY, result.getChiHour());
        }

        @Test
        @DisplayName("Hour branch calculation - early morning")
        void testHourBranchEarlyMorning() {
            // 00:30 should be Tý (index 0)
            var result = LunarDateCalculator.convertToLunar(2025, 1, 1, 0, 30);
            assertEquals(0, result.getHourBranchIndex());
            
            // 01:00 should be Sửu (index 1)
            result = LunarDateCalculator.convertToLunar(2025, 1, 1, 1, 0);
            assertEquals(1, result.getHourBranchIndex());
            assertEquals(DiaChi.SUU, result.getChiHour());
        }
    }

    @Nested
    @DisplayName("Hour Branch Index Tests")
    class HourBranchIndexTests {

        @ParameterizedTest
        @CsvSource({
            "23, 0",  // Tý
            "0, 0",   // Tý
            "1, 1",   // Sửu
            "2, 1",   // Sửu
            "3, 2",   // Dần
            "4, 2",   // Dần
            "5, 3",   // Mão
            "6, 3",   // Mão
            "7, 4",   // Thìn
            "8, 4",   // Thìn
            "9, 5",   // Tỵ
            "10, 5",  // Tỵ
            "11, 6",  // Ngọ
            "12, 6",  // Ngọ
            "13, 7",  // Mùi
            "14, 7",  // Mùi
            "15, 8",  // Thân
            "16, 8",  // Thân
            "17, 9",  // Dậu
            "18, 9",  // Dậu
            "19, 10", // Tuất
            "20, 10", // Tuất
            "21, 11", // Hợi
            "22, 11"  // Hợi
        })
        @DisplayName("Hour to branch index mapping")
        void testHourToBranchIndex(int hour, int expectedIndex) {
            assertEquals(expectedIndex, LunarDateCalculator.getHourBranchIndex(hour));
        }
    }

    @Nested
    @DisplayName("Can-Chi Calculation Tests")
    class CanChiCalculationTests {

        @Test
        @DisplayName("Year Can-Chi for known years")
        void testYearCanChi() {
            // 2024 = Giáp Thìn
            var result2024 = LunarDateCalculator.convertToLunar(2024, 3, 1, 12, 0);
            assertEquals(ThienCan.GIAP, result2024.getCanYear());
            assertEquals(DiaChi.THIN, result2024.getChiYear());
            
            // 2025 = Ất Tỵ
            var result2025 = LunarDateCalculator.convertToLunar(2025, 3, 1, 12, 0);
            assertEquals(ThienCan.AT, result2025.getCanYear());
            assertEquals(DiaChi.TI, result2025.getChiYear());
        }

        @Test
        @DisplayName("Giáp Thìn hour for 8:30 with day Nhâm Thìn")
        void testHourCanChiGiapThin() {
            // From image: 8 giờ 30 phút = Giáp Thìn
            var result = LunarDateCalculator.convertToLunar(1995, 3, 2, 8, 30);
            
            // Hour: 8:30 is Thìn (index 4)
            assertEquals(4, result.getHourBranchIndex());
            assertEquals(DiaChi.THIN, result.getChiHour());
            
            // Can of hour depends on day Can
            // Day is Nhâm (index 8), so hour Can = Giáp for Thìn hour
            assertEquals(ThienCan.GIAP, result.getCanHour());
            assertEquals("Giáp Thìn", result.getCanChiHour());
        }
    }

    @Nested
    @DisplayName("Lunar to Solar Conversion Tests")
    class LunarToSolarTests {

        @Test
        @DisplayName("Convert lunar date back to solar")
        void testLunarToSolar() {
            // First convert solar to lunar
            var lunar = LunarDateCalculator.convertToLunar(1995, 3, 2, 8, 30);
            
            // Then convert back to solar
            var solar = LunarDateCalculator.convertToSolar(
                lunar.getDay(), 
                lunar.getMonth(), 
                lunar.getYear(), 
                lunar.isLeapMonth()
            );
            
            assertEquals(1995, solar.getYear());
            assertEquals(3, solar.getMonthValue());
            assertEquals(2, solar.getDayOfMonth());
        }
    }
}
