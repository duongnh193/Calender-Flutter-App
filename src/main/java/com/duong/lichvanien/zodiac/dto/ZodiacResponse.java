package com.duong.lichvanien.zodiac.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ZodiacResponse {
    private Long id;
    private String code;
    private String nameVi;
    private int orderNo;
}
