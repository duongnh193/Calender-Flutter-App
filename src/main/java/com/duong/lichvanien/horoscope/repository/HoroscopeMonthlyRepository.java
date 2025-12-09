package com.duong.lichvanien.horoscope.repository;

import com.duong.lichvanien.horoscope.entity.HoroscopeMonthlyEntity;
import com.duong.lichvanien.zodiac.entity.ZodiacEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HoroscopeMonthlyRepository extends JpaRepository<HoroscopeMonthlyEntity, Long> {

    Optional<HoroscopeMonthlyEntity> findByZodiacAndYearAndMonth(ZodiacEntity zodiac, int year, int month);

    Optional<HoroscopeMonthlyEntity> findByZodiac_IdAndYearAndMonth(Long zodiacId, int year, int month);
}

