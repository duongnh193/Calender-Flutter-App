-- Migration V9: Fix thuan_nghich column length
-- Issue: Data truncation error - thuan_nghich values can be longer than VARCHAR(16)
-- Expected values: "THUAN" or "NGHICH" but sometimes includes extra text like "Âm nam - Nghịch lý"
-- Solution: Increase column length to VARCHAR(64) to accommodate longer descriptive values

SET NAMES utf8mb4;

-- Alter natal_chart table
ALTER TABLE natal_chart 
    MODIFY COLUMN thuan_nghich VARCHAR(64) NOT NULL COMMENT 'Thuận or Nghịch (may include descriptive text)';
