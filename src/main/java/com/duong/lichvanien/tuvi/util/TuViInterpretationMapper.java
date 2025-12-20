package com.duong.lichvanien.tuvi.util;

import com.duong.lichvanien.tuvi.dto.interpretation.*;
import com.duong.lichvanien.tuvi.entity.TuViInterpretationEntity;
import com.duong.lichvanien.tuvi.entity.TuViPalaceInterpretationEntity;
import com.duong.lichvanien.tuvi.entity.TuViStarInterpretationEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class for converting between Tu Vi interpretation Entity and DTO objects.
 */
@Slf4j
public class TuViInterpretationMapper {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    /**
     * Convert Entity to DTO.
     *
     * @param entity The interpretation entity
     * @param palaceEntities List of palace interpretation entities
     * @param name Chart owner's name (from request)
     * @return TuViInterpretationResponse DTO
     */
    public static TuViInterpretationResponse toDto(
            TuViInterpretationEntity entity,
            List<TuViPalaceInterpretationEntity> palaceEntities,
            String name) {

        // Parse overview data from JSON
        OverviewSection overview = parseOverviewData(entity.getOverviewData());

        // Convert palaces
        List<PalaceInterpretation> palaceInterpretations = palaceEntities.stream()
                .map(TuViInterpretationMapper::toPalaceDto)
                .collect(Collectors.toList());

        // Build response
        // Note: birthDate, birthHour, lunarYearCanChi are FACT and should come from natal_chart via chart_hash
        // For now, setting to null/default values since these fields are removed from interpretation entity
        TuViInterpretationResponse.TuViInterpretationResponseBuilder builder = TuViInterpretationResponse.builder()
                .name(name)
                .gender(entity.getGender().name())
                .birthDate(null)  // FACT - should come from natal_chart
                .birthHour(0)     // FACT - should come from natal_chart
                .lunarYearCanChi(null)  // FACT - should come from natal_chart
                .overview(overview)
                .generatedAt(entity.getUpdatedAt() != null 
                        ? entity.getUpdatedAt().toString() 
                        : entity.getCreatedAt().toString())
                .aiModel("database"); // Mark as from database

        // Set palace interpretations by code
        for (PalaceInterpretation palaceDto : palaceInterpretations) {
            setPalaceInterpretation(builder, palaceDto.getPalaceCode(), palaceDto);
        }

        return builder.build();
    }

    /**
     * Convert DTO to Entity.
     *
     * @param dto The interpretation DTO
     * @param chartHash The chart hash
     * @return TuViInterpretationEntity
     */
    public static TuViInterpretationEntity toEntity(TuViInterpretationResponse dto, String chartHash) {
        TuViInterpretationEntity entity = new TuViInterpretationEntity();
        
        // Only set reference to FACT (chart_hash) and gender, NOT FACT data
        entity.setChartHash(chartHash);
        entity.setGender(TuViInterpretationEntity.Gender.valueOf(dto.getGender()));
        
        // Serialize overview to JSON (ONLY interpretation content, NO FACT)
        entity.setOverviewData(serializeOverviewData(dto.getOverview()));
        
        return entity;
    }

    /**
     * Convert PalaceInterpretation DTO to Entity.
     *
     * @param dto Palace interpretation DTO
     * @param interpretationEntity Parent interpretation entity
     * @return TuViPalaceInterpretationEntity
     */
    public static TuViPalaceInterpretationEntity toPalaceEntity(
            PalaceInterpretation dto,
            TuViInterpretationEntity interpretationEntity) {
        
        TuViPalaceInterpretationEntity entity = new TuViPalaceInterpretationEntity();
        
        entity.setInterpretation(interpretationEntity);
        // Only set palace_code for reference, NOT FACT data (palace_name, palace_chi, can_chi_prefix, has_tuan, has_triet)
        entity.setPalaceCode(dto.getPalaceCode());
        
        // Only set interpretation content (NO FACT)
        entity.setSummary(dto.getSummary());
        entity.setIntroduction(dto.getIntroduction());
        entity.setDetailedAnalysis(dto.getDetailedAnalysis());
        entity.setGenderAnalysis(dto.getGenderAnalysis());
        entity.setTuanTrietEffect(dto.getTuanTrietEffect());  // This is interpretation of effects, not FACT
        entity.setAdviceSection(dto.getAdviceSection());
        entity.setConclusion(dto.getConclusion());
        
        // Convert star interpretations
        if (dto.getStarAnalyses() != null) {
            List<TuViStarInterpretationEntity> starEntities = dto.getStarAnalyses().stream()
                    .map(starDto -> toStarEntity(starDto, entity))
                    .collect(Collectors.toList());
            entity.setStarInterpretations(starEntities);
        }
        
        return entity;
    }

    /**
     * Convert PalaceInterpretation Entity to DTO.
     *
     * @param entity Palace interpretation entity
     * @return PalaceInterpretation DTO
     */
    public static PalaceInterpretation toPalaceDto(TuViPalaceInterpretationEntity entity) {
        List<StarInterpretation> starDtos = new ArrayList<>();
        
        if (entity.getStarInterpretations() != null) {
            starDtos = entity.getStarInterpretations().stream()
                    .map(TuViInterpretationMapper::toStarDto)
                    .collect(Collectors.toList());
        }
        
        // Note: palace_name, palace_chi, can_chi_prefix, hasTuan, hasTriet are FACT and should come from natal_palace
        // For backward compatibility, we set them to null here since they're removed from interpretation entity
        return PalaceInterpretation.builder()
                .palaceCode(entity.getPalaceCode())
                .palaceName(null)  // FACT - should come from natal_palace
                .palaceChi(null)   // FACT - should come from natal_palace
                .canChiPrefix(null) // FACT - should come from natal_palace
                .summary(entity.getSummary())
                .introduction(entity.getIntroduction())
                .detailedAnalysis(entity.getDetailedAnalysis())
                .genderAnalysis(entity.getGenderAnalysis())
                .starAnalyses(starDtos)
                .hasTuan(false)  // FACT - should come from natal_palace
                .hasTriet(false) // FACT - should come from natal_palace
                .tuanTrietEffect(entity.getTuanTrietEffect())
                .adviceSection(entity.getAdviceSection())
                .conclusion(entity.getConclusion())
                .build();
    }

    /**
     * Convert StarInterpretation Entity to DTO.
     *
     * @param entity Star interpretation entity
     * @return StarInterpretation DTO
     */
    public static StarInterpretation toStarDto(TuViStarInterpretationEntity entity) {
        // Note: star_name, star_type, brightness are FACT and should come from natal_star
        // For backward compatibility, set them to null since they're removed from interpretation entity
        return StarInterpretation.builder()
                .starCode(entity.getStarCode())
                .starName(null)      // FACT - should come from natal_star
                .starType(null)      // FACT - should come from natal_star
                .brightness(null)    // FACT - should come from natal_star
                .interpretation(entity.getInterpretation())
                .summary(entity.getSummary())
                .build();
    }

    /**
     * Convert StarInterpretation DTO to Entity.
     *
     * @param dto Star interpretation DTO
     * @param palaceEntity Parent palace interpretation entity
     * @return TuViStarInterpretationEntity
     */
    public static TuViStarInterpretationEntity toStarEntity(
            StarInterpretation dto,
            TuViPalaceInterpretationEntity palaceEntity) {
        
        TuViStarInterpretationEntity entity = new TuViStarInterpretationEntity();
        
        entity.setPalaceInterpretation(palaceEntity);
        // Only set star_code for reference, NOT FACT data (star_name, star_type, brightness)
        entity.setStarCode(dto.getStarCode());
        
        // Only set interpretation content (NO FACT)
        entity.setInterpretation(dto.getInterpretation());
        entity.setSummary(dto.getSummary());
        
        return entity;
    }

    /**
     * Set palace interpretation in response builder based on palace code.
     */
    private static void setPalaceInterpretation(
            TuViInterpretationResponse.TuViInterpretationResponseBuilder builder,
            String palaceCode,
            PalaceInterpretation interpretation) {
        
        switch (palaceCode) {
            case "MENH":
                builder.menhInterpretation(interpretation);
                break;
            case "QUAN_LOC":
                builder.quanLocInterpretation(interpretation);
                break;
            case "TAI_BACH":
                builder.taiBachInterpretation(interpretation);
                break;
            case "PHU_THE":
                builder.phuTheInterpretation(interpretation);
                break;
            case "TAT_ACH":
                builder.tatAchInterpretation(interpretation);
                break;
            case "TU_TUC":
                builder.tuTucInterpretation(interpretation);
                break;
            case "DIEN_TRACH":
                builder.dienTrachInterpretation(interpretation);
                break;
            case "PHU_MAU":
                builder.phuMauInterpretation(interpretation);
                break;
            case "HUYNH_DE":
                builder.huynhDeInterpretation(interpretation);
                break;
            case "PHUC_DUC":
                builder.phucDucInterpretation(interpretation);
                break;
            case "NO_BOC":
                builder.noBocInterpretation(interpretation);
                break;
            case "THIEN_DI":
                builder.thienDiInterpretation(interpretation);
                break;
            default:
                log.warn("Unknown palace code: {}", palaceCode);
        }
    }

    /**
     * Parse overview data from JSON string.
     */
    private static OverviewSection parseOverviewData(String jsonData) {
        if (jsonData == null || jsonData.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readValue(jsonData, OverviewSection.class);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse overview data from JSON: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Serialize overview data to JSON string.
     */
    private static String serializeOverviewData(OverviewSection overview) {
        if (overview == null) {
            return "{}";
        }
        try {
            return objectMapper.writeValueAsString(overview);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize overview data to JSON: {}", e.getMessage(), e);
            return "{}";
        }
    }

    /**
     * Get all palace interpretations from response as a list.
     * Useful for saving to database.
     */
    public static List<PalaceInterpretation> getAllPalaceInterpretations(TuViInterpretationResponse response) {
        List<PalaceInterpretation> palaces = new ArrayList<>();
        
        if (response.getMenhInterpretation() != null) palaces.add(response.getMenhInterpretation());
        if (response.getQuanLocInterpretation() != null) palaces.add(response.getQuanLocInterpretation());
        if (response.getTaiBachInterpretation() != null) palaces.add(response.getTaiBachInterpretation());
        if (response.getPhuTheInterpretation() != null) palaces.add(response.getPhuTheInterpretation());
        if (response.getTatAchInterpretation() != null) palaces.add(response.getTatAchInterpretation());
        if (response.getTuTucInterpretation() != null) palaces.add(response.getTuTucInterpretation());
        if (response.getDienTrachInterpretation() != null) palaces.add(response.getDienTrachInterpretation());
        if (response.getPhuMauInterpretation() != null) palaces.add(response.getPhuMauInterpretation());
        if (response.getHuynhDeInterpretation() != null) palaces.add(response.getHuynhDeInterpretation());
        if (response.getPhucDucInterpretation() != null) palaces.add(response.getPhucDucInterpretation());
        if (response.getNoBocInterpretation() != null) palaces.add(response.getNoBocInterpretation());
        if (response.getThienDiInterpretation() != null) palaces.add(response.getThienDiInterpretation());
        
        return palaces;
    }
}
