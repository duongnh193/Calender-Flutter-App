package com.duong.lichvanien.calendar.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CurrentTimeInfo {
    private final String time;       // ISO local time (HH:mm:ss)
    private final String timeLabel;  // 12-hour label, e.g. 3:34 PM
    private final String canChiHour; // full label, e.g. "Bính Thân"
}
