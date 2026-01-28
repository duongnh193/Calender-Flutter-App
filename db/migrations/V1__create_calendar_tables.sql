-- Migration V1: Create Calendar and Zodiac Tables
-- Purpose: Zodiac (12 con giáp), Day Info (lịch âm/dương), Zodiac Hour, Golden Hour Pattern

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ============================
-- 1) ZODIAC (12 con giáp)
-- ============================

DROP TABLE IF EXISTS zodiac;
CREATE TABLE zodiac (
    id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    code      VARCHAR(16) NOT NULL UNIQUE COMMENT 'ascii slug: ti, suu, dan...',
    name_vi   VARCHAR(32) NOT NULL COMMENT 'tên hiển thị: "Tý", "Sửu"...',
    order_no  TINYINT NOT NULL,

    INDEX idx_code (code),
    INDEX idx_order_no (order_no)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='12 con giáp (Zodiac signs)';

-- ============================
-- 2) DAY_INFO (lịch âm/dương)
-- ============================

DROP TABLE IF EXISTS day_info;
CREATE TABLE day_info (
    solar_date       DATE PRIMARY KEY COMMENT 'ngày dương (yyyy-MM-dd)',
    weekday          TINYINT NOT NULL COMMENT '1=Mon..7=Sun (ISO)',
    lunar_day        TINYINT NOT NULL,
    lunar_month      TINYINT NOT NULL,
    lunar_year       SMALLINT NOT NULL,
    lunar_leap_month TINYINT NOT NULL COMMENT '0=thường,1=nhuận',
    can_chi_day      VARCHAR(32) NOT NULL COMMENT '"Tân Sửu"',
    can_chi_month    VARCHAR(32) NOT NULL,
    can_chi_year     VARCHAR(32) NOT NULL,
    good_day_type    ENUM('NORMAL', 'HOANG_DAO', 'HAC_DAO') NOT NULL DEFAULT 'NORMAL',
    note             VARCHAR(255) NULL,

    KEY idx_lunar (lunar_year, lunar_month, lunar_day, lunar_leap_month)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Lịch âm/dương (Lunar/Solar calendar)';

-- ============================
-- 3) ZODIAC_HOUR
--    Mapping 12 con giáp (code) -> khung giờ
-- ============================

DROP TABLE IF EXISTS zodiac_hour;
CREATE TABLE zodiac_hour (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    branch_code VARCHAR(16) NOT NULL COMMENT 'tham chiếu zodiac.code (ti, suu,...)',
    start_hour  TINYINT NOT NULL COMMENT '0–23',
    end_hour    TINYINT NOT NULL COMMENT '0–23',

    CONSTRAINT uq_zodiac_hour_branch UNIQUE (branch_code),
    CONSTRAINT fk_zodiac_hour_branch
        FOREIGN KEY (branch_code) REFERENCES zodiac(code)
            ON UPDATE CASCADE ON DELETE CASCADE,
    INDEX idx_branch_code (branch_code)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Mapping 12 con giáp -> khung giờ';

-- ============================
-- 4) GOLDEN_HOUR_PATTERN
--    Mapping ĐỊA CHI NGÀY (code) -> list ĐỊA CHI GIỜ (code CSV)
-- ============================

DROP TABLE IF EXISTS golden_hour_pattern;
CREATE TABLE golden_hour_pattern (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    day_branch_code  VARCHAR(16) NOT NULL COMMENT 'vd: "ti","suu","dan"',
    good_branch_codes VARCHAR(255) NOT NULL COMMENT 'CSV: "ti,suu,thin,ty,than,dau"',

    CONSTRAINT uq_golden_day_branch UNIQUE (day_branch_code),
    CONSTRAINT fk_golden_day_branch
        FOREIGN KEY (day_branch_code) REFERENCES zodiac(code)
            ON UPDATE CASCADE ON DELETE CASCADE,
    INDEX idx_day_branch_code (day_branch_code)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Mapping ĐỊA CHI NGÀY -> list ĐỊA CHI GIỜ hoàng đạo';

DROP TABLE IF EXISTS good_day_rule;
CREATE TABLE good_day_rule (
                               id BIGINT PRIMARY KEY AUTO_INCREMENT,
                               lunar_month INT NOT NULL,
                               branch_code VARCHAR(10) NOT NULL,
                               fortune_type ENUM('NORMAL','HOANG_DAO','HAC_DAO') NOT NULL DEFAULT 'NORMAL'
);

SET FOREIGN_KEY_CHECKS = 1;

