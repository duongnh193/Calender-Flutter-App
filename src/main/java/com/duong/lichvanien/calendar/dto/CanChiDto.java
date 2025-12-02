package com.duong.lichvanien.calendar.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CanChiDto {
    private String day;
    private String month;
    private String year;
}
