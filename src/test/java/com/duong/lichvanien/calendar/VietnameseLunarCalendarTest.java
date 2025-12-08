package com.duong.lichvanien.calendar;

import com.duong.lichvanien.calendar.service.CurrentTimeInfoService;
import com.duong.lichvanien.calendar.util.VietnameseLunarCalendar;
import com.duong.lichvanien.calendar.util.VietnameseLunarCalendar.CanChi;
import com.duong.lichvanien.calendar.util.VietnameseLunarCalendar.LunarDate;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class VietnameseLunarCalendarTest {

    @Test
    void solar_2025_12_02_should_be_13_10_leap() {
        LocalDate solar = LocalDate.of(2025, 12, 2);
        LunarDate lunar = VietnameseLunarCalendar.solarToLunar(solar);
        CanChi canChi = VietnameseLunarCalendar.canChiFromSolar(solar);

        assertThat(lunar.getDay()).isEqualTo(13);
        assertThat(lunar.getMonth()).isEqualTo(10);
        assertThat(lunar.getYear()).isEqualTo(2025);
        assertThat(lunar.isLeap()).isTrue();
        assertThat(canChi.getDay()).isEqualTo("Ất Tỵ");
        assertThat(canChi.getMonth()).isEqualTo("Đinh Hợi");
        assertThat(canChi.getYear()).isEqualTo("Ất Tỵ");
    }

    @Test
    void lunar_new_year_2025_should_be_2025_01_29() {
        LocalDate solar = VietnameseLunarCalendar.lunarToSolar(1, 1, 2025, false);
        assertThat(solar).isEqualTo(LocalDate.of(2025, 1, 29));
    }

    @Test
    void mid_autumn_2024_lunar_15_8_should_be_2024_09_17() {
        LocalDate solar = VietnameseLunarCalendar.lunarToSolar(15, 8, 2024, false);
        assertThat(solar).isEqualTo(LocalDate.of(2024, 9, 17));
    }

    @Test
    void round_trip_on_leap_year_day_should_match() {
        LocalDate solar = LocalDate.of(2025, 7, 10);
        LunarDate lunar = VietnameseLunarCalendar.solarToLunar(solar);
        LocalDate back = VietnameseLunarCalendar.lunarToSolar(lunar.getDay(), lunar.getMonth(), lunar.getYear(), lunar.isLeap());
        assertThat(back).isEqualTo(solar);
    }

    @Test
    void branch_mapping_should_follow_ranges() {
        CurrentTimeInfoService svc = new CurrentTimeInfoService();
        assertThat(svc.branchForHour(java.time.LocalTime.of(23, 30))).isEqualTo("Tý");
        assertThat(svc.branchForHour(java.time.LocalTime.of(1, 0))).isEqualTo("Sửu");
        assertThat(svc.branchForHour(java.time.LocalTime.of(15, 0))).isEqualTo("Thân");
    }

    @Test
    void stem_for_hour_should_match_known_cases() {
        CurrentTimeInfoService svc = new CurrentTimeInfoService();
        // Day stem Tân -> group (Bính/Tân) starting stem for Tý = Mậu
        String stem = svc.stemForHour("Tân Hợi", "Thân");
        assertThat(stem).isEqualTo("Bính");
        // Day stem Ất -> starting stem Bính, Tý => Bính, Sửu => Đinh
        assertThat(svc.stemForHour("Ất Tỵ", "Sửu")).isEqualTo("Đinh");
    }
}
