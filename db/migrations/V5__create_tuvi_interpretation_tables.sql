-- Migration V5: Create Tu Vi Interpretation Tables
-- Purpose: Store Tu Vi chart interpretations in database instead of generating with AI each time

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ============================
-- 1) tuvi_interpretation (main table)
-- ============================
DROP TABLE IF EXISTS tuvi_interpretation;
CREATE TABLE tuvi_interpretation (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    
    -- Chart identification (unique constraint on chart_hash + gender)
    chart_hash VARCHAR(64) NOT NULL COMMENT 'SHA-256 hash of chart structure',
    gender ENUM('male', 'female') NOT NULL,
    
    -- Birth information (for metadata/display)
    birth_date DATE NOT NULL,
    birth_hour TINYINT NOT NULL,
    lunar_year_can_chi VARCHAR(32) NOT NULL,
    
    -- Overview section data stored as JSON
    overview_data JSON NOT NULL COMMENT 'Complete OverviewSection as JSON',
    
    -- Timestamps
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Indexes
    UNIQUE KEY uk_chart_hash_gender (chart_hash, gender),
    INDEX idx_chart_hash (chart_hash),
    INDEX idx_gender (gender),
    INDEX idx_birth_date (birth_date)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Main table storing Tu Vi chart interpretations';

-- ============================
-- 2) tuvi_palace_interpretation (12 palaces)
-- ============================
DROP TABLE IF EXISTS tuvi_palace_interpretation;
CREATE TABLE tuvi_palace_interpretation (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    
    -- Foreign key to main interpretation
    interpretation_id BIGINT UNSIGNED NOT NULL,
    
    -- Palace identification
    palace_code VARCHAR(16) NOT NULL COMMENT 'MENH, QUAN_LOC, TAI_BACH, etc.',
    palace_name VARCHAR(64) COMMENT 'Vietnamese name',
    palace_chi VARCHAR(32) COMMENT 'Địa Chi of palace',
    can_chi_prefix VARCHAR(32) COMMENT 'Can-Chi prefix like D.Hợi',
    
    -- Interpretation content
    summary TEXT COMMENT 'Summary paragraph',
    introduction TEXT COMMENT 'Introduction section',
    detailed_analysis TEXT COMMENT 'Detailed analysis section',
    gender_analysis TEXT COMMENT 'Gender-specific analysis (mainly for MENH palace)',
    
    -- Tuần/Triệt flags
    has_tuan BOOLEAN DEFAULT FALSE,
    has_triet BOOLEAN DEFAULT FALSE,
    tuan_triet_effect TEXT COMMENT 'Analysis of Tuần/Triệt effects',
    
    -- Additional sections
    advice_section TEXT COMMENT 'Advice and recommendations',
    conclusion TEXT COMMENT 'Conclusion paragraph',
    
    -- Constraints and indexes
    UNIQUE KEY uk_interpretation_palace (interpretation_id, palace_code),
    INDEX idx_interpretation_id (interpretation_id),
    INDEX idx_palace_code (palace_code),
    
    -- Foreign key constraint
    CONSTRAINT fk_palace_interpretation
        FOREIGN KEY (interpretation_id)
        REFERENCES tuvi_interpretation(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Interpretations for each of the 12 palaces in Tu Vi chart';

-- ============================
-- 3) tuvi_star_interpretation (stars in palaces)
-- ============================
DROP TABLE IF EXISTS tuvi_star_interpretation;
CREATE TABLE tuvi_star_interpretation (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    
    -- Foreign key to palace interpretation
    palace_interpretation_id BIGINT UNSIGNED NOT NULL,
    
    -- Star identification
    star_code VARCHAR(32) NOT NULL COMMENT 'TU_VI, THAM_LANG, etc.',
    star_name VARCHAR(64) COMMENT 'Vietnamese name like Tử Vi',
    star_type VARCHAR(32) COMMENT 'CHINH_TINH, PHU_TINH, etc.',
    brightness VARCHAR(32) COMMENT 'MIEU, VUONG, etc.',
    
    -- Star interpretation content
    interpretation TEXT COMMENT 'Detailed interpretation for this star in this palace context',
    summary VARCHAR(512) COMMENT 'Short summary of star influence',
    
    -- Constraints and indexes
    INDEX idx_palace_interpretation_id (palace_interpretation_id),
    INDEX idx_star_code (star_code),
    
    -- Foreign key constraint
    CONSTRAINT fk_star_palace_interpretation
        FOREIGN KEY (palace_interpretation_id)
        REFERENCES tuvi_palace_interpretation(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Interpretations for individual stars within each palace';

SET FOREIGN_KEY_CHECKS = 1;
