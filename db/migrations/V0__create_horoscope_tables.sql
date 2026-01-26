-- Migration V0: Create Horoscope Tables (Lifetime, Yearly, Monthly, Daily)
-- Purpose: Horoscope data tables for lifetime, yearly, monthly, and daily predictions

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- =========================
-- horoscope_lifetime
-- =========================
DROP TABLE IF EXISTS horoscope_lifetime;
CREATE TABLE horoscope_lifetime (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,

    zodiac_id BIGINT NOT NULL COMMENT 'FK to zodiac (12 con giáp)',
    can_chi VARCHAR(64) NOT NULL COMMENT 'ví dụ "Ất Hợi", "Giáp Tý" (chuẩn hóa text)',
    gender ENUM('male','female') NOT NULL COMMENT 'phân biệt luận cho 2 giới',

    overview   TEXT COMMENT 'tổng quan trọn đời (dài)',
    career     TEXT COMMENT 'công danh, sự nghiệp',
    love       TEXT COMMENT 'tình cảm, hôn nhân',
    health     TEXT COMMENT 'sức khỏe',
    family     TEXT COMMENT 'gia đạo, quan hệ gia đình',
    fortune    TEXT COMMENT 'tài lộc, làm ăn',
    unlucky    TEXT COMMENT 'hạn, việc cần tránh',
    advice     TEXT COMMENT 'lời khuyên / hành động',

    -- Additional fields for structured content (V4)
    love_by_month_group1 TEXT NULL COMMENT 'Tình duyên nhóm 1 (tháng 5,6,9)',
    love_by_month_group2 TEXT NULL COMMENT 'Tình duyên nhóm 2 (tháng 1,2,7,10,11,12)',
    love_by_month_group3 TEXT NULL COMMENT 'Tình duyên nhóm 3 (tháng 3,4,8)',
    compatible_ages TEXT NULL COMMENT 'Tuổi hợp làm ăn (JSON array)',
    difficult_years TEXT NULL COMMENT 'Năm khó khăn (JSON array)',
    incompatible_ages TEXT NULL COMMENT 'Tuổi đại kỵ (JSON array)',
    yearly_progression TEXT NULL COMMENT 'Diễn biến từng năm (JSON object)',
    ritual_guidance TEXT NULL COMMENT 'Hướng dẫn nghi lễ cúng sao',

    metadata JSON DEFAULT NULL COMMENT 'trường mở rộng (tags, nguồn, note)',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT uq_horoscope_lifetime UNIQUE (zodiac_id, can_chi, gender),

    CONSTRAINT fk_hl_zodiac FOREIGN KEY (zodiac_id) REFERENCES zodiac(id)
        ON UPDATE CASCADE ON DELETE CASCADE,
    
    INDEX idx_zodiac_id (zodiac_id),
    INDEX idx_can_chi (can_chi),
    INDEX idx_gender (gender)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='Horoscope lifetime predictions';

-- Optional fulltext index để tìm nhanh theo văn bản
CREATE FULLTEXT INDEX ft_hl_overview ON horoscope_lifetime(overview, career, love);

-- =========================
-- horoscope_yearly
-- =========================
DROP TABLE IF EXISTS horoscope_yearly;
CREATE TABLE horoscope_yearly (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,

    zodiac_id BIGINT NOT NULL,
    year INT NOT NULL COMMENT 'ví dụ 2025',

    summary TEXT,
    career TEXT,
    love TEXT,
    health TEXT,
    fortune TEXT,
    warnings TEXT COMMENT 'cảnh báo, hạn',

    -- Additional fields for structured content (V4)
    cung_menh TEXT NULL COMMENT 'Cung Mệnh với sao và giải thích',
    cung_xung_chieu TEXT NULL COMMENT 'Cung Xung Chiếu',
    cung_tam_hop TEXT NULL COMMENT 'Cung Tam Hợp',
    cung_nhi_hop TEXT NULL COMMENT 'Cung Nhị Hợp',
    van_han TEXT NULL COMMENT 'Vận hạn chi tiết (JSON)',
    tu_tru TEXT NULL COMMENT 'Tứ trụ (JSON)',
    phong_thuy TEXT NULL COMMENT 'Phong thủy may mắn (JSON)',
    qa_section TEXT NULL COMMENT 'Q&A section (JSON array)',
    conclusion TEXT NULL COMMENT 'Lời kết',
    monthly_breakdown TEXT NULL COMMENT 'Dự đoán theo tháng (JSON object)',

    metadata JSON DEFAULT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT uq_hy_zodiac_year UNIQUE (zodiac_id, year),

    CONSTRAINT fk_hy_zodiac FOREIGN KEY (zodiac_id) REFERENCES zodiac(id)
        ON UPDATE CASCADE ON DELETE CASCADE,
    
    INDEX idx_zodiac_id (zodiac_id),
    INDEX idx_year (year)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='Horoscope yearly predictions';

CREATE FULLTEXT INDEX ft_hy_summary ON horoscope_yearly(summary, career, love);

-- =========================
-- horoscope_monthly
-- =========================
DROP TABLE IF EXISTS horoscope_monthly;
CREATE TABLE horoscope_monthly (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,

    zodiac_id BIGINT NOT NULL,
    year INT NOT NULL COMMENT 'ví dụ 2025',
    month TINYINT UNSIGNED NOT NULL COMMENT '1..12',

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
        ON UPDATE CASCADE ON DELETE CASCADE,
    
    INDEX idx_zodiac_id (zodiac_id),
    INDEX idx_year_month (year, month)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='Horoscope monthly predictions';

CREATE FULLTEXT INDEX ft_hm_summary ON horoscope_monthly(summary, career, love);

-- =========================
-- horoscope_daily
-- =========================
DROP TABLE IF EXISTS horoscope_daily;
CREATE TABLE horoscope_daily (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,

    zodiac_id BIGINT NOT NULL COMMENT '12 con giáp',
    solar_date DATE NOT NULL COMMENT 'ngày áp dụng (dương lịch)',

    summary TEXT COMMENT 'dự báo ngắn cho ngày',
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
        ON UPDATE CASCADE ON DELETE CASCADE,
    
    INDEX idx_zodiac_id (zodiac_id),
    INDEX idx_solar_date (solar_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='Horoscope daily predictions';

CREATE INDEX idx_horoscope_daily_date ON horoscope_daily(solar_date);
CREATE FULLTEXT INDEX ft_hd_summary ON horoscope_daily(summary, career, love);

SET FOREIGN_KEY_CHECKS = 1;

