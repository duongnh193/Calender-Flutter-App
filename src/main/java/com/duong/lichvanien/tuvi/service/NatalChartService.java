package com.duong.lichvanien.tuvi.service;

import com.duong.lichvanien.tuvi.dto.*;
import com.duong.lichvanien.tuvi.entity.NatalChartEntity;
import com.duong.lichvanien.tuvi.entity.NatalPalaceEntity;
import com.duong.lichvanien.tuvi.entity.NatalStarEntity;
import com.duong.lichvanien.tuvi.repository.NatalChartRepository;
import com.duong.lichvanien.tuvi.repository.NatalPalaceRepository;
import com.duong.lichvanien.tuvi.repository.NatalStarRepository;
import com.duong.lichvanien.tuvi.util.CanonicalChartHashGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Service for managing natal chart FACT data.
 * Handles saving and retrieving FACT (source of truth) data for Tu Vi charts.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NatalChartService {

    private final NatalChartRepository natalChartRepository;
    private final NatalPalaceRepository natalPalaceRepository;
    private final NatalStarRepository natalStarRepository;

    /**
     * Save natal chart FACT data to database.
     * Generates canonical hash and saves complete FACT structure.
     *
     * @param chart The chart response (calculated)
     * @param request The original request (for input data)
     * @return The generated chart hash
     */
    @Transactional
    public String saveNatalChart(TuViChartResponse chart, TuViChartRequest request) {
        log.debug("Saving natal chart FACT data");

        // Generate canonical hash
        String chartHash = CanonicalChartHashGenerator.generateCanonicalHash(
                chart, request.getGender(), request.getIsLunar() != null ? request.getIsLunar() : false);

        // Check if already exists
        Optional<NatalChartEntity> existing = natalChartRepository.findByChartHash(chartHash);
        if (existing.isPresent()) {
            log.debug("Natal chart already exists with hash: {}", chartHash);
            return chartHash;
        }

        // Convert DTO to Entity
        CenterInfo center = chart.getCenter();
        MarkerInfo markers = chart.getMarkers();

        NatalChartEntity chartEntity = new NatalChartEntity();
        chartEntity.setChartHash(chartHash);
        chartEntity.setSolarDate(LocalDate.parse(request.getDate()));
        chartEntity.setBirthHour(request.getHour());
        chartEntity.setBirthMinute(request.getMinute() != null ? request.getMinute() : 0);
        chartEntity.setGender(NatalChartEntity.Gender.valueOf(request.getGender().toLowerCase()));
        chartEntity.setIsLunar(request.getIsLunar() != null ? request.getIsLunar() : false);
        chartEntity.setIsLeapMonth(request.getIsLeapMonth() != null ? request.getIsLeapMonth() : false);

        // Lunar date FACT
        chartEntity.setLunarYear(center.getLunarYear());
        chartEntity.setLunarMonth(center.getLunarMonth());
        chartEntity.setLunarDay(center.getLunarDay());
        chartEntity.setLunarYearCanChi(center.getLunarYearCanChi());
        chartEntity.setLunarMonthCanChi(center.getLunarMonthCanChi());
        chartEntity.setLunarDayCanChi(center.getLunarDayCanChi());
        chartEntity.setBirthHourCanChi(center.getBirthHourCanChi());
        chartEntity.setHourBranchIndex(center.getHourBranchIndex());

        // Destiny calculations FACT
        chartEntity.setBanMenh(center.getBanMenh());
        chartEntity.setBanMenhNguHanh(center.getBanMenhNguHanh());
        chartEntity.setCucName(center.getCuc());
        chartEntity.setCucValue(center.getCucValue());
        chartEntity.setCucNguHanh(center.getCucNguHanh());
        chartEntity.setAmDuong(center.getAmDuong());
        // Truncate thuanNghich to max 64 chars if needed (shouldn't happen, but safe guard)
        String thuanNghich = center.getThuanNghich();
        if (thuanNghich != null && thuanNghich.length() > 64) {
            log.warn("thuanNghich value exceeds 64 chars, truncating: {}", thuanNghich);
            thuanNghich = thuanNghich.substring(0, 64);
        }
        chartEntity.setThuanNghich(thuanNghich);

        // Main stars FACT (extract codes from names)
        // chuMenhStarCode is NOT NULL, so ensure we have a value
        String chuMenhName = center.getChuMenh();
        String chuMenhCode = null;
        
        // Only extract if chuMenh is not null and not empty
        if (chuMenhName != null && !chuMenhName.isBlank()) {
            chuMenhCode = extractStarCode(chuMenhName);
        }
        
        if (chuMenhCode == null || chuMenhCode.isBlank()) {
            log.error("chuMenh is null or could not be extracted. chuMenh value: '{}', Center: {}", 
                    chuMenhName, center);
            
            // Check if this is the special case: Mệnh vô Chính tinh
            boolean menhKhongChinhTinh = Boolean.TRUE.equals(center.getMenhKhongChinhTinh());
            
            if (menhKhongChinhTinh && chart.getPalaces() != null) {
                // Special case: Cung Mệnh không có Chính tinh
                // Apply Tu Vi fallback logic: check opposite palace (Thiên Di) first, then Tài Bạch, then Thân
                log.info("Handling menhKhongChinhTinh case - applying Tu Vi fallback logic");
                
                // 1. Check Thiên Di (opposite palace)
                chuMenhCode = findChinhTinhInPalaceByCode(chart.getPalaces(), "THIEN_DI");
                if (chuMenhCode != null) {
                    log.info("Found chuMenh from opposite palace (Thiên Di): {}", chuMenhCode);
                } else {
                    // 2. Check Tài Bạch
                    chuMenhCode = findChinhTinhInPalaceByCode(chart.getPalaces(), "TAI_BACH");
                    if (chuMenhCode != null) {
                        log.info("Found chuMenh from Tài Bạch: {}", chuMenhCode);
                    } else {
                        // 3. Check Thân palace
                        String thanCuCode = extractPalaceCode(center.getThanCu());
                        if (thanCuCode != null && !thanCuCode.isBlank()) {
                            chuMenhCode = findChinhTinhInPalaceByCode(chart.getPalaces(), thanCuCode);
                            if (chuMenhCode != null) {
                                log.info("Found chuMenh from Thân palace ({}): {}", thanCuCode, chuMenhCode);
                            }
                        }
                        
                        // 4. Last resort: first star in Mệnh palace
                        if (chuMenhCode == null || chuMenhCode.isBlank()) {
                            for (PalaceInfo palace : chart.getPalaces()) {
                                if ("MENH".equals(palace.getNameCode()) && palace.getStars() != null && !palace.getStars().isEmpty()) {
                                    chuMenhCode = palace.getStars().get(0).getCode();
                                    log.warn("Using last resort: first star in Mệnh palace: {} (type: {})", 
                                            chuMenhCode, palace.getStars().get(0).getType());
                                    break;
                                }
                            }
                        }
                    }
                }
            } else {
                // Normal fallback: try to find Chính tinh in Mệnh palace
                if (chart.getPalaces() != null) {
                    for (PalaceInfo palace : chart.getPalaces()) {
                        if ("MENH".equals(palace.getNameCode()) && palace.getStars() != null && !palace.getStars().isEmpty()) {
                            // First try to find Chính tinh
                            for (StarInfo star : palace.getStars()) {
                                if ("CHINH_TINH".equals(star.getType())) {
                                    chuMenhCode = star.getCode();
                                    log.warn("Using fallback: extracted chuMenh Chính tinh from palace stars: {}", chuMenhCode);
                                    break;
                                }
                            }
                            
                            // If still not found, use first star in Mệnh palace as last resort
                            if ((chuMenhCode == null || chuMenhCode.isBlank()) && !palace.getStars().isEmpty()) {
                                StarInfo firstStar = palace.getStars().get(0);
                                chuMenhCode = firstStar.getCode();
                                log.warn("Using last resort fallback: extracted chuMenh from first star in Mệnh palace: {} (type: {})", 
                                        chuMenhCode, firstStar.getType());
                            }
                            
                            if (chuMenhCode != null) break;
                        }
                    }
                }
            }
            
            if (chuMenhCode == null || chuMenhCode.isBlank()) {
                throw new IllegalStateException(
                    String.format("chuMenh cannot be null - chart calculation error. " +
                            "chuMenh value: '%s', chart has %d palaces, Mệnh palace has no stars", 
                            chuMenhName, 
                            chart.getPalaces() != null ? chart.getPalaces().size() : 0));
            }
        }
        chartEntity.setChuMenhStarCode(chuMenhCode);
        
        chartEntity.setChuThanStarCode(extractStarCode(center.getChuThan())); // Can be null
        
        // thanCuPalaceCode is NOT NULL, so ensure we have a value
        String thanCuCode = extractPalaceCode(center.getThanCu());
        if (thanCuCode == null || thanCuCode.isBlank()) {
            log.error("thanCu is null or could not be extracted from center. Center: {}", center);
            throw new IllegalStateException("thanCu cannot be null - chart calculation error");
        }
        chartEntity.setThanCuPalaceCode(thanCuCode);

        // Tuần/Triệt positions FACT
        chartEntity.setTuanStartChi(markers.getTuanStart());
        chartEntity.setTuanEndChi(markers.getTuanEnd());
        chartEntity.setTrietStartChi(markers.getTrietStart());
        chartEntity.setTrietEndChi(markers.getTrietEnd());

        chartEntity.setCalculatedAt(LocalDateTime.now());

        // Save chart
        chartEntity = natalChartRepository.save(chartEntity);
        log.debug("Saved natal chart with id: {}, hash: {}", chartEntity.getId(), chartHash);

        // Save palaces
        List<NatalPalaceEntity> palaceEntities = new ArrayList<>();
        for (PalaceInfo palaceDto : chart.getPalaces()) {
            NatalPalaceEntity palaceEntity = convertPalaceToEntity(palaceDto, chartEntity);
            palaceEntity = natalPalaceRepository.save(palaceEntity);
            palaceEntities.add(palaceEntity);

            // Save stars in this palace
            if (palaceDto.getStars() != null && !palaceDto.getStars().isEmpty()) {
                // Sort stars to determine order
                List<StarInfo> sortedStars = new ArrayList<>(palaceDto.getStars());
                sortedStars.sort(Comparator
                        .comparing((StarInfo s) -> getStarTypeOrder(s.getType()))
                        .thenComparing(StarInfo::getCode));

                for (int i = 0; i < sortedStars.size(); i++) {
                    StarInfo starDto = sortedStars.get(i);
                    NatalStarEntity starEntity = convertStarToEntity(starDto, palaceEntity, i);
                    natalStarRepository.save(starEntity);
                }
            }
        }

        log.info("Saved natal chart FACT data: {} palaces, hash: {}", palaceEntities.size(), chartHash);
        return chartHash;
    }

    /**
     * Find natal chart by hash.
     */
    @Transactional(readOnly = true)
    public Optional<NatalChartEntity> findByHash(String chartHash) {
        return natalChartRepository.findByChartHash(chartHash);
    }

    /**
     * Check if chart hash exists.
     */
    @Transactional(readOnly = true)
    public boolean existsByHash(String chartHash) {
        return natalChartRepository.existsByChartHash(chartHash);
    }

    /**
     * Convert PalaceInfo DTO to NatalPalaceEntity.
     */
    private NatalPalaceEntity convertPalaceToEntity(PalaceInfo palace, NatalChartEntity chartEntity) {
        NatalPalaceEntity entity = new NatalPalaceEntity();
        entity.setNatalChart(chartEntity);
        entity.setPalaceIndex(palace.getIndex());
        entity.setPalaceCode(palace.getNameCode());
        entity.setPalaceChi(palace.getDiaChiCode());
        entity.setCanChiPrefix(palace.getCanChiPrefix());
        entity.setTruongSinhStage(palace.getTruongSinhStage());
        entity.setDaiVanStartAge(palace.getDaiVanStartAge());
        entity.setDaiVanLabel(palace.getDaiVanLabel());
        entity.setHasTuan(palace.isHasTuan());
        entity.setHasTriet(palace.isHasTriet());
        entity.setIsThanCu(palace.isThanCu());
        return entity;
    }

    /**
     * Convert StarInfo DTO to NatalStarEntity.
     */
    private NatalStarEntity convertStarToEntity(StarInfo star, NatalPalaceEntity palaceEntity, int order) {
        NatalStarEntity entity = new NatalStarEntity();
        entity.setNatalPalace(palaceEntity);
        entity.setStarCode(star.getCode());
        entity.setStarType(star.getType());
        entity.setStarNguHanh(star.getNguHanh());
        
        // Handle brightness - if null, use default "BINH" (Average)
        // This can happen for stars that don't have brightness tables (e.g., some auxiliary stars)
        String brightness = star.getBrightness();
        if (brightness == null || brightness.isBlank()) {
            log.debug("Star {} has null brightness, using default BINH", star.getCode());
            brightness = "BINH";
        }
        entity.setBrightness(brightness);
        
        // Set brightness code - if null, derive from brightness
        String brightnessCode = star.getBrightnessCode();
        if (brightnessCode == null || brightnessCode.isBlank()) {
            brightnessCode = getBrightnessCodeFromBrightness(brightness);
        }
        entity.setBrightnessCode(brightnessCode);
        
        entity.setIsPositive(star.getIsPositive());
        entity.setStarOrder(order);
        return entity;
    }
    
    /**
     * Get brightness code from brightness name.
     */
    private String getBrightnessCodeFromBrightness(String brightness) {
        if (brightness == null) return "B";
        return switch (brightness.toUpperCase()) {
            case "MIEU" -> "M";
            case "VUONG" -> "V";
            case "DAC" -> "Đ";
            case "BINH" -> "B";
            case "HAM" -> "H";
            default -> "B"; // Default to Bình
        };
    }

    /**
     * Get star type order for sorting.
     */
    private int getStarTypeOrder(String type) {
        if (type == null) return 99;
        return switch (type) {
            case "CHINH_TINH" -> 0;
            case "PHU_TINH" -> 1;
            case "BANG_TINH" -> 2;
            case "TRUONG_SINH" -> 3;
            default -> 99;
        };
    }

    /**
     * Extract star code from star name.
     */
    private String extractStarCode(String starName) {
        if (starName == null || starName.isBlank()) {
            return null;
        }
        for (com.duong.lichvanien.tuvi.enums.Star star : com.duong.lichvanien.tuvi.enums.Star.values()) {
            if (star.getText().equals(starName)) {
                return star.name();
            }
        }
        log.warn("Could not find star code for name: {}", starName);
        return starName;
    }

    /**
     * Extract palace code from palace name.
     */
    private String extractPalaceCode(String palaceName) {
        if (palaceName == null || palaceName.isBlank()) {
            return null;
        }
        for (com.duong.lichvanien.tuvi.enums.CungName cung : com.duong.lichvanien.tuvi.enums.CungName.values()) {
            if (cung.getText().equals(palaceName)) {
                return cung.name();
            }
        }
        log.warn("Could not find palace code for name: {}", palaceName);
        return palaceName;
    }
    
    /**
     * Find Chính tinh star code in a palace by palace code.
     * Returns the first Chính tinh star code found, or null if none exists.
     * Helper method for fallback logic when Cung Mệnh has no Chính tinh.
     */
    private String findChinhTinhInPalaceByCode(List<PalaceInfo> palaces, String palaceCode) {
        if (palaceCode == null || palaceCode.isBlank()) {
            return null;
        }
        
        for (PalaceInfo palace : palaces) {
            if (palaceCode.equals(palace.getNameCode())) {
                if (palace.getStars() == null || palace.getStars().isEmpty()) {
                    return null;
                }
                
                // Find first Chính tinh
                for (StarInfo star : palace.getStars()) {
                    if ("CHINH_TINH".equals(star.getType())) {
                        log.debug("Found Chính tinh {} in palace {} for fallback", star.getCode(), palaceCode);
                        return star.getCode();
                    }
                }
                return null;
            }
        }
        return null;
    }
}
