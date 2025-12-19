-- Migration V4: Extend horoscope tables to support richer metadata
-- This migration adds additional fields to support structured content
-- Most content will be stored in JSON metadata field, but we add some helper fields
-- 
-- Note: MySQL does not support IF NOT EXISTS in ALTER TABLE ADD COLUMN
-- If columns already exist, this migration will fail. Check manually or use a procedure.

SET NAMES utf8mb4;

-- Add additional fields to horoscope_lifetime for better structured content
ALTER TABLE horoscope_lifetime
    ADD COLUMN love_by_month_group1 TEXT NULL COMMENT 'Tình duyên nhóm 1 (tháng 5,6,9)',
    ADD COLUMN love_by_month_group2 TEXT NULL COMMENT 'Tình duyên nhóm 2 (tháng 1,2,7,10,11,12)',
    ADD COLUMN love_by_month_group3 TEXT NULL COMMENT 'Tình duyên nhóm 3 (tháng 3,4,8)',
    ADD COLUMN compatible_ages TEXT NULL COMMENT 'Tuổi hợp làm ăn (JSON array)',
    ADD COLUMN difficult_years TEXT NULL COMMENT 'Năm khó khăn (JSON array)',
    ADD COLUMN incompatible_ages TEXT NULL COMMENT 'Tuổi đại kỵ (JSON array)',
    ADD COLUMN yearly_progression TEXT NULL COMMENT 'Diễn biến từng năm (JSON object)',
    ADD COLUMN ritual_guidance TEXT NULL COMMENT 'Hướng dẫn nghi lễ cúng sao';

-- Add additional fields to horoscope_yearly for structured sections
ALTER TABLE horoscope_yearly
    ADD COLUMN cung_menh TEXT NULL COMMENT 'Cung Mệnh với sao và giải thích',
    ADD COLUMN cung_xung_chieu TEXT NULL COMMENT 'Cung Xung Chiếu',
    ADD COLUMN cung_tam_hop TEXT NULL COMMENT 'Cung Tam Hợp',
    ADD COLUMN cung_nhi_hop TEXT NULL COMMENT 'Cung Nhị Hợp',
    ADD COLUMN van_han TEXT NULL COMMENT 'Vận hạn chi tiết (JSON)',
    ADD COLUMN tu_tru TEXT NULL COMMENT 'Tứ trụ (JSON)',
    ADD COLUMN phong_thuy TEXT NULL COMMENT 'Phong thủy may mắn (JSON)',
    ADD COLUMN qa_section TEXT NULL COMMENT 'Q&A section (JSON array)',
    ADD COLUMN conclusion TEXT NULL COMMENT 'Lời kết';

-- Add monthly breakdown to horoscope_yearly
ALTER TABLE horoscope_yearly
    ADD COLUMN monthly_breakdown TEXT NULL COMMENT 'Dự đoán theo tháng (JSON object)';

-- Note: Most detailed content will still be stored in metadata JSON field
-- These additional fields are for commonly accessed structured data
-- to improve query performance and readability
