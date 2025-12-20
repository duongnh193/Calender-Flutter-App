package com.duong.lichvanien.tuvi.service;

import com.duong.lichvanien.tuvi.dto.CenterInfo;
import com.duong.lichvanien.tuvi.dto.PalaceInfo;
import com.duong.lichvanien.tuvi.dto.StarInfo;
import com.duong.lichvanien.tuvi.entity.InterpretationFragmentEntity;
import com.duong.lichvanien.tuvi.entity.InterpretationRuleEntity;
import com.duong.lichvanien.tuvi.entity.NatalPalaceEntity;
import com.duong.lichvanien.tuvi.entity.NatalStarEntity;
import com.duong.lichvanien.tuvi.repository.InterpretationFragmentRepository;
import com.duong.lichvanien.tuvi.repository.InterpretationRuleRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for matching interpretation rules with FACT data and generating fragments.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InterpretationRuleMatchingService {

    private final InterpretationRuleRepository ruleRepository;
    private final InterpretationFragmentRepository fragmentRepository;
    private final ObjectMapper objectMapper;

    /**
     * Match rules for a palace and return matched fragments.
     *
     * @param palace Palace DTO from chart
     * @return List of matched fragments, sorted by priority (ascending, 1 = highest)
     */
    public List<InterpretationFragmentEntity> matchRulesForPalace(PalaceInfo palace) {
        log.info("Matching rules for palace: {}", palace.getNameCode());
        
        // Log palace info for debugging
        if (palace.getStars() != null) {
            log.info("Palace {} has {} stars: {}", palace.getNameCode(), palace.getStars().size(),
                    palace.getStars().stream()
                            .map(s -> s.getCode() + "(" + s.getBrightness() + ")")
                            .collect(Collectors.joining(", ")));
        } else {
            log.warn("Palace {} has no stars!", palace.getNameCode());
        }

        // Get all rules that might match this palace
        List<InterpretationRuleEntity> candidateRules = ruleRepository.findByPalaceCode(palace.getNameCode());
        log.info("Found {} candidate rules for palace {}", candidateRules.size(), palace.getNameCode());

        List<InterpretationFragmentEntity> matchedFragments = new ArrayList<>();

        for (InterpretationRuleEntity rule : candidateRules) {
            if (matchesRule(rule, palace)) {
                fragmentRepository.findByFragmentCode(rule.getFragmentCode())
                        .ifPresent(fragment -> {
                            matchedFragments.add(fragment);
                            log.debug("Matched fragment {} for rule {}", fragment.getFragmentCode(), rule.getFragmentCode());
                        });
            }
        }

        // Sort by priority (1 = highest priority)
        matchedFragments.sort(Comparator.comparing(InterpretationFragmentEntity::getPriority));

        log.info("Matched {} fragments for palace {} (from {} candidate rules)", 
                matchedFragments.size(), palace.getNameCode(), candidateRules.size());
        return matchedFragments;
    }

    /**
     * Match rules for a natal palace entity and return matched fragments.
     *
     * @param natalPalace Natal palace entity with stars
     * @return List of matched fragments, sorted by priority
     */
    public List<InterpretationFragmentEntity> matchRulesForNatalPalace(NatalPalaceEntity natalPalace) {
        log.debug("Matching rules for natal palace: {}", natalPalace.getPalaceCode());

        List<InterpretationRuleEntity> candidateRules = ruleRepository.findByPalaceCode(natalPalace.getPalaceCode());

        List<InterpretationFragmentEntity> matchedFragments = new ArrayList<>();

        for (InterpretationRuleEntity rule : candidateRules) {
            if (matchesRuleForNatalPalace(rule, natalPalace)) {
                fragmentRepository.findByFragmentCode(rule.getFragmentCode())
                        .ifPresent(matchedFragments::add);
            }
        }

        matchedFragments.sort(Comparator.comparing(InterpretationFragmentEntity::getPriority));

        log.debug("Matched {} fragments for natal palace {}", matchedFragments.size(), natalPalace.getPalaceCode());
        return matchedFragments;
    }

    /**
     * Check if a rule matches a palace DTO.
     */
    private boolean matchesRule(InterpretationRuleEntity rule, PalaceInfo palace) {
        try {
            JsonNode conditions = objectMapper.readTree(rule.getConditions());

            // Check palace code
            if (!palace.getNameCode().equals(conditions.path("palace").asText())) {
                return false;
            }

            // Check Tuần/Triệt
            if (conditions.has("has_tuan")) {
                if (conditions.path("has_tuan").asBoolean() != palace.isHasTuan()) {
                    return false;
                }
            }
            if (conditions.has("has_triet")) {
                if (conditions.path("has_triet").asBoolean() != palace.isHasTriet()) {
                    return false;
                }
            }

            // Check has_chinh_tinh (for rules matching palaces without Chính tinh)
            if (conditions.has("has_chinh_tinh")) {
                boolean requiresNoChinhTinh = !conditions.path("has_chinh_tinh").asBoolean();
                boolean palaceHasChinhTinh = palace.getStars() != null && palace.getStars().stream()
                        .anyMatch(s -> "CHINH_TINH".equals(s.getType()));
                
                if (requiresNoChinhTinh != !palaceHasChinhTinh) {
                    return false;
                }
            }

            // Check trang_sinh_state (for rules matching specific Tràng Sinh state)
            if (conditions.has("trang_sinh_state")) {
                String requiredState = conditions.path("trang_sinh_state").asText();
                // Check if palace has the required Tràng Sinh star by code
                boolean hasMatchingState = palace.getStars() != null && palace.getStars().stream()
                        .anyMatch(s -> "TRUONG_SINH".equals(s.getType()) && requiredState.equals(s.getCode()));
                
                if (!hasMatchingState) {
                    return false;
                }
            }

            // Check required stars (for combination rules)
            if (conditions.has("required_stars")) {
                Set<String> requiredStars = objectMapper.convertValue(
                        conditions.get("required_stars"),
                        new TypeReference<Set<String>>() {}
                );
                Set<String> palaceStarCodes = palace.getStars() != null
                        ? palace.getStars().stream()
                                .map(StarInfo::getCode)
                                .collect(Collectors.toSet())
                        : Collections.emptySet();

                if (!palaceStarCodes.containsAll(requiredStars)) {
                    return false;
                }
            } else if (conditions.has("stars")) {
                // Single star rule - check if both star AND brightness match the same star
                JsonNode starsArray = conditions.get("stars");
                JsonNode brightnessArray = conditions.get("brightness");
                
                if (starsArray.isArray() && starsArray.size() > 0) {
                    String requiredStar = starsArray.get(0).asText();
                    
                    // If brightness is specified, check that the SAME star has that brightness
                    if (brightnessArray != null && brightnessArray.isArray() && brightnessArray.size() > 0) {
                        String requiredBrightness = brightnessArray.get(0).asText();
                        boolean found = palace.getStars() != null
                                && palace.getStars().stream()
                                        .anyMatch(s -> {
                                            if (!requiredStar.equals(s.getCode())) {
                                                return false;
                                            }
                                            // If star brightness is null, fallback to match (assume BINH)
                                            // This allows matching when brightness hasn't been calculated yet
                                            String starBrightness = s.getBrightness();
                                            if (starBrightness == null || starBrightness.isBlank()) {
                                                log.debug("Star {} has null brightness, allowing match for rule {}", 
                                                        s.getCode(), rule.getFragmentCode());
                                                return true; // Allow match when brightness is null
                                            }
                                            return requiredBrightness.equals(starBrightness);
                                        });
                        if (!found) {
                            return false;
                        }
                    } else {
                        // No brightness requirement - just check if star exists
                        boolean found = palace.getStars() != null
                                && palace.getStars().stream()
                                        .anyMatch(s -> requiredStar.equals(s.getCode()));
                        if (!found) {
                            return false;
                        }
                    }
                }
            }

            // Check brightness separately only if stars array was not checked above
            // (This handles cases where brightness is specified without stars array)
            if (!conditions.has("stars") && conditions.has("brightness")) {
                JsonNode brightnessArray = conditions.get("brightness");
                if (brightnessArray.isArray() && brightnessArray.size() > 0) {
                    String requiredBrightness = brightnessArray.get(0).asText();
                    boolean found = palace.getStars() != null
                            && palace.getStars().stream()
                                    .anyMatch(s -> requiredBrightness.equals(s.getBrightness()));
                    if (!found) {
                        return false;
                    }
                }
            } else if (conditions.has("min_brightness")) {
                // For combination rules: check if any star has at least min_brightness
                String minBrightness = conditions.path("min_brightness").asText();
                if (palace.getStars() != null) {
                    boolean hasMinBrightness = palace.getStars().stream()
                            .anyMatch(s -> hasMinBrightness(s.getBrightness(), minBrightness));
                    if (!hasMinBrightness) {
                        return false;
                    }
                }
            }

            return true;

        } catch (Exception e) {
            log.error("Error matching rule {}: {}", rule.getFragmentCode(), e.getMessage(), e);
            return false;
        }
    }

    /**
     * Check if a rule matches a natal palace entity.
     */
    private boolean matchesRuleForNatalPalace(InterpretationRuleEntity rule, NatalPalaceEntity natalPalace) {
        try {
            JsonNode conditions = objectMapper.readTree(rule.getConditions());

            // Check palace code
            if (!natalPalace.getPalaceCode().equals(conditions.path("palace").asText())) {
                return false;
            }

            // Check Tuần/Triệt
            if (conditions.has("has_tuan")) {
                if (conditions.path("has_tuan").asBoolean() != natalPalace.getHasTuan()) {
                    return false;
                }
            }
            if (conditions.has("has_triet")) {
                if (conditions.path("has_triet").asBoolean() != natalPalace.getHasTriet()) {
                    return false;
                }
            }

            // Get stars from relationship (lazy loading - need to initialize if not loaded)
            List<NatalStarEntity> stars = new ArrayList<>();
            if (natalPalace.getStars() != null) {
                stars.addAll(natalPalace.getStars()); // This will trigger lazy loading if needed
            }

            Set<String> starCodes = stars.stream()
                    .map(NatalStarEntity::getStarCode)
                    .collect(Collectors.toSet());

            // Check required stars (for combination rules)
            if (conditions.has("required_stars")) {
                Set<String> requiredStars = objectMapper.convertValue(
                        conditions.get("required_stars"),
                        new TypeReference<Set<String>>() {}
                );
                if (!starCodes.containsAll(requiredStars)) {
                    return false;
                }
            } else if (conditions.has("stars")) {
                // Single star rule
                JsonNode starsArray = conditions.get("stars");
                if (starsArray.isArray() && starsArray.size() > 0) {
                    String requiredStar = starsArray.get(0).asText();
                    if (!starCodes.contains(requiredStar)) {
                        return false;
                    }
                }
            }

            // Check brightness
            if (conditions.has("brightness")) {
                JsonNode brightnessArray = conditions.get("brightness");
                if (brightnessArray.isArray() && brightnessArray.size() > 0) {
                    String requiredBrightness = brightnessArray.get(0).asText();
                    boolean found = stars.stream()
                            .anyMatch(s -> requiredBrightness.equals(s.getBrightness()));
                    if (!found) {
                        return false;
                    }
                }
            } else if (conditions.has("min_brightness")) {
                String minBrightness = conditions.path("min_brightness").asText();
                boolean hasMinBrightness = stars.stream()
                        .anyMatch(s -> hasMinBrightness(s.getBrightness(), minBrightness));
                if (!hasMinBrightness) {
                    return false;
                }
            }

            return true;

        } catch (Exception e) {
            log.error("Error matching rule {} for natal palace: {}", rule.getFragmentCode(), e.getMessage(), e);
            return false;
        }
    }

    /**
     * Check if brightness meets minimum requirement.
     * Brightness order: MIEU > VUONG > DAC > BINH > HAM
     */
    private boolean hasMinBrightness(String starBrightness, String minBrightness) {
        if (starBrightness == null) return false;

        Map<String, Integer> brightnessOrder = Map.of(
                "MIEU", 5,
                "VUONG", 4,
                "DAC", 3,
                "BINH", 2,
                "HAM", 1
        );

        Integer starLevel = brightnessOrder.getOrDefault(starBrightness, 0);
        Integer minLevel = brightnessOrder.getOrDefault(minBrightness, 0);

        return starLevel >= minLevel;
    }

    /**
     * Match overview-level rules based on CenterInfo.
     * Overview rules match against center-level fields (banMenhNguHanh, cucNguHanh, thuanNghich, thanCu, etc.)
     *
     * @param center CenterInfo from chart
     * @param thanMenhDongCung Whether Thân and Mệnh are in the same palace
     * @return List of matched overview fragments
     */
    public List<InterpretationFragmentEntity> matchOverviewRules(CenterInfo center, boolean thanMenhDongCung) {
        log.debug("Matching overview rules for banMenhNguHanh: {}, cucNguHanh: {}", 
                center.getBanMenhNguHanh(), center.getCucNguHanh());
        
        List<InterpretationFragmentEntity> fragments = new ArrayList<>();
        
        // Match Bản mệnh fragments
        String banMenhNguHanh = center.getBanMenhNguHanh();
        String cucNguHanh = center.getCucNguHanh();
        if (banMenhNguHanh != null) {
            // Try combination first (Bản mệnh + Cục)
            if (cucNguHanh != null) {
                String comboCode = "BAN_MENH_" + banMenhNguHanh + "_MENH_" + cucNguHanh;
                fragmentRepository.findByFragmentCode(comboCode)
                        .ifPresent(fragments::add);
            }
            
            // Fallback to base if combination not found
            if (fragments.stream().noneMatch(f -> f.getFragmentCode().startsWith("BAN_MENH_" + banMenhNguHanh))) {
                String baseCode = "BAN_MENH_" + banMenhNguHanh;
                fragmentRepository.findByFragmentCode(baseCode)
                        .ifPresent(fragments::add);
            }
        }
        
        // Match Cục fragments
        if (cucNguHanh != null) {
            // Map cucNguHanh to fragment code
            // Fragment codes: CUC_KIM_NGU, CUC_MOC_NGU, CUC_THUY_NHI, CUC_HOA_LUC, CUC_THO_NGU
            String cucCode = switch (cucNguHanh) {
                case "KIM" -> "CUC_KIM_NGU";
                case "MOC" -> "CUC_MOC_NGU";
                case "THUY" -> "CUC_THUY_NHI";
                case "HOA" -> "CUC_HOA_LUC";
                case "THO" -> "CUC_THO_NGU";
                default -> null;
            };
            if (cucCode != null) {
                fragmentRepository.findByFragmentCode(cucCode)
                        .ifPresent(fragments::add);
            }
        }
        
        // Match Thuận/Nghịch fragments
        String thuanNghich = center.getThuanNghich();
        if (thuanNghich != null) {
            String gender = center.getGender();
            String amDuong = center.getAmDuong();
            
            // Try specific match first (gender + amDuong specific)
            if (thuanNghich.contains("Thuận lý")) {
                if ("male".equals(gender) && "Dương".equals(amDuong)) {
                    fragmentRepository.findByFragmentCode("THUAN_LY_DUONG_NAM").ifPresent(fragments::add);
                } else if ("female".equals(gender) && "Âm".equals(amDuong)) {
                    fragmentRepository.findByFragmentCode("THUAN_LY_AM_NU").ifPresent(fragments::add);
                } else {
                    fragmentRepository.findByFragmentCode("THUAN_LY").ifPresent(fragments::add);
                }
            } else if (thuanNghich.contains("Nghịch lý")) {
                if ("male".equals(gender) && "Âm".equals(amDuong)) {
                    fragmentRepository.findByFragmentCode("NGHICH_LY_AM_NAM").ifPresent(fragments::add);
                } else if ("female".equals(gender) && "Dương".equals(amDuong)) {
                    fragmentRepository.findByFragmentCode("NGHICH_LY_DUONG_NU").ifPresent(fragments::add);
                } else {
                    fragmentRepository.findByFragmentCode("NGHICH_LY").ifPresent(fragments::add);
                }
            }
        }
        
        // Match Thân cư fragments (chỉ khi không có Thân Mệnh đồng cung)
        if (!thanMenhDongCung) {
            String thanCu = center.getThanCu();
            if (thanCu != null) {
                String thanCuCode = convertPalaceNameToCode(thanCu);
                if (thanCuCode != null) {
                    String fragmentCode = "THAN_CU_" + thanCuCode;
                    fragmentRepository.findByFragmentCode(fragmentCode).ifPresent(fragments::add);
                }
            }
        }
        
        // Thân Mệnh đồng cung (priority cao nhất - nếu có thì dùng fragment này thay vì THAN_CU_MENH)
        if (thanMenhDongCung) {
            fragmentRepository.findByFragmentCode("THAN_MENH_DONG_CUNG").ifPresent(fragments::add);
        }
        
        // Sort by priority (1 = highest priority)
        fragments.sort(Comparator.comparing(InterpretationFragmentEntity::getPriority));
        
        log.debug("Matched {} overview fragments", fragments.size());
        return fragments;
    }
    
    /**
     * Convert palace Vietnamese name to code (e.g., "Quan Lộc" -> "QUAN_LOC").
     */
    private String convertPalaceNameToCode(String palaceName) {
        if (palaceName == null || palaceName.isBlank()) {
            return null;
        }
        // Map Vietnamese names to codes (using HashMap because Map.of() only supports up to 10 pairs)
        Map<String, String> nameToCode = new HashMap<>();
        nameToCode.put("Mệnh", "MENH");
        nameToCode.put("Quan Lộc", "QUAN_LOC");
        nameToCode.put("Tài Bạch", "TAI_BACH");
        nameToCode.put("Phu Thê", "PHU_THE");
        nameToCode.put("Tật Ách", "TAT_ACH");
        nameToCode.put("Tử Tức", "TU_TUC");
        nameToCode.put("Điền Trạch", "DIEN_TRACH");
        nameToCode.put("Phụ Mẫu", "PHU_MAU");
        nameToCode.put("Huynh Đệ", "HUYNH_DE");
        nameToCode.put("Phúc Đức", "PHUC_DUC");
        nameToCode.put("Nô Bộc", "NO_BOC");
        nameToCode.put("Thiên Di", "THIEN_DI");
        return nameToCode.get(palaceName);
    }
}
