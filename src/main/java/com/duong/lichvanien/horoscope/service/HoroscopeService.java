package com.duong.lichvanien.horoscope.service;

import com.duong.lichvanien.common.exception.NotFoundException;
import com.duong.lichvanien.horoscope.dto.HoroscopeDailyResponse;
import com.duong.lichvanien.horoscope.dto.HoroscopeYearlyResponse;
import com.duong.lichvanien.horoscope.entity.HoroscopeDailyEntity;
import com.duong.lichvanien.horoscope.entity.HoroscopeYearlyEntity;
import com.duong.lichvanien.horoscope.repository.HoroscopeDailyRepository;
import com.duong.lichvanien.horoscope.repository.HoroscopeYearlyRepository;
import com.duong.lichvanien.zodiac.dto.ZodiacShortDto;
import com.duong.lichvanien.zodiac.entity.ZodiacEntity;
import com.duong.lichvanien.zodiac.service.ZodiacService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class HoroscopeService {

    private final HoroscopeYearlyRepository yearlyRepository;
    private final HoroscopeDailyRepository dailyRepository;
    private final ZodiacService zodiacService;

    public HoroscopeYearlyResponse getYearly(String zodiacCode, int year) {
        ZodiacEntity zodiac = zodiacService.getByCode(zodiacCode);
        HoroscopeYearlyEntity entity = yearlyRepository.findByZodiacAndYear(zodiac, year)
                .orElseThrow(() -> new NotFoundException("HOROSCOPE_YEARLY_NOT_FOUND", "No yearly horoscope for " + zodiacCode));

        return HoroscopeYearlyResponse.builder()
                .zodiac(toShort(zodiac))
                .year(entity.getYear())
                .summary(entity.getSummary())
                .love(entity.getLove())
                .career(entity.getCareer())
                .finance(entity.getFinance())
                .health(entity.getHealth())
                .build();
    }

    public HoroscopeDailyResponse getDaily(String zodiacCode, LocalDate date) {
        ZodiacEntity zodiac = zodiacService.getByCode(zodiacCode);
        HoroscopeDailyEntity entity = dailyRepository.findByZodiacAndSolarDate(zodiac, date)
                .orElseThrow(() -> new NotFoundException("HOROSCOPE_DAILY_NOT_FOUND", "No daily horoscope for " + zodiacCode));

        return HoroscopeDailyResponse.builder()
                .zodiac(toShort(zodiac))
                .date(entity.getSolarDate().toString())
                .general(entity.getGeneral())
                .love(entity.getLove())
                .career(entity.getCareer())
                .finance(entity.getFinance())
                .health(entity.getHealth())
                .luckyColor(entity.getLuckyColor())
                .luckyNumber(entity.getLuckyNumber())
                .build();
    }

    private ZodiacShortDto toShort(ZodiacEntity entity) {
        return ZodiacShortDto.builder()
                .code(entity.getCode())
                .nameVi(entity.getNameVi())
                .build();
    }
}
