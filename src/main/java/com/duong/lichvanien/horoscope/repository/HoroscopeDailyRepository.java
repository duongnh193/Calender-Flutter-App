package com.duong.lichvanien.horoscope.repository;

import com.duong.lichvanien.horoscope.entity.HoroscopeDailyEntity;
import com.duong.lichvanien.zodiac.entity.ZodiacEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface HoroscopeDailyRepository extends JpaRepository<HoroscopeDailyEntity, Long> {

    Optional<HoroscopeDailyEntity> findByZodiacAndSolarDate(ZodiacEntity zodiac, LocalDate solarDate);
}
