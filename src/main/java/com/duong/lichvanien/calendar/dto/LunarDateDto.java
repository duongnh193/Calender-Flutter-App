package com.duong.lichvanien.calendar.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LunarDateDto {
    private int day;
    private int month;
    private int year;
    private boolean leapMonth;
}
