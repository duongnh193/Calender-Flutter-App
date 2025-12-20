package com.duong.lichvanien.tuvi.service;

import com.duong.lichvanien.tuvi.dto.TuViChartResponse;
import com.duong.lichvanien.tuvi.dto.interpretation.PalaceInterpretation;
import com.duong.lichvanien.tuvi.dto.interpretation.TuViInterpretationResponse;
import com.duong.lichvanien.tuvi.entity.TuViInterpretationEntity;
import com.duong.lichvanien.tuvi.entity.TuViPalaceInterpretationEntity;
import com.duong.lichvanien.tuvi.repository.TuViInterpretationRepository;
import com.duong.lichvanien.tuvi.repository.TuViPalaceInterpretationRepository;
import com.duong.lichvanien.tuvi.util.CanonicalChartHashGenerator;
import com.duong.lichvanien.tuvi.util.TuViInterpretationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service for database operations on Tu Vi interpretation data.
 * Handles saving and retrieving interpretations from database.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TuViInterpretationDatabaseService {

    private final TuViInterpretationRepository interpretationRepository;
    private final TuViPalaceInterpretationRepository palaceInterpretationRepository;

    /**
     * Find interpretation by chart hash and gender.
     *
     * @param chart The chart response
     * @param gender Gender string ("male" or "female")
     * @param name Chart owner's name (for response)
     * @return Optional containing interpretation response if found
     */
    @Transactional(readOnly = true)
    public Optional<TuViInterpretationResponse> findByChartHash(
            TuViChartResponse chart,
            String gender,
            String name) {
        
        // Generate canonical chart hash (includes ALL FACT data)
        String chartHash = CanonicalChartHashGenerator.generateCanonicalHash(chart, gender, false);
        log.debug("Looking up interpretation in database with chart hash: {}", chartHash);
        
        // Convert gender string to enum
        TuViInterpretationEntity.Gender genderEnum;
        try {
            genderEnum = TuViInterpretationEntity.Gender.valueOf(gender.toLowerCase());
        } catch (IllegalArgumentException e) {
            log.warn("Invalid gender value: {}", gender);
            return Optional.empty();
        }
        
        // Query from database
        Optional<TuViInterpretationEntity> entityOpt = interpretationRepository
                .findByChartHashAndGender(chartHash, genderEnum);
        
        if (entityOpt.isEmpty()) {
            log.debug("No interpretation found in database for chart hash: {}", chartHash);
            return Optional.empty();
        }
        
        // Throw exception if not found (when strict mode is enabled)
        // For now, return Optional.empty() and let caller decide
        
        TuViInterpretationEntity entity = entityOpt.get();
        log.info("Found interpretation in database for chart hash: {}", chartHash);
        
        // Load palace interpretations
        List<TuViPalaceInterpretationEntity> palaceEntities = palaceInterpretationRepository
                .findByInterpretationId(entity.getId());
        
        // Convert to DTO
        TuViInterpretationResponse response = TuViInterpretationMapper.toDto(
                entity, palaceEntities, name);
        
        return Optional.of(response);
    }

    /**
     * Save interpretation to database.
     *
     * @param chart The chart response
     * @param gender Gender string ("male" or "female")
     * @param interpretation The interpretation response to save
     * @return Saved interpretation response (with updated metadata)
     */
    @Transactional
    public TuViInterpretationResponse save(
            TuViChartResponse chart,
            String gender,
            TuViInterpretationResponse interpretation) {
        
        // Generate canonical chart hash (includes ALL FACT data)
        String chartHash = CanonicalChartHashGenerator.generateCanonicalHash(chart, gender, false);
        log.info("Saving interpretation to database with chart hash: {}", chartHash);
        
        // Check if already exists
        TuViInterpretationEntity.Gender genderEnum = TuViInterpretationEntity.Gender.valueOf(gender.toLowerCase());
        Optional<TuViInterpretationEntity> existingOpt = interpretationRepository
                .findByChartHashAndGender(chartHash, genderEnum);
        
        TuViInterpretationEntity entity;
        if (existingOpt.isPresent()) {
            // Update existing
            entity = existingOpt.get();
            log.debug("Updating existing interpretation with id: {}", entity.getId());
            
            // Update fields (FACT fields removed - only update interpretation content)
            entity.setOverviewData(TuViInterpretationMapper.toEntity(interpretation, chartHash).getOverviewData());
            
            // Delete old palace interpretations (cascade will handle stars)
            palaceInterpretationRepository.deleteByInterpretationId(entity.getId());
        } else {
            // Create new
            entity = TuViInterpretationMapper.toEntity(interpretation, chartHash);
        }
        
        // Save main entity
        entity = interpretationRepository.save(entity);
        log.debug("Saved interpretation entity with id: {}", entity.getId());
        
        // Save palace interpretations with cascade to stars
        List<PalaceInterpretation> palaceDtos = TuViInterpretationMapper.getAllPalaceInterpretations(interpretation);
        for (PalaceInterpretation palaceDto : palaceDtos) {
            TuViPalaceInterpretationEntity palaceEntity = TuViInterpretationMapper
                    .toPalaceEntity(palaceDto, entity);
            palaceInterpretationRepository.save(palaceEntity);
        }
        
        log.info("Successfully saved interpretation to database with chart hash: {}", chartHash);
        
        // Return the saved interpretation (re-fetch to ensure consistency)
        List<TuViPalaceInterpretationEntity> savedPalaces = palaceInterpretationRepository
                .findByInterpretationId(entity.getId());
        return TuViInterpretationMapper.toDto(entity, savedPalaces, interpretation.getName());
    }
}
