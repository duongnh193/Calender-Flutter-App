package com.duong.lichvanien.calendar.controller;

import com.duong.lichvanien.calendar.dto.DayInfoResponse;
import com.duong.lichvanien.calendar.dto.LunarDateDto;
import com.duong.lichvanien.calendar.dto.MonthCalendarResponse;
import com.duong.lichvanien.calendar.service.CalendarService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/calendar")
@RequiredArgsConstructor
public class CalendarController {

    private final CalendarService calendarService;

    @GetMapping("/day")
    public DayInfoResponse getDayInfo(
            @RequestParam(value = "date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        LocalDate target = date != null ? date : LocalDate.now();
        return calendarService.getDayInfo(target);
    }

    @GetMapping("/month")
    public MonthCalendarResponse getMonth(
            @RequestParam @Min(1900) @Max(2100) int year,
            @RequestParam @Min(1) @Max(12) int month) {
        return calendarService.getMonthInfo(year, month);
    }

    @GetMapping("/convert/solar-to-lunar")
    public LunarDateDto convertSolarToLunar(
            @RequestParam("date") @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return calendarService.convertSolarToLunar(date);
    }

    @GetMapping("/convert/lunar-to-solar")
    public String convertLunarToSolar(
            @RequestParam int lunarYear,
            @RequestParam int lunarMonth,
            @RequestParam int lunarDay,
            @RequestParam(defaultValue = "false") boolean leapMonth) {
        return calendarService.convertLunarToSolar(lunarYear, lunarMonth, lunarDay, leapMonth).toString();
    }
}
