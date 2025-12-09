package com.duong.lichvanien.horoscope;

import com.duong.lichvanien.horoscope.dto.CanChiResult;
import com.duong.lichvanien.horoscope.service.CanChiService;
import com.duong.lichvanien.zodiac.entity.ZodiacEntity;
import com.duong.lichvanien.zodiac.repository.ZodiacRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
@DisplayName("CanChiService Tests")
class CanChiServiceTest {

    @Mock
    private ZodiacRepository zodiacRepository;

    @InjectMocks
    private CanChiService canChiService;

    @BeforeEach
    void setUp() {
        // Mock zodiac lookups with lenient mode
        lenient().when(zodiacRepository.findByCode(anyString())).thenAnswer(invocation -> {
            String code = invocation.getArgument(0);
            ZodiacEntity zodiac = new ZodiacEntity();
            zodiac.setCode(code);
            zodiac.setId(getZodiacIdByCode(code));
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

    @Test
    @DisplayName("Should calculate Can-Chi for a known date - 1984 (Giáp Tý)")
    void testCalculateCanChi_GiapTy() {
        // 1984 is Giáp Tý year
        LocalDate birthDate = LocalDate.of(1984, 3, 15);

        CanChiResult result = canChiService.calculateFromBirthDate(birthDate);

        assertNotNull(result);
        assertEquals("Giáp", result.getCanYear());
        assertEquals("Tý", result.getChiYear());
        assertEquals("Giáp Tý", result.getCanChiYear());
        assertEquals("ti", result.getZodiacCode());
    }

    @Test
    @DisplayName("Should calculate Can-Chi for 1985 (Ất Sửu)")
    void testCalculateCanChi_AtSuu() {
        // 1985 is Ất Sửu year (after Lunar New Year)
        LocalDate birthDate = LocalDate.of(1985, 3, 1);

        CanChiResult result = canChiService.calculateFromBirthDate(birthDate);

        assertNotNull(result);
        assertEquals("Ất", result.getCanYear());
        assertEquals("Sửu", result.getChiYear());
        assertEquals("Ất Sửu", result.getCanChiYear());
        assertEquals("suu", result.getZodiacCode());
    }

    @Test
    @DisplayName("Should calculate Can-Chi for 2000 (Canh Thìn)")
    void testCalculateCanChi_CanhThin() {
        // 2000 is Canh Thìn year
        LocalDate birthDate = LocalDate.of(2000, 6, 15);

        CanChiResult result = canChiService.calculateFromBirthDate(birthDate);

        assertNotNull(result);
        assertEquals("Canh", result.getCanYear());
        assertEquals("Thìn", result.getChiYear());
        assertEquals("Canh Thìn", result.getCanChiYear());
        assertEquals("thin", result.getZodiacCode());
    }

    @ParameterizedTest
    @DisplayName("Should handle various birth dates")
    @CsvSource({
            "1990-05-15, Canh, Ngọ, ngo",
            "1995-08-20, Ất, Hợi, hoi",
            "2020-02-25, Canh, Tý, ti"   // After Lunar New Year 2020
    })
    void testCalculateCanChi_Various(String dateStr, String expectedCan, String expectedChi, String expectedCode) {
        LocalDate birthDate = LocalDate.parse(dateStr);

        CanChiResult result = canChiService.calculateFromBirthDate(birthDate);

        assertNotNull(result);
        assertEquals(expectedCan, result.getCanYear());
        assertEquals(expectedChi, result.getChiYear());
        assertEquals(expectedCode, result.getZodiacCode());
    }

    @Test
    @DisplayName("Should handle date before Lunar New Year correctly")
    void testCalculateCanChi_BeforeLunarNewYear() {
        // 2020-01-25 is before Lunar New Year 2020 (Jan 25 is the new year date)
        // So it could be either Kỷ Hợi (2019) or Canh Tý (2020) depending on exact time
        LocalDate birthDate = LocalDate.of(2020, 1, 20); // Definitely before LNY

        CanChiResult result = canChiService.calculateFromBirthDate(birthDate);

        assertNotNull(result);
        // Should still be previous lunar year
        assertEquals("Kỷ", result.getCanYear());
        assertEquals("Hợi", result.getChiYear());
    }

    @Test
    @DisplayName("Should validate valid Can-Chi strings")
    void testIsValidCanChi_Valid() {
        assertTrue(canChiService.isValidCanChi("Giáp Tý"));
        assertTrue(canChiService.isValidCanChi("Ất Sửu"));
        assertTrue(canChiService.isValidCanChi("Canh Thìn"));
        assertTrue(canChiService.isValidCanChi("Quý Hợi"));
    }

    @Test
    @DisplayName("Should reject invalid Can-Chi strings")
    void testIsValidCanChi_Invalid() {
        assertFalse(canChiService.isValidCanChi(null));
        assertFalse(canChiService.isValidCanChi(""));
        assertFalse(canChiService.isValidCanChi("Invalid"));
        assertFalse(canChiService.isValidCanChi("Giáp"));  // Missing Chi
        assertFalse(canChiService.isValidCanChi("Tý"));    // Missing Can
        assertFalse(canChiService.isValidCanChi("ABC XYZ")); // Invalid both
    }

    @Test
    @DisplayName("Should normalize Can-Chi strings")
    void testNormalizeCanChi() {
        assertEquals("Giáp Tý", canChiService.normalizeCanChi("  Giáp  Tý  "));
        assertEquals("Ất Sửu", canChiService.normalizeCanChi("Ất Sửu"));
        assertNull(canChiService.normalizeCanChi(null));
    }

    @Test
    @DisplayName("Should extract zodiac code from Can-Chi")
    void testGetZodiacCodeFromCanChi() {
        assertEquals("ti", canChiService.getZodiacCodeFromCanChi("Giáp Tý"));
        assertEquals("suu", canChiService.getZodiacCodeFromCanChi("Ất Sửu"));
        assertEquals("ti", canChiService.getZodiacCodeFromCanChi("Tý")); // Just Chi
        assertNull(canChiService.getZodiacCodeFromCanChi(null));
        assertNull(canChiService.getZodiacCodeFromCanChi(""));
    }

    @Test
    @DisplayName("Should get today in Vietnam timezone")
    void testGetTodayInVietnam() {
        LocalDate today = canChiService.getTodayInVietnam();
        assertNotNull(today);
        // Should be within a reasonable range of today
        assertTrue(today.isAfter(LocalDate.now().minusDays(2)));
        assertTrue(today.isBefore(LocalDate.now().plusDays(2)));
    }

    @Test
    @DisplayName("Should include day and month Can-Chi in result")
    void testCalculateCanChi_IncludesDayAndMonth() {
        LocalDate birthDate = LocalDate.of(1990, 5, 15);

        CanChiResult result = canChiService.calculateFromBirthDate(birthDate);

        assertNotNull(result.getCanChiDay());
        assertNotNull(result.getCanChiMonth());
        assertNotNull(result.getCanDay());
        assertNotNull(result.getChiDay());
        assertNotNull(result.getCanMonth());
        assertNotNull(result.getChiMonth());
    }
}

