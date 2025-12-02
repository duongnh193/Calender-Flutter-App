package com.duong.lichvanien.calendar.util;

import com.duong.lichvanien.calendar.lunar.external.DiaChi;
import com.duong.lichvanien.calendar.lunar.external.LunarCalendarLib;
import com.duong.lichvanien.calendar.lunar.external.ThienCan;

import java.time.LocalDate;

/**
 * Wrapper around the lunar calendar implementation (MIT) for Vietnamese conversions.
 */
public final class VietnameseLunarCalendar {

    private static final double TIME_ZONE = 7.0;

    private VietnameseLunarCalendar() {
    }

    public record LunarDate(int day, int month, int year, boolean leapMonth) {
        public int getDay() {
            return day;
        }

        public int getMonth() {
            return month;
        }

        public int getYear() {
            return year;
        }

        public boolean isLeap() {
            return leapMonth;
        }
    }

    public record CanChi(String day, String month, String year) {
        public String getDay() {
            return day;
        }

        public String getMonth() {
            return month;
        }

        public String getYear() {
            return year;
        }
    }

    public static LunarDate solarToLunar(LocalDate solarDate) {
        int[] lunar = LunarCalendarLib.convertSolar2Lunar(
                solarDate.getDayOfMonth(),
                solarDate.getMonthValue(),
                solarDate.getYear(),
                TIME_ZONE
        );
        return new LunarDate(lunar[0], lunar[1], lunar[2], lunar[3] == 1);
    }

    public static LocalDate lunarToSolar(int lunarDay, int lunarMonth, int lunarYear, boolean leapMonth) {
        int[] solar = LunarCalendarLib.convertLunar2Solar(
                lunarDay,
                lunarMonth,
                lunarYear,
                leapMonth ? 1 : 0,
                TIME_ZONE
        );
        return LocalDate.of(solar[2], solar[1], solar[0]);
    }

    public static CanChi canChiFromSolar(LocalDate solarDate) {
        ThienCan tcDay = LunarCalendarLib.getThienCanNgay(solarDate);
        DiaChi dcDay = LunarCalendarLib.getDiaChiNgay(solarDate);
        ThienCan tcMonth = LunarCalendarLib.getThienCanThang(solarDate);
        DiaChi dcMonth = LunarCalendarLib.getDiaChiThang(solarDate);
        ThienCan tcYear = LunarCalendarLib.getThienCanNam(solarDate);
        DiaChi dcYear = LunarCalendarLib.getDiaChiNam(solarDate);
        return new CanChi(
                tcDay.getText() + " " + dcDay.getText(),
                tcMonth.getText() + " " + dcMonth.getText(),
                tcYear.getText() + " " + dcYear.getText()
        );
    }
}
