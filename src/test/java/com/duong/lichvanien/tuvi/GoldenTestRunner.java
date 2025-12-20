package com.duong.lichvanien.tuvi;

import com.duong.lichvanien.tuvi.dto.TuViChartRequest;
import com.duong.lichvanien.tuvi.dto.TuViChartResponse;
import com.duong.lichvanien.tuvi.service.NatalChartService;
import com.duong.lichvanien.tuvi.service.TuViChartService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mockito.Mockito;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * Golden test runner for Tu Vi chart calculations.
 * Loads fixture files and compares calculated results against expected values.
 */
@DisplayName("Tu Vi Golden Tests")
class GoldenTestRunner {

    private TuViChartService service;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // Create mock NatalChartService for testing
        NatalChartService natalChartService = Mockito.mock(NatalChartService.class);
        service = new TuViChartService(natalChartService);
        objectMapper = new ObjectMapper();
    }

    @TestFactory
    @DisplayName("Run all golden test fixtures")
    Collection<DynamicTest> runGoldenTests() throws IOException {
        List<DynamicTest> tests = new ArrayList<>();

        // Load all fixture files
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath:fixtures/tuvi/*.json");

        for (Resource resource : resources) {
            String filename = resource.getFilename();
            tests.add(dynamicTest("Golden test: " + filename, () -> runSingleTest(resource)));
        }

        return tests;
    }

    private void runSingleTest(Resource resource) throws IOException {
        JsonNode fixture = objectMapper.readTree(resource.getInputStream());
        // String description = fixture.path("description").asText("No description"); // Not used

        // Parse input
        JsonNode inputNode = fixture.path("input");
        TuViChartRequest request = TuViChartRequest.builder()
                .date(inputNode.path("date").asText())
                .hour(inputNode.path("hour").asInt())
                .minute(inputNode.path("minute").asInt(0))
                .gender(inputNode.path("gender").asText())
                .isLunar(inputNode.path("isLunar").asBoolean(false))
                .isLeapMonth(inputNode.path("isLeapMonth").asBoolean(false))
                .name(inputNode.path("name").asText(null))
                .build();

        // Generate chart
        TuViChartResponse response = service.generateChart(request);

        // Validate against expected values
        JsonNode expected = fixture.path("expected");
        
        // Validate center info
        JsonNode expectedCenter = expected.path("center");
        if (!expectedCenter.isMissingNode()) {
            validateCenter(response, expectedCenter);
        }

        // Validate Mệnh palace position
        if (expected.has("menhPalaceChi")) {
            String expectedMenh = expected.path("menhPalaceChi").asText();
            assertEquals(expectedMenh, response.getPalaces().get(0).getDiaChiCode(),
                    "Mệnh palace DiaChi mismatch");
        }

        // Validate direction
        if (expected.has("isThuan")) {
            boolean expectedThuan = expected.path("isThuan").asBoolean();
            assertEquals(expectedThuan, "THUAN".equals(response.getCycles().getDirection()),
                    "Direction (thuận/nghịch) mismatch");
        }

        // Validate star positions
        JsonNode expectedStars = expected.path("starPositions");
        if (!expectedStars.isMissingNode()) {
            validateStarPositions(response, expectedStars);
        }

        // Validate markers
        JsonNode expectedMarkers = expected.path("markers");
        if (!expectedMarkers.isMissingNode()) {
            validateMarkers(response, expectedMarkers);
        }
    }

    private void validateCenter(TuViChartResponse response, JsonNode expected) {
        var center = response.getCenter();

        if (expected.has("lunarYearCanChi")) {
            assertEquals(expected.path("lunarYearCanChi").asText(), center.getLunarYearCanChi(),
                    "Lunar year Can-Chi mismatch");
        }

        if (expected.has("lunarYear")) {
            assertEquals(expected.path("lunarYear").asInt(), center.getLunarYear(),
                    "Lunar year mismatch");
        }

        if (expected.has("lunarMonth")) {
            assertEquals(expected.path("lunarMonth").asInt(), center.getLunarMonth(),
                    "Lunar month mismatch");
        }

        if (expected.has("lunarDay")) {
            assertEquals(expected.path("lunarDay").asInt(), center.getLunarDay(),
                    "Lunar day mismatch");
        }

        if (expected.has("lunarDayCanChi")) {
            assertEquals(expected.path("lunarDayCanChi").asText(), center.getLunarDayCanChi(),
                    "Lunar day Can-Chi mismatch");
        }

        if (expected.has("birthHourCanChi")) {
            assertEquals(expected.path("birthHourCanChi").asText(), center.getBirthHourCanChi(),
                    "Birth hour Can-Chi mismatch");
        }

        if (expected.has("banMenh")) {
            assertEquals(expected.path("banMenh").asText(), center.getBanMenh(),
                    "Bản mệnh (Nạp Âm) mismatch");
        }

        if (expected.has("banMenhNguHanh")) {
            assertEquals(expected.path("banMenhNguHanh").asText(), center.getBanMenhNguHanh(),
                    "Bản mệnh Ngũ Hành mismatch");
        }

        if (expected.has("cuc")) {
            assertEquals(expected.path("cuc").asText(), center.getCuc(),
                    "Cục mismatch");
        }

        if (expected.has("cucValue")) {
            assertEquals(expected.path("cucValue").asInt(), center.getCucValue(),
                    "Cục value mismatch");
        }
    }

    private void validateStarPositions(TuViChartResponse response, JsonNode expected) {
        var fields = expected.fields();
        while (fields.hasNext()) {
            var entry = fields.next();
            String starName = entry.getKey();
            String expectedChi = entry.getValue().asText();

            // Find star in response
            boolean found = false;
            for (var palace : response.getPalaces()) {
                if (palace.getDiaChiCode().equals(expectedChi)) {
                    if (palace.getStars() != null) {
                        for (var star : palace.getStars()) {
                            if (star.getCode().equals(starName)) {
                                found = true;
                                break;
                            }
                        }
                    }
                }
                if (found) break;
            }

            assertTrue(found, String.format("Star %s should be at %s", starName, expectedChi));
        }
    }

    private void validateMarkers(TuViChartResponse response, JsonNode expected) {
        var markers = response.getMarkers();

        if (expected.has("tuanStart")) {
            assertEquals(expected.path("tuanStart").asText(), markers.getTuanStart(),
                    "Tuần start mismatch");
        }

        if (expected.has("tuanEnd")) {
            assertEquals(expected.path("tuanEnd").asText(), markers.getTuanEnd(),
                    "Tuần end mismatch");
        }

        if (expected.has("trietStart")) {
            assertEquals(expected.path("trietStart").asText(), markers.getTrietStart(),
                    "Triệt start mismatch");
        }

        if (expected.has("trietEnd")) {
            assertEquals(expected.path("trietEnd").asText(), markers.getTrietEnd(),
                    "Triệt end mismatch");
        }
    }
}
