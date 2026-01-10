package com.duong.lichvanien.tuvi.service;

import com.duong.lichvanien.tuvi.dto.CenterInfo;
import com.duong.lichvanien.tuvi.dto.PalaceInfo;
import com.duong.lichvanien.tuvi.dto.StarInfo;
import com.duong.lichvanien.tuvi.dto.TuViChartResponse;
import com.duong.lichvanien.tuvi.dto.essay.PalaceEssay;
import com.duong.lichvanien.tuvi.dto.interpretation.OverviewSection;
import com.duong.lichvanien.tuvi.dto.interpretation.PalaceInterpretation;
import com.duong.lichvanien.tuvi.dto.interpretation.StarInterpretation;
import com.duong.lichvanien.tuvi.dto.interpretation.TuViInterpretationResponse;
import com.duong.lichvanien.tuvi.entity.InterpretationFragmentEntity;
import com.duong.lichvanien.tuvi.enums.CungName;
import com.duong.lichvanien.tuvi.enums.Star;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for composing interpretations from matched fragments.
 * Converts fragments into structured interpretation DTOs.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InterpretationCompositionService {

    private final InterpretationRuleMatchingService ruleMatchingService;
    private final PalaceEssayCompositionService palaceEssayCompositionService;
    private final OverviewEssayCompositionService overviewEssayCompositionService;

    /**
     * Generate interpretation response from chart using rule-based fragments.
     *
     * @param chart The chart response
     * @param gender Gender for interpretation
     * @param name Name for display
     * @return Interpretation response
     */
    public TuViInterpretationResponse generateInterpretationFromFragments(
            TuViChartResponse chart,
            String gender,
            String name) {

        log.info("Generating interpretation from fragments for: {}", name);

        // Generate overview section
        OverviewSection overview = generateOverviewFromFragments(chart, gender);

        // Generate palace interpretations for all palaces
        PalaceInterpretation menhInterpretation = generatePalaceInterpretation(chart, CungName.MENH);
        PalaceInterpretation quanLocInterpretation = generatePalaceInterpretation(chart, CungName.QUAN_LOC);
        PalaceInterpretation taiBachInterpretation = generatePalaceInterpretation(chart, CungName.TAI_BACH);
        PalaceInterpretation phuTheInterpretation = generatePalaceInterpretation(chart, CungName.PHU_THE);
        PalaceInterpretation tatAchInterpretation = generatePalaceInterpretation(chart, CungName.TAT_ACH);
        PalaceInterpretation tuTucInterpretation = generatePalaceInterpretation(chart, CungName.TU_TUC);
        PalaceInterpretation dienTrachInterpretation = generatePalaceInterpretation(chart, CungName.DIEN_TRACH);
        PalaceInterpretation phuMauInterpretation = generatePalaceInterpretation(chart, CungName.PHU_MAU);
        PalaceInterpretation huynhDeInterpretation = generatePalaceInterpretation(chart, CungName.HUYNH_DE);
        PalaceInterpretation phucDucInterpretation = generatePalaceInterpretation(chart, CungName.PHUC_DUC);
        PalaceInterpretation noBocInterpretation = generatePalaceInterpretation(chart, CungName.NO_BOC);
        PalaceInterpretation thienDiInterpretation = generatePalaceInterpretation(chart, CungName.THIEN_DI);

        // Build response with all palace interpretations
        return TuViInterpretationResponse.builder()
                .name(name)
                .gender(gender)
                .birthDate(chart.getCenter().getSolarDate()) // From chart
                .birthHour(chart.getCenter().getBirthHour()) // From chart
                .lunarYearCanChi(chart.getCenter().getLunarYearCanChi()) // From chart
                .overview(overview)
                .menhInterpretation(menhInterpretation)
                .quanLocInterpretation(quanLocInterpretation)
                .taiBachInterpretation(taiBachInterpretation)
                .phuTheInterpretation(phuTheInterpretation)
                .tatAchInterpretation(tatAchInterpretation)
                .tuTucInterpretation(tuTucInterpretation)
                .dienTrachInterpretation(dienTrachInterpretation)
                .phuMauInterpretation(phuMauInterpretation)
                .huynhDeInterpretation(huynhDeInterpretation)
                .phucDucInterpretation(phucDucInterpretation)
                .noBocInterpretation(noBocInterpretation)
                .thienDiInterpretation(thienDiInterpretation)
                .generatedAt(java.time.LocalDateTime.now().toString())
                .aiModel("rule-based")
                .build();
    }

    /**
     * Generate overview section using OverviewEssayCompositionService.
     * Áp dụng quy tắc văn phong mới: học thuật nhưng dễ đọc, không tiêu cực, không lời khuyên.
     */
    private OverviewSection generateOverviewFromFragments(TuViChartResponse chart, String gender) {
        // Get chart center info for additional fields
        CenterInfo center = chart.getCenter();
        if (center == null) {
            return OverviewSection.builder()
                    .overallSummary("Lá số phản ánh một cấu trúc độc đáo với những tiềm năng riêng biệt.")
                    .build();
        }

        // Check if Thân is in the same palace as Mệnh
        boolean thanMenhDongCung = false;
        PalaceInfo thanPalace = null;
        if (chart.getPalaces() != null) {
            thanPalace = chart.getPalaces().stream()
                    .filter(PalaceInfo::isThanCu)
                    .findFirst()
                    .orElse(null);
            thanMenhDongCung = thanPalace != null && "MENH".equals(thanPalace.getNameCode());
        }

        // Get brightness of Chủ mệnh from Mệnh palace
        String chuMenhBrightness = getChuMenhBrightness(chart, center.getChuMenh());
        
        // Get brightness of Chủ thân from Thân palace
        String chuThanBrightness = getChuThanBrightness(thanPalace, center.getChuThan());

        // Generate interpretations using OverviewEssayCompositionService
        String chuMenhInterpretation = overviewEssayCompositionService.composeChuMenhInterpretation(
                center.getChuMenh(), 
                chuMenhBrightness, 
                Boolean.TRUE.equals(center.getMenhKhongChinhTinh()));
                
        String chuThanInterpretation = overviewEssayCompositionService.composeChuThanInterpretation(
                center.getChuThan(), 
                chuThanBrightness);
                
        String banMenhInterpretation = overviewEssayCompositionService.composeBanMenhInterpretation(
                center.getBanMenh(), 
                center.getBanMenhNguHanh());
                
        String cucInterpretation = overviewEssayCompositionService.composeCucInterpretation(
                center.getCuc(), 
                center.getCucValue(), 
                center.getMenhCucRelation());
        
        String thuanNghichInterpretation = overviewEssayCompositionService.composeThuanNghichInterpretation(
                center.getThuanNghich());
        
        String thanCuInterpretation = overviewEssayCompositionService.composeThanCuInterpretation(
                center.getThanCu(), 
                thanMenhDongCung);
        
        String overallSummary = overviewEssayCompositionService.composeOverallSummary(center, chart);
        
        return OverviewSection.builder()
                // Bản mệnh
                .banMenhName(center.getBanMenh())
                .banMenhNguHanh(center.getBanMenhNguHanh())
                .banMenhInterpretation(banMenhInterpretation.isEmpty() ? null : banMenhInterpretation)
                // Cục mệnh
                .cucName(center.getCuc())
                .cucValue(center.getCucValue())
                .menhCucRelation(center.getMenhCucRelation())
                .cucInterpretation(cucInterpretation.isEmpty() ? null : cucInterpretation)
                // Chủ mệnh
                .chuMenh(center.getChuMenh())
                .chuMenhInterpretation(chuMenhInterpretation.isEmpty() ? null : chuMenhInterpretation)
                // Chủ thân
                .chuThan(center.getChuThan())
                .chuThanInterpretation(chuThanInterpretation.isEmpty() ? null : chuThanInterpretation)
                // Lai nhân (Thân cư)
                .thanCu(center.getThanCu())
                .thanMenhDongCung(thanMenhDongCung)
                .laiNhanInterpretation(thanCuInterpretation.isEmpty() ? null : thanCuInterpretation)
                .thanCuInterpretation(thanCuInterpretation.isEmpty() ? null : thanCuInterpretation)
                // Âm Dương / Thuận Nghịch
                .thuanNghich(center.getThuanNghich())
                .thuanNghichInterpretation(thuanNghichInterpretation.isEmpty() ? null : thuanNghichInterpretation)
                // Overall summary
                .overallSummary(overallSummary)
                .build();
    }

    /**
     * Get brightness of Chủ mệnh from Mệnh palace.
     */
    private String getChuMenhBrightness(TuViChartResponse chart, String chuMenh) {
        if (chuMenh == null || chart.getPalaces() == null) {
            return null;
        }
        
        String starCode = convertStarNameToCode(chuMenh);
        if (starCode == null) {
            return null;
        }
        
        PalaceInfo menhPalace = findPalace(chart, CungName.MENH);
        if (menhPalace == null || menhPalace.getStars() == null) {
            return null;
        }
        
        return menhPalace.getStars().stream()
                .filter(s -> starCode.equals(s.getCode()))
                .map(StarInfo::getBrightness)
                .findFirst()
                .orElse(null);
    }

    /**
     * Get brightness of Chủ thân from Thân palace.
     */
    private String getChuThanBrightness(PalaceInfo thanPalace, String chuThan) {
        if (chuThan == null || thanPalace == null || thanPalace.getStars() == null) {
            return null;
        }
        
        String starCode = convertStarNameToCode(chuThan);
        if (starCode == null) {
            return null;
        }
        
        return thanPalace.getStars().stream()
                .filter(s -> starCode.equals(s.getCode()))
                .map(StarInfo::getBrightness)
                .findFirst()
                .orElse(null);
    }

    /**
     * Generate palace interpretation from fragments.
     * Uses PalaceEssayCompositionService to generate a complete essay (800-1000 words).
     */
    private PalaceInterpretation generatePalaceInterpretation(TuViChartResponse chart, CungName palaceName) {
        PalaceInfo palace = findPalace(chart, palaceName);
        if (palace == null) {
            return null;
        }

        List<InterpretationFragmentEntity> fragments = ruleMatchingService.matchRulesForPalace(palace);

        // Generate full essay using PalaceEssayCompositionService
        PalaceEssay essay = palaceEssayCompositionService.composeFullEssay(palace, fragments, chart);

        // Build star analyses from fragments and palace stars
        List<StarInterpretation> starAnalyses = buildStarAnalyses(palace, fragments);

        // Use essay summary as the short summary
        String summary = essay.getSummary();
        if (summary == null || summary.isBlank()) {
            // Fallback to old logic if essay summary is empty
            summary = buildFallbackSummary(palace, fragments, starAnalyses);
        }

        // Use essay fullEssay as detailedAnalysis
        String detailedAnalysis = essay.getFullEssay();

        // Extract sections for structured fields
        Map<String, String> sections = essay.getSections();
        String introduction = sections != null ? sections.get("introduction") : null;
        String conclusion = sections != null ? sections.get("conclusion") : null;

        return PalaceInterpretation.builder()
                .palaceCode(palace.getNameCode())
                .palaceName(palace.getName())
                .palaceChi(palace.getDiaChi())
                .canChiPrefix(palace.getCanChiPrefix())
                .summary(summary)
                .introduction(introduction)
                .detailedAnalysis(detailedAnalysis.isEmpty() ? null : detailedAnalysis.trim())
                .genderAnalysis(null) // Can be added based on gender modifiers
                .starAnalyses(starAnalyses.isEmpty() ? null : starAnalyses)
                .hasTuan(palace.isHasTuan())
                .hasTriet(palace.isHasTriet())
                .tuanTrietEffect(generateTuanTrietEffect(fragments))
                .adviceSection(null) // Removed - following no-advice principle
                .conclusion(conclusion)
                .build();
    }

    /**
     * Build fallback summary when essay summary is empty.
     */
    private String buildFallbackSummary(PalaceInfo palace, 
                                        List<InterpretationFragmentEntity> fragments,
                                        List<StarInterpretation> starAnalyses) {
        // Compose interpretation sections from fragments
        List<String> positiveFragments = new ArrayList<>();
        List<String> neutralFragments = new ArrayList<>();
        List<String> negativeFragments = new ArrayList<>();

        for (InterpretationFragmentEntity fragment : fragments) {
            switch (fragment.getTone()) {
                case positive -> positiveFragments.add(fragment.getContent());
                case neutral -> neutralFragments.add(fragment.getContent());
                case negative -> negativeFragments.add(fragment.getContent());
            }
        }

        // Check if palace has no Chính tinh (only has PHU_TINH or TRUONG_SINH)
        boolean hasChinhTinh = palace.getStars() != null && palace.getStars().stream()
                .anyMatch(s -> "CHINH_TINH".equals(s.getType()));
        boolean hasOnlyPhuTinhOrTruongSinh = !hasChinhTinh && palace.getStars() != null 
                && !palace.getStars().isEmpty()
                && palace.getStars().stream()
                    .allMatch(s -> "PHU_TINH".equals(s.getType()) || "TRUONG_SINH".equals(s.getType()));

        // Build interpretation sections with fallback logic
        String summary;
        if (!positiveFragments.isEmpty()) {
            summary = String.join(" ", positiveFragments.subList(0, Math.min(2, positiveFragments.size())));
        } else if (!neutralFragments.isEmpty()) {
            summary = String.join(" ", neutralFragments.subList(0, Math.min(2, neutralFragments.size())));
        } else if (!negativeFragments.isEmpty()) {
            summary = String.join(" ", negativeFragments.subList(0, Math.min(2, negativeFragments.size())));
        } else if (!starAnalyses.isEmpty()) {
            summary = starAnalyses.get(0).getSummary();
        } else if (hasOnlyPhuTinhOrTruongSinh) {
            summary = "Cung này không có Chính tinh, chỉ có phụ tinh hoặc Trường Sinh. " +
                     "Các sao này có vai trò hỗ trợ và bổ trợ, cần xem xét trong tổng thể lá số.";
        } else {
            summary = "Chưa có thông tin giải luận.";
        }

        return summary;
    }

    /**
     * Generate Tuần/Triệt effect text from fragments.
     */
    private String generateTuanTrietEffect(List<InterpretationFragmentEntity> fragments) {
        String effect = fragments.stream()
                .filter(f -> f.getFragmentCode().startsWith("TUAN_") || f.getFragmentCode().startsWith("TRIET_"))
                .map(InterpretationFragmentEntity::getContent)
                .collect(Collectors.joining(" "));
        return effect.isEmpty() ? null : effect;
    }

    /**
     * Build star analyses from palace stars and matched fragments.
     */
    private List<StarInterpretation> buildStarAnalyses(PalaceInfo palace, List<InterpretationFragmentEntity> fragments) {
        if (palace.getStars() == null || palace.getStars().isEmpty()) {
            return new ArrayList<>();
        }

        // Group fragments by star code (if fragment code contains star code)
        Map<String, List<InterpretationFragmentEntity>> fragmentsByStar = fragments.stream()
                .filter(f -> {
                    // Only include fragments for specific stars (not combinations or modifiers)
                    String code = f.getFragmentCode();
                    return palace.getStars().stream()
                            .anyMatch(star -> code.contains(star.getCode()));
                })
                .collect(Collectors.groupingBy(f -> {
                    // Extract star code from fragment code (e.g., "TU_VI_MENH_MIEU" -> "TU_VI")
                    for (StarInfo star : palace.getStars()) {
                        if (f.getFragmentCode().contains(star.getCode())) {
                            return star.getCode();
                        }
                    }
                    return "UNKNOWN";
                }));

        List<StarInterpretation> starAnalyses = new ArrayList<>();
        for (StarInfo star : palace.getStars()) {
            List<InterpretationFragmentEntity> starFragments = fragmentsByStar.getOrDefault(star.getCode(), new ArrayList<>());
            if (!starFragments.isEmpty()) {
                String interpretation = starFragments.stream()
                        .map(InterpretationFragmentEntity::getContent)
                        .collect(Collectors.joining(" "));
                
                starAnalyses.add(StarInterpretation.builder()
                        .starCode(star.getCode())
                        .starName(star.getName())
                        .starType(star.getType())
                        .brightness(star.getBrightness())
                        .interpretation(interpretation)
                        .summary(starFragments.get(0).getContent()) // Use first fragment as summary
                        .build());
            }
        }

        return starAnalyses;
    }
    
    /**
     * Convert Vietnamese star name to star code (enum name).
     * Example: "Thiên Cơ" -> "THIEN_CO", "Thái Âm" -> "THAI_AM"
     */
    private String convertStarNameToCode(String starName) {
        if (starName == null || starName.isBlank()) {
            return null;
        }
        
        // Try to find matching Star enum by Vietnamese name
        for (Star star : Star.values()) {
            if (star.getText().equals(starName)) {
                return star.name();
            }
        }
        
        log.warn("Could not find star code for name: {}", starName);
        return null;
    }
    
    /**
     * Find palace by name in chart.
     */
    private PalaceInfo findPalace(TuViChartResponse chart, CungName palaceName) {
        if (chart.getPalaces() == null) {
            return null;
        }
        return chart.getPalaces().stream()
                .filter(p -> palaceName.name().equals(p.getNameCode()))
                .findFirst()
                .orElse(null);
    }
}
