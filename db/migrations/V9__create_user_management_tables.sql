-- Migration V9: Create User Management Tables
-- Purpose: User CRUD, JWT sessions, fingerprint tracking, payment tracking for anti-cheating

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ============================
-- 1) User Table
-- ============================
DROP TABLE IF EXISTS app_user;
CREATE TABLE app_user (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    uuid VARCHAR(36) NOT NULL UNIQUE COMMENT 'UUID v4 for external reference',
    email VARCHAR(255) NULL UNIQUE COMMENT 'User email (optional)',
    phone VARCHAR(20) NULL UNIQUE COMMENT 'User phone (optional)',
    password_hash VARCHAR(255) NOT NULL COMMENT 'BCrypt hashed password',
    full_name VARCHAR(255) NULL COMMENT 'Display name',
    status ENUM('ACTIVE', 'INACTIVE', 'BANNED') NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    last_login_at DATETIME NULL,

    INDEX idx_uuid (uuid),
    INDEX idx_email (email),
    INDEX idx_phone (phone),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Application users';

-- ============================
-- 2) Fingerprint Table (must be created before user_session due to FK)
-- ============================
DROP TABLE IF EXISTS fingerprint;
CREATE TABLE fingerprint (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    fingerprint_id VARCHAR(64) NOT NULL UNIQUE COMMENT 'SHA-256 hash of normalized fingerprint',

    -- Client-side fingerprint data (JSON)
    fingerprint_data JSON NOT NULL COMMENT 'Raw fingerprint data from client: canvas_hash, webgl_hash, screen_size, timezone, language',

    -- Server-side fingerprint data
    ip_address VARCHAR(45) NOT NULL COMMENT 'IPv4 or IPv6 address',
    user_agent TEXT NULL COMMENT 'Browser/App user agent string',
    accept_headers TEXT NULL COMMENT 'Accept-* headers for additional fingerprinting',

    -- Normalized hash (combination of client + server)
    normalized_hash VARCHAR(64) NOT NULL COMMENT 'SHA-256 hash after normalization',

    -- Tracking
    first_seen_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_seen_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    usage_count INT UNSIGNED NOT NULL DEFAULT 1 COMMENT 'Number of times this fingerprint was used',

    INDEX idx_fingerprint_id (fingerprint_id),
    INDEX idx_normalized_hash (normalized_hash),
    INDEX idx_ip_address (ip_address),
    INDEX idx_first_seen_at (first_seen_at),
    INDEX idx_last_seen_at (last_seen_at)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Device/browser fingerprints for tracking and anti-cheating';

-- ============================
-- 3) User Session Table
-- ============================
DROP TABLE IF EXISTS user_session;
CREATE TABLE user_session (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNSIGNED NULL COMMENT 'NULL if anonymous session',
    session_token VARCHAR(255) NOT NULL UNIQUE COMMENT 'JWT token ID (jti claim)',
    refresh_token VARCHAR(255) NULL UNIQUE COMMENT 'Refresh token for session renewal',
    fingerprint_id VARCHAR(64) NOT NULL COMMENT 'SHA-256 hash linking to fingerprint table',

    -- Device/Client info
    ip_address VARCHAR(45) NOT NULL COMMENT 'IPv4 or IPv6',
    user_agent TEXT NULL,
    device_type VARCHAR(50) NULL COMMENT 'mobile, desktop, tablet',
    platform VARCHAR(50) NULL COMMENT 'android, ios, web',

    -- Session lifecycle
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at DATETIME NOT NULL COMMENT 'When the session expires',
    last_activity_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN NOT NULL DEFAULT TRUE COMMENT 'FALSE when logged out or revoked',

    FOREIGN KEY fk_session_user (user_id) REFERENCES app_user(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_fingerprint_id (fingerprint_id),
    INDEX idx_session_token (session_token),
    INDEX idx_refresh_token (refresh_token),
    INDEX idx_ip_address (ip_address),
    INDEX idx_expires_at (expires_at),
    INDEX idx_is_active (is_active),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='User sessions with JWT tokens';

-- ============================
-- 4) Payment Transaction Table
-- ============================
DROP TABLE IF EXISTS payment_transaction;
CREATE TABLE payment_transaction (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    transaction_uuid VARCHAR(36) NOT NULL UNIQUE COMMENT 'UUID for external reference',

    -- User/Session linking
    user_id BIGINT UNSIGNED NULL COMMENT 'NULL if anonymous payment',
    fingerprint_id VARCHAR(64) NOT NULL COMMENT 'Link with fingerprint for anti-cheating',
    session_id BIGINT UNSIGNED NULL COMMENT 'Session that initiated payment',

    -- Transaction details
    transaction_type ENUM('TUVI_INTERPRETATION', 'HOROSCOPE_LIFETIME', 'SUBSCRIPTION', 'OTHER') NOT NULL,
    amount DECIMAL(15,2) NOT NULL COMMENT 'Amount in currency',
    currency VARCHAR(3) NOT NULL DEFAULT 'VND',

    -- Payment processing
    payment_method VARCHAR(50) NULL COMMENT 'momo, zalopay, vnpay, bank_transfer, etc.',
    payment_status ENUM('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED', 'CANCELLED', 'REFUNDED') NOT NULL DEFAULT 'PENDING',
    payment_gateway_transaction_id VARCHAR(255) NULL COMMENT 'Transaction ID from payment gateway',
    payment_gateway_response JSON NULL COMMENT 'Full response from payment gateway',

    -- Content linking (for Tu Vi interpretation)
    chart_hash VARCHAR(64) NULL COMMENT 'Link with Tu Vi chart hash',
    content_type VARCHAR(50) NULL COMMENT 'Type of content purchased',
    content_id VARCHAR(255) NULL COMMENT 'ID of content purchased',

    -- Metadata
    metadata JSON NULL COMMENT 'Additional transaction data',
    ip_address VARCHAR(45) NULL COMMENT 'IP at time of transaction',

    -- Timestamps
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    completed_at DATETIME NULL COMMENT 'When payment was completed',
    expires_at DATETIME NULL COMMENT 'When payment link expires',

    FOREIGN KEY fk_payment_user (user_id) REFERENCES app_user(id) ON DELETE SET NULL,
    FOREIGN KEY fk_payment_session (session_id) REFERENCES user_session(id) ON DELETE SET NULL,
    INDEX idx_transaction_uuid (transaction_uuid),
    INDEX idx_user_id (user_id),
    INDEX idx_fingerprint_id (fingerprint_id),
    INDEX idx_session_id (session_id),
    INDEX idx_payment_status (payment_status),
    INDEX idx_payment_method (payment_method),
    INDEX idx_chart_hash (chart_hash),
    INDEX idx_content_type_id (content_type, content_id),
    INDEX idx_created_at (created_at),
    INDEX idx_completed_at (completed_at)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Payment transactions for content access';

-- ============================
-- 5) Access Log Table
-- ============================
DROP TABLE IF EXISTS access_log;
CREATE TABLE access_log (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,

    -- User/Session linking
    user_id BIGINT UNSIGNED NULL,
    session_id BIGINT UNSIGNED NULL,
    fingerprint_id VARCHAR(64) NOT NULL,

    -- Request info
    ip_address VARCHAR(45) NOT NULL,
    endpoint VARCHAR(255) NOT NULL COMMENT 'API endpoint path',
    method VARCHAR(10) NOT NULL COMMENT 'HTTP method',
    request_body_hash VARCHAR(64) NULL COMMENT 'SHA-256 hash of request body for duplicate detection',
    query_params TEXT NULL COMMENT 'Query parameters',

    -- Response info
    response_status INT NULL COMMENT 'HTTP response status code',
    response_time_ms INT NULL COMMENT 'Response time in milliseconds',

    -- Metadata
    user_agent TEXT NULL,
    referer VARCHAR(500) NULL,

    -- Timestamp
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Note: No FK constraints to avoid blocking on log inserts
    INDEX idx_user_id (user_id),
    INDEX idx_session_id (session_id),
    INDEX idx_fingerprint_id (fingerprint_id),
    INDEX idx_ip_address (ip_address),
    INDEX idx_endpoint (endpoint),
    INDEX idx_method (method),
    INDEX idx_response_status (response_status),
    INDEX idx_created_at (created_at),
    INDEX idx_user_endpoint (user_id, endpoint),
    INDEX idx_fingerprint_endpoint (fingerprint_id, endpoint)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='API access logs for auditing and analytics';

-- ============================
-- 6) Content Access Table (tracks what content a fingerprint/user has access to)
-- ============================
DROP TABLE IF EXISTS content_access;
CREATE TABLE content_access (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,

    -- Access identification
    user_id BIGINT UNSIGNED NULL,
    fingerprint_id VARCHAR(64) NOT NULL,

    -- Content identification
    content_type VARCHAR(50) NOT NULL COMMENT 'TUVI_INTERPRETATION, HOROSCOPE_LIFETIME, etc.',
    content_id VARCHAR(255) NOT NULL COMMENT 'chart_hash for Tu Vi, can_chi for horoscope, etc.',

    -- Payment linking
    payment_transaction_id BIGINT UNSIGNED NULL,

    -- Access details
    access_granted_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    access_expires_at DATETIME NULL COMMENT 'NULL means permanent access',
    access_count INT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'Number of times accessed',
    last_accessed_at DATETIME NULL,

    -- Status
    is_active BOOLEAN NOT NULL DEFAULT TRUE,

    FOREIGN KEY fk_access_user (user_id) REFERENCES app_user(id) ON DELETE SET NULL,
    FOREIGN KEY fk_access_payment (payment_transaction_id) REFERENCES payment_transaction(id) ON DELETE SET NULL,
    UNIQUE KEY uk_fingerprint_content (fingerprint_id, content_type, content_id),
    INDEX idx_user_id (user_id),
    INDEX idx_fingerprint_id (fingerprint_id),
    INDEX idx_content_type_id (content_type, content_id),
    INDEX idx_payment_transaction_id (payment_transaction_id),
    INDEX idx_access_granted_at (access_granted_at),
    INDEX idx_is_active (is_active)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Tracks content access rights per fingerprint/user';

SET FOREIGN_KEY_CHECKS = 1;

