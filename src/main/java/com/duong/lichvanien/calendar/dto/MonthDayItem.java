package com.duong.lichvanien.calendar.dto;

import lombok.Builder;
import lombok.Data;
import com.duong.lichvanien.calendar.entity.GoodDayType;

@Data
@Builder
public class MonthDayItem {
    private String solarDate;
    private int dayOfMonth;
    private LunarDateDto lunar;
    private GoodDayType goodDayType;
    private boolean special;
}
