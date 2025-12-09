package com.duong.lichvanien.zodiac.service;

import com.duong.lichvanien.common.exception.NotFoundException;
import com.duong.lichvanien.zodiac.dto.ZodiacResponse;
import com.duong.lichvanien.zodiac.dto.ZodiacShortDto;
import com.duong.lichvanien.zodiac.entity.ZodiacEntity;
import com.duong.lichvanien.zodiac.repository.ZodiacRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ZodiacService {

    private final ZodiacRepository zodiacRepository;

    public List<ZodiacResponse> findAll() {
        return zodiacRepository.findAll().stream()
                .sorted((a, b) -> Integer.compare(a.getOrderNo(), b.getOrderNo()))
                .map(this::map)
                .toList();
    }

    public ZodiacEntity getByCode(String code) {
        return zodiacRepository.findByCode(code)
                .orElseThrow(() -> new NotFoundException("ZODIAC_NOT_FOUND", "Zodiac not found: " + code));
    }

    public ZodiacEntity getById(Long id) {
        return zodiacRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("ZODIAC_NOT_FOUND", "Zodiac not found with id: " + id));
    }

    public ZodiacShortDto toShortDto(ZodiacEntity entity) {
        return ZodiacShortDto.builder()
                .code(entity.getCode())
                .nameVi(entity.getNameVi())
                .build();
    }

    private ZodiacResponse map(ZodiacEntity e) {
        return ZodiacResponse.builder()
                .id(e.getId())
                .code(e.getCode())
                .nameVi(e.getNameVi())
                .orderNo(e.getOrderNo())
                .build();
    }
}
