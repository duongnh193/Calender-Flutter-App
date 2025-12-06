package com.duong.lichvanien.calendar.goodday;

import com.duong.lichvanien.calendar.entity.GoodDayType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GoodDayRuleService {

    private static final Map<String, String> BRANCH_NAME_TO_CODE = Map.ofEntries(
            Map.entry("TÝ", "ti"),
            Map.entry("SỬU", "suu"),
            Map.entry("DẦN", "dan"),
            Map.entry("MÃO", "mao"),
            Map.entry("MEO", "mao"),
            Map.entry("THÌN", "thin"),
            Map.entry("TỴ", "ty"),
            Map.entry("TY", "ty"),
            Map.entry("NGỌ", "ngo"),
            Map.entry("MÙI", "mui"),
            Map.entry("THÂN", "than"),
            Map.entry("DẬU", "dau"),
            Map.entry("TUẤT", "tuat"),
            Map.entry("HỢI", "hoi")
    );

    private final GoodDayRuleRepository repository;

    public GoodDayType resolve(int lunarMonth, String dayCanChi) {
        String branchName = extractBranch(dayCanChi);
        String code = toBranchCode(branchName);
        if (code == null) {
            return GoodDayType.NORMAL;
        }
        return repository.findByLunarMonthAndBranchCode(lunarMonth, code)
                .map(GoodDayRuleEntity::getFortuneType)
                .orElse(GoodDayType.NORMAL);
    }

    private String extractBranch(String canChi) {
        if (canChi == null) {
            return null;
        }
        String[] parts = canChi.trim().split("\\s+");
        return parts.length == 0 ? null : parts[parts.length - 1];
    }

    private String toBranchCode(String branchName) {
        if (branchName == null || branchName.isBlank()) {
            return null;
        }
        String key = branchName.trim().toUpperCase(Locale.ROOT);
        return BRANCH_NAME_TO_CODE.get(key);
    }
}
