package com.duong.lichvanien.tuvi.calculator;

import com.duong.lichvanien.tuvi.enums.DiaChi;
import com.duong.lichvanien.tuvi.enums.ThienCan;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Calculator for lunar dates and Can-Chi conversions.
 * Based on Ho Ngoc Duc's algorithm for Vietnamese lunar calendar.
 */
public class LunarDateCalculator {

    private static final double TIME_ZONE = 7.0; // Asia/Ho_Chi_Minh

    /**
     * Result of lunar date conversion.
     */
    @Data
    @Builder
    public static class LunarDate {
        private int day;
        private int month;
        private int year;
        private boolean leapMonth;
        
        // Can-Chi components
        private ThienCan canYear;
        private DiaChi chiYear;
        private ThienCan canMonth;
        private DiaChi chiMonth;
        private ThienCan canDay;
        private DiaChi chiDay;
        private ThienCan canHour;
        private DiaChi chiHour;
        private int hourBranchIndex;
        
        public String getCanChiYear() {
            return canYear.getText() + " " + chiYear.getText();
        }
        
        public String getCanChiMonth() {
            return canMonth.getText() + " " + chiMonth.getText();
        }
        
        public String getCanChiDay() {
            return canDay.getText() + " " + chiDay.getText();
        }
        
        public String getCanChiHour() {
            return canHour.getText() + " " + chiHour.getText();
        }
    }

    /**
     * Convert solar date to lunar date with full Can-Chi information.
     */
    public static LunarDate convertToLunar(LocalDateTime solarDateTime) {
        return convertToLunar(
            solarDateTime.getYear(),
            solarDateTime.getMonthValue(),
            solarDateTime.getDayOfMonth(),
            solarDateTime.getHour(),
            solarDateTime.getMinute()
        );
    }

    /**
     * Convert solar date to lunar date with full Can-Chi information.
     */
    public static LunarDate convertToLunar(int solarYear, int solarMonth, int solarDay, int hour, int minute) {
        // Convert to lunar date
        int[] lunar = convertSolar2Lunar(solarDay, solarMonth, solarYear, TIME_ZONE);
        int lunarDay = lunar[0];
        int lunarMonth = lunar[1];
        int lunarYear = lunar[2];
        boolean isLeapMonth = lunar[3] == 1;

        // Calculate Julian Day Number for Can-Chi day
        int jdn = jdFromDate(solarDay, solarMonth, solarYear);

        // Calculate hour branch (12 branches, 2-hour periods)
        // 23:00-00:59 = Tý (0), 01:00-02:59 = Sửu (1), etc.
        int hourBranchIndex = getHourBranchIndex(hour);

        // Can-Chi Year (based on lunar year)
        ThienCan canYear = ThienCan.fromIndex((lunarYear + 6) % 10);
        DiaChi chiYear = DiaChi.fromIndex((lunarYear + 8) % 12);

        // Can-Chi Month
        ThienCan canMonth = calculateCanMonth(lunarYear, lunarMonth);
        DiaChi chiMonth = DiaChi.fromIndex((lunarMonth + 1) % 12);

        // Can-Chi Day
        ThienCan canDay = ThienCan.fromIndex((jdn + 9) % 10);
        DiaChi chiDay = DiaChi.fromIndex((jdn + 1) % 12);

        // Can-Chi Hour
        ThienCan canHour = calculateCanHour(canDay, hourBranchIndex);
        DiaChi chiHour = DiaChi.fromIndex(hourBranchIndex);

        return LunarDate.builder()
                .day(lunarDay)
                .month(lunarMonth)
                .year(lunarYear)
                .leapMonth(isLeapMonth)
                .canYear(canYear)
                .chiYear(chiYear)
                .canMonth(canMonth)
                .chiMonth(chiMonth)
                .canDay(canDay)
                .chiDay(chiDay)
                .canHour(canHour)
                .chiHour(chiHour)
                .hourBranchIndex(hourBranchIndex)
                .build();
    }

    /**
     * Convert lunar date back to solar date.
     */
    public static LocalDate convertToSolar(int lunarDay, int lunarMonth, int lunarYear, boolean isLeapMonth) {
        int[] solar = convertLunar2Solar(lunarDay, lunarMonth, lunarYear, isLeapMonth ? 1 : 0, TIME_ZONE);
        return LocalDate.of(solar[2], solar[1], solar[0]);
    }

    /**
     * Get hour branch index (0-11) from hour (0-23).
     * Tý: 23:00-00:59 (index 0)
     * Sửu: 01:00-02:59 (index 1)
     * Dần: 03:00-04:59 (index 2)
     * etc.
     */
    public static int getHourBranchIndex(int hour) {
        // Special case: 23:00-00:59 is Tý (index 0)
        if (hour == 23) {
            return 0;
        }
        // Other hours: floor((hour + 1) / 2) % 12
        return ((hour + 1) / 2) % 12;
    }

    /**
     * Calculate Can of month based on lunar year and month.
     * Formula: (Year * 12 + Month + 3) % 10
     */
    private static ThienCan calculateCanMonth(int lunarYear, int lunarMonth) {
        return ThienCan.fromIndex((lunarYear * 12 + lunarMonth + 3) % 10);
    }

    /**
     * Calculate Can of hour based on Can of day and hour branch.
     * Using the formula based on day Can.
     */
    private static ThienCan calculateCanHour(ThienCan canDay, int hourBranchIndex) {
        // Ngũ thử nguyên độn - formula to find hour Can from day Can
        int dayIndex = canDay.getIndex();
        // Base formula: (dayIndex % 5) * 2 + hourBranchIndex
        int baseIndex = (dayIndex % 5) * 2;
        return ThienCan.fromIndex(baseIndex + hourBranchIndex);
    }

    // ===== Solar-Lunar Conversion (Ho Ngoc Duc algorithm) =====

    private static int INT(double d) {
        return (int) Math.floor(d);
    }

    private static int jdFromDate(int dd, int mm, int yy) {
        int a = (14 - mm) / 12;
        int y = yy + 4800 - a;
        int m = mm + 12 * a - 3;
        return dd + INT((153 * m + 2) / 5.0) + 365 * y + INT(y / 4.0) - INT(y / 100.0) + INT(y / 400.0) - 32045;
    }

    private static int[] jdToDate(int jd) {
        int a = jd + 32044;
        int b = INT((4 * a + 3) / 146097.0);
        int c = a - INT(146097 * b / 4.0);
        int d = INT((4 * c + 3) / 1461.0);
        int e = c - INT(1461 * d / 4.0);
        int m = INT((5 * e + 2) / 153.0);
        int day = e - INT((153 * m + 2) / 5.0) + 1;
        int month = m + 3 - 12 * INT(m / 10.0);
        int year = 100 * b + d - 4800 + INT(m / 10.0);
        return new int[]{day, month, year};
    }

    private static double sunLongitude(int jdn, double timeZone) {
        double T = (jdn - 2451545.5 - timeZone / 24) / 36525;
        double T2 = T * T;
        double dr = Math.PI / 180;
        double M = 357.52910 + 35999.05030 * T - 0.0001559 * T2 - 0.00000048 * T * T2;
        double L0 = 280.46645 + 36000.76983 * T + 0.0003032 * T2;
        double DL = (1.914600 - 0.004817 * T - 0.000014 * T2) * Math.sin(dr * M)
                + (0.019993 - 0.000101 * T) * Math.sin(2 * dr * M)
                + 0.000290 * Math.sin(3 * dr * M);
        double L = L0 + DL;
        L = L * dr;
        L = L - Math.PI * 2 * INT(L / (Math.PI * 2));
        return L;
    }

    private static int getSunLongitudeSegment(int jdn, double timeZone) {
        return INT(sunLongitude(jdn, timeZone) / Math.PI * 6);
    }

    private static int newMoonDay(int k, double timeZone) {
        double T = k / 1236.85;
        double T2 = T * T;
        double T3 = T2 * T;
        double dr = Math.PI / 180;
        double Jd1 = 2415020.75933 + 29.53058868 * k + 0.0001178 * T2 - 0.000000155 * T3;
        Jd1 += 0.00033 * Math.sin((166.56 + 132.87 * T - 0.009173 * T2) * dr);
        double M = 359.2242 + 29.10535608 * k - 0.0000333 * T2 - 0.00000347 * T3;
        double Mpr = 306.0253 + 385.81691806 * k + 0.0107306 * T2 + 0.00001236 * T3;
        double F = 21.2964 + 390.67050646 * k - 0.0016528 * T2 - 0.00000239 * T3;
        double C1 = (0.1734 - 0.000393 * T) * Math.sin(M * dr)
                + 0.0021 * Math.sin(2 * dr * M)
                - 0.4068 * Math.sin(Mpr * dr)
                + 0.0161 * Math.sin(2 * dr * Mpr)
                - 0.0004 * Math.sin(3 * dr * Mpr)
                + 0.0104 * Math.sin(2 * dr * F)
                - 0.0051 * Math.sin((M + Mpr) * dr)
                - 0.0074 * Math.sin((M - Mpr) * dr)
                + 0.0004 * Math.sin((2 * F + M) * dr)
                - 0.0004 * Math.sin((2 * F - M) * dr)
                - 0.0006 * Math.sin((2 * F + Mpr) * dr)
                + 0.0010 * Math.sin((2 * F - Mpr) * dr)
                + 0.0005 * Math.sin((2 * Mpr + M) * dr);
        double deltaT;
        if (T < -11) {
            deltaT = 0.001 + 0.000839 * T + 0.0002261 * T2 - 0.00000845 * T3 - 0.000000081 * T * T3;
        } else {
            deltaT = -0.000278 + 0.000265 * T + 0.000262 * T2;
        }
        double JdNew = Jd1 + C1 - deltaT;
        return INT(JdNew + 0.5 + timeZone / 24);
    }

    private static int lunarMonth11(int year, double timeZone) {
        int off = jdFromDate(31, 12, year) - 2415021;
        int k = INT(off / 29.530588853);
        int nm = newMoonDay(k, timeZone);
        int sunLong = getSunLongitudeSegment(nm, timeZone);
        if (sunLong >= 9) {
            nm = newMoonDay(k - 1, timeZone);
        }
        return nm;
    }

    private static int leapMonthOffset(int a11, double timeZone) {
        int k = INT(0.5 + (a11 - 2415021.076998695) / 29.530588853);
        int last = getSunLongitudeSegment(newMoonDay(k, timeZone), timeZone);
        int i = 1;
        int arc = getSunLongitudeSegment(newMoonDay(k + i, timeZone), timeZone);
        while (arc != last && i < 14) {
            last = arc;
            i++;
            arc = getSunLongitudeSegment(newMoonDay(k + i, timeZone), timeZone);
        }
        return i - 1;
    }

    /**
     * Convert Gregorian date to lunar date.
     * @return int[]{lunarDay, lunarMonth, lunarYear, leapFlag(0/1)}
     */
    public static int[] convertSolar2Lunar(int dd, int mm, int yy, double timeZone) {
        int dayNumber = jdFromDate(dd, mm, yy);
        int k = INT((dayNumber - 2415021.076998695) / 29.530588853);
        int monthStart = newMoonDay(k + 1, timeZone);
        if (monthStart > dayNumber) {
            monthStart = newMoonDay(k, timeZone);
        }

        int a11 = lunarMonth11(yy, timeZone);
        int b11 = a11;
        int lunarYear;
        if (a11 >= monthStart) {
            lunarYear = yy;
            a11 = lunarMonth11(yy - 1, timeZone);
        } else {
            lunarYear = yy + 1;
            b11 = lunarMonth11(yy + 1, timeZone);
        }

        int lunarDay = dayNumber - monthStart + 1;
        int diff = INT((monthStart - a11) / 29.0);
        int lunarMonth = diff + 11;
        int lunarLeap = 0;
        int leapDiff = -1;
        if (b11 - a11 > 365) {
            leapDiff = leapMonthOffset(a11, timeZone);
        }
        if (leapDiff >= 0 && diff >= leapDiff) {
            lunarMonth = diff + 10;
            if (diff == leapDiff) {
                lunarLeap = 1;
            }
        }
        if (lunarMonth > 12) {
            lunarMonth -= 12;
        }
        if (lunarMonth >= 11 && diff < 4) {
            lunarYear -= 1;
        }
        return new int[]{lunarDay, lunarMonth, lunarYear, lunarLeap};
    }

    /**
     * Convert lunar date to Gregorian date.
     * @param lunarLeap 1 if leap lunar month, otherwise 0.
     * @return int[]{dd, mm, yy}
     */
    public static int[] convertLunar2Solar(int lunarDay, int lunarMonth, int lunarYear, int lunarLeap, double timeZone) {
        int a11;
        int b11;
        if (lunarMonth < 11) {
            a11 = lunarMonth11(lunarYear - 1, timeZone);
            b11 = lunarMonth11(lunarYear, timeZone);
        } else {
            a11 = lunarMonth11(lunarYear, timeZone);
            b11 = lunarMonth11(lunarYear + 1, timeZone);
        }
        int k = INT(0.5 + (a11 - 2415021.076998695) / 29.530588853);
        int off = lunarMonth - 11;
        if (off < 0) {
            off += 12;
        }
        int leapOff = -1;
        if (b11 - a11 > 365) {
            leapOff = leapMonthOffset(a11, timeZone);
        }
        if (leapOff >= 0) {
            int leapMonth = leapOff - 2;
            if (leapMonth < 0) {
                leapMonth += 12;
            }
            if (lunarLeap != 0 && lunarMonth != leapMonth) {
                return new int[]{0, 0, 0};
            } else if (lunarLeap != 0 || off >= leapOff) {
                off += 1;
            }
        }
        int monthStart = newMoonDay(k + off, timeZone);
        return jdToDate(monthStart + lunarDay - 1);
    }
}
