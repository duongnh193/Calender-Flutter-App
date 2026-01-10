package com.duong.lichvanien.tuvi.service;

import com.duong.lichvanien.tuvi.config.GrokProperties;
import com.duong.lichvanien.tuvi.dto.*;
import com.duong.lichvanien.tuvi.dto.interpretation.CycleInterpretationResponse;
import com.duong.lichvanien.tuvi.dto.interpretation.TuViInterpretationResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Unit tests for GrokAIService.
 */
@ExtendWith(MockitoExtension.class)
class GrokAIServiceTest {

    @Mock
    private GrokProperties grokProperties;

    private ObjectMapper objectMapper;
    private GrokAIService grokAIService;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        grokAIService = new GrokAIService(grokProperties, objectMapper);
    }

    @Test
    @DisplayName("Should return false when API key is not configured")
    void isAvailable_WhenNoApiKey_ReturnsFalse() {
        when(grokProperties.isAvailable()).thenReturn(false);
        
        assertThat(grokAIService.isAvailable()).isFalse();
    }

    @Test
    @DisplayName("Should return true when API key is configured")
    void isAvailable_WhenApiKeyConfigured_ReturnsTrue() {
        when(grokProperties.isAvailable()).thenReturn(true);
        
        assertThat(grokAIService.isAvailable()).isTrue();
    }

    @Test
    @DisplayName("Should return model name from properties")
    void getModelName_ReturnsConfiguredModel() {
        when(grokProperties.getModel()).thenReturn("grok-beta");
        
        assertThat(grokAIService.getModelName()).isEqualTo("grok-beta");
    }

    @Test
    @DisplayName("Should return null when API key not configured for full interpretation")
    void generateFullInterpretation_WhenNoApiKey_ReturnsNull() {
        when(grokProperties.isAvailable()).thenReturn(false);
        
        TuViChartResponse chart = createMockChart();
        TuViInterpretationResponse result = grokAIService.generateFullInterpretation(chart, "Test", "male");
        
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Should return null when API key not configured for cycle interpretation")
    void generateCycleInterpretation_WhenNoApiKey_ReturnsNull() {
        when(grokProperties.isAvailable()).thenReturn(false);
        
        TuViChartResponse chart = createMockChart();
        CycleInterpretationResponse result = grokAIService.generateCycleInterpretation(chart, "Test", "male");
        
        assertThat(result).isNull();
    }

    /**
     * Create a mock TuViChartResponse for testing.
     */
    private TuViChartResponse createMockChart() {
        CenterInfo center = CenterInfo.builder()
                .name("Test User")
                .solarDate("1995-03-02")
                .birthHour(8)
                .birthMinute(30)
                .birthHourCanChi("Thìn")
                .lunarYear(1995)
                .lunarMonth(2)
                .lunarDay(2)
                .lunarYearCanChi("Ất Hợi")
                .lunarMonthCanChi("Mậu Dần")
                .lunarDayCanChi("Giáp Tý")
                .banMenh("Sơn Đầu Hỏa")
                .banMenhNguHanh("HOA")
                .cuc("Thổ ngũ cục")
                .cucValue(5)
                .cucNguHanh("THO")
                .menhCucRelation("Hỏa sinh Thổ")
                .amDuong("Âm")
                .thuanNghich("Nghịch")
                .chuMenh("Tử Vi")
                .chuThan("Thiên Phủ")
                .thanCu("Quan Lộc")
                .build();

        List<PalaceInfo> palaces = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            PalaceInfo palace = PalaceInfo.builder()
                    .index(i)
                    .name("Palace " + i)
                    .nameCode("PALACE_" + i)
                    .diaChiCode("TY")
                    .canChiPrefix("G.Tý")
                    .truongSinhStage("Trường Sinh")
                    .daiVanStartAge(5 + i * 10)
                    .daiVanLabel(String.valueOf(5 + i * 10))
                    .hasTuan(false)
                    .hasTriet(false)
                    .isThanCu(false)
                    .stars(new ArrayList<>())
                    .build();
            palaces.add(palace);
        }

        CycleInfo cycles = CycleInfo.builder()
                .direction("THUAN")
                .directionText("Thuận")
                .daiVanStartAge(5)
                .cyclePeriod(10)
                .daiVanList(new ArrayList<>())
                .build();

        MarkerInfo markers = MarkerInfo.builder()
                .tuanStart("TUAT")
                .tuanEnd("HOI")
                .trietStart("THIN")
                .trietEnd("TY")
                .build();

        return TuViChartResponse.builder()
                .center(center)
                .palaces(palaces)
                .cycles(cycles)
                .markers(markers)
                .chartHash("test-chart-hash")
                .calculatedAt("2025-01-10T10:00:00+07:00")
                .build();
    }
}

