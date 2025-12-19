package com.duong.lichvanien.tuvi.calculator;

import com.duong.lichvanien.tuvi.enums.DiaChi;
import com.duong.lichvanien.tuvi.enums.NguHanh;
import com.duong.lichvanien.tuvi.enums.Star;
import com.duong.lichvanien.tuvi.enums.ThienCan;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for star calculators.
 */
class StarCalculatorTest {

    @Nested
    @DisplayName("Tử Vi Star Position Tests")
    class TuViStarTests {

        @Test
        @DisplayName("Thổ ngũ cục, day 2 => Tử Vi at Mão")
        void testTuViThoNguCucDay2() {
            // From tracuutuvi.com example: Thổ ngũ cục, day 2 of lunar month
            DiaChi position = TuViStarCalculator.calculateTuViPosition(5, 2);
            assertEquals(DiaChi.MAO, position);
        }

        @Test
        @DisplayName("Tử Vi group stars placed correctly")
        void testTuViGroupPlacement() {
            Map<Star, DiaChi> positions = TuViStarCalculator.placeAllStars(5, 2, true);
            
            // Tử Vi at Mão (index 3)
            assertEquals(DiaChi.MAO, positions.get(Star.TU_VI));
            
            // Other stars follow pattern
            assertNotNull(positions.get(Star.LIEM_TRINH));
            assertNotNull(positions.get(Star.THIEN_DONG));
            assertNotNull(positions.get(Star.VU_KHUC));
            assertNotNull(positions.get(Star.THAI_DUONG));
            assertNotNull(positions.get(Star.THIEN_CO));
        }
    }

    @Nested
    @DisplayName("Thiên Phủ Star Position Tests")
    class ThienPhuStarTests {

        @Test
        @DisplayName("Thiên Phủ mirrors Tử Vi correctly")
        void testThienPhuMirror() {
            // When Tử Vi is at Mão (index 3), Thiên Phủ should be at Sửu (index 1)
            DiaChi thienPhuChi = ThienPhuStarCalculator.calculateThienPhuPosition(DiaChi.MAO);
            assertEquals(DiaChi.SUU, thienPhuChi);
        }

        @Test
        @DisplayName("Thiên Phủ group stars placed correctly")
        void testThienPhuGroupPlacement() {
            Map<Star, DiaChi> positions = ThienPhuStarCalculator.placeAllStars(DiaChi.MAO);
            
            assertNotNull(positions.get(Star.THIEN_PHU));
            assertNotNull(positions.get(Star.THAI_AM));
            assertNotNull(positions.get(Star.THAM_LANG));
            assertNotNull(positions.get(Star.CU_MON));
            assertNotNull(positions.get(Star.THIEN_TUONG));
            assertNotNull(positions.get(Star.THIEN_LUONG));
            assertNotNull(positions.get(Star.THAT_SAT));
            assertNotNull(positions.get(Star.PHA_QUAN));
        }
    }

    @Nested
    @DisplayName("Auxiliary Star Tests")
    class AuxiliaryStarTests {

        @Test
        @DisplayName("Lộc Tồn for Ất year should be at Mão")
        void testLocTonAtYear() {
            Map<Star, DiaChi> positions = AuxiliaryStarCalculator.calculateLocTonGroup(ThienCan.AT);
            assertEquals(DiaChi.MAO, positions.get(Star.LOC_TON));
            assertEquals(DiaChi.THIN, positions.get(Star.KINH_DUONG)); // Mão + 1
            assertEquals(DiaChi.DAN, positions.get(Star.DA_LA));       // Mão - 1
        }

        @Test
        @DisplayName("Văn Xương, Văn Khúc for Thìn hour")
        void testVanXuongVanKhucThinHour() {
            Map<Star, DiaChi> positions = AuxiliaryStarCalculator.calculateVanXuongVanKhuc(4); // Thìn = index 4
            assertNotNull(positions.get(Star.VAN_XUONG));
            assertNotNull(positions.get(Star.VAN_KHUC));
        }

        @Test
        @DisplayName("Tả Phù, Hữu Bật for month 2")
        void testTaPhuHuuBatMonth2() {
            Map<Star, DiaChi> positions = AuxiliaryStarCalculator.calculateTaPhuHuuBat(2);
            assertEquals(DiaChi.TI, positions.get(Star.TA_PHU));   // Thìn + 1 = Tỵ
            assertEquals(DiaChi.DAU, positions.get(Star.HUU_BAT)); // Tuất - 1 = Dậu
        }
    }

    @Nested
    @DisplayName("Marker Calculation Tests")
    class MarkerTests {

        @Test
        @DisplayName("Tuần for Ất Hợi year")
        void testTuanAtHoi() {
            // Ất = index 1, Hợi = index 11
            // 10-year cycle starting from Giáp Tuất (Can 0, Chi 10)
            // Tuần should be at Thân-Dậu
            DiaChi[] tuan = MarkerCalculator.calculateTuan(ThienCan.AT, DiaChi.HOI);
            assertNotNull(tuan);
            assertEquals(2, tuan.length);
        }

        @Test
        @DisplayName("Triệt for Ất year should be at Ngọ-Mùi")
        void testTrietAtYear() {
            // Ất (index 1) => Triệt at Ngọ-Mùi
            DiaChi[] triet = MarkerCalculator.calculateTriet(ThienCan.AT);
            assertEquals(DiaChi.NGO, triet[0]);
            assertEquals(DiaChi.MUI, triet[1]);
        }

        @Test
        @DisplayName("Check if position is in Tuần")
        void testIsInTuan() {
            DiaChi[] tuan = MarkerCalculator.calculateTuan(ThienCan.AT, DiaChi.HOI);
            assertTrue(MarkerCalculator.isInTuan(tuan[0], ThienCan.AT, DiaChi.HOI));
            assertTrue(MarkerCalculator.isInTuan(tuan[1], ThienCan.AT, DiaChi.HOI));
        }
    }

    @Nested
    @DisplayName("Trường Sinh Tests")
    class TruongSinhTests {

        @Test
        @DisplayName("Trường Sinh for Thổ cục, thuận direction")
        void testTruongSinhThoThuan() {
            Map<DiaChi, String> stages = TruongSinhCalculator.calculateAllStages(NguHanh.THO, true);
            
            assertEquals(12, stages.size());
            assertTrue(stages.containsValue("Trường Sinh"));
            assertTrue(stages.containsValue("Mộc Dục"));
            assertTrue(stages.containsValue("Đế Vượng"));
            assertTrue(stages.containsValue("Tử"));
        }

        @Test
        @DisplayName("Trường Sinh star positions")
        void testTruongSinhStarPositions() {
            Map<Star, DiaChi> positions = TruongSinhCalculator.calculateStarPositions(NguHanh.THO, true);
            
            assertNotNull(positions.get(Star.TRUONG_SINH));
            assertNotNull(positions.get(Star.DE_VUONG));
            assertNotNull(positions.get(Star.TU));
            assertEquals(12, positions.size());
        }
    }
}
