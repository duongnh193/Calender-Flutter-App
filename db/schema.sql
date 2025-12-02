CREATE DATABASE IF NOT EXISTS lich_van_nien
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;
USE lich_van_nien;

-- ============================
-- 1) ZODIAC (12 con giáp)
-- ============================

CREATE TABLE zodiac (
                        id        BIGINT AUTO_INCREMENT PRIMARY KEY,
                        code      VARCHAR(16) NOT NULL UNIQUE,   -- ascii slug: ti, suu, dan...
                        name_vi   VARCHAR(32) NOT NULL,          -- tên hiển thị: "Tý", "Sửu"...
                        order_no  TINYINT NOT NULL
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;

-- ============================
-- 2) DAY_INFO (lịch âm/dương)
-- ============================

CREATE TABLE day_info (
                          solar_date       DATE PRIMARY KEY,       -- ngày dương (yyyy-MM-dd)
                          weekday          TINYINT NOT NULL,       -- 1=Mon..7=Sun (ISO)
                          lunar_day        TINYINT NOT NULL,
                          lunar_month      TINYINT NOT NULL,
                          lunar_year       SMALLINT NOT NULL,
                          lunar_leap_month TINYINT NOT NULL,       -- 0=thường,1=nhuận
                          can_chi_day      VARCHAR(32) NOT NULL,   -- "Tân Sửu"
                          can_chi_month    VARCHAR(32) NOT NULL,
                          can_chi_year     VARCHAR(32) NOT NULL,
                          is_good_day      TINYINT(1) NOT NULL DEFAULT 0,
                          note             VARCHAR(255) NULL,
                          KEY idx_lunar (lunar_year, lunar_month, lunar_day, lunar_leap_month)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;

-- ============================
-- 3) ZODIAC_HOUR
--    Mapping 12 con giáp (code) -> khung giờ
-- ============================

CREATE TABLE zodiac_hour (
                             id          BIGINT AUTO_INCREMENT PRIMARY KEY,
                             branch_code VARCHAR(16) NOT NULL,   -- tham chiếu zodiac.code (ti, suu,...)
                             start_hour  TINYINT NOT NULL,       -- 0–23
                             end_hour    TINYINT NOT NULL,       -- 0–23
                             CONSTRAINT uq_zodiac_hour_branch UNIQUE (branch_code),
                             CONSTRAINT fk_zodiac_hour_branch
                                 FOREIGN KEY (branch_code) REFERENCES zodiac(code)
                                     ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;

-- ============================
-- 4) GOLDEN_HOUR_PATTERN
--    Mapping ĐỊA CHI NGÀY (code) -> list ĐỊA CHI GIỜ (code CSV)
-- ============================

CREATE TABLE golden_hour_pattern (
                                     id               BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     day_branch_code  VARCHAR(16) NOT NULL,   -- vd: "ti","suu","dan"
                                     good_branch_codes VARCHAR(255) NOT NULL, -- CSV: "ti,suu,thin,ty,than,dau"
                                     CONSTRAINT uq_golden_day_branch UNIQUE (day_branch_code),
                                     CONSTRAINT fk_golden_day_branch
                                         FOREIGN KEY (day_branch_code) REFERENCES zodiac(code)
                                             ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;

-- ============================
-- 5) HOROSCOPE_YEARLY (tử vi năm)
-- ============================

CREATE TABLE horoscope_yearly (
                                  id         BIGINT AUTO_INCREMENT PRIMARY KEY,
                                  zodiac_id  BIGINT NOT NULL,
                                  year       INT NOT NULL,
                                  summary    TEXT,
                                  love       TEXT,
                                  career     TEXT,
                                  finance    TEXT,
                                  health     TEXT,
                                  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                                      ON UPDATE CURRENT_TIMESTAMP,
                                  CONSTRAINT uq_horoscope_yearly UNIQUE (zodiac_id, year),
                                  CONSTRAINT fk_horoscope_yearly_zodiac
                                      FOREIGN KEY (zodiac_id) REFERENCES zodiac(id)
                                          ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;

-- ============================
-- 6) HOROSCOPE_DAILY (tử vi ngày) - optional
-- ============================

CREATE TABLE horoscope_daily (
                                 id           BIGINT AUTO_INCREMENT PRIMARY KEY,
                                 zodiac_id    BIGINT NOT NULL,
                                 solar_date   DATE NOT NULL,
                                 general      TEXT,
                                 love         TEXT,
                                 career       TEXT,
                                 finance      TEXT,
                                 health       TEXT,
                                 lucky_color  VARCHAR(64),
                                 lucky_number VARCHAR(64),
                                 updated_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                                     ON UPDATE CURRENT_TIMESTAMP,
                                 CONSTRAINT uq_horoscope_daily UNIQUE (zodiac_id, solar_date),
                                 CONSTRAINT fk_horoscope_daily_zodiac
                                     FOREIGN KEY (zodiac_id) REFERENCES zodiac(id)
                                         ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;

SET FOREIGN_KEY_CHECKS = 1;