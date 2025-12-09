package com.duong.lichvanien.horoscope;

import com.duong.lichvanien.horoscope.dto.LifetimeByBirthRequest;
import com.duong.lichvanien.horoscope.entity.HoroscopeLifetimeEntity;
import com.duong.lichvanien.horoscope.repository.HoroscopeLifetimeRepository;
import com.duong.lichvanien.zodiac.entity.ZodiacEntity;
import com.duong.lichvanien.zodiac.repository.ZodiacRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Lifetime By Birth Integration Tests")
class LifetimeByBirthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ZodiacRepository zodiacRepository;

    @Autowired
    private HoroscopeLifetimeRepository lifetimeRepository;

    private ZodiacEntity zodiacTy;
    private ZodiacEntity zodiacTuat;

    @BeforeEach
    void setUp() {
        // Create zodiacs
        zodiacTy = createZodiac("ti", "Tý", 1);
        zodiacTuat = createZodiac("tuat", "Tuất", 11);
    }

    private ZodiacEntity createZodiac(String code, String nameVi, int orderNo) {
        ZodiacEntity zodiac = new ZodiacEntity();
        zodiac.setCode(code);
        zodiac.setNameVi(nameVi);
        zodiac.setOrderNo(orderNo);
        return zodiacRepository.save(zodiac);
    }

    private HoroscopeLifetimeEntity createLifetime(ZodiacEntity zodiac, String canChi, HoroscopeLifetimeEntity.Gender gender) {
        HoroscopeLifetimeEntity entity = new HoroscopeLifetimeEntity();
        entity.setZodiac(zodiac);
        entity.setCanChi(canChi);
        entity.setGender(gender);
        entity.setOverview("Test overview for " + canChi);
        entity.setCareer("Test career");
        entity.setLove("Test love");
        entity.setHealth("Test health");
        entity.setFamily("Test family");
        entity.setFortune("Test fortune");
        entity.setUnlucky("Test unlucky");
        entity.setAdvice("Test advice");
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        return lifetimeRepository.save(entity);
    }

    @Nested
    @DisplayName("POST /api/v1/horoscope/lifetime/by-birth")
    class LifetimeByBirthEndpoint {

        @Test
        @DisplayName("Should return 200 with exact match")
        void shouldReturn200WithExactMatch() throws Exception {
            // Create test data
            createLifetime(zodiacTy, "Giáp Tý", HoroscopeLifetimeEntity.Gender.male);

            LifetimeByBirthRequest request = LifetimeByBirthRequest.builder()
                    .date("1984-03-15")  // Giáp Tý year
                    .hour(10)
                    .minute(30)
                    .gender("male")
                    .build();

            mockMvc.perform(post("/api/v1/horoscope/lifetime/by-birth")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.canChi", is("Giáp Tý")))
                    .andExpect(jsonPath("$.gender", is("male")))
                    .andExpect(jsonPath("$.zodiacCode", is("ti")))
                    .andExpect(jsonPath("$.computed", is(true)))
                    .andExpect(jsonPath("$.isFallback", is(false)))
                    .andExpect(jsonPath("$.hourBranch", is("ty")))  // 10:30 = Tỵ
                    .andExpect(jsonPath("$.overview", containsString("Giáp Tý")));
        }

        @Test
        @DisplayName("Should return 200 with fallback when exact match not found")
        void shouldReturn200WithFallback() throws Exception {
            // Create fallback data (different canChi but same zodiac)
            createLifetime(zodiacTy, "Bính Tý", HoroscopeLifetimeEntity.Gender.female);

            LifetimeByBirthRequest request = LifetimeByBirthRequest.builder()
                    .date("1984-03-15")  // Giáp Tý year
                    .hour(23)
                    .minute(0)
                    .gender("female")
                    .build();

            mockMvc.perform(post("/api/v1/horoscope/lifetime/by-birth")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.zodiacCode", is("ti")))
                    .andExpect(jsonPath("$.gender", is("female")))
                    .andExpect(jsonPath("$.computed", is(true)))
                    .andExpect(jsonPath("$.isFallback", is(true)))
                    .andExpect(jsonPath("$.message", containsString("fallback")));
        }

        @Test
        @DisplayName("Should return 400 for invalid date format")
        void shouldReturn400ForInvalidDate() throws Exception {
            LifetimeByBirthRequest request = LifetimeByBirthRequest.builder()
                    .date("invalid-date")
                    .hour(10)
                    .minute(0)
                    .gender("male")
                    .build();

            mockMvc.perform(post("/api/v1/horoscope/lifetime/by-birth")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 for invalid gender")
        void shouldReturn400ForInvalidGender() throws Exception {
            LifetimeByBirthRequest request = LifetimeByBirthRequest.builder()
                    .date("1990-02-15")
                    .hour(10)
                    .minute(0)
                    .gender("invalid")
                    .build();

            mockMvc.perform(post("/api/v1/horoscope/lifetime/by-birth")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 for hour out of range")
        void shouldReturn400ForInvalidHour() throws Exception {
            String json = """
                    {
                      "date": "1990-02-15",
                      "hour": 25,
                      "minute": 0,
                      "gender": "male"
                    }
                    """;

            mockMvc.perform(post("/api/v1/horoscope/lifetime/by-birth")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Hour Branch Boundary Tests")
    class HourBranchBoundaryTests {

        @BeforeEach
        void setUpLifetimeData() {
            createLifetime(zodiacTy, "Giáp Tý", HoroscopeLifetimeEntity.Gender.male);
        }

        @Test
        @DisplayName("Hour 23:00 should be Tý branch")
        void hour23_shouldBeTy() throws Exception {
            LifetimeByBirthRequest request = LifetimeByBirthRequest.builder()
                    .date("1984-03-15")
                    .hour(23)
                    .minute(0)
                    .gender("male")
                    .build();

            mockMvc.perform(post("/api/v1/horoscope/lifetime/by-birth")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.hourBranch", is("ti")))
                    .andExpect(jsonPath("$.hourBranchName", is("Tý")));
        }

        @Test
        @DisplayName("Hour 00:00 should be Tý branch")
        void hour00_00_shouldBeTy() throws Exception {
            LifetimeByBirthRequest request = LifetimeByBirthRequest.builder()
                    .date("1984-03-15")
                    .hour(0)
                    .minute(0)
                    .gender("male")
                    .build();

            mockMvc.perform(post("/api/v1/horoscope/lifetime/by-birth")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.hourBranch", is("ti")));
        }

        @Test
        @DisplayName("Hour 00:59 should be Tý branch")
        void hour00_59_shouldBeTy() throws Exception {
            LifetimeByBirthRequest request = LifetimeByBirthRequest.builder()
                    .date("1984-03-15")
                    .hour(0)
                    .minute(59)
                    .gender("male")
                    .build();

            mockMvc.perform(post("/api/v1/horoscope/lifetime/by-birth")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.hourBranch", is("ti")));
        }

        @Test
        @DisplayName("Hour 01:00 should be Sửu branch")
        void hour01_00_shouldBeSuu() throws Exception {
            LifetimeByBirthRequest request = LifetimeByBirthRequest.builder()
                    .date("1984-03-15")
                    .hour(1)
                    .minute(0)
                    .gender("male")
                    .build();

            mockMvc.perform(post("/api/v1/horoscope/lifetime/by-birth")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.hourBranch", is("suu")))
                    .andExpect(jsonPath("$.hourBranchName", is("Sửu")));
        }

        @Test
        @DisplayName("Hour 22:59 should be Hợi branch")
        void hour22_59_shouldBeHoi() throws Exception {
            LifetimeByBirthRequest request = LifetimeByBirthRequest.builder()
                    .date("1984-03-15")
                    .hour(22)
                    .minute(59)
                    .gender("male")
                    .build();

            mockMvc.perform(post("/api/v1/horoscope/lifetime/by-birth")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.hourBranch", is("hoi")))
                    .andExpect(jsonPath("$.hourBranchName", is("Hợi")));
        }
    }
}

