package com.duong.lichvanien.horoscope;

import com.duong.lichvanien.horoscope.dto.CanChiInfo;
import com.duong.lichvanien.horoscope.dto.LifetimeByBirthRequest;
import com.duong.lichvanien.horoscope.dto.LifetimeByBirthResponse;
import com.duong.lichvanien.horoscope.entity.HoroscopeLifetimeEntity;
import com.duong.lichvanien.horoscope.repository.HoroscopeLifetimeRepository;
import com.duong.lichvanien.horoscope.service.CanChiResolver;
import com.duong.lichvanien.horoscope.service.LifetimeByBirthService;
import com.duong.lichvanien.zodiac.entity.ZodiacEntity;
import com.duong.lichvanien.zodiac.repository.ZodiacRepository;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("LifetimeByBirthService Tests")
class LifetimeByBirthServiceTest {

    @Mock
    private CanChiResolver canChiResolver;

    @Mock
    private HoroscopeLifetimeRepository lifetimeRepository;

    @Mock
    private ZodiacRepository zodiacRepository;

    private LifetimeByBirthService service;

    private ZodiacEntity zodiacTuat;

    @BeforeEach
    void setUp() throws Exception {
        SimpleMeterRegistry meterRegistry = new SimpleMeterRegistry();
        service = new LifetimeByBirthService(
                canChiResolver,
                lifetimeRepository,
                zodiacRepository,
                meterRegistry
        );
        // Call initMetrics via reflection since it's package-private
        java.lang.reflect.Method initMetrics = LifetimeByBirthService.class.getDeclaredMethod("initMetrics");
        initMetrics.setAccessible(true);
        initMetrics.invoke(service);

        // Setup zodiac
        zodiacTuat = new ZodiacEntity();
        zodiacTuat.setId(11L);
        zodiacTuat.setCode("tuat");
        zodiacTuat.setNameVi("Tuất");
        zodiacTuat.setOrderNo(11);
    }

    @Nested
    @DisplayName("Get Lifetime By Birth - Success Cases")
    class SuccessCases {

        @Test
        @DisplayName("Should return exact match when found")
        void shouldReturnExactMatch() {
            // Arrange
            LifetimeByBirthRequest request = LifetimeByBirthRequest.builder()
                    .date("1990-02-15")
                    .hour(23)
                    .minute(30)
                    .gender("male")
                    .build();

            CanChiInfo canChiInfo = CanChiInfo.builder()
                    .canChiYear("Canh Tuất")
                    .canYear("Canh")
                    .chiYear("Tuất")
                    .zodiacId(11L)
                    .zodiacCode("tuat")
                    .zodiacName("Tuất")
                    .hourBranchCode("ti")
                    .hourBranchName("Tý")
                    .hourBranchIndex(0)
                    .solarDate("1990-02-15")
                    .originalDate("1990-02-15")
                    .wasLunar(false)
                    .lunarYear(1990)
                    .lunarMonth(1)
                    .lunarDay(20)
                    .wasLeapMonth(false)
                    .build();

            when(canChiResolver.resolve(any(LocalDate.class), eq(23), eq(30), eq(false), eq(false)))
                    .thenReturn(canChiInfo);
            when(canChiResolver.normalizeCanChi("Canh Tuất")).thenReturn("Canh Tuất");
            when(canChiResolver.normalizeCanChiForKey("Canh Tuất")).thenReturn("canhtuất");

            HoroscopeLifetimeEntity entity = createLifetimeEntity("Canh Tuất", HoroscopeLifetimeEntity.Gender.male);
            when(lifetimeRepository.findAllByNormalizedCanChiAndGender(eq("Canh Tuất"), eq(HoroscopeLifetimeEntity.Gender.male)))
                    .thenReturn(List.of(entity));

            // Act
            LifetimeByBirthResponse response = service.getLifetimeByBirth(request);

            // Assert
            assertNotNull(response);
            assertEquals("Canh Tuất", response.getCanChi());
            assertEquals("male", response.getGender());
            assertEquals(11L, response.getZodiacId());
            assertEquals("tuat", response.getZodiacCode());
            assertEquals("ti", response.getHourBranch());
            assertTrue(response.getComputed());
            assertFalse(response.getIsFallback());
            assertNull(response.getMessage());
        }

        @Test
        @DisplayName("Should return fallback when exact match not found but zodiac match exists")
        void shouldReturnFallback() {
            // Arrange
            LifetimeByBirthRequest request = LifetimeByBirthRequest.builder()
                    .date("1990-02-15")
                    .hour(10)
                    .minute(0)
                    .gender("female")
                    .build();

            CanChiInfo canChiInfo = CanChiInfo.builder()
                    .canChiYear("Canh Tuất")
                    .zodiacId(11L)
                    .zodiacCode("tuat")
                    .zodiacName("Tuất")
                    .hourBranchCode("ty")
                    .hourBranchName("Tỵ")
                    .hourBranchIndex(5)
                    .solarDate("1990-02-15")
                    .originalDate("1990-02-15")
                    .wasLunar(false)
                    .build();

            when(canChiResolver.resolve(any(), anyInt(), anyInt(), anyBoolean(), anyBoolean()))
                    .thenReturn(canChiInfo);
            when(canChiResolver.normalizeCanChi(anyString())).thenReturn("Canh Tuất");
            when(canChiResolver.normalizeCanChiForKey(anyString())).thenReturn("canhtuất");

            // No exact match
            when(lifetimeRepository.findAllByNormalizedCanChiAndGender(anyString(), any()))
                    .thenReturn(List.of());
            when(lifetimeRepository.findByCanChiAndGender(anyString(), any()))
                    .thenReturn(Optional.empty());

            // But zodiac fallback exists
            HoroscopeLifetimeEntity fallbackEntity = createLifetimeEntity("Giáp Tuất", HoroscopeLifetimeEntity.Gender.female);
            when(lifetimeRepository.findFirstByZodiacIdAndGender(eq(11L), eq(HoroscopeLifetimeEntity.Gender.female)))
                    .thenReturn(Optional.of(fallbackEntity));

            // Act
            LifetimeByBirthResponse response = service.getLifetimeByBirth(request);

            // Assert
            assertNotNull(response);
            assertNull(response.getCanChi()); // null indicates fallback
            assertTrue(response.getIsFallback());
            assertNotNull(response.getMessage());
            // Message contains "zodiac-level default" when fallback is used
            assertTrue(response.getMessage().contains("zodiac-level default") ||
                       response.getMessage().contains("not found"));
        }
    }

    @Nested
    @DisplayName("Get Lifetime By Birth - Validation Cases")
    class ValidationCases {

        @Test
        @DisplayName("Should throw exception for invalid date")
        void shouldThrowForInvalidDate() {
            LifetimeByBirthRequest request = LifetimeByBirthRequest.builder()
                    .date("invalid-date")
                    .hour(10)
                    .minute(0)
                    .gender("male")
                    .build();

            assertThrows(Exception.class, () -> service.getLifetimeByBirth(request));
        }

        @Test
        @DisplayName("Should throw exception for invalid gender")
        void shouldThrowForInvalidGender() {
            LifetimeByBirthRequest request = LifetimeByBirthRequest.builder()
                    .date("1990-02-15")
                    .hour(10)
                    .minute(0)
                    .gender("invalid")
                    .build();

            CanChiInfo canChiInfo = CanChiInfo.builder()
                    .canChiYear("Canh Tuất")
                    .zodiacCode("tuat")
                    .build();

            when(canChiResolver.resolve(any(), anyInt(), anyInt(), anyBoolean(), anyBoolean()))
                    .thenReturn(canChiInfo);

            assertThrows(Exception.class, () -> service.getLifetimeByBirth(request));
        }
    }

    @Nested
    @DisplayName("Lunar Date Handling")
    class LunarDateTests {

        @Test
        @DisplayName("Should handle lunar date input")
        void shouldHandleLunarDate() {
            LifetimeByBirthRequest request = LifetimeByBirthRequest.builder()
                    .date("1990-01-20")
                    .hour(8)
                    .minute(30)
                    .isLunar(true)
                    .isLeapMonth(false)
                    .gender("male")
                    .build();

            CanChiInfo canChiInfo = CanChiInfo.builder()
                    .canChiYear("Kỷ Tỵ")
                    .zodiacId(6L)
                    .zodiacCode("ty")
                    .zodiacName("Tỵ")
                    .hourBranchCode("thin")
                    .hourBranchName("Thìn")
                    .solarDate("1990-02-15")
                    .originalDate("1990-01-20")
                    .wasLunar(true)
                    .build();

            when(canChiResolver.resolve(eq(LocalDate.of(1990, 1, 20)), eq(8), eq(30), eq(true), eq(false)))
                    .thenReturn(canChiInfo);
            when(canChiResolver.normalizeCanChi(anyString())).thenReturn("Kỷ Tỵ");
            when(canChiResolver.normalizeCanChiForKey(anyString())).thenReturn("kỷtỵ");

            when(lifetimeRepository.findAllByNormalizedCanChiAndGender(anyString(), any()))
                    .thenReturn(List.of());
            when(lifetimeRepository.findByCanChiAndGender(anyString(), any()))
                    .thenReturn(Optional.empty());
            when(lifetimeRepository.findFirstByZodiacIdAndGender(anyLong(), any()))
                    .thenReturn(Optional.empty());

            LifetimeByBirthResponse response = service.getLifetimeByBirth(request);

            assertNotNull(response);
            verify(canChiResolver).resolve(any(), anyInt(), anyInt(), eq(true), eq(false));
        }
    }

    private HoroscopeLifetimeEntity createLifetimeEntity(String canChi, HoroscopeLifetimeEntity.Gender gender) {
        HoroscopeLifetimeEntity entity = new HoroscopeLifetimeEntity();
        entity.setId(1L);
        entity.setZodiac(zodiacTuat);
        entity.setCanChi(canChi);
        entity.setGender(gender);
        entity.setOverview("Test overview");
        entity.setCareer("Test career");
        entity.setLove("Test love");
        entity.setHealth("Test health");
        entity.setFamily("Test family");
        entity.setFortune("Test fortune");
        entity.setUnlucky("Test unlucky");
        entity.setAdvice("Test advice");
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        return entity;
    }
}

