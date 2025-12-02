package com.duong.lichvanien.horoscope.controller;

import com.duong.lichvanien.horoscope.dto.HoroscopeDailyResponse;
import com.duong.lichvanien.horoscope.dto.HoroscopeYearlyResponse;
import com.duong.lichvanien.horoscope.service.HoroscopeService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/horoscopes")
@RequiredArgsConstructor
public class HoroscopeController {

    private final HoroscopeService horoscopeService;

    @GetMapping("/yearly")
    public HoroscopeYearlyResponse getYearly(
            @RequestParam String zodiacCode,
            @RequestParam @Min(1900) @Max(2100) int year) {
        return horoscopeService.getYearly(zodiacCode, year);
    }

    @GetMapping("/daily")
    public HoroscopeDailyResponse getDaily(
            @RequestParam String zodiacCode,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return horoscopeService.getDaily(zodiacCode, date);
    }
}
