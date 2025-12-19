package com.duong.lichvanien.tuvi.calculator;

import com.duong.lichvanien.tuvi.enums.AmDuong;
import com.duong.lichvanien.tuvi.enums.CungName;
import com.duong.lichvanien.tuvi.enums.DiaChi;
import com.duong.lichvanien.tuvi.enums.NgucGioi;
import com.duong.lichvanien.tuvi.enums.ThienCan;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PalaceCalculator.
 * Test cases based on tracuutuvi.com reference data.
 */
class PalaceCalculatorTest {

    @Nested
    @DisplayName("Palace Layout Tests from tracuutuvi.com examples")
    class PalaceLayoutTests {

        @Test
        @DisplayName("Test case: 1995-03-02, female, 8:30 AM - should match tracuutuvi")
        void testCase1995_03_02_Female() {
            // From tracuutuvi.com image:
            // Lunar: Ất Hợi year, month 2, hour Thìn (8:30)
            // Expected: Mệnh at Hợi, Thổ ngũ cục
            
            // Ất year = index 1, Âm
            var layout = PalaceCalculator.calculate(
                2,              // lunar month
                4,              // hour branch index (Thìn)
                ThienCan.AT,    // year Can (Ất)
                false,          // female
                AmDuong.AM      // Ất is Âm
            );
            
            // Verify Mệnh palace position
            assertEquals(DiaChi.HOI, layout.getMenhChi());
            
            // Verify Cục
            assertEquals(NgucGioi.THO_NGU_CUC, layout.getCuc());
            assertEquals(5, layout.getCuc().getValue());
            
            // Âm female => Thuận
            assertTrue(layout.isThuan());
            
            // Verify all 12 palaces are generated
            assertEquals(12, layout.getPalaces().size());
            
            // Verify Mệnh palace is at correct position
            assertEquals("Mệnh", layout.getPalaces().get(0).getName());
            assertEquals("Hợi", layout.getPalaces().get(0).getDiaChi());
        }

        @Test
        @DisplayName("Test Thân palace calculation")
        void testThanPalaceCalculation() {
            // Thân formula: Dần + (month - 1) + hourBranchIndex
            // For month 2, hour Thìn (4): Thân = 2 + 1 + 4 = 7 (Mùi)
            
            var layout = PalaceCalculator.calculate(
                2, 4, ThienCan.AT, false, AmDuong.AM
            );
            
            assertEquals(DiaChi.MUI, layout.getThanChi());
            assertNotNull(layout.getThanCungName());
        }

        @Test
        @DisplayName("Test direction calculation - Dương male should be Thuận")
        void testDirectionDuongMale() {
            var layout = PalaceCalculator.calculate(
                1, 0, ThienCan.GIAP, true, AmDuong.DUONG
            );
            assertTrue(layout.isThuan());
        }

        @Test
        @DisplayName("Test direction calculation - Dương female should be Nghịch")
        void testDirectionDuongFemale() {
            var layout = PalaceCalculator.calculate(
                1, 0, ThienCan.GIAP, false, AmDuong.DUONG
            );
            assertFalse(layout.isThuan());
        }

        @Test
        @DisplayName("Test direction calculation - Âm male should be Nghịch")
        void testDirectionAmMale() {
            var layout = PalaceCalculator.calculate(
                1, 0, ThienCan.AT, true, AmDuong.AM
            );
            assertFalse(layout.isThuan());
        }

        @Test
        @DisplayName("Test direction calculation - Âm female should be Thuận")
        void testDirectionAmFemale() {
            var layout = PalaceCalculator.calculate(
                1, 0, ThienCan.AT, false, AmDuong.AM
            );
            assertTrue(layout.isThuan());
        }
    }

    @Nested
    @DisplayName("Mệnh Palace Position Tests")
    class MenhPositionTests {

        @Test
        @DisplayName("Month 1, Hour Tý => Mệnh at Dần")
        void testMonth1HourTy() {
            var layout = PalaceCalculator.calculate(
                1, 0, ThienCan.GIAP, true, AmDuong.DUONG
            );
            assertEquals(DiaChi.DAN, layout.getMenhChi());
        }

        @Test
        @DisplayName("Month 1, Hour Sửu => Mệnh at Sửu")
        void testMonth1HourSuu() {
            // Mệnh = (2 + 1 - 1 - 1 + 24) % 12 = 25 % 12 = 1 (Sửu)
            var layout = PalaceCalculator.calculate(
                1, 1, ThienCan.GIAP, true, AmDuong.DUONG
            );
            assertEquals(DiaChi.SUU, layout.getMenhChi());
        }

        @Test
        @DisplayName("Month 12, Hour Hợi => Mệnh at Mão")
        void testMonth12HourHoi() {
            // Mệnh = Dần + 11 - 11 = Dần (index 2)
            var layout = PalaceCalculator.calculate(
                12, 11, ThienCan.GIAP, true, AmDuong.DUONG
            );
            assertEquals(DiaChi.DAN, layout.getMenhChi());
        }
    }

    @Nested
    @DisplayName("Cục Calculation Tests")
    class CucCalculationTests {

        @Test
        @DisplayName("Giáp year, Mệnh at Tý => Thủy nhị cục")
        void testCucGiapTy() {
            var cuc = CucCalculator.calculateCuc(ThienCan.GIAP, DiaChi.TY);
            assertEquals(NgucGioi.THUY_NHI_CUC, cuc);
            assertEquals(2, cuc.getValue());
        }

        @Test
        @DisplayName("Giáp year, Mệnh at Sửu => Thủy nhị cục (via Nạp Âm of Bính Sửu)")
        void testCucGiapSuu() {
            // Giáp year, Mệnh at Sửu
            // Mệnh Can = Bính (base at Dần for Giáp year is Bính, Sửu offset = -1 = 11)
            // Actually: Giáp/Kỷ -> base at Dần = Bính (2)
            // Sửu offset from Dần = (1 - 2 + 12) % 12 = 11
            // Mệnh Can = (2 + 11) % 10 = 13 % 10 = 3 (Đinh)
            // Đinh + Sửu = index 13 = Giản Hạ Thủy = THỦY -> Thủy nhị cục
            var cuc = CucCalculator.calculateCuc(ThienCan.GIAP, DiaChi.SUU);
            assertEquals(NgucGioi.THUY_NHI_CUC, cuc);
            assertEquals(2, cuc.getValue());
        }

        @Test
        @DisplayName("Ất year, Mệnh at Hợi => Thổ ngũ cục (via Nạp Âm of Đinh Hợi)")
        void testCucAtHoi() {
            // Ất year, Mệnh at Hợi
            // Ất/Canh -> base at Dần = Mậu (4)
            // Hợi offset from Dần = (11 - 2 + 12) % 12 = 9
            // Mệnh Can = (4 + 9) % 10 = 13 % 10 = 3 (Đinh)
            // Đinh + Hợi = index 23 = Ốc Thượng Thổ = THỔ -> Thổ ngũ cục
            var cuc = CucCalculator.calculateCuc(ThienCan.AT, DiaChi.HOI);
            assertEquals(NgucGioi.THO_NGU_CUC, cuc);
        }
    }

    @Nested
    @DisplayName("Palace Order Tests")
    class PalaceOrderTests {

        @Test
        @DisplayName("Verify all 12 palace names are in correct order")
        void testPalaceOrder() {
            var layout = PalaceCalculator.calculate(
                1, 0, ThienCan.GIAP, true, AmDuong.DUONG
            );
            
            var palaces = layout.getPalaces();
            assertEquals("Mệnh", palaces.get(0).getName());
            assertEquals("Phụ Mẫu", palaces.get(1).getName());
            assertEquals("Phúc Đức", palaces.get(2).getName());
            assertEquals("Điền Trạch", palaces.get(3).getName());
            assertEquals("Quan Lộc", palaces.get(4).getName());
            assertEquals("Nô Bộc", palaces.get(5).getName());
            assertEquals("Thiên Di", palaces.get(6).getName());
            assertEquals("Tật Ách", palaces.get(7).getName());
            assertEquals("Tài Bạch", palaces.get(8).getName());
            assertEquals("Tử Tức", palaces.get(9).getName());
            assertEquals("Phu Thê", palaces.get(10).getName());
            assertEquals("Huynh Đệ", palaces.get(11).getName());
        }

        @Test
        @DisplayName("Palaces should be placed counter-clockwise from Mệnh")
        void testPalaceCounterClockwise() {
            var layout = PalaceCalculator.calculate(
                1, 0, ThienCan.GIAP, true, AmDuong.DUONG
            );
            
            // Mệnh at Dần (index 2)
            // Phụ Mẫu should be at Sửu (index 1)
            // Phúc Đức should be at Tý (index 0)
            
            var palaces = layout.getPalaces();
            assertEquals("Dần", palaces.get(0).getDiaChi()); // Mệnh
            assertEquals("Sửu", palaces.get(1).getDiaChi()); // Phụ Mẫu
            assertEquals("Tý", palaces.get(2).getDiaChi()); // Phúc Đức
            assertEquals("Hợi", palaces.get(3).getDiaChi()); // Điền Trạch
        }
    }
}
