-- Migration V8: Create Interpretation Fragments and Rules Tables
-- Purpose: Store rule-based interpretation fragments for Tu Vi charts
-- This enables deterministic interpretation without AI

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ============================
-- interpretation_fragments table
-- Stores individual interpretation fragments (mệnh đề tử vi)
-- ============================
DROP TABLE IF EXISTS interpretation_fragments;
CREATE TABLE interpretation_fragments (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    fragment_code VARCHAR(64) NOT NULL UNIQUE COMMENT 'Unique identifier (snake_case)',
    content TEXT NOT NULL COMMENT 'Interpretation fragment content (1-2 sentences)',
    tone ENUM('positive', 'neutral', 'negative') NOT NULL COMMENT 'Tone of the fragment',
    priority TINYINT NOT NULL DEFAULT 3 COMMENT 'Priority level (1=highest, 5=lowest)',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_fragment_code (fragment_code),
    INDEX idx_tone (tone),
    INDEX idx_priority (priority)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Interpretation fragments - reusable Tu Vi interpretation units';

-- ============================
-- interpretation_rules table
-- Stores rules that map FACT conditions to fragments
-- ============================
DROP TABLE IF EXISTS interpretation_rules;
CREATE TABLE interpretation_rules (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    fragment_code VARCHAR(64) NOT NULL COMMENT 'Reference to interpretation_fragments.fragment_code',
    conditions JSON NOT NULL COMMENT 'FACT conditions that trigger this fragment (palace, stars, brightness, etc.)',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_fragment_code (fragment_code),
    -- Note: JSON columns cannot be indexed directly in MySQL
    -- Use JSON functions (JSON_CONTAINS, JSON_EXTRACT) for queries instead
    FOREIGN KEY (fragment_code) REFERENCES interpretation_fragments(fragment_code) ON DELETE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Rules mapping FACT conditions to interpretation fragments';

SET FOREIGN_KEY_CHECKS = 1;
