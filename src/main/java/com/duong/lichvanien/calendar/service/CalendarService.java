package com.duong.lichvanien.calendar.service;

import com.duong.lichvanien.calendar.dto.CanChiDto;
import com.duong.lichvanien.calendar.dto.DayInfoResponse;
import com.duong.lichvanien.calendar.dto.LunarDateDto;
import com.duong.lichvanien.calendar.dto.MonthCalendarResponse;
import com.duong.lichvanien.calendar.dto.MonthDayItem;
import com.duong.lichvanien.calendar.entity.DayInfoEntity;
import com.duong.lichvanien.calendar.entity.GoodDayType;
import com.duong.lichvanien.calendar.repository.DayInfoRepository;
import com.duong.lichvanien.common.exception.BadRequestException;
import com.duong.lichvanien.common.exception.NotFoundException;
import com.duong.lichvanien.goldenhour.dto.GoldenHourResponse;
import com.duong.lichvanien.goldenhour.service.GoldenHourService;
import lombok.RequiredArgsConstructor;
import org.aspectj.bridge.MessageUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CalendarService {

    private final DayInfoRepository dayInfoRepository;
    private final GoldenHourService goldenHourService;
    private final CurrentTimeInfoService currentTimeInfoService;

    @Value("${app.calendar.min-date:1900-01-01}")
    private String minDateStr;
    @Value("${app.calendar.max-date:2100-12-31}")
    private String maxDateStr;

    @Transactional(readOnly = true)
    public DayInfoResponse getDayInfo(LocalDate date) {
        validateRange(date);
        DayInfoEntity entity = dayInfoRepository.findBySolarDate(date)
                .orElseThrow(() -> new NotFoundException("DAY_NOT_FOUND", "Day not found in supported range"));
        List<GoldenHourResponse> goldenHours = goldenHourService.getGoldenHours(entity.getCanChiDay());
        return mapToResponse(entity, goldenHours);
    }

    @Transactional(readOnly = true)
    public MonthCalendarResponse getMonthInfo(int year, int month) {
        YearMonth ym = YearMonth.of(year, month);
        validateRange(ym.atDay(1));
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();
        List<DayInfoEntity> entities = dayInfoRepository.findBySolarDateBetween(start, end);
        List<MonthDayItem> items = entities.stream()
                .map(e -> MonthDayItem.builder()
                        .solarDate(e.getSolarDate().toString())
                        .dayOfMonth(e.getSolarDate().getDayOfMonth())
                        .lunar(LunarDateDto.builder()
                                .day(e.getLunarDay())
                        .month(e.getLunarMonth())
                        .year(e.getLunarYear())
                        .leapMonth(e.getLunarLeapMonth() == 1)
                        .build())
                        .goodDayType(e.getGoodDayType())
                        .special(e.getGoodDayType() == GoodDayType.HOANG_DAO)
                        .build())
                .toList();
        return MonthCalendarResponse.builder()
                .year(year)
                .month(month)
                .days(items)
                .build();
    }

    @Transactional(readOnly = true)
    public LunarDateDto convertSolarToLunar(LocalDate solarDate) {
        validateRange(solarDate);
        DayInfoEntity entity = dayInfoRepository.findBySolarDate(solarDate)
                .orElseThrow(() -> new NotFoundException("DAY_NOT_FOUND", "Day not found in supported range"));
        return LunarDateDto.builder()
                .day(entity.getLunarDay())
                .month(entity.getLunarMonth())
                .year(entity.getLunarYear())
                .leapMonth(entity.getLunarLeapMonth() == 1)
                .build();
    }

    @Transactional(readOnly = true)
    public LocalDate convertLunarToSolar(int lunarYear, int lunarMonth, int lunarDay, boolean leapMonth) {
        DayInfoEntity entity = dayInfoRepository
                .findByLunarYearAndLunarMonthAndLunarDayAndLunarLeapMonth(
                        lunarYear, lunarMonth, lunarDay, leapMonth ? 1 : 0)
                .orElseThrow(() -> new NotFoundException("DAY_NOT_FOUND", "Day not found in supported range"));
        return entity.getSolarDate();
    }

    private void validateRange(LocalDate date) {
        LocalDate min = LocalDate.parse(minDateStr);
        LocalDate max = LocalDate.parse(maxDateStr);
        if (date.isBefore(min) || date.isAfter(max)) {
            throw new BadRequestException("DATE_OUT_OF_RANGE", "Date is outside supported range");
        }
    }

    private DayInfoResponse mapToResponse(DayInfoEntity entity, List<GoldenHourResponse> goldenHours) {
        return DayInfoResponse.builder()
                .solarDate(entity.getSolarDate().toString())
                .weekday(entity.getSolarDate().getDayOfWeek().name())
                .lunar(LunarDateDto.builder()
                        .day(entity.getLunarDay())
                        .month(entity.getLunarMonth())
                        .year(entity.getLunarYear())
                        .leapMonth(entity.getLunarLeapMonth() == 1)
                        .build())
                .canChi(CanChiDto.builder()
                        .day(entity.getCanChiDay())
                        .month(entity.getCanChiMonth())
                        .year(entity.getCanChiYear())
                        .build())
                .goodDayType(entity.getGoodDayType())
                .note(entity.getNote())
                .goldenHours(goldenHours)
                .currentTime(currentTimeInfoService.getCurrentTimeInfo(
                        entity.getSolarDate(),
                        entity.getCanChiDay()
                ))
                .build();
    }
}
