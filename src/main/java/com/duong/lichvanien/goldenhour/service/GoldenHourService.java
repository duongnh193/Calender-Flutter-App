package com.duong.lichvanien.goldenhour.service;

import com.duong.lichvanien.common.exception.NotFoundException;
import com.duong.lichvanien.goldenhour.dto.GoldenHourResponse;
import com.duong.lichvanien.goldenhour.entity.GoldenHourPatternEntity;
import com.duong.lichvanien.goldenhour.entity.ZodiacHourEntity;
import com.duong.lichvanien.goldenhour.repository.GoldenHourPatternRepository;
import com.duong.lichvanien.goldenhour.repository.ZodiacHourRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GoldenHourService {

    private final GoldenHourPatternRepository patternRepository;
    private final ZodiacHourRepository zodiacHourRepository;

    public List<GoldenHourResponse> getGoldenHours(String canChiDay) {
        if (canChiDay == null || canChiDay.isBlank()) {
            return List.of();
        }
        String[] parts = canChiDay.trim().split("\\s+");
        String branchRaw = parts.length > 1 ? parts[1] : parts[0];
        String branchCode = normalizeBranchCode(branchRaw);

        GoldenHourPatternEntity pattern = patternRepository.findByDayBranchCode(branchCode)
                .orElseThrow(() -> new NotFoundException("GOLDEN_HOUR_PATTERN_NOT_FOUND", "No golden hour pattern for branch " + branchCode));

        List<String> branches = Arrays.stream(pattern.getGoodBranchCodes().split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();

        Map<String, ZodiacHourEntity> hours = zodiacHourRepository.findByBranchCodeIn(branches)
                .stream()
                .collect(Collectors.toMap(ZodiacHourEntity::getBranchCode, it -> it));

        return branches.stream()
                .map(b -> {
                    ZodiacHourEntity hour = hours.get(b);
                    if (hour == null) {
                        throw new NotFoundException("ZODIAC_HOUR_NOT_FOUND", "No zodiac hour for branch " + b);
                    }
                    return GoldenHourResponse.builder()
                            .branch(b)
                            .startHour(hour.getStartHour())
                            .endHour(hour.getEndHour())
                            .label(hour.getStartHour() + "-" + hour.getEndHour() + "h")
                            .build();
                })
                .toList();
    }

    private String normalizeBranchCode(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new NotFoundException("INVALID_BRANCH", "Invalid branch from can chi day");
        }
        String lower = raw.toLowerCase(Locale.ROOT).trim();
        return switch (lower) {
            case "tý", "ti" -> "ti";
            case "sửu", "suu" -> "suu";
            case "dần", "dan" -> "dan";
            case "mão", "mao" -> "mao";
            case "thìn", "thin" -> "thin";
            case "tỵ", "ty" -> "ty";
            case "ngọ", "ngo" -> "ngo";
            case "mùi", "mui" -> "mui";
            case "thân", "than" -> "than";
            case "dậu", "dau" -> "dau";
            case "tuất", "tuat" -> "tuat";
            case "hợi", "hoi" -> "hoi";
            default -> lower;
        };
    }
}
