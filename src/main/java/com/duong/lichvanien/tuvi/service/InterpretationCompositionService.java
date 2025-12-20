package com.duong.lichvanien.tuvi.service;

import com.duong.lichvanien.tuvi.dto.CenterInfo;
import com.duong.lichvanien.tuvi.dto.PalaceInfo;
import com.duong.lichvanien.tuvi.dto.StarInfo;
import com.duong.lichvanien.tuvi.dto.TuViChartResponse;
import com.duong.lichvanien.tuvi.dto.interpretation.OverviewSection;
import com.duong.lichvanien.tuvi.dto.interpretation.PalaceInterpretation;
import com.duong.lichvanien.tuvi.dto.interpretation.StarInterpretation;
import com.duong.lichvanien.tuvi.dto.interpretation.TuViInterpretationResponse;
import com.duong.lichvanien.tuvi.entity.InterpretationFragmentEntity;
import com.duong.lichvanien.tuvi.enums.CungName;
import com.duong.lichvanien.tuvi.enums.Star;
import com.duong.lichvanien.tuvi.repository.InterpretationFragmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
    private final InterpretationFragmentRepository fragmentRepository;

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
     * Generate overview section from fragments.
     */
    private OverviewSection generateOverviewFromFragments(TuViChartResponse chart, String gender) {
        // For now, generate simple overview based on Mệnh palace
        PalaceInfo menhPalace = findPalace(chart, CungName.MENH);
        if (menhPalace == null) {
            return OverviewSection.builder()
                    .overallSummary("Chưa có thông tin giải luận.")
                    .build();
        }

        List<InterpretationFragmentEntity> fragments = ruleMatchingService.matchRulesForPalace(menhPalace);
        
        // Compose overview from fragments
        String overviewText = fragments.stream()
                .limit(3) // Take top 3 highest priority fragments
                .map(InterpretationFragmentEntity::getContent)
                .collect(Collectors.joining(" "));

        // Get chart center info for additional fields
        CenterInfo center = chart.getCenter();
        if (center == null) {
            return OverviewSection.builder()
                    .overallSummary(overviewText.isEmpty() ? "Chưa có thông tin giải luận." : overviewText)
                    .build();
        }

        // Check if Thân is in the same palace as Mệnh
        boolean thanMenhDongCung = false;
        if (chart.getPalaces() != null) {
            PalaceInfo thanPalace = chart.getPalaces().stream()
                    .filter(p -> p.isThanCu())
                    .findFirst()
                    .orElse(null);
            thanMenhDongCung = thanPalace != null && "MENH".equals(thanPalace.getNameCode());
        }

        // Match overview-level fragments from CenterInfo
        List<InterpretationFragmentEntity> overviewFragments = ruleMatchingService.matchOverviewRules(center, thanMenhDongCung);
        
        // Group fragments by type for different interpretation fields
        Map<String, List<InterpretationFragmentEntity>> fragmentsByType = overviewFragments.stream()
                .collect(Collectors.groupingBy(f -> {
                    String code = f.getFragmentCode();
                    if (code.startsWith("BAN_MENH_")) return "BAN_MENH";
                    if (code.startsWith("CUC_")) return "CUC";
                    if (code.startsWith("THUAN_") || code.startsWith("NGHICH_")) return "THUAN_NGHICH";
                    if (code.startsWith("THAN_")) return "THAN_CU";
                    return "OTHER";
                }));
        
        // Compose interpretation texts from fragments
        String banMenhInterpretation = fragmentsByType.getOrDefault("BAN_MENH", List.of()).stream()
                .map(InterpretationFragmentEntity::getContent)
                .collect(Collectors.joining(" "));
                
        String cucInterpretation = fragmentsByType.getOrDefault("CUC", List.of()).stream()
                .map(InterpretationFragmentEntity::getContent)
                .collect(Collectors.joining(" "));
                
        String thuanNghichInterpretation = fragmentsByType.getOrDefault("THUAN_NGHICH", List.of()).stream()
                .map(InterpretationFragmentEntity::getContent)
                .collect(Collectors.joining(" "));
                
        String laiNhanInterpretation = fragmentsByType.getOrDefault("THAN_CU", List.of()).stream()
                .map(InterpretationFragmentEntity::getContent)
                .collect(Collectors.joining(" "));
        
        // Generate Chủ Mệnh interpretation from star fragments in Mệnh palace
        String chuMenhInterpretation = generateChuMenhInterpretation(chart, center.getChuMenh());
        
        // If Cung Mệnh không có Chính tinh, thêm note đặc biệt vào interpretation
        if (Boolean.TRUE.equals(center.getMenhKhongChinhTinh()) && !chuMenhInterpretation.isEmpty()) {
            chuMenhInterpretation = "Lá số có đặc điểm: Cung Mệnh không có Chính tinh (Mệnh vô Chính tinh). " +
                    "Chủ mệnh được xác định từ cung đối diện hoặc cung Thân theo nguyên tắc Tử Vi học. " +
                    chuMenhInterpretation;
            log.info("Added special note for menhKhongChinhTinh case to chuMenhInterpretation");
        }
        
        // Generate Chủ Thân interpretation from star fragments in Thân palace
        String chuThanInterpretation = generateChuThanInterpretation(chart, center.getChuThan());
        
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
                .laiNhanInterpretation(laiNhanInterpretation.isEmpty() ? null : laiNhanInterpretation)
                .thanCuInterpretation(laiNhanInterpretation.isEmpty() ? null : laiNhanInterpretation)
                // Âm Dương / Thuận Nghịch
                .thuanNghich(center.getThuanNghich())
                .thuanNghichInterpretation(thuanNghichInterpretation.isEmpty() ? null : thuanNghichInterpretation)
                // Overall summary
                .overallSummary(overviewText.isEmpty() ? "Chưa có thông tin giải luận." : overviewText)
                .build();
    }

    /**
     * Generate palace interpretation from fragments.
     */
    private PalaceInterpretation generatePalaceInterpretation(TuViChartResponse chart, CungName palaceName) {
        PalaceInfo palace = findPalace(chart, palaceName);
        if (palace == null) {
            return null;
        }

        List<InterpretationFragmentEntity> fragments = ruleMatchingService.matchRulesForPalace(palace);

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

        // Build star analyses from fragments and palace stars
        List<StarInterpretation> starAnalyses = buildStarAnalyses(palace, fragments);

        // Check if palace has no Chính tinh (only has PHU_TINH or TRUONG_SINH)
        boolean hasChinhTinh = palace.getStars() != null && palace.getStars().stream()
                .anyMatch(s -> "CHINH_TINH".equals(s.getType()));
        boolean hasOnlyPhuTinhOrTruongSinh = !hasChinhTinh && palace.getStars() != null 
                && !palace.getStars().isEmpty()
                && palace.getStars().stream()
                    .allMatch(s -> "PHU_TINH".equals(s.getType()) || "TRUONG_SINH".equals(s.getType()));

        // Build interpretation sections with fallback logic
        // Priority: positive > neutral > negative > starAnalyses > fallback note for no Chính tinh
        String summary;
        if (!positiveFragments.isEmpty()) {
            summary = String.join(" ", positiveFragments.subList(0, Math.min(2, positiveFragments.size())));
        } else if (!neutralFragments.isEmpty()) {
            summary = String.join(" ", neutralFragments.subList(0, Math.min(2, neutralFragments.size())));
        } else if (!negativeFragments.isEmpty()) {
            summary = String.join(" ", negativeFragments.subList(0, Math.min(2, negativeFragments.size())));
        } else if (!starAnalyses.isEmpty()) {
            // Use first star's interpretation as summary
            summary = starAnalyses.get(0).getSummary();
        } else if (hasOnlyPhuTinhOrTruongSinh) {
            // Special case: Palace has no Chính tinh, only phụ tinh or Trường Sinh
            // These stars are modifiers and don't have dedicated interpretation fragments
            summary = "Cung này không có Chính tinh, chỉ có phụ tinh hoặc Trường Sinh. " +
                     "Các sao này có vai trò hỗ trợ và bổ trợ, không có giải luận riêng biệt theo từng sao. " +
                     "Việc giải luận chủ yếu dựa vào Chính tinh trong cung, do đó cung này cần xem xét trong tổng thể lá số.";
        } else {
            summary = "Chưa có thông tin giải luận.";
        }

        String detailedAnalysis = String.join(" ", 
                positiveFragments.size() > 2 ? positiveFragments.subList(2, positiveFragments.size()) : List.of());

        if (!neutralFragments.isEmpty()) {
            if (!positiveFragments.isEmpty()) {
                // Only add neutral if we already have positive
                detailedAnalysis += " " + String.join(" ", neutralFragments);
            } else {
                // If no positive, add neutral beyond first 2 (already used in summary)
                detailedAnalysis += " " + String.join(" ", 
                        neutralFragments.size() > 2 ? neutralFragments.subList(2, neutralFragments.size()) : List.of());
            }
        }

        if (!negativeFragments.isEmpty()) {
            if (!positiveFragments.isEmpty() || !neutralFragments.isEmpty()) {
                // Only add negative if we have positive or neutral
                detailedAnalysis += " " + String.join(" ", negativeFragments);
            } else {
                // If no positive/neutral, add negative beyond first 2 (already used in summary)
                detailedAnalysis += " " + String.join(" ", 
                        negativeFragments.size() > 2 ? negativeFragments.subList(2, negativeFragments.size()) : List.of());
            }
        }

        return PalaceInterpretation.builder()
                .palaceCode(palace.getNameCode())
                .palaceName(palace.getName())
                .palaceChi(palace.getDiaChi())
                .canChiPrefix(palace.getCanChiPrefix())
                .summary(summary)
                .introduction(null) // Can be generated separately
                .detailedAnalysis(detailedAnalysis.isEmpty() ? null : detailedAnalysis.trim())
                .genderAnalysis(null) // Can be added based on gender modifiers
                .starAnalyses(starAnalyses.isEmpty() ? null : starAnalyses)
                .hasTuan(palace.isHasTuan())
                .hasTriet(palace.isHasTriet())
                .tuanTrietEffect(generateTuanTrietEffect(fragments))
                .adviceSection(null) // Can be generated from fragments
                .conclusion(null) // Can be generated from fragments
                .build();
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
     * Generate Chủ Mệnh interpretation from dedicated CHU_MENH fragments.
     * Fragment code format: CHU_MENH_{STAR_CODE} (e.g., "CHU_MENH_THIEN_CO", "CHU_MENH_THAI_AM")
     */
    private String generateChuMenhInterpretation(TuViChartResponse chart, String chuMenh) {
        if (chuMenh == null || chuMenh.isBlank()) {
            return "";
        }
        
        // Convert Vietnamese star name to star code (e.g., "Thiên Cơ" -> "THIEN_CO")
        String starCode = convertStarNameToCode(chuMenh);
        if (starCode == null) {
            log.warn("Could not convert Chủ mệnh star name to code: {}", chuMenh);
            return "";
        }
        
        // Look up fragment with format: CHU_MENH_{STAR_CODE}
        String fragmentCode = "CHU_MENH_" + starCode;
        Optional<InterpretationFragmentEntity> fragment = fragmentRepository.findByFragmentCode(fragmentCode);
        
        if (fragment.isPresent()) {
            log.debug("Found CHU_MENH fragment for star: {} (code: {})", chuMenh, starCode);
            return fragment.get().getContent();
        }
        
        log.debug("No CHU_MENH fragment found for star: {} (code: {})", chuMenh, starCode);
        return "";
    }
    
    /**
     * Generate Chủ Thân interpretation from dedicated CHU_THAN fragments.
     * Fragment code format: {STAR_CODE}_CHU_THAN_{BRIGHTNESS} (e.g., "THAI_AM_CHU_THAN_MIEU", "THIEN_CO_CHU_THAN_BINH")
     * Falls back to old format CHU_THAN_{STAR_CODE} for backward compatibility.
     */
    private String generateChuThanInterpretation(TuViChartResponse chart, String chuThan) {
        if (chuThan == null || chuThan.isBlank()) {
            return "";
        }
        
        // Convert Vietnamese star name to star code (e.g., "Thái Âm" -> "THAI_AM")
        String starCode = convertStarNameToCode(chuThan);
        if (starCode == null) {
            log.warn("Could not convert Chủ thân star name to code: {}", chuThan);
            return "";
        }
        
        // Try to get brightness from Thân palace
        String brightness = "BINH"; // Default brightness
        PalaceInfo thanPalace = chart.getPalaces() != null ? chart.getPalaces().stream()
                .filter(p -> p.isThanCu())
                .findFirst()
                .orElse(null) : null;
        
        if (thanPalace != null && thanPalace.getStars() != null) {
            // Find the star matching chuThan and get its brightness
            for (StarInfo star : thanPalace.getStars()) {
                if (starCode.equals(star.getCode())) {
                    brightness = star.getBrightness() != null && !star.getBrightness().isBlank() 
                            ? star.getBrightness() 
                            : "BINH";
                    break;
                }
            }
        }
        
        // Try new format first: {STAR_CODE}_CHU_THAN_{BRIGHTNESS}
        String fragmentCode = starCode + "_CHU_THAN_" + brightness;
        Optional<InterpretationFragmentEntity> fragment = fragmentRepository.findByFragmentCode(fragmentCode);
        
        if (fragment.isPresent()) {
            log.debug("Found CHU_THAN fragment for star: {} (code: {}, brightness: {})", chuThan, starCode, brightness);
            return fragment.get().getContent();
        }
        
        // Fallback to old format: CHU_THAN_{STAR_CODE} for backward compatibility
        fragmentCode = "CHU_THAN_" + starCode;
        fragment = fragmentRepository.findByFragmentCode(fragmentCode);
        if (fragment.isPresent()) {
            log.debug("Found CHU_THAN fragment (old format) for star: {} (code: {})", chuThan, starCode);
            return fragment.get().getContent();
        }
        
        log.debug("No CHU_THAN fragment found for star: {} (code: {}, brightness: {})", chuThan, starCode, brightness);
        return "";
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
