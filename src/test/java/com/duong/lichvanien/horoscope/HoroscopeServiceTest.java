package com.duong.lichvanien.horoscope;

import com.duong.lichvanien.common.exception.BadRequestException;
import com.duong.lichvanien.common.exception.NotFoundException;
import com.duong.lichvanien.horoscope.dto.*;
import com.duong.lichvanien.horoscope.entity.*;
import com.duong.lichvanien.horoscope.repository.*;
import com.duong.lichvanien.horoscope.service.CanChiService;
import com.duong.lichvanien.horoscope.service.HoroscopeService;
import com.duong.lichvanien.zodiac.entity.ZodiacEntity;
import com.duong.lichvanien.zodiac.service.ZodiacService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("HoroscopeService Tests")
class HoroscopeServiceTest {

    @Mock
    private HoroscopeYearlyRepository yearlyRepository;

    @Mock
    private HoroscopeDailyRepository dailyRepository;

    @Mock
    private HoroscopeMonthlyRepository monthlyRepository;

    @Mock
    private HoroscopeLifetimeRepository lifetimeRepository;

    @Mock
    private ZodiacService zodiacService;

    @Mock
    private CanChiService canChiService;

    @InjectMocks
    private HoroscopeService horoscopeService;

    private ZodiacEntity zodiacTy;

    @BeforeEach
    void setUp() {
        zodiacTy = new ZodiacEntity();
        zodiacTy.setId(1L);
        zodiacTy.setCode("ti");
        zodiacTy.setNameVi("Tý");
        zodiacTy.setOrderNo(1);
    }

    @Nested
    @DisplayName("Lifetime Horoscope Tests")
    class LifetimeTests {

        @Test
        @DisplayName("Should get lifetime horoscope by Can-Chi and gender")
        void getLifetime_Success() {
            // Arrange
            when(canChiService.normalizeCanChi("Giáp Tý")).thenReturn("Giáp Tý");
            when(canChiService.isValidCanChi("Giáp Tý")).thenReturn(true);

            HoroscopeLifetimeEntity entity = createLifetimeEntity();
            when(lifetimeRepository.findByNormalizedCanChiAndGender(eq("Giáp Tý"), eq(HoroscopeLifetimeEntity.Gender.male)))
                    .thenReturn(Optional.of(entity));

            // Act
            HoroscopeLifetimeResponse response = horoscopeService.getLifetime("Giáp Tý", "male");

            // Assert
            assertNotNull(response);
            assertEquals("Giáp Tý", response.getCanChi());
            assertEquals("male", response.getGender());
            assertEquals("Tý", response.getZodiacName());
        }

        @Test
        @DisplayName("Should throw BadRequestException for invalid Can-Chi")
        void getLifetime_InvalidCanChi() {
            when(canChiService.normalizeCanChi("Invalid")).thenReturn("Invalid");
            when(canChiService.isValidCanChi("Invalid")).thenReturn(false);

            assertThrows(BadRequestException.class, () ->
                    horoscopeService.getLifetime("Invalid", "male"));
        }

        @Test
        @DisplayName("Should throw BadRequestException for invalid gender")
        void getLifetime_InvalidGender() {
            when(canChiService.normalizeCanChi("Giáp Tý")).thenReturn("Giáp Tý");
            when(canChiService.isValidCanChi("Giáp Tý")).thenReturn(true);

            assertThrows(BadRequestException.class, () ->
                    horoscopeService.getLifetime("Giáp Tý", "invalid"));
        }

        @Test
        @DisplayName("Should throw NotFoundException when lifetime horoscope not found")
        void getLifetime_NotFound() {
            when(canChiService.normalizeCanChi("Giáp Tý")).thenReturn("Giáp Tý");
            when(canChiService.isValidCanChi("Giáp Tý")).thenReturn(true);
            when(lifetimeRepository.findByNormalizedCanChiAndGender(anyString(), any()))
                    .thenReturn(Optional.empty());
            when(lifetimeRepository.findByCanChiAndGender(anyString(), any()))
                    .thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () ->
                    horoscopeService.getLifetime("Giáp Tý", "male"));
        }

        private HoroscopeLifetimeEntity createLifetimeEntity() {
            HoroscopeLifetimeEntity entity = new HoroscopeLifetimeEntity();
            entity.setId(1L);
            entity.setZodiac(zodiacTy);
            entity.setCanChi("Giáp Tý");
            entity.setGender(HoroscopeLifetimeEntity.Gender.male);
            entity.setOverview("Overview text");
            entity.setCareer("Career text");
            entity.setLove("Love text");
            entity.setHealth("Health text");
            entity.setFamily("Family text");
            entity.setFortune("Fortune text");
            entity.setUnlucky("Unlucky text");
            entity.setAdvice("Advice text");
            entity.setCreatedAt(LocalDateTime.now());
            entity.setUpdatedAt(LocalDateTime.now());
            return entity;
        }
    }

    @Nested
    @DisplayName("Yearly Horoscope Tests")
    class YearlyTests {

        @Test
        @DisplayName("Should get yearly horoscope by zodiac code")
        void getYearly_ByCode_Success() {
            when(zodiacService.getByCode("ti")).thenReturn(zodiacTy);

            HoroscopeYearlyEntity entity = createYearlyEntity();
            when(yearlyRepository.findByZodiacAndYear(zodiacTy, 2025))
                    .thenReturn(Optional.of(entity));

            HoroscopeYearlyResponse response = horoscopeService.getYearly(null, "ti", 2025);

            assertNotNull(response);
            assertEquals(2025, response.getYear());
            assertEquals("ti", response.getZodiac().getCode());
        }

        @Test
        @DisplayName("Should get yearly horoscope by zodiac ID")
        void getYearly_ById_Success() {
            when(zodiacService.getById(1L)).thenReturn(zodiacTy);

            HoroscopeYearlyEntity entity = createYearlyEntity();
            when(yearlyRepository.findByZodiacAndYear(zodiacTy, 2025))
                    .thenReturn(Optional.of(entity));

            HoroscopeYearlyResponse response = horoscopeService.getYearly(1L, null, 2025);

            assertNotNull(response);
            assertEquals(2025, response.getYear());
        }

        @Test
        @DisplayName("Should throw BadRequestException when no zodiac identifier provided")
        void getYearly_NoIdentifier() {
            assertThrows(BadRequestException.class, () ->
                    horoscopeService.getYearly(null, null, 2025));
        }

        @Test
        @DisplayName("Should throw NotFoundException when yearly horoscope not found")
        void getYearly_NotFound() {
            when(zodiacService.getByCode("ti")).thenReturn(zodiacTy);
            when(yearlyRepository.findByZodiacAndYear(zodiacTy, 2025))
                    .thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () ->
                    horoscopeService.getYearly(null, "ti", 2025));
        }

        private HoroscopeYearlyEntity createYearlyEntity() {
            HoroscopeYearlyEntity entity = new HoroscopeYearlyEntity();
            entity.setId(1L);
            entity.setZodiac(zodiacTy);
            entity.setYear(2025);
            entity.setSummary("Summary text");
            entity.setCareer("Career text");
            entity.setLove("Love text");
            entity.setHealth("Health text");
            entity.setFortune("Fortune text");
            entity.setWarnings("Warnings text");
            entity.setCreatedAt(LocalDateTime.now());
            entity.setUpdatedAt(LocalDateTime.now());
            return entity;
        }
    }

    @Nested
    @DisplayName("Monthly Horoscope Tests")
    class MonthlyTests {

        @Test
        @DisplayName("Should get monthly horoscope successfully")
        void getMonthly_Success() {
            when(zodiacService.getByCode("ti")).thenReturn(zodiacTy);

            HoroscopeMonthlyEntity entity = createMonthlyEntity();
            when(monthlyRepository.findByZodiacAndYearAndMonth(zodiacTy, 2025, 12))
                    .thenReturn(Optional.of(entity));

            HoroscopeMonthlyResponse response = horoscopeService.getMonthly(null, "ti", 2025, 12);

            assertNotNull(response);
            assertEquals(2025, response.getYear());
            assertEquals(12, response.getMonth());
        }

        @Test
        @DisplayName("Should throw BadRequestException for invalid month")
        void getMonthly_InvalidMonth() {
            assertThrows(BadRequestException.class, () ->
                    horoscopeService.getMonthly(null, "ti", 2025, 13));

            assertThrows(BadRequestException.class, () ->
                    horoscopeService.getMonthly(null, "ti", 2025, 0));
        }

        private HoroscopeMonthlyEntity createMonthlyEntity() {
            HoroscopeMonthlyEntity entity = new HoroscopeMonthlyEntity();
            entity.setId(1L);
            entity.setZodiac(zodiacTy);
            entity.setYear(2025);
            entity.setMonth(12);
            entity.setSummary("Summary text");
            entity.setCareer("Career text");
            entity.setLove("Love text");
            entity.setHealth("Health text");
            entity.setFortune("Fortune text");
            entity.setCreatedAt(LocalDateTime.now());
            entity.setUpdatedAt(LocalDateTime.now());
            return entity;
        }
    }

    @Nested
    @DisplayName("Daily Horoscope Tests")
    class DailyTests {

        @Test
        @DisplayName("Should get daily horoscope successfully")
        void getDaily_Success() {
            LocalDate date = LocalDate.of(2025, 12, 9);
            when(zodiacService.getByCode("ti")).thenReturn(zodiacTy);

            HoroscopeDailyEntity entity = createDailyEntity(date);
            when(dailyRepository.findByZodiacAndSolarDate(zodiacTy, date))
                    .thenReturn(Optional.of(entity));

            HoroscopeDailyResponse response = horoscopeService.getDaily(null, "ti", date);

            assertNotNull(response);
            assertEquals("2025-12-09", response.getDate());
            assertEquals("ti", response.getZodiac().getCode());
        }

        @Test
        @DisplayName("Should use today's date when date not provided")
        void getDaily_DefaultToToday() {
            when(zodiacService.getByCode("ti")).thenReturn(zodiacTy);
            when(dailyRepository.findByZodiacAndSolarDate(eq(zodiacTy), any(LocalDate.class)))
                    .thenReturn(Optional.of(createDailyEntity(LocalDate.now())));

            // Should not throw - uses today's date
            HoroscopeDailyResponse response = horoscopeService.getDaily(null, "ti", null);
            assertNotNull(response);
        }

        private HoroscopeDailyEntity createDailyEntity(LocalDate date) {
            HoroscopeDailyEntity entity = new HoroscopeDailyEntity();
            entity.setId(1L);
            entity.setZodiac(zodiacTy);
            entity.setSolarDate(date);
            entity.setSummary("Summary text");
            entity.setCareer("Career text");
            entity.setLove("Love text");
            entity.setHealth("Health text");
            entity.setFortune("Fortune text");
            entity.setLuckyColor("Đỏ");
            entity.setLuckyNumber("3,7");
            entity.setCreatedAt(LocalDateTime.now());
            entity.setUpdatedAt(LocalDateTime.now());
            return entity;
        }
    }
}

