package com.duong.lichvanien.goldenhour.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GoldenHourResponse {
    private String branch;
    private int startHour;
    private int endHour;
    private String label;
}
