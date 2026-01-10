-- Migration V12: Fix refresh_token column length
-- Purpose: JWT tokens can be longer than 255 characters, need to increase column size
-- JWT tokens typically range from 400-800 characters depending on claims

SET NAMES utf8mb4;

-- Drop the existing unique index first
# ALTER TABLE user_session DROP INDEX  idx_refresh_token;

-- Change refresh_token from VARCHAR(255) to VARCHAR(1000) to accommodate long JWT tokens
-- Using VARCHAR instead of TEXT to maintain UNIQUE constraint capability
ALTER TABLE user_session
MODIFY COLUMN refresh_token VARCHAR(768) NULL COMMENT 'Refresh token for session renewal (JWT token can be long)';

-- Recreate unique index on refresh_token
-- MySQL 5.7+ supports unique index on VARCHAR(1000) up to 3072 bytes (with utf8mb4 = 768 chars)
-- For safety, we'll use a prefix index of 255 characters which should be unique enough
-- (JWT tokens have jti claim which is UUID, so first 255 chars should be unique)
CREATE UNIQUE INDEX idx_refresh_token ON user_session (refresh_token(255));

-- Note: session_token stores jti (token ID) which is UUID format (~36 chars), so VARCHAR(255) is sufficient

