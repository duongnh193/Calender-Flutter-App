-- Migration V10: Change tuvi_star_interpretation.summary from VARCHAR(512) to TEXT
-- Purpose: Allow storing full summary content without length restriction
-- The summary field was previously limited to 512 characters, but fragment content can exceed this limit

SET NAMES utf8mb4;

-- Change summary column from VARCHAR(512) to TEXT to support longer content
ALTER TABLE tuvi_star_interpretation
    MODIFY COLUMN summary TEXT COMMENT 'Short summary of star influence (full content from fragment)';
