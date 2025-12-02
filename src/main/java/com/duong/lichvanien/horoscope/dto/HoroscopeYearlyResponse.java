package com.duong.lichvanien.horoscope.dto;

import com.duong.lichvanien.zodiac.dto.ZodiacShortDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HoroscopeYearlyResponse {
    private ZodiacShortDto zodiac;
    private int year;
    private String summary;
    private String love;
    private String career;
    private String finance;
    private String health;
}
