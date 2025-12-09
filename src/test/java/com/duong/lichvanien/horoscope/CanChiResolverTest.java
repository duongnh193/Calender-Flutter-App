package com.duong.lichvanien.horoscope;

import com.duong.lichvanien.horoscope.dto.CanChiInfo;
import com.duong.lichvanien.horoscope.service.CanChiResolver;
import com.duong.lichvanien.zodiac.entity.ZodiacEntity;
import com.duong.lichvanien.zodiac.repository.ZodiacRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("CanChiResolver Tests")
class CanChiResolverTest {

    @Mock
    private ZodiacRepository zodiacRepository;

    @InjectMocks
    private CanChiResolver canChiResolver;

    @BeforeEach
    void setUp() {
        // Mock zodiac lookups
        lenient().when(zodiacRepository.findByCode(anyString())).thenAnswer(invocation -> {
            String code = invocation.getArgument(0);
            ZodiacEntity zodiac = new ZodiacEntity();
            zodiac.setCode(code);
            zodiac.setId(getZodiacIdByCode(code));
            zodiac.setNameVi(getZodiacNameByCode(code));
            return Optional.of(zodiac);
        });
    }

    private Long getZodiacIdByCode(String code) {
        return switch (code) {
            case "ti" -> 1L;
            case "suu" -> 2L;
            case "dan" -> 3L;
            case "mao" -> 4L;
            case "thin" -> 5L;
            case "ty" -> 6L;
            case "ngo" -> 7L;
            case "mui" -> 8L;
            case "than" -> 9L;
            case "dau" -> 10L;
            case "tuat" -> 11L;
            case "hoi" -> 12L;
            default -> null;
        };
    }

    private String getZodiacNameByCode(String code) {
        return switch (code) {
            case "ti" -> "Tý";
            case "suu" -> "Sửu";
            case "dan" -> "Dần";
            case "mao" -> "Mão";
            case "thin" -> "Thìn";
            case "ty" -> "Tỵ";
            case "ngo" -> "Ngọ";
            case "mui" -> "Mùi";
            case "than" -> "Thân";
            case "dau" -> "Dậu";
            case "tuat" -> "Tuất";
            case "hoi" -> "Hợi";
            default -> null;
        };
    }

    // ==================== Hour Branch Tests ====================

    @Nested
    @DisplayName("Hour to Branch Mapping Tests")
    class HourBranchTests {

        @Test
        @DisplayName("Hour 23:00 should be Tý (index 0)")
        void hour23_shouldBeTy() {
            int index = canChiResolver.getHourBranchIndex(23, 0);
            assertEquals(0, index);
            assertEquals("ti", canChiResolver.getHourBranchCode(23, 0));
            assertEquals("Tý", canChiResolver.getHourBranchName(23, 0));
        }

        @Test
        @DisplayName("Hour 23:59 should be Tý (index 0)")
        void hour23_59_shouldBeTy() {
            int index = canChiResolver.getHourBranchIndex(23, 59);
            assertEquals(0, index);
            assertEquals("ti", canChiResolver.getHourBranchCode(23, 59));
        }

        @Test
        @DisplayName("Hour 00:00 should be Tý (index 0)")
        void hour00_00_shouldBeTy() {
            int index = canChiResolver.getHourBranchIndex(0, 0);
            assertEquals(0, index);
            assertEquals("ti", canChiResolver.getHourBranchCode(0, 0));
        }

        @Test
        @DisplayName("Hour 00:59 should be Tý (index 0)")
        void hour00_59_shouldBeTy() {
            int index = canChiResolver.getHourBranchIndex(0, 59);
            assertEquals(0, index);
            assertEquals("ti", canChiResolver.getHourBranchCode(0, 59));
        }

        @Test
        @DisplayName("Hour 01:00 should be Sửu (index 1)")
        void hour01_00_shouldBeSuu() {
            int index = canChiResolver.getHourBranchIndex(1, 0);
            assertEquals(1, index);
            assertEquals("suu", canChiResolver.getHourBranchCode(1, 0));
            assertEquals("Sửu", canChiResolver.getHourBranchName(1, 0));
        }

        @Test
        @DisplayName("Hour 02:59 should be Sửu (index 1)")
        void hour02_59_shouldBeSuu() {
            int index = canChiResolver.getHourBranchIndex(2, 59);
            assertEquals(1, index);
            assertEquals("suu", canChiResolver.getHourBranchCode(2, 59));
        }

        @ParameterizedTest
        @DisplayName("Test all hour ranges")
        @CsvSource({
                "23, 0, 0, ti, Tý",      // Tý: 23:00 - 00:59
                "0, 0, 0, ti, Tý",
                "0, 59, 0, ti, Tý",
                "1, 0, 1, suu, Sửu",      // Sửu: 01:00 - 02:59
                "2, 30, 1, suu, Sửu",
                "3, 0, 2, dan, Dần",      // Dần: 03:00 - 04:59
                "4, 59, 2, dan, Dần",
                "5, 0, 3, mao, Mão",      // Mão: 05:00 - 06:59
                "6, 30, 3, mao, Mão",
                "7, 0, 4, thin, Thìn",    // Thìn: 07:00 - 08:59
                "8, 59, 4, thin, Thìn",
                "9, 0, 5, ty, Tỵ",        // Tỵ: 09:00 - 10:59
                "10, 30, 5, ty, Tỵ",
                "11, 0, 6, ngo, Ngọ",     // Ngọ: 11:00 - 12:59
                "12, 59, 6, ngo, Ngọ",
                "13, 0, 7, mui, Mùi",     // Mùi: 13:00 - 14:59
                "14, 30, 7, mui, Mùi",
                "15, 0, 8, than, Thân",   // Thân: 15:00 - 16:59
                "16, 59, 8, than, Thân",
                "17, 0, 9, dau, Dậu",     // Dậu: 17:00 - 18:59
                "18, 30, 9, dau, Dậu",
                "19, 0, 10, tuat, Tuất",  // Tuất: 19:00 - 20:59
                "20, 59, 10, tuat, Tuất",
                "21, 0, 11, hoi, Hợi",    // Hợi: 21:00 - 22:59
                "22, 59, 11, hoi, Hợi"
        })
        void testHourMapping(int hour, int minute, int expectedIndex, String expectedCode, String expectedName) {
            assertEquals(expectedIndex, canChiResolver.getHourBranchIndex(hour, minute));
            assertEquals(expectedCode, canChiResolver.getHourBranchCode(hour, minute));
            assertEquals(expectedName, canChiResolver.getHourBranchName(hour, minute));
        }

        @Test
        @DisplayName("Boundary test: 22:59 should be Hợi")
        void hour22_59_shouldBeHoi() {
            assertEquals(11, canChiResolver.getHourBranchIndex(22, 59));
            assertEquals("hoi", canChiResolver.getHourBranchCode(22, 59));
        }
    }

    // ==================== Resolve Can-Chi Tests ====================

    @Nested
    @DisplayName("Resolve Can-Chi from Date Tests")
    class ResolveTests {

        @Test
        @DisplayName("Should resolve Can-Chi for 1990-02-15 23:30 (solar)")
        void resolve_1990_02_15_solar() {
            LocalDate date = LocalDate.of(1990, 2, 15);

            CanChiInfo result = canChiResolver.resolve(date, 23, 30, false, false);

            assertNotNull(result);
            assertEquals("ti", result.getHourBranchCode());
            assertEquals("Tý", result.getHourBranchName());
            assertEquals(0, result.getHourBranchIndex());
            assertEquals("1990-02-15", result.getOriginalDate());
            assertFalse(result.getWasLunar());
        }

        @Test
        @DisplayName("Should resolve Can-Chi for 1984-03-15 (Giáp Tý year)")
        void resolve_1984_solar() {
            LocalDate date = LocalDate.of(1984, 3, 15);

            CanChiInfo result = canChiResolver.resolve(date, 10, 0, false, false);

            assertNotNull(result);
            assertEquals("Giáp", result.getCanYear());
            assertEquals("Tý", result.getChiYear());
            assertEquals("Giáp Tý", result.getCanChiYear());
            assertEquals("ti", result.getZodiacCode());
            assertEquals(1L, result.getZodiacId());
        }

        @Test
        @DisplayName("Should resolve Can-Chi for 2000-06-15 (Canh Thìn year)")
        void resolve_2000_solar() {
            LocalDate date = LocalDate.of(2000, 6, 15);

            CanChiInfo result = canChiResolver.resolve(date, 12, 0, false, false);

            assertNotNull(result);
            assertEquals("Canh", result.getCanYear());
            assertEquals("Thìn", result.getChiYear());
            assertEquals("Canh Thìn", result.getCanChiYear());
            assertEquals("thin", result.getZodiacCode());
            assertEquals(5L, result.getZodiacId());
        }

        @Test
        @DisplayName("Should handle lunar date conversion")
        void resolve_lunar_date() {
            // Lunar date: 1990-01-20 (in lunar calendar)
            LocalDate lunarDate = LocalDate.of(1990, 1, 20);

            CanChiInfo result = canChiResolver.resolve(lunarDate, 8, 0, true, false);

            assertNotNull(result);
            assertTrue(result.getWasLunar());
            assertEquals("1990-01-20", result.getOriginalDate());
            // The solar date should be different from the lunar date
            assertNotNull(result.getSolarDate());
        }

        @Test
        @DisplayName("Should include day and month Can-Chi")
        void resolve_includesDayAndMonth() {
            LocalDate date = LocalDate.of(1990, 5, 15);

            CanChiInfo result = canChiResolver.resolve(date, 14, 30, false, false);

            assertNotNull(result);
            assertNotNull(result.getCanChiDay());
            assertNotNull(result.getCanChiMonth());
            assertNotNull(result.getCanDay());
            assertNotNull(result.getChiDay());
            assertNotNull(result.getCanMonth());
            assertNotNull(result.getChiMonth());
        }
    }

    // ==================== Normalization Tests ====================

    @Nested
    @DisplayName("Normalization Tests")
    class NormalizationTests {

        @Test
        @DisplayName("Should normalize Can-Chi string")
        void normalizeCanChi() {
            assertEquals("Giáp Tý", canChiResolver.normalizeCanChi("  Giáp  Tý  "));
            assertEquals("Ất Sửu", canChiResolver.normalizeCanChi("Ất   Sửu"));
            assertNull(canChiResolver.normalizeCanChi(null));
        }

        @Test
        @DisplayName("Should normalize Can-Chi for cache key")
        void normalizeCanChiForKey() {
            assertEquals("giáptý", canChiResolver.normalizeCanChiForKey("Giáp Tý"));
            assertEquals("ấtsửu", canChiResolver.normalizeCanChiForKey("  Ất  Sửu  "));
            assertNull(canChiResolver.normalizeCanChiForKey(null));
        }
    }
}

