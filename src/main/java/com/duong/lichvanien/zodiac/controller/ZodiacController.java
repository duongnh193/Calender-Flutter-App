package com.duong.lichvanien.zodiac.controller;

import com.duong.lichvanien.zodiac.dto.ZodiacResponse;
import com.duong.lichvanien.zodiac.service.ZodiacService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/zodiacs")
@RequiredArgsConstructor
public class ZodiacController {

    private final ZodiacService zodiacService;

    @GetMapping
    public List<ZodiacResponse> getAll() {
        return zodiacService.findAll();
    }
}
