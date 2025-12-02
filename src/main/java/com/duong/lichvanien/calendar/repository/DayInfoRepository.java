package com.duong.lichvanien.calendar.repository;

import com.duong.lichvanien.calendar.entity.DayInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DayInfoRepository extends JpaRepository<DayInfoEntity, LocalDate> {

    Optional<DayInfoEntity> findBySolarDate(LocalDate solarDate);

    List<DayInfoEntity> findBySolarDateBetween(LocalDate start, LocalDate end);

    Optional<DayInfoEntity> findByLunarYearAndLunarMonthAndLunarDayAndLunarLeapMonth(
            int lunarYear, int lunarMonth, int lunarDay, int lunarLeapMonth);
}
