package com.duong.lichvanien.calendar;

import com.duong.lichvanien.calendar.entity.DayInfoEntity;
import com.duong.lichvanien.calendar.repository.DayInfoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class DayInfoRepositoryTest {

    @Autowired
    DayInfoRepository dayInfoRepository;

    @Test
    void solar_2025_12_02_should_be_13_10_leap_correct_can_chi() {
        DayInfoEntity entity = new DayInfoEntity();
        entity.setSolarDate(LocalDate.of(2025, 12, 2));
        entity.setWeekday(2);
        entity.setLunarDay(13);
        entity.setLunarMonth(10);
        entity.setLunarYear(2025);
        entity.setLunarLeapMonth(1);
        entity.setCanChiDay("Ất Tỵ");
        entity.setCanChiMonth("Đinh Hợi");
        entity.setCanChiYear("Ất Tỵ");
        entity.setGoodDay(false);
        dayInfoRepository.save(entity);

        DayInfoEntity e = dayInfoRepository.findById(LocalDate.of(2025, 12, 2)).orElseThrow();
        assertThat(e.getLunarDay()).isEqualTo(13);
        assertThat(e.getLunarMonth()).isEqualTo(10);
        assertThat(e.getLunarYear()).isEqualTo(2025);
        assertThat(e.getLunarLeapMonth()).isEqualTo(1);
        assertThat(e.getCanChiDay()).isEqualTo("Ất Tỵ");
        assertThat(e.getCanChiMonth()).isEqualTo("Đinh Hợi");
        assertThat(e.getCanChiYear()).isEqualTo("Ất Tỵ");
    }
}
