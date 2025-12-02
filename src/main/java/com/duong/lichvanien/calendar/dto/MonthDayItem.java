package com.duong.lichvanien.calendar.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MonthDayItem {
    private String solarDate;
    private int dayOfMonth;
    private LunarDateDto lunar;
    private boolean goodDay;
    private boolean special;
}
