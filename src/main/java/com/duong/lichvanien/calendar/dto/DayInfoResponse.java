package com.duong.lichvanien.calendar.dto;

import com.duong.lichvanien.goldenhour.dto.GoldenHourResponse;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DayInfoResponse {
    private String solarDate;
    private String weekday;
    private LunarDateDto lunar;
    private CanChiDto canChi;
    private boolean goodDay;
    private String note;
    private List<GoldenHourResponse> goldenHours;
}
