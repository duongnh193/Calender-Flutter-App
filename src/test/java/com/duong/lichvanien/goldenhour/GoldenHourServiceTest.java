package com.duong.lichvanien.goldenhour;

import com.duong.lichvanien.common.exception.NotFoundException;
import com.duong.lichvanien.goldenhour.entity.GoldenHourPatternEntity;
import com.duong.lichvanien.goldenhour.entity.ZodiacHourEntity;
import com.duong.lichvanien.goldenhour.repository.GoldenHourPatternRepository;
import com.duong.lichvanien.goldenhour.repository.ZodiacHourRepository;
import com.duong.lichvanien.goldenhour.service.GoldenHourService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoldenHourServiceTest {

    @Mock
    GoldenHourPatternRepository patternRepository;

    @Mock
    ZodiacHourRepository zodiacHourRepository;

    @InjectMocks
    GoldenHourService goldenHourService;

    @Test
    void getGoldenHours_shouldReturnHours() {
        GoldenHourPatternEntity pattern = new GoldenHourPatternEntity();
        pattern.setDayBranchCode("suu");
        pattern.setGoodBranchCodes("ti,suu");

        ZodiacHourEntity ty = new ZodiacHourEntity();
        ty.setBranchCode("ti");
        ty.setStartHour(23);
        ty.setEndHour(1);

        ZodiacHourEntity suu = new ZodiacHourEntity();
        suu.setBranchCode("suu");
        suu.setStartHour(1);
        suu.setEndHour(3);

        when(patternRepository.findByDayBranchCode("suu")).thenReturn(Optional.of(pattern));
        when(zodiacHourRepository.findByBranchCodeIn(List.of("ti", "suu")))
                .thenReturn(List.of(ty, suu));

        var result = goldenHourService.getGoldenHours("suu");

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getBranch()).isEqualTo("ti");
        assertThat(result.get(0).getLabel()).isEqualTo("23-1h");
    }

    @Test
    void getGoldenHours_shouldThrow_whenPatternMissing() {
        when(patternRepository.findByDayBranchCode("ti")).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> goldenHourService.getGoldenHours("ti"));
    }
}
