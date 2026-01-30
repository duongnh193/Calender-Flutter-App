-- Migration V4: Extend horoscope tables to support richer metadata
-- This migration adds additional fields to support structured content
-- Most content will be stored in JSON metadata field, but we add some helper fields
-- 
-- NOTE:
-- This migration is IDEMPOTENT (safe to run multiple times).
-- We check INFORMATION_SCHEMA and only add missing columns to avoid failures when DB already has them.

SET NAMES utf8mb4;

-- ============================
-- 1) horoscope_lifetime columns
-- ============================
SET @tbl := 'horoscope_lifetime';

SET @col := 'love_by_month_group1';
SET @exists := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = @tbl AND COLUMN_NAME = @col);
SET @sql := IF(@exists = 0, CONCAT('ALTER TABLE ', @tbl, ' ADD COLUMN love_by_month_group1 TEXT NULL COMMENT ''Tình duyên nhóm 1 (tháng 5,6,9)'''), 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @col := 'love_by_month_group2';
SET @exists := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = @tbl AND COLUMN_NAME = @col);
SET @sql := IF(@exists = 0, CONCAT('ALTER TABLE ', @tbl, ' ADD COLUMN love_by_month_group2 TEXT NULL COMMENT ''Tình duyên nhóm 2 (tháng 1,2,7,10,11,12)'''), 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @col := 'love_by_month_group3';
SET @exists := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = @tbl AND COLUMN_NAME = @col);
SET @sql := IF(@exists = 0, CONCAT('ALTER TABLE ', @tbl, ' ADD COLUMN love_by_month_group3 TEXT NULL COMMENT ''Tình duyên nhóm 3 (tháng 3,4,8)'''), 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @col := 'compatible_ages';
SET @exists := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = @tbl AND COLUMN_NAME = @col);
SET @sql := IF(@exists = 0, CONCAT('ALTER TABLE ', @tbl, ' ADD COLUMN compatible_ages TEXT NULL COMMENT ''Tuổi hợp làm ăn (JSON array)'''), 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @col := 'difficult_years';
SET @exists := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = @tbl AND COLUMN_NAME = @col);
SET @sql := IF(@exists = 0, CONCAT('ALTER TABLE ', @tbl, ' ADD COLUMN difficult_years TEXT NULL COMMENT ''Năm khó khăn (JSON array)'''), 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @col := 'incompatible_ages';
SET @exists := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = @tbl AND COLUMN_NAME = @col);
SET @sql := IF(@exists = 0, CONCAT('ALTER TABLE ', @tbl, ' ADD COLUMN incompatible_ages TEXT NULL COMMENT ''Tuổi đại kỵ (JSON array)'''), 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @col := 'yearly_progression';
SET @exists := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = @tbl AND COLUMN_NAME = @col);
SET @sql := IF(@exists = 0, CONCAT('ALTER TABLE ', @tbl, ' ADD COLUMN yearly_progression TEXT NULL COMMENT ''Diễn biến từng năm (JSON object)'''), 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @col := 'ritual_guidance';
SET @exists := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = @tbl AND COLUMN_NAME = @col);
SET @sql := IF(@exists = 0, CONCAT('ALTER TABLE ', @tbl, ' ADD COLUMN ritual_guidance TEXT NULL COMMENT ''Hướng dẫn nghi lễ cúng sao'''), 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Add additional fields to horoscope_yearly for structured sections
-- ============================
-- 2) horoscope_yearly columns
-- ============================
SET @tbl := 'horoscope_yearly';

SET @col := 'cung_menh';
SET @exists := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = @tbl AND COLUMN_NAME = @col);
SET @sql := IF(@exists = 0, CONCAT('ALTER TABLE ', @tbl, ' ADD COLUMN cung_menh TEXT NULL COMMENT ''Cung Mệnh với sao và giải thích'''), 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @col := 'cung_xung_chieu';
SET @exists := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = @tbl AND COLUMN_NAME = @col);
SET @sql := IF(@exists = 0, CONCAT('ALTER TABLE ', @tbl, ' ADD COLUMN cung_xung_chieu TEXT NULL COMMENT ''Cung Xung Chiếu'''), 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @col := 'cung_tam_hop';
SET @exists := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = @tbl AND COLUMN_NAME = @col);
SET @sql := IF(@exists = 0, CONCAT('ALTER TABLE ', @tbl, ' ADD COLUMN cung_tam_hop TEXT NULL COMMENT ''Cung Tam Hợp'''), 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @col := 'cung_nhi_hop';
SET @exists := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = @tbl AND COLUMN_NAME = @col);
SET @sql := IF(@exists = 0, CONCAT('ALTER TABLE ', @tbl, ' ADD COLUMN cung_nhi_hop TEXT NULL COMMENT ''Cung Nhị Hợp'''), 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @col := 'van_han';
SET @exists := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = @tbl AND COLUMN_NAME = @col);
SET @sql := IF(@exists = 0, CONCAT('ALTER TABLE ', @tbl, ' ADD COLUMN van_han TEXT NULL COMMENT ''Vận hạn chi tiết (JSON)'''), 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @col := 'tu_tru';
SET @exists := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = @tbl AND COLUMN_NAME = @col);
SET @sql := IF(@exists = 0, CONCAT('ALTER TABLE ', @tbl, ' ADD COLUMN tu_tru TEXT NULL COMMENT ''Tứ trụ (JSON)'''), 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @col := 'phong_thuy';
SET @exists := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = @tbl AND COLUMN_NAME = @col);
SET @sql := IF(@exists = 0, CONCAT('ALTER TABLE ', @tbl, ' ADD COLUMN phong_thuy TEXT NULL COMMENT ''Phong thủy may mắn (JSON)'''), 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @col := 'qa_section';
SET @exists := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = @tbl AND COLUMN_NAME = @col);
SET @sql := IF(@exists = 0, CONCAT('ALTER TABLE ', @tbl, ' ADD COLUMN qa_section TEXT NULL COMMENT ''Q&A section (JSON array)'''), 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @col := 'conclusion';
SET @exists := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = @tbl AND COLUMN_NAME = @col);
SET @sql := IF(@exists = 0, CONCAT('ALTER TABLE ', @tbl, ' ADD COLUMN conclusion TEXT NULL COMMENT ''Lời kết'''), 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Add monthly breakdown to horoscope_yearly
SET @col := 'monthly_breakdown';
SET @exists := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = @tbl AND COLUMN_NAME = @col);
SET @sql := IF(@exists = 0, CONCAT('ALTER TABLE ', @tbl, ' ADD COLUMN monthly_breakdown TEXT NULL COMMENT ''Dự đoán theo tháng (JSON object)'''), 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Note: Most detailed content will still be stored in metadata JSON field
-- These additional fields are for commonly accessed structured data
-- to improve query performance and readability
