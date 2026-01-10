package com.duong.lichvanien.tuvi.service;

import com.duong.lichvanien.tuvi.config.GrokProperties;
import com.duong.lichvanien.tuvi.dto.TuViChartResponse;
import com.duong.lichvanien.tuvi.dto.interpretation.CycleInterpretationResponse;
import com.duong.lichvanien.tuvi.dto.interpretation.DaiVanInterpretation;
import com.duong.lichvanien.tuvi.dto.interpretation.OverviewSection;
import com.duong.lichvanien.tuvi.dto.interpretation.PalaceInterpretation;
import com.duong.lichvanien.tuvi.dto.interpretation.TuViInterpretationResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Service for interacting with xAI's Grok API to generate Tu Vi interpretations.
 * Handles both full interpretations and cycle (Đại hạn/Tiểu vận) interpretations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GrokAIService {

    private final GrokProperties grokProperties;
    private final ObjectMapper objectMapper;

    /**
     * Generate full Tu Vi interpretation using Grok API.
     *
     * @param chart  The Tu Vi chart data
     * @param name   Chart owner's name
     * @param gender Chart owner's gender
     * @return TuViInterpretationResponse or null if error
     */
    public TuViInterpretationResponse generateFullInterpretation(
            TuViChartResponse chart, String name, String gender) {
        
        if (!grokProperties.isAvailable()) {
            log.warn("Grok API key not configured, cannot generate full interpretation");
            return null;
        }

        String systemPrompt = GrokPromptBuilder.buildFullSystemPrompt();
        String userPrompt = GrokPromptBuilder.buildFullInterpretationPrompt(chart, name, gender);

        try {
            String response = callGrokApi(systemPrompt, userPrompt);
            if (response == null || response.isBlank()) {
                log.error("Empty response from Grok API for full interpretation");
                return null;
            }

            return parseFullInterpretationResponse(response, chart, name, gender);

        } catch (Exception e) {
            log.error("Error generating full interpretation with Grok: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Generate cycle (Đại hạn/Tiểu vận) interpretation using Grok API.
     *
     * @param chart  The Tu Vi chart data
     * @param name   Chart owner's name
     * @param gender Chart owner's gender
     * @return CycleInterpretationResponse or null if error
     */
    public CycleInterpretationResponse generateCycleInterpretation(
            TuViChartResponse chart, String name, String gender) {
        
        if (!grokProperties.isAvailable()) {
            log.warn("Grok API key not configured, cannot generate cycle interpretation");
            return null;
        }

        String systemPrompt = GrokPromptBuilder.buildCycleSystemPrompt();
        String userPrompt = GrokPromptBuilder.buildCycleInterpretationPrompt(chart, name, gender);

        try {
            String response = callGrokApi(systemPrompt, userPrompt);
            if (response == null || response.isBlank()) {
                log.error("Empty response from Grok API for cycle interpretation");
                return null;
            }

            return parseCycleInterpretationResponse(response, chart, name, gender);

        } catch (Exception e) {
            log.error("Error generating cycle interpretation with Grok: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Call Grok API with given prompts.
     */
    private String callGrokApi(String systemPrompt, String userPrompt) {
        try {
            WebClient client = WebClient.builder()
                    .baseUrl(grokProperties.getBaseUrl())
                    .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + grokProperties.getKey())
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .build();

            Map<String, Object> requestBody = Map.of(
                    "model", grokProperties.getModel(),
                    "messages", List.of(
                            Map.of("role", "system", "content", systemPrompt),
                            Map.of("role", "user", "content", userPrompt)
                    ),
                    "max_tokens", grokProperties.getMaxTokens(),
                    "temperature", grokProperties.getTemperature()
            );

            log.debug("Calling Grok API: model={}, max_tokens={}", 
                    grokProperties.getModel(), grokProperties.getMaxTokens());

            String response = client.post()
                    .uri("/chat/completions")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(grokProperties.getTimeoutSeconds()))
                    .block();

            if (response != null && !response.isBlank()) {
                JsonNode root = objectMapper.readTree(response);
                JsonNode choices = root.path("choices");
                if (choices.isArray() && !choices.isEmpty()) {
                    String content = choices.get(0).path("message").path("content").asText();
                    log.debug("Grok API response received, content length: {}", content.length());
                    return content;
                }
            }

            log.error("No content in Grok API response");
            return null;

        } catch (WebClientResponseException e) {
            log.error("Grok API error: {} {} - Response body: {}",
                    e.getStatusCode(),
                    e.getStatusText(),
                    e.getResponseBodyAsString());
            return null;
        } catch (Exception e) {
            log.error("Error calling Grok API: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Parse full interpretation response from Grok.
     */
    private TuViInterpretationResponse parseFullInterpretationResponse(
            String response, TuViChartResponse chart, String name, String gender) {
        
        try {
            String json = GrokPromptBuilder.extractJsonFromResponse(response);
            JsonNode root = objectMapper.readTree(json);

            // Parse overview section
            JsonNode overviewNode = root.path("overview");
            OverviewSection overview = OverviewSection.builder()
                    .introduction(overviewNode.path("introduction").asText(null))
                    .banMenhName(chart.getCenter().getBanMenh())
                    .banMenhNguHanh(chart.getCenter().getBanMenhNguHanh())
                    .banMenhInterpretation(overviewNode.path("banMenhInterpretation").asText(null))
                    .cucName(chart.getCenter().getCuc())
                    .cucValue(chart.getCenter().getCucValue())
                    .menhCucRelation(chart.getCenter().getMenhCucRelation())
                    .cucInterpretation(overviewNode.path("cucInterpretation").asText(null))
                    .chuMenh(chart.getCenter().getChuMenh())
                    .chuMenhInterpretation(overviewNode.path("chuMenhInterpretation").asText(null))
                    .chuThan(chart.getCenter().getChuThan())
                    .chuThanInterpretation(overviewNode.path("chuThanInterpretation").asText(null))
                    .thanCu(chart.getCenter().getThanCu())
                    .thanCuInterpretation(overviewNode.path("thanCuInterpretation").asText(null))
                    .thuanNghich(chart.getCenter().getThuanNghich())
                    .thuanNghichInterpretation(overviewNode.path("thuanNghichInterpretation").asText(null))
                    .overallSummary(overviewNode.path("overallSummary").asText(null))
                    .build();

            // Parse palace interpretations
            TuViInterpretationResponse.TuViInterpretationResponseBuilder responseBuilder = 
                    TuViInterpretationResponse.builder()
                    .name(name)
                    .gender(gender)
                    .birthDate(chart.getCenter().getSolarDate())
                    .birthHour(chart.getCenter().getBirthHour())
                    .lunarYearCanChi(chart.getCenter().getLunarYearCanChi())
                    .overview(overview)
                    .generatedAt(ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                    .aiModel(grokProperties.getModel());

            // Parse each palace
            JsonNode palacesNode = root.path("palaces");
            if (palacesNode.isArray()) {
                for (JsonNode palaceNode : palacesNode) {
                    String palaceCode = palaceNode.path("palaceCode").asText();
                    PalaceInterpretation palaceInterp = parsePalaceInterpretation(palaceNode, chart, palaceCode);
                    
                    // Set to appropriate field based on palace code
                    setPalaceInterpretation(responseBuilder, palaceCode, palaceInterp);
                }
            }

            return responseBuilder.build();

        } catch (Exception e) {
            log.error("Error parsing full interpretation response: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Parse a single palace interpretation from JSON.
     */
    private PalaceInterpretation parsePalaceInterpretation(
            JsonNode palaceNode, TuViChartResponse chart, String palaceCode) {
        
        // Find palace info from chart
        var palaceInfo = chart.getPalaces().stream()
                .filter(p -> palaceCode.equals(p.getNameCode()))
                .findFirst()
                .orElse(null);

        return PalaceInterpretation.builder()
                .palaceCode(palaceCode)
                .palaceName(palaceInfo != null ? palaceInfo.getName() : null)
                .palaceChi(palaceInfo != null ? palaceInfo.getDiaChiCode() : null)
                .canChiPrefix(palaceInfo != null ? palaceInfo.getCanChiPrefix() : null)
                .summary(palaceNode.path("summary").asText(null))
                .introduction(palaceNode.path("introduction").asText(null))
                .detailedAnalysis(palaceNode.path("detailedAnalysis").asText(null))
                .tuanTrietEffect(palaceNode.path("tuanTrietEffect").asText(null))
                .adviceSection(palaceNode.path("adviceSection").asText(null))
                .conclusion(palaceNode.path("conclusion").asText(null))
                .hasTuan(palaceInfo != null && palaceInfo.isHasTuan())
                .hasTriet(palaceInfo != null && palaceInfo.isHasTriet())
                .build();
    }

    /**
     * Set palace interpretation to the appropriate field in response builder.
     */
    private void setPalaceInterpretation(
            TuViInterpretationResponse.TuViInterpretationResponseBuilder builder,
            String palaceCode, PalaceInterpretation interp) {
        
        switch (palaceCode) {
            case "MENH" -> builder.menhInterpretation(interp);
            case "QUAN_LOC" -> builder.quanLocInterpretation(interp);
            case "TAI_BACH" -> builder.taiBachInterpretation(interp);
            case "PHU_THE" -> builder.phuTheInterpretation(interp);
            case "TAT_ACH" -> builder.tatAchInterpretation(interp);
            case "TU_TUC" -> builder.tuTucInterpretation(interp);
            case "DIEN_TRACH" -> builder.dienTrachInterpretation(interp);
            case "PHU_MAU" -> builder.phuMauInterpretation(interp);
            case "HUYNH_DE" -> builder.huynhDeInterpretation(interp);
            case "PHUC_DUC" -> builder.phucDucInterpretation(interp);
            case "NO_BOC" -> builder.noBocInterpretation(interp);
            case "THIEN_DI" -> builder.thienDiInterpretation(interp);
            default -> log.warn("Unknown palace code: {}", palaceCode);
        }
    }

    /**
     * Parse cycle interpretation response from Grok.
     */
    private CycleInterpretationResponse parseCycleInterpretationResponse(
            String response, TuViChartResponse chart, String name, String gender) {
        
        try {
            String json = GrokPromptBuilder.extractJsonFromResponse(response);
            JsonNode root = objectMapper.readTree(json);

            // Parse Đại vận interpretations
            List<DaiVanInterpretation> daiVanInterpretations = new ArrayList<>();
            JsonNode daiVanNode = root.path("daiVanInterpretations");
            if (daiVanNode.isArray()) {
                for (JsonNode dvNode : daiVanNode) {
                    String palaceCode = dvNode.path("palaceCode").asText();
                    
                    // Find palace info from chart
                    var palaceInfo = chart.getPalaces().stream()
                            .filter(p -> palaceCode.equals(p.getNameCode()))
                            .findFirst()
                            .orElse(null);

                    DaiVanInterpretation dvInterp = DaiVanInterpretation.builder()
                            .startAge(dvNode.path("startAge").asInt())
                            .endAge(dvNode.path("endAge").asInt())
                            .palaceCode(palaceCode)
                            .palaceName(dvNode.path("palaceName").asText(null))
                            .palaceChi(palaceInfo != null ? palaceInfo.getDiaChiCode() : null)
                            .summary(dvNode.path("summary").asText(null))
                            .interpretation(dvNode.path("interpretation").asText(null))
                            .keyThemes(dvNode.path("keyThemes").asText(null))
                            .advice(dvNode.path("advice").asText(null))
                            .hasTuan(palaceInfo != null && palaceInfo.isHasTuan())
                            .hasTriet(palaceInfo != null && palaceInfo.isHasTriet())
                            .build();
                    
                    daiVanInterpretations.add(dvInterp);
                }
            }

            return CycleInterpretationResponse.builder()
                    .chartHash(chart.getChartHash())
                    .name(name)
                    .gender(gender)
                    .birthDate(chart.getCenter().getSolarDate())
                    .lunarYearCanChi(chart.getCenter().getLunarYearCanChi())
                    .cycleInfo(chart.getCycles())
                    .introduction(root.path("introduction").asText(null))
                    .overallCycleSummary(root.path("overallCycleSummary").asText(null))
                    .daiVanInterpretations(daiVanInterpretations)
                    .generalAdvice(root.path("generalAdvice").asText(null))
                    .generatedAt(ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                    .aiModel(grokProperties.getModel())
                    .fromCache(false)
                    .build();

        } catch (Exception e) {
            log.error("Error parsing cycle interpretation response: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Check if Grok API is available.
     */
    public boolean isAvailable() {
        return grokProperties.isAvailable();
    }

    /**
     * Get the current model name.
     */
    public String getModelName() {
        return grokProperties.getModel();
    }
}

