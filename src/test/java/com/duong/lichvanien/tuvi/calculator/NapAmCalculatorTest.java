package com.duong.lichvanien.tuvi.calculator;

import com.duong.lichvanien.tuvi.enums.DiaChi;
import com.duong.lichvanien.tuvi.enums.NguHanh;
import com.duong.lichvanien.tuvi.enums.ThienCan;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for NapAmCalculator.
 */
class NapAmCalculatorTest {

    @Test
    @DisplayName("NapAm for 1995 (Ất Hợi) should be Sơn Đầu Hỏa")
    void testNapAm1995AtHoi() {
        // From tracuutuvi image: 1995 - Ất Hợi => Sơn Đầu Hỏa (Lửa trên núi)
        var napAm = NapAmCalculator.getNapAmForYear(1995);
        
        assertEquals("Sơn Đầu Hỏa", napAm.getName());
        assertEquals("Lửa trên núi", napAm.getDescription());
        assertEquals(NguHanh.HOA, napAm.getNguHanh());
    }

    @Test
    @DisplayName("NapAm for Giáp Tuất, Ất Hợi should be Sơn Đầu Hỏa")
    void testNapAmGiapTuatAtHoi() {
        // Giáp Tuất (index 10)
        var napAm1 = NapAmCalculator.getNapAm(ThienCan.GIAP, DiaChi.TUAT);
        assertEquals("Sơn Đầu Hỏa", napAm1.getName());
        assertEquals(NguHanh.HOA, napAm1.getNguHanh());
        
        // Ất Hợi (index 11)
        var napAm2 = NapAmCalculator.getNapAm(ThienCan.AT, DiaChi.HOI);
        assertEquals("Sơn Đầu Hỏa", napAm2.getName());
        assertEquals(NguHanh.HOA, napAm2.getNguHanh());
    }

    @Test
    @DisplayName("NapAm for Giáp Tý, Ất Sửu should be Hải Trung Kim")
    void testNapAmGiapTyAtSuu() {
        // First pair: Giáp Tý
        var napAm1 = NapAmCalculator.getNapAm(ThienCan.GIAP, DiaChi.TY);
        assertEquals("Hải Trung Kim", napAm1.getName());
        assertEquals(NguHanh.KIM, napAm1.getNguHanh());
        
        // Second pair: Ất Sửu
        var napAm2 = NapAmCalculator.getNapAm(ThienCan.AT, DiaChi.SUU);
        assertEquals("Hải Trung Kim", napAm2.getName());
        assertEquals(NguHanh.KIM, napAm2.getNguHanh());
    }

    @ParameterizedTest
    @CsvSource({
        "2024, Phúc Đăng Hỏa, HOA",    // Giáp Thìn - index 40
        "2023, Kim Bạch Kim, KIM",      // Quý Mão - index 39
        "2022, Kim Bạch Kim, KIM",      // Nhâm Dần - index 38
        "2020, Bích Thượng Thổ, THO",  // Canh Tý - index 36
        "1990, Lộ Bàng Thổ, THO",      // Canh Ngọ - index 6
        "1984, Hải Trung Kim, KIM"      // Giáp Tý - index 0
    })
    @DisplayName("NapAm for various years")
    void testNapAmForVariousYears(int year, String expectedName, String expectedElement) {
        var napAm = NapAmCalculator.getNapAmForYear(year);
        assertEquals(expectedName, napAm.getName());
        assertEquals(NguHanh.valueOf(expectedElement), napAm.getNguHanh());
    }

    @Test
    @DisplayName("Can-Chi index calculation")
    void testCanChiIndex() {
        // Giáp Tý = 0
        assertEquals(0, NapAmCalculator.getCanChiIndex(ThienCan.GIAP, DiaChi.TY));
        
        // Ất Sửu = 1
        assertEquals(1, NapAmCalculator.getCanChiIndex(ThienCan.AT, DiaChi.SUU));
        
        // Quý Hợi = 59
        assertEquals(59, NapAmCalculator.getCanChiIndex(ThienCan.QUY, DiaChi.HOI));
    }
}
