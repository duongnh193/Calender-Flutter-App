package com.duong.lichvanien.horoscope;

import com.duong.lichvanien.horoscope.entity.HoroscopeDailyEntity;
import com.duong.lichvanien.horoscope.entity.HoroscopeLifetimeEntity;
import com.duong.lichvanien.horoscope.entity.HoroscopeMonthlyEntity;
import com.duong.lichvanien.horoscope.entity.HoroscopeYearlyEntity;
import com.duong.lichvanien.horoscope.repository.HoroscopeDailyRepository;
import com.duong.lichvanien.horoscope.repository.HoroscopeLifetimeRepository;
import com.duong.lichvanien.horoscope.repository.HoroscopeMonthlyRepository;
import com.duong.lichvanien.horoscope.repository.HoroscopeYearlyRepository;
import com.duong.lichvanien.zodiac.entity.ZodiacEntity;
import com.duong.lichvanien.zodiac.repository.ZodiacRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("HoroscopeController Integration Tests")
class HoroscopeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ZodiacRepository zodiacRepository;

    @Autowired
    private HoroscopeLifetimeRepository lifetimeRepository;

    @Autowired
    private HoroscopeYearlyRepository yearlyRepository;

    @Autowired
    private HoroscopeMonthlyRepository monthlyRepository;

    @Autowired
    private HoroscopeDailyRepository dailyRepository;

    private ZodiacEntity zodiacTy;

    @BeforeEach
    void setUp() {
        // Create zodiac
        zodiacTy = new ZodiacEntity();
        zodiacTy.setCode("ti");
        zodiacTy.setNameVi("Tý");
        zodiacTy.setOrderNo(1);
        zodiacTy = zodiacRepository.save(zodiacTy);
    }

    @Nested
    @DisplayName("GET /api/v1/horoscope/lifetime")
    class LifetimeEndpoint {

        @Test
        @DisplayName("Should return lifetime horoscope successfully")
        void getLifetime_Success() throws Exception {
            // Create test data
            HoroscopeLifetimeEntity entity = new HoroscopeLifetimeEntity();
            entity.setZodiac(zodiacTy);
            entity.setCanChi("Giáp Tý");
            entity.setGender(HoroscopeLifetimeEntity.Gender.male);
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
            lifetimeRepository.save(entity);

            mockMvc.perform(get("/api/v1/horoscope/lifetime")
                            .param("canChi", "Giáp Tý")
                            .param("gender", "male")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.canChi", is("Giáp Tý")))
                    .andExpect(jsonPath("$.gender", is("male")))
                    .andExpect(jsonPath("$.zodiacCode", is("ti")))
                    .andExpect(jsonPath("$.overview", is("Test overview")));
        }

        @Test
        @DisplayName("Should return 400 for invalid Can-Chi")
        void getLifetime_InvalidCanChi() throws Exception {
            mockMvc.perform(get("/api/v1/horoscope/lifetime")
                            .param("canChi", "Invalid")
                            .param("gender", "male"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code", is("INVALID_CAN_CHI")));
        }

        @Test
        @DisplayName("Should return 400 for invalid gender")
        void getLifetime_InvalidGender() throws Exception {
            mockMvc.perform(get("/api/v1/horoscope/lifetime")
                            .param("canChi", "Giáp Tý")
                            .param("gender", "invalid"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code", is("INVALID_GENDER")));
        }

        @Test
        @DisplayName("Should return 404 when not found")
        void getLifetime_NotFound() throws Exception {
            mockMvc.perform(get("/api/v1/horoscope/lifetime")
                            .param("canChi", "Ất Sửu")
                            .param("gender", "female"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code", is("NOT_FOUND")));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/horoscope/yearly")
    class YearlyEndpoint {

        @Test
        @DisplayName("Should return yearly horoscope by zodiac code")
        void getYearly_ByCode_Success() throws Exception {
            HoroscopeYearlyEntity entity = createYearlyEntity(2025);
            yearlyRepository.save(entity);

            mockMvc.perform(get("/api/v1/horoscope/yearly")
                            .param("zodiacCode", "ti")
                            .param("year", "2025"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.year", is(2025)))
                    .andExpect(jsonPath("$.zodiac.code", is("ti")));
        }

        @Test
        @DisplayName("Should return yearly horoscope by zodiac ID")
        void getYearly_ById_Success() throws Exception {
            HoroscopeYearlyEntity entity = createYearlyEntity(2025);
            yearlyRepository.save(entity);

            mockMvc.perform(get("/api/v1/horoscope/yearly")
                            .param("zodiacId", zodiacTy.getId().toString())
                            .param("year", "2025"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.year", is(2025)));
        }

        @Test
        @DisplayName("Should return 400 when neither zodiacId nor zodiacCode provided")
        void getYearly_NoIdentifier() throws Exception {
            mockMvc.perform(get("/api/v1/horoscope/yearly")
                            .param("year", "2025"))
                    .andExpect(status().isBadRequest());
        }

        private HoroscopeYearlyEntity createYearlyEntity(int year) {
            HoroscopeYearlyEntity entity = new HoroscopeYearlyEntity();
            entity.setZodiac(zodiacTy);
            entity.setYear(year);
            entity.setSummary("Test summary");
            entity.setCareer("Test career");
            entity.setLove("Test love");
            entity.setHealth("Test health");
            entity.setFortune("Test fortune");
            entity.setWarnings("Test warnings");
            entity.setCreatedAt(LocalDateTime.now());
            entity.setUpdatedAt(LocalDateTime.now());
            return entity;
        }
    }

    @Nested
    @DisplayName("GET /api/v1/horoscope/monthly")
    class MonthlyEndpoint {

        @Test
        @DisplayName("Should return monthly horoscope successfully")
        void getMonthly_Success() throws Exception {
            HoroscopeMonthlyEntity entity = createMonthlyEntity(2025, 12);
            monthlyRepository.save(entity);

            mockMvc.perform(get("/api/v1/horoscope/monthly")
                            .param("zodiacCode", "ti")
                            .param("year", "2025")
                            .param("month", "12"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.year", is(2025)))
                    .andExpect(jsonPath("$.month", is(12)))
                    .andExpect(jsonPath("$.zodiac.code", is("ti")));
        }

        private HoroscopeMonthlyEntity createMonthlyEntity(int year, int month) {
            HoroscopeMonthlyEntity entity = new HoroscopeMonthlyEntity();
            entity.setZodiac(zodiacTy);
            entity.setYear(year);
            entity.setMonth(month);
            entity.setSummary("Test summary");
            entity.setCareer("Test career");
            entity.setLove("Test love");
            entity.setHealth("Test health");
            entity.setFortune("Test fortune");
            entity.setCreatedAt(LocalDateTime.now());
            entity.setUpdatedAt(LocalDateTime.now());
            return entity;
        }
    }

    @Nested
    @DisplayName("GET /api/v1/horoscope/daily")
    class DailyEndpoint {

        @Test
        @DisplayName("Should return daily horoscope successfully")
        void getDaily_Success() throws Exception {
            LocalDate date = LocalDate.of(2025, 12, 9);
            HoroscopeDailyEntity entity = createDailyEntity(date);
            dailyRepository.save(entity);

            mockMvc.perform(get("/api/v1/horoscope/daily")
                            .param("zodiacCode", "ti")
                            .param("date", "2025-12-09"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.date", is("2025-12-09")))
                    .andExpect(jsonPath("$.zodiac.code", is("ti")))
                    .andExpect(jsonPath("$.luckyColor", is("Đỏ")));
        }

        private HoroscopeDailyEntity createDailyEntity(LocalDate date) {
            HoroscopeDailyEntity entity = new HoroscopeDailyEntity();
            entity.setZodiac(zodiacTy);
            entity.setSolarDate(date);
            entity.setSummary("Test summary");
            entity.setCareer("Test career");
            entity.setLove("Test love");
            entity.setHealth("Test health");
            entity.setFortune("Test fortune");
            entity.setLuckyColor("Đỏ");
            entity.setLuckyNumber("3,7");
            entity.setCreatedAt(LocalDateTime.now());
            entity.setUpdatedAt(LocalDateTime.now());
            return entity;
        }
    }

    @Nested
    @DisplayName("GET /api/v1/horoscope/can-chi")
    class CanChiEndpoint {

        @Test
        @DisplayName("Should calculate Can-Chi from birth date")
        void calculateCanChi_Success() throws Exception {
            mockMvc.perform(get("/api/v1/horoscope/can-chi")
                            .param("birthDate", "1984-03-15"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.canYear", is("Giáp")))
                    .andExpect(jsonPath("$.chiYear", is("Tý")))
                    .andExpect(jsonPath("$.canChiYear", is("Giáp Tý")))
                    .andExpect(jsonPath("$.zodiacCode", is("ti")));
        }

        @Test
        @DisplayName("Should return Can-Chi for 2000 (Canh Thìn)")
        void calculateCanChi_Year2000() throws Exception {
            mockMvc.perform(get("/api/v1/horoscope/can-chi")
                            .param("birthDate", "2000-06-15"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.canChiYear", is("Canh Thìn")))
                    .andExpect(jsonPath("$.zodiacCode", is("thin")));
        }
    }
}

