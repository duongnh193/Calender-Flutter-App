-- Migration V7: Update Interpretation Tables - Remove FACT fields
-- Purpose: Remove FACT data from interpretation tables, keep only interpretation content
-- FACT data is now stored in natal_chart, natal_palace, natal_star tables

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ============================
-- 1) Update tuvi_interpretation table
-- Remove FACT fields, keep only interpretation and reference to FACT
-- ============================

-- Remove FACT fields that should not be here
ALTER TABLE tuvi_interpretation
    DROP COLUMN birth_date,
    DROP COLUMN birth_hour,
    DROP COLUMN lunar_year_can_chi;

-- Add foreign key reference to natal_chart (chart_hash already exists)
-- Note: MySQL doesn't support ADD FOREIGN KEY IF NOT EXISTS, so we'll add it separately if needed

-- ============================
-- 2) Update tuvi_palace_interpretation table
-- Remove FACT fields (palace_chi, can_chi_prefix, has_tuan, has_triet)
-- Keep only interpretation content and palace_code for reference
-- ============================

ALTER TABLE tuvi_palace_interpretation
    DROP COLUMN palace_name,
    DROP COLUMN palace_chi,
    DROP COLUMN can_chi_prefix,
    DROP COLUMN has_tuan,
    DROP COLUMN has_triet;

-- Note: tuan_triet_effect is kept because it's INTERPRETATION of Tuần/Triệt effects, not FACT

-- ============================
-- 3) Update tuvi_star_interpretation table
-- Remove FACT fields (star_name, star_type, brightness)
-- Keep only interpretation content and star_code for reference
-- ============================

ALTER TABLE tuvi_star_interpretation
    DROP COLUMN star_name,
    DROP COLUMN star_type,
    DROP COLUMN brightness;

-- Note: star_code is kept for reference to FACT, but is NOT stored as FACT here

-- ============================
-- 4) Add foreign key constraint from tuvi_interpretation to natal_chart
-- (if not already exists)
-- ============================

-- Check if constraint exists and add if not
-- Note: MySQL doesn't support IF NOT EXISTS for constraints directly
-- We'll handle this in application code or use a procedure

SET FOREIGN_KEY_CHECKS = 1;

-- ============================
-- Notes:
-- - After migration, tuvi_interpretation.chart_hash should reference natal_chart.chart_hash
-- - palace_code and star_code in interpretation tables are for reference only
-- - All FACT data must come from natal_chart, natal_palace, natal_star tables
-- ============================
