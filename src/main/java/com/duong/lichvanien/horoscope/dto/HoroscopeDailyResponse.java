package com.duong.lichvanien.horoscope.dto;

import com.duong.lichvanien.zodiac.dto.ZodiacShortDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HoroscopeDailyResponse {
    private ZodiacShortDto zodiac;
    private String date;
    private String general;
    private String love;
    private String career;
    private String finance;
    private String health;
    private String luckyColor;
    private String luckyNumber;
}
