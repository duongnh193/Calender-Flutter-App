package com.duong.lichvanien.calendar.service;

import com.duong.lichvanien.calendar.entity.DayInfoEntity;
import com.duong.lichvanien.calendar.entity.GoodDayType;
import com.duong.lichvanien.calendar.goodday.GoodDayRuleService;
import com.duong.lichvanien.calendar.repository.DayInfoRepository;
import com.duong.lichvanien.calendar.util.VietnameseLunarCalendar;
import com.duong.lichvanien.calendar.util.VietnameseLunarCalendar.CanChi;
import com.duong.lichvanien.calendar.util.VietnameseLunarCalendar.LunarDate;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Component
@Profile("gen-day-info")
@RequiredArgsConstructor
public class DayInfoRegenerator implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DayInfoRegenerator.class);

    private final DayInfoRepository dayInfoRepository;
    private final GoodDayRuleService goodDayRuleService;

    @Value("${app.calendar.gen-start-year:2000}")
    private int startYear;
    @Value("${app.calendar.gen-end-year:2035}")
    private int endYear;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("Regenerating day_info from {} to {}", startYear, endYear);
        dayInfoRepository.deleteAllInBatch();
        LocalDate current = LocalDate.of(startYear, 1, 1);
        LocalDate end = LocalDate.of(endYear, 12, 31);
        int count = 0;
        while (!current.isAfter(end)) {
            LunarDate lunar = VietnameseLunarCalendar.solarToLunar(current);
            CanChi canChi = VietnameseLunarCalendar.canChiFromSolar(current);

            DayInfoEntity entity = new DayInfoEntity();
            entity.setSolarDate(current);
            entity.setWeekday(current.getDayOfWeek().getValue());
            entity.setLunarDay(lunar.day());
            entity.setLunarMonth(lunar.month());
            entity.setLunarYear(lunar.year());
            entity.setLunarLeapMonth(lunar.leapMonth() ? 1 : 0);
            entity.setCanChiDay(canChi.day());
            entity.setCanChiMonth(canChi.month());
            entity.setCanChiYear(canChi.year());
            GoodDayType fortune = goodDayRuleService.resolve(lunar.month(), canChi.day());
            entity.setGoodDayType(fortune);
            entity.setNote(null);

            dayInfoRepository.save(entity);
            count++;
            if (count % 500 == 0) {
                dayInfoRepository.flush();
                log.info("Generated {} records up to {}", count, current);
            }
            current = current.plusDays(1);
        }
        log.info("Completed regenerating day_info, total {} records", count);
    }
}
