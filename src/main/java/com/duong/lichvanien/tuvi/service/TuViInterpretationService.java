package com.duong.lichvanien.tuvi.service;

import com.duong.lichvanien.tuvi.dto.*;
import com.duong.lichvanien.tuvi.dto.interpretation.*;
import com.duong.lichvanien.tuvi.enums.CungName;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for generating Tu Vi chart interpretations using AI.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TuViInterpretationService {

    private final TuViChartService chartService;
    private final TuViAIService aiService;
    private final TuViInterpretationCacheService cacheService;

    // Cached prompt templates
    private String systemPrompt;
    private String overviewPromptTemplate;
    private String menhPalacePromptTemplate;
    private String genericPalacePromptTemplate;

    // Palace meanings for context
    private static final Map<String, String> PALACE_MEANINGS = Map.ofEntries(
            Map.entry("MENH", "bản mệnh, tính cách, vận mệnh tổng quát"),
            Map.entry("PHU_MAU", "quan hệ với cha mẹ, ông bà, người bề trên"),
            Map.entry("PHUC_DUC", "phúc đức tổ tiên, nghiệp quả, đời sống tâm linh"),
            Map.entry("DIEN_TRACH", "nhà cửa, bất động sản, di sản thừa kế"),
            Map.entry("QUAN_LOC", "công danh, sự nghiệp, học vấn, thăng tiến"),
            Map.entry("NO_BOC", "cấp dưới, nhân viên, bạn bè hỗ trợ công việc"),
            Map.entry("THIEN_DI", "đi xa, xuất ngoại, hoạt động bên ngoài nhà"),
            Map.entry("TAT_ACH", "sức khỏe, bệnh tật, tai nạn"),
            Map.entry("TAI_BACH", "tiền tài, thu nhập, tài lộc, cách kiếm tiền"),
            Map.entry("TU_TUC", "con cái, hậu duệ, quan hệ với con"),
            Map.entry("PHU_THE", "vợ chồng, hôn nhân, tình duyên, đối tác"),
            Map.entry("HUYNH_DE", "anh chị em ruột, đồng nghiệp cùng cấp")
    );

    /**
     * Generate complete interpretation for a Tu Vi chart.
     * Checks cache first, then generates if not cached.
     */
    public TuViInterpretationResponse generateInterpretation(TuViChartRequest request) {
        log.info("Generating interpretation for: {}", request.getName());

        // Generate the chart first (deterministic, no cache needed)
        TuViChartResponse chart = chartService.generateChart(request);

        // Try to get from cache first
        Optional<TuViInterpretationResponse> cached = cacheService.getCachedInterpretation(
                chart, request.getGender()
        );

        if (cached.isPresent()) {
            log.info("Returning cached interpretation for: {}", request.getName());
            // Update name and metadata (may differ per request even with same chart)
            TuViInterpretationResponse cachedResponse = cached.get();
            return TuViInterpretationResponse.builder()
                    .name(request.getName())
                    .gender(cachedResponse.getGender())
                    .birthDate(cachedResponse.getBirthDate())
                    .birthHour(cachedResponse.getBirthHour())
                    .lunarYearCanChi(cachedResponse.getLunarYearCanChi())
                    .overview(cachedResponse.getOverview())
                    .menhInterpretation(cachedResponse.getMenhInterpretation())
                    .quanLocInterpretation(cachedResponse.getQuanLocInterpretation())
                    .taiBachInterpretation(cachedResponse.getTaiBachInterpretation())
                    .phuTheInterpretation(cachedResponse.getPhuTheInterpretation())
                    .tatAchInterpretation(cachedResponse.getTatAchInterpretation())
                    .tuTucInterpretation(cachedResponse.getTuTucInterpretation())
                    .dienTrachInterpretation(cachedResponse.getDienTrachInterpretation())
                    .phuMauInterpretation(cachedResponse.getPhuMauInterpretation())
                    .huynhDeInterpretation(cachedResponse.getHuynhDeInterpretation())
                    .phucDucInterpretation(cachedResponse.getPhucDucInterpretation())
                    .noBocInterpretation(cachedResponse.getNoBocInterpretation())
                    .thienDiInterpretation(cachedResponse.getThienDiInterpretation())
                    .generatedAt(cachedResponse.getGeneratedAt())
                    .aiModel(cachedResponse.getAiModel())
                    .build();
        }

        log.info("Cache miss - generating new interpretation with AI");

        // Load prompt templates if not cached
        loadPromptTemplates();

        // Build response
        TuViInterpretationResponse.TuViInterpretationResponseBuilder responseBuilder = 
            TuViInterpretationResponse.builder()
                .name(request.getName())
                .gender(request.getGender())
                .birthDate(request.getDate())
                .birthHour(request.getHour())
                .lunarYearCanChi(chart.getCenter().getLunarYearCanChi())
                .generatedAt(ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                .aiModel(aiService.getProviderName());

        // Generate Overview section
        OverviewSection overview = generateOverviewSection(chart, request.getGender());
        responseBuilder.overview(overview);

        // Generate interpretation for each palace
        for (PalaceInfo palace : chart.getPalaces()) {
            PalaceInterpretation interpretation = generatePalaceInterpretation(
                    palace, chart, request.getGender()
            );

            // Set to appropriate field based on palace code
            setPalaceInterpretation(responseBuilder, palace.getNameCode(), interpretation);
        }

        TuViInterpretationResponse response = responseBuilder.build();

        // Cache the complete interpretation
        cacheService.cacheInterpretation(chart, request.getGender(), response);

        return response;
    }

    /**
     * Generate Overview section interpretation.
     * Checks cache first, generates with AI if not cached.
     */
    private OverviewSection generateOverviewSection(TuViChartResponse chart, String gender) {
        // Try cache first
        Optional<OverviewSection> cached = cacheService.getCachedOverview(chart, gender);
        if (cached.isPresent()) {
            log.debug("Using cached overview section");
            return cached.get();
        }

        log.debug("Generating overview section with AI");
        CenterInfo center = chart.getCenter();

        // Build the prompt
        String prompt = overviewPromptTemplate
                .replace("{name}", nvl(center.getName(), "Thân chủ"))
                .replace("{gender}", "male".equals(gender) ? "Nam" : "Nữ")
                .replace("{solarDate}", nvl(center.getSolarDate(), ""))
                .replace("{lunarDate}", formatLunarDate(center))
                .replace("{birthHour}", String.valueOf(center.getBirthHour()))
                .replace("{birthHourCanChi}", nvl(center.getBirthHourCanChi(), ""))
                .replace("{yearCanChi}", nvl(center.getLunarYearCanChi(), ""))
                .replace("{amDuong}", nvl(center.getAmDuong(), ""))
                .replace("{thuanNghich}", nvl(center.getThuanNghich(), ""))
                .replace("{banMenh}", nvl(center.getBanMenh(), ""))
                .replace("{banMenhNguHanh}", nvl(center.getBanMenhNguHanh(), ""))
                .replace("{banMenhDescription}", nvl(center.getBanMenhDescription(), ""))
                .replace("{cuc}", nvl(center.getCuc(), ""))
                .replace("{cucValue}", String.valueOf(center.getCucValue()))
                .replace("{cucNguHanh}", nvl(center.getCucNguHanh(), ""))
                .replace("{menhCucRelation}", nvl(center.getMenhCucRelation(), ""))
                .replace("{chuMenh}", nvl(center.getChuMenh(), "không xác định"))
                .replace("{chuThan}", nvl(center.getChuThan(), "không xác định"))
                .replace("{thanCu}", nvl(center.getThanCu(), ""))
                .replace("{thanMenhDongCung}", isThanMenhDongCung(chart) ? "Có" : "Không");

        // Call AI to generate content
        String aiResponse = aiService.generateContent(systemPrompt, prompt);

        // Build OverviewSection from AI response
        OverviewSection overview = OverviewSection.builder()
                .introduction(extractSection(aiResponse, "giới thiệu", 0))
                .banMenhName(center.getBanMenh())
                .banMenhNguHanh(center.getBanMenhNguHanh())
                .banMenhInterpretation(extractSection(aiResponse, "Bản mệnh", 1))
                .cucName(center.getCuc())
                .cucValue(center.getCucValue())
                .menhCucRelation(center.getMenhCucRelation())
                .cucInterpretation(extractSection(aiResponse, "Cục mệnh", 2))
                .chuMenh(center.getChuMenh())
                .chuMenhInterpretation(extractSection(aiResponse, "Chủ mệnh", 3))
                .chuThan(center.getChuThan())
                .chuThanInterpretation(extractSection(aiResponse, "Chủ thân", 4))
                .thanCu(center.getThanCu())
                .laiNhanInterpretation(extractSection(aiResponse, "Lai nhân", 5))
                .thuanNghich(center.getThuanNghich())
                .thuanNghichInterpretation(extractSection(aiResponse, "Âm dương", 6))
                .thanMenhDongCung(isThanMenhDongCung(chart))
                .overallSummary(extractSection(aiResponse, "Tổng kết", 7))
                .build();

        // Cache the overview
        cacheService.cacheOverview(chart, gender, overview);

        return overview;
    }

    /**
     * Generate interpretation for a single palace.
     * Checks cache first, generates with AI if not cached.
     */
    private PalaceInterpretation generatePalaceInterpretation(
            PalaceInfo palace, TuViChartResponse chart, String gender) {

        // Try cache first
        Optional<PalaceInterpretation> cached = cacheService.getCachedPalace(
                chart, gender, palace.getNameCode()
        );
        if (cached.isPresent()) {
            log.debug("Using cached palace interpretation: {}", palace.getNameCode());
            return cached.get();
        }

        log.debug("Generating palace interpretation with AI: {}", palace.getNameCode());
        CenterInfo center = chart.getCenter();
        boolean isMenhPalace = "MENH".equals(palace.getNameCode());

        // Choose template
        String template = isMenhPalace ? menhPalacePromptTemplate : genericPalacePromptTemplate;

        // Categorize stars
        List<String> chinhTinh = new ArrayList<>();
        List<String> phuTinh = new ArrayList<>();
        List<String> bangTinh = new ArrayList<>();

        if (palace.getStars() != null) {
            for (StarInfo star : palace.getStars()) {
                String starEntry = star.getName();
                switch (star.getType()) {
                    case "CHINH_TINH" -> chinhTinh.add(starEntry);
                    case "PHU_TINH" -> phuTinh.add(starEntry);
                    default -> bangTinh.add(starEntry);
                }
            }
        }

        // Build Tuần/Triệt section
        String tuanTrietSection = "";
        if (palace.isHasTuan() || palace.isHasTriet()) {
            tuanTrietSection = "Cung này có " +
                    (palace.isHasTuan() ? "Tuần " : "") +
                    (palace.isHasTuan() && palace.isHasTriet() ? "và " : "") +
                    (palace.isHasTriet() ? "Triệt" : "") +
                    ". Hãy phân tích ảnh hưởng của chúng.";
        } else {
            tuanTrietSection = "Cung này không có Tuần hay Triệt, bỏ qua phần này.";
        }

        // Build prompt
        String prompt = template
                .replace("{gender}", "male".equals(gender) ? "Nam" : "Nữ")
                .replace("{banMenh}", nvl(center.getBanMenh(), ""))
                .replace("{banMenhNguHanh}", nvl(center.getBanMenhNguHanh(), ""))
                .replace("{cuc}", nvl(center.getCuc(), ""))
                .replace("{thuanNghich}", nvl(center.getThuanNghich(), ""))
                .replace("{palaceName}", palace.getName())
                .replace("{palaceIndex}", String.valueOf(palace.getIndex()))
                .replace("{palaceMeaning}", PALACE_MEANINGS.getOrDefault(palace.getNameCode(), ""))
                .replace("{palaceChi}", palace.getDiaChi())
                .replace("{canChiPrefix}", nvl(palace.getCanChiPrefix(), ""))
                .replace("{isThanCu}", palace.isThanCu() ? "Có" : "Không")
                .replace("{hasTuan}", palace.isHasTuan() ? "Có" : "Không")
                .replace("{hasTriet}", palace.isHasTriet() ? "Có" : "Không")
                .replace("{truongSinhStage}", nvl(palace.getTruongSinhStage(), ""))
                .replace("{chuMenh}", nvl(center.getChuMenh(), ""))
                .replace("{chinhTinhList}", chinhTinh.isEmpty() ? "Không có chính tinh" : String.join(", ", chinhTinh))
                .replace("{phuTinhList}", phuTinh.isEmpty() ? "Không có phụ tinh" : String.join(", ", phuTinh))
                .replace("{bangTinhList}", bangTinh.isEmpty() ? "Không có bàng tinh" : String.join(", ", bangTinh))
                .replace("{allStarsList}", getAllStarsString(palace))
                .replace("{tuanTrietSection}", tuanTrietSection);

        // Call AI
        String aiResponse = aiService.generateContent(systemPrompt, prompt);

        // Build star interpretations
        List<StarInterpretation> starInterpretations = new ArrayList<>();
        if (palace.getStars() != null) {
            for (StarInfo star : palace.getStars()) {
                starInterpretations.add(StarInterpretation.builder()
                        .starCode(star.getCode())
                        .starName(star.getName())
                        .starType(star.getType())
                        .brightness(star.getBrightness())
                        .interpretation(extractStarSection(aiResponse, star.getName()))
                        .build());
            }
        }

        PalaceInterpretation interpretation = PalaceInterpretation.builder()
                .palaceCode(palace.getNameCode())
                .palaceName(palace.getName())
                .palaceChi(palace.getDiaChi())
                .canChiPrefix(palace.getCanChiPrefix())
                .summary(extractSection(aiResponse, "tóm tắt", 0))
                .introduction(extractSection(aiResponse, "GIỚI THIỆU", 1))
                .detailedAnalysis(extractSection(aiResponse, "LUẬN CHÍNH TINH", 2))
                .genderAnalysis(isMenhPalace ? extractSection(aiResponse, "GIỚI TÍNH", 3) : null)
                .starAnalyses(starInterpretations)
                .hasTuan(palace.isHasTuan())
                .hasTriet(palace.isHasTriet())
                .tuanTrietEffect(palace.isHasTuan() || palace.isHasTriet() ? 
                        extractSection(aiResponse, "TUẦN/TRIỆT", -1) : null)
                .adviceSection(extractSection(aiResponse, "LỜI KHUYÊN", -2))
                .conclusion(extractSection(aiResponse, "KẾT LUẬN", -1))
                .build();

        // Cache the palace interpretation
        cacheService.cachePalace(chart, gender, palace.getNameCode(), interpretation);

        return interpretation;
    }

    /**
     * Load prompt templates from resources.
     */
    private void loadPromptTemplates() {
        if (systemPrompt == null) {
            systemPrompt = loadPromptFile("prompts/tuvi/system_prompt.txt");
        }
        if (overviewPromptTemplate == null) {
            overviewPromptTemplate = loadPromptFile("prompts/tuvi/overview_prompt.txt");
        }
        if (menhPalacePromptTemplate == null) {
            menhPalacePromptTemplate = loadPromptFile("prompts/tuvi/menh_palace_prompt.txt");
        }
        if (genericPalacePromptTemplate == null) {
            genericPalacePromptTemplate = loadPromptFile("prompts/tuvi/generic_palace_prompt.txt");
        }
    }

    private String loadPromptFile(String path) {
        try {
            ClassPathResource resource = new ClassPathResource(path);
            return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("Failed to load prompt template: {}", path, e);
            return "";
        }
    }

    // Helper methods
    private String nvl(String value, String defaultValue) {
        return value != null && !value.isBlank() ? value : defaultValue;
    }

    private String formatLunarDate(CenterInfo center) {
        return String.format("Ngày %d tháng %d%s năm %s",
                center.getLunarDay(),
                center.getLunarMonth(),
                center.isLeapMonth() ? " nhuận" : "",
                center.getLunarYearCanChi());
    }

    private boolean isThanMenhDongCung(TuViChartResponse chart) {
        if (chart.getPalaces() == null || chart.getPalaces().isEmpty()) return false;
        PalaceInfo menhPalace = chart.getPalaces().get(0); // Mệnh is always first
        return menhPalace.isThanCu();
    }

    private String getAllStarsString(PalaceInfo palace) {
        if (palace.getStars() == null || palace.getStars().isEmpty()) {
            return "Không có sao";
        }
        return palace.getStars().stream()
                .map(StarInfo::getName)
                .collect(Collectors.joining(", "));
    }

    private String extractSection(String text, String keyword, int sectionIndex) {
        if (text == null || text.isBlank()) {
            return "Đang cập nhật nội dung luận giải...";
        }
        // Simple extraction - in production, use more sophisticated parsing
        // For now, return the whole text split by paragraphs
        String[] paragraphs = text.split("\n\n");
        if (sectionIndex >= 0 && sectionIndex < paragraphs.length) {
            return paragraphs[sectionIndex].trim();
        }
        if (sectionIndex < 0) {
            int idx = paragraphs.length + sectionIndex;
            if (idx >= 0) {
                return paragraphs[idx].trim();
            }
        }
        return text.trim();
    }

    private String extractStarSection(String text, String starName) {
        if (text == null || text.isBlank() || starName == null) {
            return null;
        }
        // Look for section mentioning the star name
        String searchTerm = "có sao " + starName;
        int startIdx = text.toLowerCase().indexOf(searchTerm.toLowerCase());
        if (startIdx == -1) {
            searchTerm = starName;
            startIdx = text.indexOf(starName);
        }
        if (startIdx != -1) {
            int endIdx = text.indexOf("\n\n", startIdx);
            if (endIdx == -1) endIdx = Math.min(startIdx + 500, text.length());
            return text.substring(startIdx, endIdx).trim();
        }
        return null;
    }

    private void setPalaceInterpretation(
            TuViInterpretationResponse.TuViInterpretationResponseBuilder builder,
            String palaceCode,
            PalaceInterpretation interpretation) {

        switch (palaceCode) {
            case "MENH" -> builder.menhInterpretation(interpretation);
            case "PHU_MAU" -> builder.phuMauInterpretation(interpretation);
            case "PHUC_DUC" -> builder.phucDucInterpretation(interpretation);
            case "DIEN_TRACH" -> builder.dienTrachInterpretation(interpretation);
            case "QUAN_LOC" -> builder.quanLocInterpretation(interpretation);
            case "NO_BOC" -> builder.noBocInterpretation(interpretation);
            case "THIEN_DI" -> builder.thienDiInterpretation(interpretation);
            case "TAT_ACH" -> builder.tatAchInterpretation(interpretation);
            case "TAI_BACH" -> builder.taiBachInterpretation(interpretation);
            case "TU_TUC" -> builder.tuTucInterpretation(interpretation);
            case "PHU_THE" -> builder.phuTheInterpretation(interpretation);
            case "HUYNH_DE" -> builder.huynhDeInterpretation(interpretation);
        }
    }
}
