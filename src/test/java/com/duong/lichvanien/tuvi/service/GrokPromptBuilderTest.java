package com.duong.lichvanien.tuvi.service;

import com.duong.lichvanien.tuvi.dto.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for GrokPromptBuilder.
 */
class GrokPromptBuilderTest {

    @Test
    @DisplayName("Should build full system prompt")
    void buildFullSystemPrompt_ReturnsNonEmptyPrompt() {
        String prompt = GrokPromptBuilder.buildFullSystemPrompt();

        assertThat(prompt).isNotBlank();
        assertThat(prompt).contains("Tử Vi");
        assertThat(prompt).contains("chuyên gia");
    }

    @Test
    @DisplayName("Should build cycle system prompt")
    void buildCycleSystemPrompt_ReturnsNonEmptyPrompt() {
        String prompt = GrokPromptBuilder.buildCycleSystemPrompt();

        assertThat(prompt).isNotBlank();
        assertThat(prompt).contains("Đại vận");
        assertThat(prompt).contains("Tiểu vận");
    }

    @Test
    @DisplayName("Should build full interpretation prompt with chart data")
    void buildFullInterpretationPrompt_IncludesChartData() {
        TuViChartResponse chart = createMockChart();

        String prompt = GrokPromptBuilder.buildFullInterpretationPrompt(chart, "Test User", "male");

        assertThat(prompt).isNotBlank();
        assertThat(prompt).contains("Test User");
        assertThat(prompt).contains("Nam");
        assertThat(prompt).contains("Ất Hợi");
        assertThat(prompt).contains("Sơn Đầu Hỏa");
        assertThat(prompt).contains("JSON");
    }

    @Test
    @DisplayName("Should build cycle interpretation prompt with cycle data")
    void buildCycleInterpretationPrompt_IncludesCycleData() {
        TuViChartResponse chart = createMockChart();

        String prompt = GrokPromptBuilder.buildCycleInterpretationPrompt(chart, "Test User", "female");

        assertThat(prompt).isNotBlank();
        assertThat(prompt).contains("Test User");
        assertThat(prompt).contains("Nữ");
        assertThat(prompt).contains("Đại vận");
        assertThat(prompt).contains("Thuận");
        assertThat(prompt).contains("JSON");
    }

    @Test
    @DisplayName("Should extract JSON from plain response")
    void extractJsonFromResponse_PlainJson_ReturnsJson() {
        String response = "{\"key\": \"value\"}";

        String json = GrokPromptBuilder.extractJsonFromResponse(response);

        assertThat(json).isEqualTo("{\"key\": \"value\"}");
    }

    @Test
    @DisplayName("Should extract JSON from markdown code block")
    void extractJsonFromResponse_MarkdownCodeBlock_ReturnsJson() {
        String response = "```json\n{\"key\": \"value\"}\n```";

        String json = GrokPromptBuilder.extractJsonFromResponse(response);

        assertThat(json).isEqualTo("{\"key\": \"value\"}");
    }

    @Test
    @DisplayName("Should extract JSON from plain code block")
    void extractJsonFromResponse_PlainCodeBlock_ReturnsJson() {
        String response = "```\n{\"key\": \"value\"}\n```";

        String json = GrokPromptBuilder.extractJsonFromResponse(response);

        assertThat(json).isEqualTo("{\"key\": \"value\"}");
    }

    @Test
    @DisplayName("Should return null for null input")
    void extractJsonFromResponse_NullInput_ReturnsNull() {
        String json = GrokPromptBuilder.extractJsonFromResponse(null);

        assertThat(json).isNull();
    }

    @Test
    @DisplayName("Should return null for blank input")
    void extractJsonFromResponse_BlankInput_ReturnsNull() {
        String json = GrokPromptBuilder.extractJsonFromResponse("   ");

        assertThat(json).isNull();
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
        String[] palaceNames = {"Mệnh", "Phụ Mẫu", "Phúc Đức", "Điền Trạch", "Quan Lộc", "Nô Bộc",
                "Thiên Di", "Tật Ách", "Tài Bạch", "Tử Tức", "Phu Thê", "Huynh Đệ"};
        String[] palaceCodes = {"MENH", "PHU_MAU", "PHUC_DUC", "DIEN_TRACH", "QUAN_LOC", "NO_BOC",
                "THIEN_DI", "TAT_ACH", "TAI_BACH", "TU_TUC", "PHU_THE", "HUYNH_DE"};
        
        for (int i = 0; i < 12; i++) {
            PalaceInfo palace = PalaceInfo.builder()
                    .index(i)
                    .name(palaceNames[i])
                    .nameCode(palaceCodes[i])
                    .diaChiCode("TY")
                    .canChiPrefix("G.Tý")
                    .truongSinhStage("Trường Sinh")
                    .daiVanStartAge(5 + i * 10)
                    .daiVanLabel(String.valueOf(5 + i * 10))
                    .hasTuan(i == 0)
                    .hasTriet(i == 6)
                    .isThanCu(i == 4)
                    .stars(new ArrayList<>())
                    .build();
            palaces.add(palace);
        }

        List<CycleInfo.DaiVanEntry> daiVanList = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            daiVanList.add(CycleInfo.DaiVanEntry.builder()
                    .palaceIndex(i)
                    .palaceName(palaceNames[i])
                    .startAge(5 + i * 10)
                    .endAge(14 + i * 10)
                    .label(String.valueOf(5 + i * 10))
                    .build());
        }

        CycleInfo cycles = CycleInfo.builder()
                .direction("THUAN")
                .directionText("Thuận")
                .daiVanStartAge(5)
                .cyclePeriod(10)
                .daiVanList(daiVanList)
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

