-- Charset / collation
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- =========================
-- horoscope_lifetime
-- =========================
DROP TABLE IF EXISTS horoscope_lifetime;
CREATE TABLE horoscope_lifetime (
                                    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,

                                    zodiac_id BIGINT NOT NULL,        -- FK to zodiac (12 con giáp)
                                    can_chi VARCHAR(64)        NOT NULL,      -- ví dụ "Ất Hợi", "Giáp Tý" (chuẩn hóa text)
                                    gender ENUM('male','female') NOT NULL,    -- phân biệt luận cho 2 giới

                                    overview   TEXT,    -- tổng quan trọn đời (dài)
                                    career     TEXT,    -- công danh, sự nghiệp
                                    love       TEXT,    -- tình cảm, hôn nhân
                                    health     TEXT,    -- sức khỏe
                                    family     TEXT,    -- gia đạo, quan hệ gia đình
                                    fortune    TEXT,    -- tài lộc, làm ăn
                                    unlucky    TEXT,    -- hạn, việc cần tránh
                                    advice     TEXT,    -- lời khuyên / hành động

                                    metadata JSON DEFAULT NULL,                -- trường mở rộng (tags, nguồn, note)
                                    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                                    CONSTRAINT uq_horoscope_lifetime UNIQUE (zodiac_id, can_chi, gender),

                                    CONSTRAINT fk_hl_zodiac FOREIGN KEY (zodiac_id) REFERENCES zodiac(id)
                                        ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Optional fulltext index để tìm nhanh theo văn bản
CREATE FULLTEXT INDEX ft_hl_overview ON horoscope_lifetime(overview, career, love);

-- =========================
-- horoscope_yearly
-- =========================
DROP TABLE IF EXISTS horoscope_yearly;
CREATE TABLE horoscope_yearly (
                                  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,

                                  zodiac_id BIGINT NOT NULL,
                                  year INT NOT NULL,                         -- ví dụ 2025

                                  summary TEXT,      -- tóm tắt cho năm đó
                                  career TEXT,
                                  love TEXT,
                                  health TEXT,
                                  fortune TEXT,
                                  warnings TEXT,     -- hạn, kỵ cần chú ý trong năm

                                  metadata JSON DEFAULT NULL,
                                  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                                  CONSTRAINT uq_hy_zodiac_year UNIQUE (zodiac_id, year),

                                  CONSTRAINT fk_hy_zodiac FOREIGN KEY (zodiac_id) REFERENCES zodiac(id)
                                      ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE FULLTEXT INDEX ft_hy_summary ON horoscope_yearly(summary, career, love);

-- =========================
-- horoscope_monthly
-- =========================
DROP TABLE IF EXISTS horoscope_monthly;
CREATE TABLE horoscope_monthly (
                                   id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,

                                   zodiac_id BIGINT NOT NULL,
                                   year INT NOT NULL,                         -- ví dụ 2025
                                   month TINYINT UNSIGNED NOT NULL,           -- 1..12

                                   summary TEXT,
                                   career TEXT,
                                   love TEXT,
                                   health TEXT,
                                   fortune TEXT,

                                   metadata JSON DEFAULT NULL,
                                   created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                   updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                                   CONSTRAINT uq_hm_zodiac_year_month UNIQUE (zodiac_id, year, month),

                                   CONSTRAINT fk_hm_zodiac FOREIGN KEY (zodiac_id) REFERENCES zodiac(id)
                                       ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE FULLTEXT INDEX ft_hm_summary ON horoscope_monthly(summary, career, love);

-- =========================
-- horoscope_daily
-- =========================
DROP TABLE IF EXISTS horoscope_daily;
CREATE TABLE horoscope_daily (
                                 id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,

                                 zodiac_id BIGINT NOT NULL,        -- 12 con giáp
                                 solar_date DATE NOT NULL,                  -- ngày áp dụng (dương lịch)

                                 summary TEXT,          -- dự báo ngắn cho ngày
                                 career TEXT,
                                 love TEXT,
                                 health TEXT,
                                 fortune TEXT,
                                 lucky_color VARCHAR(64),
                                 lucky_number VARCHAR(64),

                                 metadata JSON DEFAULT NULL,
                                 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                                 CONSTRAINT uq_hd_zodiac_date UNIQUE (zodiac_id, solar_date),

                                 CONSTRAINT fk_hd_zodiac FOREIGN KEY (zodiac_id) REFERENCES zodiac(id)
                                     ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_horoscope_daily_date ON horoscope_daily(solar_date);
CREATE FULLTEXT INDEX ft_hd_summary ON horoscope_daily(summary, career, love);

-- =========================
-- Notes and helpful views
-- =========================

-- A view to quickly fetch lifetime by (zodiac code, can_chi, gender)
-- (Assumes zodiac table exists and contains code/name)
DROP VIEW IF EXISTS v_horoscope_lifetime_quick;
CREATE VIEW v_horoscope_lifetime_quick AS
SELECT h.*, z.code AS zodiac_code, z.name_vi AS zodiac_name
FROM horoscope_lifetime h
         JOIN zodiac z ON z.id = h.zodiac_id;

SET FOREIGN_KEY_CHECKS = 1;
