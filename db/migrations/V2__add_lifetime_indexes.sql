-- Migration V2: Add indexes for lifetime horoscope lookup optimization
-- Author: Backend Engineer
-- Date: 2025-12-09
-- Description: Adds indexes to optimize Can-Chi and zodiac-based lookups

SET NAMES utf8mb4;

-- ==========================================
-- 1. Add normalized Can-Chi column and index
-- ==========================================

-- Add normalized can_chi column (lowercase, no spaces) for faster lookups
-- Note: MySQL doesn't have a built-in accent remover, so we just normalize spaces and case
ALTER TABLE horoscope_lifetime
    ADD COLUMN IF NOT EXISTS can_chi_norm VARCHAR(128)
        GENERATED ALWAYS AS (LOWER(REPLACE(can_chi, ' ', ''))) STORED;

-- Create index on normalized can_chi + gender for fast lookups
CREATE INDEX IF NOT EXISTS idx_hl_canchi_norm_gender
    ON horoscope_lifetime(can_chi_norm, gender);

-- ==========================================
-- 2. Add fallback index for zodiac + gender
-- ==========================================

-- Create composite index for fallback lookups by zodiac_id + gender
CREATE INDEX IF NOT EXISTS idx_hl_zodiac_gender
    ON horoscope_lifetime(zodiac_id, gender);

-- ==========================================
-- 3. Add index on zodiac_id alone for general queries
-- ==========================================

CREATE INDEX IF NOT EXISTS idx_hl_zodiac
    ON horoscope_lifetime(zodiac_id);

-- ==========================================
-- 4. Verify indexes were created
-- ==========================================

-- This will show all indexes on horoscope_lifetime table
-- SHOW INDEX FROM horoscope_lifetime;

-- ==========================================
-- Notes:
-- - can_chi_norm is a generated column that automatically updates when can_chi changes
-- - The index on (can_chi_norm, gender) optimizes the normalized lookup query
-- - The index on (zodiac_id, gender) optimizes the fallback query
-- ==========================================

