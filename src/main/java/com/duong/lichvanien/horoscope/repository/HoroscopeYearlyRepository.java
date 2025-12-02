package com.duong.lichvanien.horoscope.repository;

import com.duong.lichvanien.horoscope.entity.HoroscopeYearlyEntity;
import com.duong.lichvanien.zodiac.entity.ZodiacEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HoroscopeYearlyRepository extends JpaRepository<HoroscopeYearlyEntity, Long> {

    Optional<HoroscopeYearlyEntity> findByZodiacAndYear(ZodiacEntity zodiac, int year);
}
