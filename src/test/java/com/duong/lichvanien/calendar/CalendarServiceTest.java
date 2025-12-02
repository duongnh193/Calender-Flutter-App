package com.duong.lichvanien.calendar;

import com.duong.lichvanien.calendar.dto.DayInfoResponse;
import com.duong.lichvanien.calendar.entity.DayInfoEntity;
import com.duong.lichvanien.calendar.repository.DayInfoRepository;
import com.duong.lichvanien.calendar.service.CalendarService;
import com.duong.lichvanien.common.exception.BadRequestException;
import com.duong.lichvanien.common.exception.NotFoundException;
import com.duong.lichvanien.goldenhour.dto.GoldenHourResponse;
import com.duong.lichvanien.goldenhour.service.GoldenHourService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CalendarServiceTest {

    @Mock
    DayInfoRepository dayInfoRepository;

    @Mock
    GoldenHourService goldenHourService;

    @InjectMocks
    CalendarService calendarService;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(calendarService, "minDateStr", "1900-01-01");
        ReflectionTestUtils.setField(calendarService, "maxDateStr", "2100-12-31");
    }

    @Test
    void getDayInfo_shouldReturnData_whenExists() {
        LocalDate date = LocalDate.of(2025, 11, 28);
        DayInfoEntity entity = new DayInfoEntity();
        entity.setSolarDate(date);
        entity.setLunarDay(9);
        entity.setLunarMonth(10);
        entity.setLunarYear(2025);
        entity.setLunarLeapMonth(0);
        entity.setCanChiDay("Tân Sửu");
        entity.setCanChiMonth("Đinh Hợi");
        entity.setCanChiYear("Ất Tỵ");
        entity.setWeekday(date.getDayOfWeek().getValue());

        when(dayInfoRepository.findBySolarDate(date)).thenReturn(Optional.of(entity));
        when(goldenHourService.getGoldenHours("Tân Sửu"))
                .thenReturn(List.of(GoldenHourResponse.builder()
                        .branch("Tý")
                        .startHour(23)
                        .endHour(1)
                        .label("23-1h")
                        .build()));

        DayInfoResponse resp = calendarService.getDayInfo(date);

        assertThat(resp.getSolarDate()).isEqualTo("2025-11-28");
        assertThat(resp.getLunar().getDay()).isEqualTo(9);
        assertThat(resp.getGoldenHours()).hasSize(1);
    }

    @Test
    void getDayInfo_shouldThrow_whenOutOfRange() {
        LocalDate date = LocalDate.of(1800, 1, 1);
        assertThrows(BadRequestException.class, () -> calendarService.getDayInfo(date));
    }

    @Test
    void getDayInfo_shouldThrow_whenNotFound() {
        LocalDate date = LocalDate.of(2025, 1, 1);
        when(dayInfoRepository.findBySolarDate(date)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> calendarService.getDayInfo(date));
    }
}
