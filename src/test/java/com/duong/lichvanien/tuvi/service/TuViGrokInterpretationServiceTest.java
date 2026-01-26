package com.duong.lichvanien.tuvi.service;

import com.duong.lichvanien.affiliate.service.AffiliateService;
import com.duong.lichvanien.tuvi.dto.*;
import com.duong.lichvanien.tuvi.dto.interpretation.CycleInterpretationResponse;
import com.duong.lichvanien.tuvi.dto.interpretation.TuViInterpretationResponse;
import com.duong.lichvanien.tuvi.entity.TuViCycleInterpretationEntity;
import com.duong.lichvanien.tuvi.repository.TuViCycleInterpretationRepository;
import com.duong.lichvanien.user.repository.ContentAccessRepository;
import com.duong.lichvanien.user.service.PaymentService;
import com.duong.lichvanien.xu.repository.XuTransactionRepository;
import com.duong.lichvanien.xu.service.XuService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for TuViGrokInterpretationService.
 */
@ExtendWith(MockitoExtension.class)
class TuViGrokInterpretationServiceTest {

    @Mock
    private TuViChartService chartService;

    @Mock
    private GrokAIService grokAIService;

    @Mock
    private TuViInterpretationDatabaseService interpretationDatabaseService;

    @Mock
    private TuViCycleInterpretationRepository cycleInterpretationRepository;

    @Mock
    private XuService xuService;

    @Mock
    private AffiliateService affiliateService;

    @Mock
    private PaymentService paymentService;

    @Mock
    private XuTransactionRepository xuTransactionRepository;

    @Mock
    private ContentAccessRepository contentAccessRepository;

    private ObjectMapper objectMapper;
    private TuViGrokInterpretationService service;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        service = new TuViGrokInterpretationService(
                chartService,
                grokAIService,
                interpretationDatabaseService,
                cycleInterpretationRepository,
                xuService,
                xuTransactionRepository,
                affiliateService,
                paymentService,
                contentAccessRepository,
                objectMapper
        );
    }

    @Test
    @DisplayName("Should return cached full interpretation when available")
    void generateFullInterpretation_WhenCached_ReturnsCached() {
        TuViChartRequest request = createMockRequest();
        TuViChartResponse chart = createMockChart();
        TuViInterpretationResponse cachedResponse = TuViInterpretationResponse.builder()
                .name("Test")
                .gender("male")
                .build();

        when(chartService.generateChart(any())).thenReturn(chart);
        when(interpretationDatabaseService.findByChartHash(any(), any(), any()))
                .thenReturn(Optional.of(cachedResponse));

        TuViInterpretationResponse result = service.generateFullInterpretation(request);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Test");
        verify(grokAIService, never()).generateFullInterpretation(any(), any(), any());
    }

    @Test
    @DisplayName("Should return cached cycle interpretation when available")
    void generateCycleInterpretation_WhenCached_ReturnsCached() {
        TuViChartRequest request = createMockRequest();
        TuViChartResponse chart = createMockChart();
        
        TuViCycleInterpretationEntity cachedEntity = new TuViCycleInterpretationEntity();
        cachedEntity.setChartHash("test-hash");
        cachedEntity.setGender(TuViCycleInterpretationEntity.Gender.male);
        cachedEntity.setName("Test");
        cachedEntity.setCycleInterpretationData("{\"introduction\":\"Test intro\",\"overallCycleSummary\":\"Summary\",\"daiVanInterpretations\":[],\"generalAdvice\":\"Advice\"}");
        cachedEntity.setAiModel("grok-beta");
        cachedEntity.setGeneratedAt(java.time.LocalDateTime.now());

        when(chartService.generateChart(any())).thenReturn(chart);
        when(cycleInterpretationRepository.findByChartHashAndGender(any(), any()))
                .thenReturn(Optional.of(cachedEntity));

        CycleInterpretationResponse result = service.generateCycleInterpretation(request, "test-fingerprint");

        assertThat(result).isNotNull();
        assertThat(result.isFromCache()).isTrue();
        verify(grokAIService, never()).generateCycleInterpretation(any(), any(), any());
    }

    @Test
    @DisplayName("Should check xu balance for access control")
    void checkFullInterpretationAccess_WhenHasEnoughXu_ReturnsTrue() {
        Long userId = 1L;
        String chartHash = "test-hash";

        when(affiliateService.getInterpretationPriceXu()).thenReturn(10);
        when(xuService.hasEnoughXu(userId, 10)).thenReturn(true);

        boolean hasAccess = service.checkFullInterpretationAccess(userId, null, chartHash);

        assertThat(hasAccess).isTrue();
    }

    @Test
    @DisplayName("Should return false when not enough xu")
    void checkFullInterpretationAccess_WhenNotEnoughXu_ReturnsFalse() {
        Long userId = 1L;
        String chartHash = "test-hash";

        when(affiliateService.getInterpretationPriceXu()).thenReturn(10);
        when(xuService.hasEnoughXu(userId, 10)).thenReturn(false);

        boolean hasAccess = service.checkFullInterpretationAccess(userId, null, chartHash);

        assertThat(hasAccess).isFalse();
    }

    @Test
    @DisplayName("Should deduct xu when purchasing interpretation")
    void deductXuForFullInterpretation_WhenHasEnoughXu_DeductsAndReturnsTrue() {
        Long userId = 1L;
        String chartHash = "test-hash";

        when(affiliateService.getInterpretationPriceXu()).thenReturn(10);
        when(xuService.hasEnoughXu(userId, 10)).thenReturn(true);

        boolean result = service.deductXuForFullInterpretation(userId, chartHash);

        assertThat(result).isTrue();
        verify(xuService).deductXu(eq(userId), eq(10), eq(chartHash), anyString());
    }

    @Test
    @DisplayName("Should return interpretation price from affiliate service")
    void getFullInterpretationPriceXu_ReturnsConfiguredPrice() {
        when(affiliateService.getInterpretationPriceXu()).thenReturn(15);

        Integer price = service.getFullInterpretationPriceXu();

        assertThat(price).isEqualTo(15);
    }

    @Test
    @DisplayName("Should check Grok availability")
    void isGrokAvailable_DelegatesToGrokService() {
        when(grokAIService.isAvailable()).thenReturn(true);

        assertThat(service.isGrokAvailable()).isTrue();

        when(grokAIService.isAvailable()).thenReturn(false);

        assertThat(service.isGrokAvailable()).isFalse();
    }

    /**
     * Create a mock TuViChartRequest for testing.
     */
    private TuViChartRequest createMockRequest() {
        return TuViChartRequest.builder()
                .date("1995-03-02")
                .hour(8)
                .minute(30)
                .gender("male")
                .name("Test User")
                .isLunar(false)
                .isLeapMonth(false)
                .build();
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
                .lunarYearCanChi("Ất Hợi")
                .build();

        List<PalaceInfo> palaces = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            palaces.add(PalaceInfo.builder()
                    .index(i)
                    .name("Palace " + i)
                    .nameCode("PALACE_" + i)
                    .build());
        }

        CycleInfo cycles = CycleInfo.builder()
                .direction("THUAN")
                .directionText("Thuận")
                .daiVanStartAge(5)
                .cyclePeriod(10)
                .build();

        return TuViChartResponse.builder()
                .center(center)
                .palaces(palaces)
                .cycles(cycles)
                .chartHash("test-hash")
                .build();
    }
}

