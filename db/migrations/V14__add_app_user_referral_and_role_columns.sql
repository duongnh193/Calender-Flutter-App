-- Migration V14: Add role and referral fields to app_user
-- Purpose: Align app_user schema with production DB (role + referral tracking)

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- IMPORTANT:
-- This migration is written to be IDEMPOTENT for environments where V10/V11 may have been applied partially.
-- We check INFORMATION_SCHEMA and only add missing columns/indexes/foreign keys.

-- 1) role column + index
SET @col_role_exists := (
    SELECT COUNT(*)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'app_user'
      AND COLUMN_NAME = 'role'
);
SET @sql := IF(
    @col_role_exists = 0,
    'ALTER TABLE app_user ADD COLUMN role VARCHAR(20) NOT NULL DEFAULT ''USER'' COMMENT ''User role: USER or ADMIN'' AFTER status',
    'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @idx_role_exists := (
    SELECT COUNT(*)
    FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'app_user'
      AND INDEX_NAME = 'idx_role'
);
SET @sql := IF(
    @idx_role_exists = 0,
    'CREATE INDEX idx_role ON app_user(role)',
    'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 2) referral_code column + unique index
SET @col_referral_code_exists := (
    SELECT COUNT(*)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'app_user'
      AND COLUMN_NAME = 'referral_code'
);
SET @sql := IF(
    @col_referral_code_exists = 0,
    'ALTER TABLE app_user ADD COLUMN referral_code VARCHAR(50) NULL COMMENT ''User referral code (for sharing)''',
    'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @idx_referral_code_exists := (
    SELECT COUNT(*)
    FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'app_user'
      AND INDEX_NAME = 'idx_referral_code'
);
SET @sql := IF(
    @idx_referral_code_exists = 0,
    'CREATE UNIQUE INDEX idx_referral_code ON app_user(referral_code)',
    'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 3) referred_by_user_id column + index + FK
SET @col_referred_by_exists := (
    SELECT COUNT(*)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'app_user'
      AND COLUMN_NAME = 'referred_by_user_id'
);
SET @sql := IF(
    @col_referred_by_exists = 0,
    'ALTER TABLE app_user ADD COLUMN referred_by_user_id BIGINT UNSIGNED NULL COMMENT ''User who referred this user''',
    'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @idx_referred_by_exists := (
    SELECT COUNT(*)
    FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'app_user'
      AND INDEX_NAME = 'idx_referred_by_user_id'
);
SET @sql := IF(
    @idx_referred_by_exists = 0,
    'CREATE INDEX idx_referred_by_user_id ON app_user(referred_by_user_id)',
    'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @fk_referred_by_exists := (
    SELECT COUNT(*)
    FROM information_schema.KEY_COLUMN_USAGE
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'app_user'
      AND CONSTRAINT_NAME = 'fk_user_referred_by'
);
SET @sql := IF(
    @fk_referred_by_exists = 0,
    'ALTER TABLE app_user ADD CONSTRAINT fk_user_referred_by FOREIGN KEY (referred_by_user_id) REFERENCES app_user(id) ON DELETE SET NULL',
    'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 4) referral_count column
SET @col_referral_count_exists := (
    SELECT COUNT(*)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'app_user'
      AND COLUMN_NAME = 'referral_count'
);
SET @sql := IF(
    @col_referral_count_exists = 0,
    'ALTER TABLE app_user ADD COLUMN referral_count INT UNSIGNED NOT NULL DEFAULT 0 COMMENT ''Number of successful referrals (for non-affiliate users)''',
    'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET FOREIGN_KEY_CHECKS = 1;



