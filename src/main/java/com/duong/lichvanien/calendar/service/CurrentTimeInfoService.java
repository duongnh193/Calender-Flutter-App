package com.duong.lichvanien.calendar.service;

import com.duong.lichvanien.calendar.dto.CurrentTimeInfo;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Locale;

@Service
public class CurrentTimeInfoService {

    private static final ZoneId HCMC_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");
    private static final DateTimeFormatter ISO_TIME = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter LABEL_TIME =
            DateTimeFormatter.ofPattern("h:mm a", Locale.US);

    private static final List<String> STEMS = List.of(
            "Giáp", "Ất", "Bính", "Đinh", "Mậu",
            "Kỷ", "Canh", "Tân", "Nhâm", "Quý"
    );
    private static final List<String> BRANCHES = List.of(
            "Tý", "Sửu", "Dần", "Mão", "Thìn", "Tỵ",
            "Ngọ", "Mùi", "Thân", "Dậu", "Tuất", "Hợi"
    );

    /**
     * Compute current time info (VN timezone) and can-chi hour for the given target date.
     */
    public CurrentTimeInfo getCurrentTimeInfo(LocalDate targetDate, String canChiDayLabel) {
        ZonedDateTime now = ZonedDateTime.now(HCMC_ZONE);
        LocalTime localTime = now.toLocalTime().withNano(0);
        String hourBranch = branchForHour(localTime);
        String hourStem = stemForHour(canChiDayLabel, hourBranch);
        String canChiHour = String.format("%s %s", hourStem, hourBranch);
        return CurrentTimeInfo.builder()
                .time(localTime.format(ISO_TIME))
                .timeLabel(localTime.format(LABEL_TIME))
                .canChiHour(canChiHour)
                .build();
    }

    public String branchForHour(LocalTime time) {
        int hour = time.getHour();
        // Map hour to branch index
        int index;
        if (hour == 23) {
            index = 0; // Tý
        } else {
            index = ((hour + 1) / 2) % 12;
        }
        return BRANCHES.get(index);
    }

    public String stemForHour(String canChiDay, String hourBranch) {
        String dayStem = extractStem(canChiDay);
        int dayStemIndex = STEMS.indexOf(dayStem);
        if (dayStemIndex < 0) {
            return "";
        }
        int hourBranchIndex = BRANCHES.indexOf(hourBranch);
        // Starting stem for Tý hour based on day stem group
        int group = dayStemIndex % 5;
        int startStemIndex;
        switch (group) {
            case 0: // Giáp, Kỷ
                startStemIndex = 0; // Giáp
                break;
            case 1: // Ất, Canh
                startStemIndex = 2; // Bính
                break;
            case 2: // Bính, Tân
                startStemIndex = 4; // Mậu
                break;
            case 3: // Đinh, Nhâm
                startStemIndex = 6; // Canh
                break;
            case 4: // Mậu, Quý
                startStemIndex = 8; // Nhâm
                break;
            default:
                startStemIndex = 0;
        }
        int stemIndex = (startStemIndex + hourBranchIndex) % 10;
        return STEMS.get(stemIndex);
    }

    private String extractStem(String canChiDay) {
        if (canChiDay == null || canChiDay.isBlank()) {
            return "";
        }
        String[] parts = canChiDay.trim().split("\\s+");
        return parts.length > 0 ? parts[0] : "";
    }
}
