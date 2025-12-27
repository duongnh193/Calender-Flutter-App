package com.duong.lichvanien.tuvi.service;

import com.duong.lichvanien.tuvi.dto.PalaceInfo;
import com.duong.lichvanien.tuvi.dto.StarInfo;
import com.duong.lichvanien.tuvi.dto.TuViChartResponse;
import com.duong.lichvanien.tuvi.dto.essay.PalaceEssay;
import com.duong.lichvanien.tuvi.entity.InterpretationFragmentEntity;
import com.duong.lichvanien.tuvi.util.PalaceAxisMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PalaceEssayCompositionService.
 * Tests the composition of 800-1000 word essays for each palace.
 * 
 * New 6-part structure:
 * 1. Core Nature (Bản chất sao)
 * 2. Axis Influence (Ảnh hưởng theo trục)
 * 3. Brightness Expression (Biểu hiện theo mức độ sáng)
 * 4. Key Strengths (Điểm mạnh)
 * 5. Natural Limitations (Hạn chế tự nhiên)
 * 6. Neutral Synthesis (Tổng hợp trung lập)
 */
@ExtendWith(MockitoExtension.class)
class PalaceEssayCompositionServiceTest {

    @InjectMocks
    private PalaceEssayCompositionService service;

    private PalaceInfo testPalace;
    private TuViChartResponse testChart;
    private List<InterpretationFragmentEntity> testFragments;

    @BeforeEach
    void setUp() {
        service = new PalaceEssayCompositionService();
        
        // Setup test palace
        testPalace = createTestPalace("MENH", "Mệnh", "TY");
        
        // Setup test chart
        testChart = TuViChartResponse.builder()
                .palaces(List.of(testPalace))
                .build();
        
        // Setup test fragments
        testFragments = createTestFragments();
    }

    @Nested
    @DisplayName("composeIntroduction Tests")
    class ComposeIntroductionTests {

        @Test
        @DisplayName("Should compose introduction for MENH palace")
        void shouldComposeIntroductionForMenhPalace() {
            String introduction = service.composeIntroduction(testPalace);
            
            assertNotNull(introduction);
            assertFalse(introduction.isBlank());
            assertTrue(introduction.contains("Mệnh"));
        }

        @Test
        @DisplayName("Should compose introduction for QUAN_LOC palace")
        void shouldComposeIntroductionForQuanLocPalace() {
            PalaceInfo quanLocPalace = createTestPalace("QUAN_LOC", "Quan Lộc", "MAO");
            
            String introduction = service.composeIntroduction(quanLocPalace);
            
            assertNotNull(introduction);
            assertFalse(introduction.isBlank());
            assertTrue(introduction.contains("Quan Lộc") || introduction.contains("sự nghiệp"));
        }

        @Test
        @DisplayName("Should handle unknown palace code gracefully")
        void shouldHandleUnknownPalaceCode() {
            PalaceInfo unknownPalace = createTestPalace("UNKNOWN", "Unknown", "TY");
            
            String introduction = service.composeIntroduction(unknownPalace);
            
            assertNotNull(introduction);
            assertFalse(introduction.isBlank());
        }
    }

    @Nested
    @DisplayName("composeCoreNature Tests")
    class ComposeCoreNatureTests {

        @Test
        @DisplayName("Should compose core nature for palace with single main star")
        void shouldComposeCoreNatureForSingleMainStar() {
            PalaceInfo palaceWithStar = createTestPalaceWithMainStar("TU_VI", "Tử Vi");
            
            String coreNature = service.composeCoreNature(palaceWithStar, testFragments);
            
            assertNotNull(coreNature);
            assertFalse(coreNature.isBlank());
            assertTrue(coreNature.contains("Tử Vi"));
        }

        @Test
        @DisplayName("Should compose core nature for palace with multiple main stars")
        void shouldComposeCoreNatureForMultipleMainStars() {
            PalaceInfo palaceWithStars = createTestPalaceWithMultipleMainStars();
            
            String coreNature = service.composeCoreNature(palaceWithStars, testFragments);
            
            assertNotNull(coreNature);
            assertFalse(coreNature.isBlank());
            assertTrue(coreNature.contains("đồng thời") || coreNature.contains("kết hợp"));
        }

        @Test
        @DisplayName("Should handle palace with no main star")
        void shouldHandlePalaceWithNoMainStar() {
            PalaceInfo palaceNoMainStar = createTestPalaceWithNoMainStar();
            
            String coreNature = service.composeCoreNature(palaceNoMainStar, testFragments);
            
            assertNotNull(coreNature);
            assertFalse(coreNature.isBlank());
            assertTrue(coreNature.contains("không có Chính tinh"));
        }
    }

    @Nested
    @DisplayName("composeBrightnessExpression Tests")
    class ComposeBrightnessExpressionTests {

        @Test
        @DisplayName("Should compose brightness expression for MIEU")
        void shouldComposeBrightnessExpressionForMieu() {
            PalaceInfo palace = createTestPalaceWithBrightness("MIEU");
            
            String expression = service.composeBrightnessExpression(palace, testFragments);
            
            assertNotNull(expression);
            assertTrue(expression.contains("miếu địa") || expression.contains("miếu"));
        }

        @Test
        @DisplayName("Should compose brightness expression for HAM")
        void shouldComposeBrightnessExpressionForHam() {
            PalaceInfo palace = createTestPalaceWithBrightness("HAM");
            
            String expression = service.composeBrightnessExpression(palace, testFragments);
            
            assertNotNull(expression);
            assertTrue(expression.contains("hãm") || expression.contains("giới hạn"));
        }

        @Test
        @DisplayName("Should handle palace with no main star")
        void shouldHandlePalaceWithNoMainStarBrightness() {
            PalaceInfo palaceNoMainStar = createTestPalaceWithNoMainStar();
            
            String expression = service.composeBrightnessExpression(palaceNoMainStar, testFragments);
            
            assertNotNull(expression);
            assertTrue(expression.contains("không có Chính tinh"));
        }
    }

    @Nested
    @DisplayName("composeKeyStrengths Tests")
    class ComposeKeyStrengthsTests {

        @Test
        @DisplayName("Should compose key strengths with positive fragments")
        void shouldComposeKeyStrengthsWithPositiveFragments() {
            PalaceInfo palaceWithStar = createTestPalaceWithMainStar("TU_VI", "Tử Vi");
            List<InterpretationFragmentEntity> positiveFragments = createPositiveFragments();
            
            String strengths = service.composeKeyStrengths(palaceWithStar, positiveFragments);
            
            assertNotNull(strengths);
            assertFalse(strengths.isBlank());
        }

        @Test
        @DisplayName("Should handle empty fragments")
        void shouldHandleEmptyFragments() {
            PalaceInfo palaceWithStar = createTestPalaceWithMainStar("TU_VI", "Tử Vi");
            
            String strengths = service.composeKeyStrengths(palaceWithStar, new ArrayList<>());
            
            assertNotNull(strengths);
            assertFalse(strengths.isBlank());
        }
    }

    @Nested
    @DisplayName("composeNaturalLimitations Tests")
    class ComposeNaturalLimitationsTests {

        @Test
        @DisplayName("Should compose natural limitations with Tuần")
        void shouldComposeNaturalLimitationsWithTuan() {
            PalaceInfo palaceWithTuan = createTestPalaceWithTuan();
            
            String limitations = service.composeNaturalLimitations(palaceWithTuan, testFragments);
            
            assertNotNull(limitations);
            assertTrue(limitations.contains("Tuần"));
        }

        @Test
        @DisplayName("Should compose natural limitations with Triệt")
        void shouldComposeNaturalLimitationsWithTriet() {
            PalaceInfo palaceWithTriet = createTestPalaceWithTriet();
            
            String limitations = service.composeNaturalLimitations(palaceWithTriet, testFragments);
            
            assertNotNull(limitations);
            assertTrue(limitations.contains("Triệt"));
        }

        @Test
        @DisplayName("Should not contain negative words")
        void shouldNotContainNegativeWords() {
            PalaceInfo palaceWithStar = createTestPalaceWithMainStar("TU_VI", "Tử Vi");
            
            String limitations = service.composeNaturalLimitations(palaceWithStar, testFragments);
            
            // Should use neutral framing, not negative words
            assertFalse(limitations.contains("xấu"));
            assertFalse(limitations.contains("tệ"));
        }
    }

    @Nested
    @DisplayName("composeNeutralSynthesis Tests")
    class ComposeNeutralSynthesisTests {

        @Test
        @DisplayName("Should compose neutral synthesis with trend description")
        void shouldComposeNeutralSynthesisWithTrend() {
            PalaceInfo palaceWithStar = createTestPalaceWithMainStar("TU_VI", "Tử Vi");
            
            String synthesis = service.composeNeutralSynthesis(palaceWithStar, testFragments);
            
            assertNotNull(synthesis);
            assertFalse(synthesis.isBlank());
            assertTrue(synthesis.contains("xu hướng") || synthesis.contains("tiềm năng"));
        }

        @Test
        @DisplayName("Should not contain forbidden phrases in synthesis")
        void shouldNotContainForbiddenPhrasesInSynthesis() {
            PalaceInfo palaceWithStar = createTestPalaceWithMainStar("TU_VI", "Tử Vi");
            
            String synthesis = service.composeNeutralSynthesis(palaceWithStar, testFragments);
            
            assertFalse(synthesis.contains("cuộc đời sẽ"));
            assertFalse(synthesis.contains("số phận"));
            assertFalse(synthesis.contains("định mệnh"));
        }
    }

    @Nested
    @DisplayName("composeFullEssay Tests")
    class ComposeFullEssayTests {

        @Test
        @DisplayName("Should compose full essay with all 6 sections plus introduction")
        void shouldComposeFullEssayWithAllSections() {
            PalaceInfo palaceWithStar = createTestPalaceWithMainStar("TU_VI", "Tử Vi");
            
            PalaceEssay essay = service.composeFullEssay(palaceWithStar, testFragments, testChart);
            
            assertNotNull(essay);
            assertNotNull(essay.getFullEssay());
            assertNotNull(essay.getSummary());
            assertNotNull(essay.getSections());
            assertEquals(7, essay.getSections().size()); // 6 main sections + introduction
            
            // Check all sections exist
            assertTrue(essay.getSections().containsKey("introduction"));
            assertTrue(essay.getSections().containsKey("coreNature"));
            assertTrue(essay.getSections().containsKey("axisInfluence"));
            assertTrue(essay.getSections().containsKey("brightnessExpression"));
            assertTrue(essay.getSections().containsKey("keyStrengths"));
            assertTrue(essay.getSections().containsKey("naturalLimitations"));
            assertTrue(essay.getSections().containsKey("neutralSynthesis"));
        }

        @Test
        @DisplayName("Should set palace code and name correctly")
        void shouldSetPalaceCodeAndNameCorrectly() {
            PalaceEssay essay = service.composeFullEssay(testPalace, testFragments, testChart);
            
            assertEquals("MENH", essay.getPalaceCode());
            assertEquals("Mệnh", essay.getPalaceName());
        }

        @Test
        @DisplayName("Should calculate word count")
        void shouldCalculateWordCount() {
            PalaceInfo palaceWithStar = createTestPalaceWithMainStar("TU_VI", "Tử Vi");
            
            PalaceEssay essay = service.composeFullEssay(palaceWithStar, testFragments, testChart);
            
            assertNotNull(essay.getWordCount());
            assertTrue(essay.getWordCount() > 0);
        }
    }

    @Nested
    @DisplayName("PalaceAxisMapper Tests")
    class PalaceAxisMapperTests {

        @Test
        @DisplayName("Should return correct axis for MENH")
        void shouldReturnCorrectAxisForMenh() {
            String axis = PalaceAxisMapper.getAxis("MENH");
            
            assertNotNull(axis);
            assertTrue(axis.contains("bản chất") || axis.contains("tính cách"));
        }

        @Test
        @DisplayName("Should return correct axis for QUAN_LOC")
        void shouldReturnCorrectAxisForQuanLoc() {
            String axis = PalaceAxisMapper.getAxis("QUAN_LOC");
            
            assertNotNull(axis);
            assertTrue(axis.contains("sự nghiệp") || axis.contains("công danh"));
        }

        @Test
        @DisplayName("Should return correct axis for TAI_BACH")
        void shouldReturnCorrectAxisForTaiBach() {
            String axis = PalaceAxisMapper.getAxis("TAI_BACH");
            
            assertNotNull(axis);
            assertTrue(axis.contains("tài chính") || axis.contains("tiền bạc"));
        }

        @Test
        @DisplayName("Should validate all palace codes")
        void shouldValidateAllPalaceCodes() {
            String[] palaceCodes = {
                "MENH", "PHU_MAU", "PHUC_DUC", "DIEN_TRACH", "QUAN_LOC", "NO_BOC",
                "THIEN_DI", "TAT_ACH", "TAI_BACH", "TU_TUC", "PHU_THE", "HUYNH_DE"
            };
            
            for (String code : palaceCodes) {
                assertTrue(PalaceAxisMapper.isValidPalaceCode(code), 
                        "Palace code should be valid: " + code);
                assertNotNull(PalaceAxisMapper.getPalaceName(code),
                        "Palace name should not be null: " + code);
            }
        }
    }

    @Nested
    @DisplayName("PalaceEssay DTO Tests")
    class PalaceEssayDtoTests {

        @Test
        @DisplayName("Should calculate word count correctly")
        void shouldCalculateWordCountCorrectly() {
            PalaceEssay essay = PalaceEssay.builder()
                    .fullEssay("Đây là một bài luận mẫu có nhiều từ để kiểm tra.")
                    .build();
            
            // Word count depends on split by whitespace
            assertTrue(essay.calculateWordCount() > 0);
        }

        @Test
        @DisplayName("Should check minimum length correctly")
        void shouldCheckMinimumLengthCorrectly() {
            PalaceEssay shortEssay = PalaceEssay.builder()
                    .fullEssay("Quá ngắn")
                    .build();
            
            assertFalse(shortEssay.hasMinimumLength());
        }

        @Test
        @DisplayName("Should handle null fullEssay")
        void shouldHandleNullFullEssay() {
            PalaceEssay essay = PalaceEssay.builder().build();
            
            assertEquals(0, essay.calculateWordCount());
            assertFalse(essay.hasMinimumLength());
        }
    }

    // ==================== HELPER METHODS ====================

    private PalaceInfo createTestPalace(String code, String name, String diaChi) {
        return PalaceInfo.builder()
                .nameCode(code)
                .name(name)
                .diaChi(diaChi)
                .stars(new ArrayList<>())
                .hasTuan(false)
                .hasTriet(false)
                .build();
    }

    private PalaceInfo createTestPalaceWithMainStar(String starCode, String starName) {
        StarInfo mainStar = StarInfo.builder()
                .code(starCode)
                .name(starName)
                .type("CHINH_TINH")
                .brightness("MIEU")
                .build();
        
        return PalaceInfo.builder()
                .nameCode("MENH")
                .name("Mệnh")
                .diaChi("TY")
                .stars(List.of(mainStar))
                .hasTuan(false)
                .hasTriet(false)
                .build();
    }

    private PalaceInfo createTestPalaceWithMultipleMainStars() {
        StarInfo star1 = StarInfo.builder()
                .code("TU_VI")
                .name("Tử Vi")
                .type("CHINH_TINH")
                .brightness("MIEU")
                .build();
        
        StarInfo star2 = StarInfo.builder()
                .code("THIEN_PHU")
                .name("Thiên Phủ")
                .type("CHINH_TINH")
                .brightness("VUONG")
                .build();
        
        return PalaceInfo.builder()
                .nameCode("MENH")
                .name("Mệnh")
                .diaChi("TY")
                .stars(List.of(star1, star2))
                .hasTuan(false)
                .hasTriet(false)
                .build();
    }

    private PalaceInfo createTestPalaceWithNoMainStar() {
        StarInfo auxStar = StarInfo.builder()
                .code("VAN_XUONG")
                .name("Văn Xương")
                .type("PHU_TINH")
                .brightness("BINH")
                .build();
        
        return PalaceInfo.builder()
                .nameCode("MENH")
                .name("Mệnh")
                .diaChi("TY")
                .stars(List.of(auxStar))
                .hasTuan(false)
                .hasTriet(false)
                .build();
    }

    private PalaceInfo createTestPalaceWithBrightness(String brightness) {
        StarInfo mainStar = StarInfo.builder()
                .code("TU_VI")
                .name("Tử Vi")
                .type("CHINH_TINH")
                .brightness(brightness)
                .build();
        
        return PalaceInfo.builder()
                .nameCode("MENH")
                .name("Mệnh")
                .diaChi("TY")
                .stars(List.of(mainStar))
                .hasTuan(false)
                .hasTriet(false)
                .build();
    }

    private PalaceInfo createTestPalaceWithTuan() {
        return PalaceInfo.builder()
                .nameCode("MENH")
                .name("Mệnh")
                .diaChi("TY")
                .stars(new ArrayList<>())
                .hasTuan(true)
                .hasTriet(false)
                .build();
    }

    private PalaceInfo createTestPalaceWithTriet() {
        return PalaceInfo.builder()
                .nameCode("MENH")
                .name("Mệnh")
                .diaChi("TY")
                .stars(new ArrayList<>())
                .hasTuan(false)
                .hasTriet(true)
                .build();
    }

    private List<InterpretationFragmentEntity> createTestFragments() {
        List<InterpretationFragmentEntity> fragments = new ArrayList<>();
        
        InterpretationFragmentEntity fragment1 = new InterpretationFragmentEntity();
        fragment1.setFragmentCode("TU_VI_MENH_MIEU");
        fragment1.setContent("Tử Vi miếu địa tại cung Mệnh thể hiện bản chất lãnh đạo.");
        fragment1.setTone(InterpretationFragmentEntity.Tone.positive);
        fragment1.setPriority(1);
        fragments.add(fragment1);
        
        InterpretationFragmentEntity fragment2 = new InterpretationFragmentEntity();
        fragment2.setFragmentCode("TU_VI_MENH_NEUTRAL");
        fragment2.setContent("Sao này có ảnh hưởng đến cách nhìn nhận bản thân.");
        fragment2.setTone(InterpretationFragmentEntity.Tone.neutral);
        fragment2.setPriority(2);
        fragments.add(fragment2);
        
        return fragments;
    }

    private List<InterpretationFragmentEntity> createMixedToneFragments() {
        List<InterpretationFragmentEntity> fragments = new ArrayList<>();
        
        InterpretationFragmentEntity positive = new InterpretationFragmentEntity();
        positive.setFragmentCode("POSITIVE_1");
        positive.setContent("Điểm mạnh là khả năng lãnh đạo tốt.");
        positive.setTone(InterpretationFragmentEntity.Tone.positive);
        positive.setPriority(1);
        fragments.add(positive);
        
        InterpretationFragmentEntity negative = new InterpretationFragmentEntity();
        negative.setFragmentCode("NEGATIVE_1");
        negative.setContent("Có xu hướng cứng nhắc trong một số tình huống.");
        negative.setTone(InterpretationFragmentEntity.Tone.negative);
        negative.setPriority(2);
        fragments.add(negative);
        
        InterpretationFragmentEntity neutral = new InterpretationFragmentEntity();
        neutral.setFragmentCode("NEUTRAL_1");
        neutral.setContent("Tính cách này phụ thuộc vào hoàn cảnh.");
        neutral.setTone(InterpretationFragmentEntity.Tone.neutral);
        neutral.setPriority(3);
        fragments.add(neutral);
        
        return fragments;
    }

    private List<InterpretationFragmentEntity> createPositiveFragments() {
        List<InterpretationFragmentEntity> fragments = new ArrayList<>();
        
        InterpretationFragmentEntity positive1 = new InterpretationFragmentEntity();
        positive1.setFragmentCode("POSITIVE_1");
        positive1.setContent("Điểm mạnh là khả năng lãnh đạo tốt.");
        positive1.setTone(InterpretationFragmentEntity.Tone.positive);
        positive1.setPriority(1);
        fragments.add(positive1);
        
        InterpretationFragmentEntity positive2 = new InterpretationFragmentEntity();
        positive2.setFragmentCode("POSITIVE_2");
        positive2.setContent("Có khả năng thích ứng cao.");
        positive2.setTone(InterpretationFragmentEntity.Tone.positive);
        positive2.setPriority(2);
        fragments.add(positive2);
        
        return fragments;
    }
}
