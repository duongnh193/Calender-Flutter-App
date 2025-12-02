package com.duong.lichvanien.zodiac.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ZodiacShortDto {
    private String code;
    private String nameVi;
}
