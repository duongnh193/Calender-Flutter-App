-- Migration V11: Create Xu, Affiliate, and SePay Tables
-- Purpose: Xu currency system, Affiliate program, SePay integration

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ============================
-- 1) Xu Package Table
-- ============================
DROP TABLE IF EXISTS xu_package;
CREATE TABLE xu_package (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL COMMENT 'Package name',
    xu_amount INT UNSIGNED NOT NULL COMMENT 'Amount of xu in this package',
    price_vnd DECIMAL(15,2) NOT NULL COMMENT 'Price in VND',
    bonus_xu INT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'Bonus xu (promotion)',
    is_active BOOLEAN NOT NULL DEFAULT TRUE COMMENT 'Whether package is available',
    display_order INT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'Order for display',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_is_active (is_active),
    INDEX idx_display_order (display_order)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Xu packages for purchase';

-- ============================
-- 2) User Xu Account Table
-- ============================
DROP TABLE IF EXISTS user_xu_account;
CREATE TABLE user_xu_account (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNSIGNED NOT NULL UNIQUE COMMENT 'Link to app_user',
    xu_balance INT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'Current xu balance',
    total_xu_earned INT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'Total xu earned (deposits + commissions + bonuses)',
    total_xu_spent INT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'Total xu spent',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY fk_xu_account_user (user_id) REFERENCES app_user(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='User xu account balance';

-- ============================
-- 3) Xu Transaction Table
-- ============================
DROP TABLE IF EXISTS xu_transaction;
CREATE TABLE xu_transaction (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNSIGNED NOT NULL COMMENT 'Link to app_user',
    transaction_type ENUM('DEPOSIT', 'PURCHASE', 'REFERRAL_BONUS', 'AFFILIATE_COMMISSION', 'WITHDRAW', 'REFUND') NOT NULL,
    xu_amount INT NOT NULL COMMENT 'Positive for credit, negative for debit',
    vnd_amount DECIMAL(15,2) NULL COMMENT 'VND equivalent (for deposits/withdrawals)',
    reference_id VARCHAR(255) NULL COMMENT 'Reference to related transaction (sepay_transaction_id, withdrawal_request_id, etc.)',
    description TEXT NULL COMMENT 'Transaction description',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY fk_xu_transaction_user (user_id) REFERENCES app_user(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_transaction_type (transaction_type),
    INDEX idx_reference_id (reference_id),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Xu transaction history';

-- ============================
-- 4) Affiliate Config Table
-- ============================
DROP TABLE IF EXISTS affiliate_config;
CREATE TABLE affiliate_config (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    config_key VARCHAR(100) NOT NULL UNIQUE COMMENT 'Configuration key',
    config_value TEXT NOT NULL COMMENT 'Configuration value (JSON or string)',
    description TEXT NULL COMMENT 'Description of this config',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_config_key (config_key)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Affiliate system configuration';

-- Insert default affiliate configs
INSERT INTO affiliate_config (config_key, config_value, description) VALUES
('min_xu_to_join', '100', 'Minimum xu required to join affiliate program'),
('commission_rate', '0.30', 'Commission rate (30% = 0.30)'),
('free_referral_count', '3', 'Number of referrals needed for free xu reward'),
('free_referral_xu_reward', '10', 'Xu reward after free_referral_count referrals'),
('interpretation_price_xu', '10', 'Price per interpretation in xu'),
('xu_to_vnd_rate', '1000', '1 xu = 1000 VND');

-- ============================
-- 5) Affiliate Member Table
-- ============================
DROP TABLE IF EXISTS affiliate_member;
CREATE TABLE affiliate_member (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNSIGNED NOT NULL UNIQUE COMMENT 'Link to app_user',
    affiliate_code VARCHAR(50) NOT NULL UNIQUE COMMENT 'Unique affiliate code for sharing',
    status ENUM('ACTIVE', 'INACTIVE', 'SUSPENDED') NOT NULL DEFAULT 'ACTIVE',
    total_referrals INT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'Total number of successful referrals',
    total_commission_xu INT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'Total commission earned (in xu)',
    pending_commission_xu INT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'Pending commission (not withdrawn yet)',
    withdrawn_commission_xu INT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'Total commission withdrawn',
    joined_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY fk_affiliate_member_user (user_id) REFERENCES app_user(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_affiliate_code (affiliate_code),
    INDEX idx_status (status)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Affiliate program members';

-- ============================
-- 6) Referral Table
-- ============================
DROP TABLE IF EXISTS referral;
CREATE TABLE referral (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    referrer_user_id BIGINT UNSIGNED NOT NULL COMMENT 'User who referred',
    referred_user_id BIGINT UNSIGNED NOT NULL COMMENT 'User who was referred',
    affiliate_member_id BIGINT UNSIGNED NULL COMMENT 'Link to affiliate_member if referrer is affiliate',
    first_payment_amount DECIMAL(15,2) NULL COMMENT 'First payment amount from referred user',
    commission_xu INT UNSIGNED NULL COMMENT 'Commission earned (in xu)',
    commission_paid BOOLEAN NOT NULL DEFAULT FALSE COMMENT 'Whether commission was paid',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    converted_at DATETIME NULL COMMENT 'When referred user made first payment',

    FOREIGN KEY fk_referral_referrer (referrer_user_id) REFERENCES app_user(id) ON DELETE CASCADE,
    FOREIGN KEY fk_referral_referred (referred_user_id) REFERENCES app_user(id) ON DELETE CASCADE,
    FOREIGN KEY fk_referral_affiliate (affiliate_member_id) REFERENCES affiliate_member(id) ON DELETE SET NULL,
    UNIQUE KEY uk_referral (referrer_user_id, referred_user_id),
    INDEX idx_referrer_user_id (referrer_user_id),
    INDEX idx_referred_user_id (referred_user_id),
    INDEX idx_affiliate_member_id (affiliate_member_id),
    INDEX idx_commission_paid (commission_paid),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Referral tracking';

-- ============================
-- 7) Withdrawal Request Table
-- ============================
DROP TABLE IF EXISTS withdrawal_request;
CREATE TABLE withdrawal_request (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    affiliate_member_id BIGINT UNSIGNED NOT NULL COMMENT 'Link to affiliate_member',
    xu_amount INT UNSIGNED NOT NULL COMMENT 'Amount of xu to withdraw',
    vnd_amount DECIMAL(15,2) NOT NULL COMMENT 'VND equivalent',
    bank_name VARCHAR(255) NOT NULL COMMENT 'Bank name',
    bank_account VARCHAR(50) NOT NULL COMMENT 'Bank account number',
    account_holder_name VARCHAR(255) NOT NULL COMMENT 'Account holder name',
    status ENUM('PENDING', 'APPROVED', 'REJECTED', 'COMPLETED') NOT NULL DEFAULT 'PENDING',
    admin_note TEXT NULL COMMENT 'Admin note (for rejection reason, etc.)',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    processed_at DATETIME NULL COMMENT 'When admin processed this request',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY fk_withdrawal_affiliate (affiliate_member_id) REFERENCES affiliate_member(id) ON DELETE CASCADE,
    INDEX idx_affiliate_member_id (affiliate_member_id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Affiliate withdrawal requests';

-- ============================
-- 8) SePay Transaction Table
-- ============================
DROP TABLE IF EXISTS sepay_transaction;
CREATE TABLE sepay_transaction (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNSIGNED NULL COMMENT 'Link to app_user (nullable for anonymous)',
    sepay_transaction_id VARCHAR(255) NULL COMMENT 'Transaction ID from SePay',
    content VARCHAR(255) NOT NULL COMMENT 'Transfer content (for matching)',
    amount_vnd DECIMAL(15,2) NOT NULL COMMENT 'Amount in VND',
    xu_credited INT UNSIGNED NULL COMMENT 'Amount of xu credited',
    xu_package_id BIGINT UNSIGNED NULL COMMENT 'Link to xu_package if applicable',
    status ENUM('PENDING', 'COMPLETED', 'FAILED', 'CANCELLED') NOT NULL DEFAULT 'PENDING',
    raw_response TEXT NULL COMMENT 'Raw response from SePay webhook',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    completed_at DATETIME NULL COMMENT 'When transaction was completed',

    FOREIGN KEY fk_sepay_user (user_id) REFERENCES app_user(id) ON DELETE SET NULL,
    FOREIGN KEY fk_sepay_package (xu_package_id) REFERENCES xu_package(id) ON DELETE SET NULL,
    INDEX idx_user_id (user_id),
    INDEX idx_sepay_transaction_id (sepay_transaction_id),
    INDEX idx_content (content),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='SePay payment transactions';

-- ============================
-- 9) Update app_user table - Add referral fields
-- ============================
ALTER TABLE app_user
ADD COLUMN referral_code VARCHAR(50) NULL UNIQUE COMMENT 'User referral code (for sharing)',
ADD COLUMN referred_by_user_id BIGINT UNSIGNED NULL COMMENT 'User who referred this user',
ADD COLUMN referral_count INT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'Number of successful referrals (for non-affiliate users)',
ADD INDEX idx_referral_code (referral_code),
ADD INDEX idx_referred_by_user_id (referred_by_user_id),
ADD FOREIGN KEY fk_user_referred_by (referred_by_user_id) REFERENCES app_user(id) ON DELETE SET NULL;

SET FOREIGN_KEY_CHECKS = 1;

