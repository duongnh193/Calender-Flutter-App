-- Migration V3: Update existing records with normalized Can-Chi
-- Run this after V2 if the generated column approach doesn't work in your MySQL version
-- Author: Backend Engineer
-- Date: 2025-12-09

SET NAMES utf8mb4;

-- For older MySQL versions that don't support generated columns, 
-- use this manual update approach:

-- First, add the column if it doesn't exist (non-generated version)
-- ALTER TABLE horoscope_lifetime
--     ADD COLUMN can_chi_norm VARCHAR(128) NULL;

-- Update existing records
-- UPDATE horoscope_lifetime
-- SET can_chi_norm = LOWER(REPLACE(can_chi, ' ', ''))
-- WHERE can_chi_norm IS NULL OR can_chi_norm = '';

-- Create trigger to auto-update can_chi_norm on insert/update
-- DELIMITER //
-- CREATE TRIGGER trg_lifetime_canchi_norm_insert
-- BEFORE INSERT ON horoscope_lifetime
-- FOR EACH ROW
-- BEGIN
--     SET NEW.can_chi_norm = LOWER(REPLACE(NEW.can_chi, ' ', ''));
-- END//
-- 
-- CREATE TRIGGER trg_lifetime_canchi_norm_update
-- BEFORE UPDATE ON horoscope_lifetime
-- FOR EACH ROW
-- BEGIN
--     IF NEW.can_chi != OLD.can_chi THEN
--         SET NEW.can_chi_norm = LOWER(REPLACE(NEW.can_chi, ' ', ''));
--     END IF;
-- END//
-- DELIMITER ;

-- Verification query
SELECT 
    can_chi, 
    can_chi_norm,
    gender,
    zodiac_id
FROM horoscope_lifetime
LIMIT 10;

