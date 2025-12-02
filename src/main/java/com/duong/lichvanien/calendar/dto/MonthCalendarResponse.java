package com.duong.lichvanien.calendar.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MonthCalendarResponse {
    private int year;
    private int month;
    private List<MonthDayItem> days;
}
