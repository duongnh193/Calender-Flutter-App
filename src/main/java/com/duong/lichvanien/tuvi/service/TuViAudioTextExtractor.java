package com.duong.lichvanien.tuvi.service;

import com.duong.lichvanien.tuvi.dto.interpretation.OverviewSection;
import com.duong.lichvanien.tuvi.dto.interpretation.PalaceInterpretation;
import com.duong.lichvanien.tuvi.dto.interpretation.TuViInterpretationResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service for extracting text content from TuViInterpretationResponse
 * for text-to-speech conversion.
 */
@Slf4j
@Service
public class TuViAudioTextExtractor {

    /**
     * Extract text content for a specific palace from interpretation response.
     *
     * @param interpretation Full interpretation response
     * @param palaceCode     Palace code (OVERVIEW, MENH, QUAN_LOC, etc.)
     * @return Combined text content for the palace
     * @throws IllegalArgumentException if palace code is invalid or not found
     */
    public String extractTextForPalace(TuViInterpretationResponse interpretation, String palaceCode) {
        if (interpretation == null) {
            throw new IllegalArgumentException("Interpretation response cannot be null");
        }

        if (palaceCode == null || palaceCode.isBlank()) {
            throw new IllegalArgumentException("Palace code cannot be null or empty");
        }

        String normalizedCode = palaceCode.toUpperCase().trim();

        if ("OVERVIEW".equals(normalizedCode)) {
            return extractOverviewText(interpretation.getOverview());
        }

        // Extract palace interpretation
        PalaceInterpretation palaceInterp = getPalaceInterpretation(interpretation, normalizedCode);
        if (palaceInterp == null) {
            throw new IllegalArgumentException("Palace code not found: " + palaceCode);
        }

        return extractPalaceText(palaceInterp);
    }

    /**
     * Extract text from OverviewSection.
     */
    private String extractOverviewText(OverviewSection overview) {
        if (overview == null) {
            return "";
        }

        List<String> sections = new ArrayList<>();

        if (notBlank(overview.getIntroduction())) {
            sections.add(overview.getIntroduction());
        }

        if (notBlank(overview.getBanMenhInterpretation())) {
            String banMenhText = "Bản mệnh: " + overview.getBanMenhName();
            if (notBlank(overview.getBanMenhNguHanh())) {
                banMenhText += " (" + overview.getBanMenhNguHanh() + ")";
            }
            banMenhText += ". " + overview.getBanMenhInterpretation();
            sections.add(banMenhText);
        }

        if (notBlank(overview.getCucInterpretation())) {
            String cucText = "Cục mệnh: " + overview.getCucName();
            if (overview.getCucValue() > 0) {
                cucText += " (giá trị " + overview.getCucValue() + ")";
            }
            if (notBlank(overview.getMenhCucRelation())) {
                cucText += ". " + overview.getMenhCucRelation();
            }
            cucText += ". " + overview.getCucInterpretation();
            sections.add(cucText);
        }

        if (notBlank(overview.getChuMenhInterpretation())) {
            String chuMenhText = "Chủ mệnh: " + overview.getChuMenh();
            chuMenhText += ". " + overview.getChuMenhInterpretation();
            sections.add(chuMenhText);
        }

        if (notBlank(overview.getChuThanInterpretation())) {
            String chuThanText = "Chủ thân: " + overview.getChuThan();
            chuThanText += ". " + overview.getChuThanInterpretation();
            sections.add(chuThanText);
        }

        if (notBlank(overview.getThanCuInterpretation())) {
            String thanCuText = "Thân cư: " + overview.getThanCu();
            thanCuText += ". " + overview.getThanCuInterpretation();
            sections.add(thanCuText);
        }

        if (notBlank(overview.getThuanNghichInterpretation())) {
            String thuanNghichText = "Thuận nghịch: " + overview.getThuanNghich();
            thuanNghichText += ". " + overview.getThuanNghichInterpretation();
            sections.add(thuanNghichText);
        }

        if (notBlank(overview.getOverallSummary())) {
            sections.add("Tổng kết: " + overview.getOverallSummary());
        }

        return String.join(". ", sections);
    }

    /**
     * Extract text from PalaceInterpretation.
     */
    private String extractPalaceText(PalaceInterpretation palace) {
        if (palace == null) {
            return "";
        }

        List<String> sections = new ArrayList<>();

        // Palace name and context
        String palaceName = palace.getPalaceName() != null ? palace.getPalaceName() : palace.getPalaceCode();
        if (notBlank(palace.getPalaceChi())) {
            palaceName += " (" + palace.getPalaceChi() + ")";
        }

        if (notBlank(palace.getIntroduction())) {
            sections.add(palaceName + ". " + palace.getIntroduction());
        } else {
            sections.add("Cung " + palaceName + ".");
        }

        if (notBlank(palace.getSummary())) {
            sections.add("Tóm tắt: " + palace.getSummary());
        }

        if (notBlank(palace.getDetailedAnalysis())) {
            sections.add("Phân tích chi tiết: " + palace.getDetailedAnalysis());
        }

        if (notBlank(palace.getTuanTrietEffect())) {
            sections.add("Ảnh hưởng Tuần Triệt: " + palace.getTuanTrietEffect());
        }

        if (notBlank(palace.getAdviceSection())) {
            sections.add("Lời khuyên: " + palace.getAdviceSection());
        }

        if (notBlank(palace.getConclusion())) {
            sections.add("Kết luận: " + palace.getConclusion());
        }

        return String.join(". ", sections);
    }

    /**
     * Get PalaceInterpretation by palace code.
     */
    private PalaceInterpretation getPalaceInterpretation(TuViInterpretationResponse interpretation, String palaceCode) {
        return switch (palaceCode) {
            case "MENH" -> interpretation.getMenhInterpretation();
            case "QUAN_LOC" -> interpretation.getQuanLocInterpretation();
            case "TAI_BACH" -> interpretation.getTaiBachInterpretation();
            case "PHU_THE" -> interpretation.getPhuTheInterpretation();
            case "TAT_ACH" -> interpretation.getTatAchInterpretation();
            case "TU_TUC" -> interpretation.getTuTucInterpretation();
            case "DIEN_TRACH" -> interpretation.getDienTrachInterpretation();
            case "PHU_MAU" -> interpretation.getPhuMauInterpretation();
            case "HUYNH_DE" -> interpretation.getHuynhDeInterpretation();
            case "PHUC_DUC" -> interpretation.getPhucDucInterpretation();
            case "NO_BOC" -> interpretation.getNoBocInterpretation();
            case "THIEN_DI" -> interpretation.getThienDiInterpretation();
            default -> null;
        };
    }

    /**
     * Check if string is not blank.
     */
    private boolean notBlank(String str) {
        return str != null && !str.isBlank();
    }
}

