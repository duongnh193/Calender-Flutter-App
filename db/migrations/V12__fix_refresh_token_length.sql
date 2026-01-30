-- Migration V12: Fix refresh_token column length
-- Purpose: JWT tokens can be longer than 255 characters, need to increase column size
-- JWT tokens typically range from 400-800 characters depending on claims

SET NAMES utf8mb4;

-- NOTE:
-- This migration is IDEMPOTENT. It will:
-- - Increase refresh_token length to 768 if smaller
-- - Ensure there is a UNIQUE index for refresh_token (prefix 255) if none exists
-- - Avoid failing when indexes/columns already exist

-- Change refresh_token from VARCHAR(255) to VARCHAR(1000) to accommodate long JWT tokens
-- Using VARCHAR instead of TEXT to maintain UNIQUE constraint capability
SET @current_len := (
    SELECT CHARACTER_MAXIMUM_LENGTH
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'user_session'
      AND COLUMN_NAME = 'refresh_token'
    LIMIT 1
);

SET @sql := IF(
    @current_len IS NULL,
    'SELECT 1',
    IF(
        @current_len < 768,
        'ALTER TABLE user_session MODIFY COLUMN refresh_token VARCHAR(768) NULL COMMENT ''Refresh token for session renewal (JWT token can be long)''',
        'SELECT 1'
    )
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Recreate unique index on refresh_token
-- MySQL 5.7+ supports unique index on VARCHAR(1000) up to 3072 bytes (with utf8mb4 = 768 chars)
-- For safety, we'll use a prefix index of 255 characters which should be unique enough
-- (JWT tokens have jti claim which is UUID, so first 255 chars should be unique)
SET @has_any_unique := (
    SELECT COUNT(*)
    FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'user_session'
      AND COLUMN_NAME = 'refresh_token'
      AND NON_UNIQUE = 0
);

-- If no UNIQUE index exists for refresh_token, ensure idx_refresh_token exists as UNIQUE (prefix 255)
SET @has_idx_name := (
    SELECT COUNT(*)
    FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'user_session'
      AND INDEX_NAME = 'idx_refresh_token'
);

SET @sql := IF(
    @has_any_unique = 0 AND @has_idx_name > 0,
    'ALTER TABLE user_session DROP INDEX idx_refresh_token',
    'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(
    @has_any_unique = 0,
    'CREATE UNIQUE INDEX idx_refresh_token ON user_session (refresh_token(255))',
    'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Note: session_token stores jti (token ID) which is UUID format (~36 chars), so VARCHAR(255) is sufficient

