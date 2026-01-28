-- Migration V14: Add role and referral fields to app_user
-- Purpose: Align app_user schema with production DB (role + referral tracking)

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- Add missing columns and indexes for role & referral tracking
ALTER TABLE app_user
    -- User role (if V10 chưa được áp dụng trên DB hiện tại)
    ADD COLUMN role VARCHAR(20) NOT NULL DEFAULT 'USER'
        COMMENT 'User role: USER or ADMIN'
        AFTER status,

    -- Referral fields (mirror logic from V11 section 9)
    ADD COLUMN referral_code VARCHAR(50) NULL UNIQUE
        COMMENT 'User referral code (for sharing)',
    ADD COLUMN referred_by_user_id BIGINT UNSIGNED NULL
        COMMENT 'User who referred this user',
    ADD COLUMN referral_count INT UNSIGNED NOT NULL DEFAULT 0
        COMMENT 'Number of successful referrals (for non-affiliate users)',

    -- Indexes & FK for referral fields
    ADD INDEX idx_role (role),
    ADD INDEX idx_referral_code (referral_code),
    ADD INDEX idx_referred_by_user_id (referred_by_user_id),
    ADD CONSTRAINT fk_user_referred_by
        FOREIGN KEY (referred_by_user_id)
        REFERENCES app_user(id)
        ON DELETE SET NULL;

SET FOREIGN_KEY_CHECKS = 1;



