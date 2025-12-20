-- Migration V6: Create Natal Chart FACT Tables
-- Purpose: Separate FACT (natal chart data) from INTERPRETATION (diễn giải)
-- FACT tables store the calculated chart data (source of truth)
-- INTERPRETATION tables store only interpretation content, referencing FACT via chart_hash

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ============================
-- 1) natal_chart (Main FACT table)
-- ============================
DROP TABLE IF EXISTS natal_chart;
CREATE TABLE natal_chart (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    
    -- Chart hash (SHA-256, UNIQUE) - canonical hash of all FACT data
    chart_hash VARCHAR(64) NOT NULL UNIQUE COMMENT 'SHA-256 hash of canonical chart FACT data',
    
    -- Birth input (original input để verify)
    solar_date DATE NOT NULL,
    birth_hour TINYINT NOT NULL,
    birth_minute TINYINT NOT NULL DEFAULT 0,
    gender ENUM('male', 'female') NOT NULL,
    is_lunar BOOLEAN NOT NULL DEFAULT FALSE,
    is_leap_month BOOLEAN NOT NULL DEFAULT FALSE,
    
    -- Calculated lunar date (FACT)
    lunar_year INT NOT NULL,
    lunar_month TINYINT NOT NULL,
    lunar_day TINYINT NOT NULL,
    lunar_year_can_chi VARCHAR(32) NOT NULL COMMENT 'Giáp Tý, Ất Sửu, etc.',
    lunar_month_can_chi VARCHAR(32) NOT NULL,
    lunar_day_can_chi VARCHAR(32) NOT NULL,
    birth_hour_can_chi VARCHAR(32) NOT NULL,
    hour_branch_index TINYINT NOT NULL COMMENT '0-11 for 12 branches',
    
    -- Destiny calculations (FACT)
    ban_menh VARCHAR(64) NOT NULL COMMENT 'Nạp Âm element name, e.g., Sơn Đầu Hỏa',
    ban_menh_ngu_hanh VARCHAR(16) NOT NULL COMMENT 'KIM, MOC, THUY, HOA, THO',
    cuc_name VARCHAR(64) NOT NULL COMMENT 'e.g., Thổ ngũ cục',
    cuc_value TINYINT NOT NULL COMMENT '2-6',
    cuc_ngu_hanh VARCHAR(16) NOT NULL,
    am_duong VARCHAR(16) NOT NULL COMMENT 'Âm or Dương',
    thuan_nghich VARCHAR(16) NOT NULL COMMENT 'Thuận or Nghịch',
    
    -- Main stars (FACT)
    chu_menh_star_code VARCHAR(32) NOT NULL COMMENT 'Star code ở cung Mệnh (chủ tinh)',
    chu_than_star_code VARCHAR(32) COMMENT 'Star code ở cung Thân',
    than_cu_palace_code VARCHAR(16) NOT NULL COMMENT 'Palace code chứa Thân (MENH, QUAN_LOC, etc.)',
    
    -- Tuần/Triệt positions (FACT)
    tuan_start_chi VARCHAR(16) NOT NULL COMMENT 'TY, SUU, etc.',
    tuan_end_chi VARCHAR(16) NOT NULL,
    triet_start_chi VARCHAR(16) NOT NULL,
    triet_end_chi VARCHAR(16) NOT NULL,
    
    -- Metadata
    calculated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Indexes
    INDEX idx_chart_hash (chart_hash),
    INDEX idx_lunar_date (lunar_year, lunar_month, lunar_day),
    INDEX idx_gender (gender),
    INDEX idx_solar_date (solar_date)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Natal chart FACT data - source of truth for Tu Vi chart calculations';

-- ============================
-- 2) natal_palace (12 palaces FACT)
-- ============================
DROP TABLE IF EXISTS natal_palace;
CREATE TABLE natal_palace (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    natal_chart_id BIGINT UNSIGNED NOT NULL,
    
    -- Palace identification (FACT)
    palace_index TINYINT NOT NULL COMMENT '0-11 (thứ tự cung)',
    palace_code VARCHAR(16) NOT NULL COMMENT 'MENH, QUAN_LOC, TAI_BACH, etc.',
    palace_chi VARCHAR(16) NOT NULL COMMENT 'HOI, TY, etc. (Địa Chi)',
    can_chi_prefix VARCHAR(32) COMMENT 'D.Hợi (nếu có)',
    
    -- Trường Sinh stage (FACT)
    truong_sinh_stage VARCHAR(32) COMMENT 'Trường sinh, Mộc dục, Quan đới, etc.',
    
    -- Đại Vận (FACT)
    dai_van_start_age INT COMMENT 'Tuổi bắt đầu đại vận này',
    dai_van_label VARCHAR(32) COMMENT 'Th.1, Th.2, etc.',
    
    -- Tuần/Triệt (FACT)
    has_tuan BOOLEAN NOT NULL DEFAULT FALSE,
    has_triet BOOLEAN NOT NULL DEFAULT FALSE,
    
    -- Thân cư (FACT)
    is_than_cu BOOLEAN NOT NULL DEFAULT FALSE,
    
    -- Constraints
    UNIQUE KEY uk_chart_palace (natal_chart_id, palace_code),
    INDEX idx_natal_chart_id (natal_chart_id),
    INDEX idx_palace_code (palace_code),
    INDEX idx_palace_index (palace_index),
    
    -- Foreign key constraint
    FOREIGN KEY (natal_chart_id) REFERENCES natal_chart(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Palace FACT data - 12 palaces per chart with exact properties';

-- ============================
-- 3) natal_star (Stars in palaces FACT)
-- ============================
DROP TABLE IF EXISTS natal_star;
CREATE TABLE natal_star (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    natal_palace_id BIGINT UNSIGNED NOT NULL,
    
    -- Star identification (FACT)
    star_code VARCHAR(32) NOT NULL COMMENT 'TU_VI, THAM_LANG, etc.',
    star_type VARCHAR(32) NOT NULL COMMENT 'CHINH_TINH, PHU_TINH, BANG_TINH',
    star_ngu_hanh VARCHAR(16) COMMENT 'KIM, MOC, THUY, HOA, THO',
    
    -- Brightness (FACT - tính từ vị trí và cục)
    brightness VARCHAR(32) NOT NULL COMMENT 'MIEU, VUONG, DAC, DIA, BAI',
    brightness_code VARCHAR(4) COMMENT 'M, V, Đ, B, H',
    
    -- Star properties (FACT)
    is_positive BOOLEAN COMMENT 'TRUE = cát tinh, FALSE = tai tinh, NULL = trung tính',
    
    -- Order in palace (FACT - quan trọng cho diễn giải)
    star_order TINYINT NOT NULL COMMENT '0 = sao đầu tiên, 1 = sao thứ hai, etc.',
    
    -- Constraints
    INDEX idx_natal_palace_id (natal_palace_id),
    INDEX idx_star_code (star_code),
    INDEX idx_star_type (star_type),
    INDEX idx_star_order (star_order),
    
    -- Foreign key constraint
    FOREIGN KEY (natal_palace_id) REFERENCES natal_palace(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Star FACT data - stars in each palace with exact properties and order';

SET FOREIGN_KEY_CHECKS = 1;
