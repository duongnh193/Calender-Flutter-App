-- Script to create admin user manually
-- Usage: Run this SQL script directly in your database
--
-- IMPORTANT:
-- 1. Change the email, phone, password_hash before running
-- 2. Generate BCrypt hash for your password at: https://bcrypt-generator.com/
-- 3. Or use Spring BCryptPasswordEncoder to generate hash

SET NAMES utf8mb4;

-- Option 1: Create admin with email
INSERT INTO app_user (
    uuid,
    email,
    password_hash,
    full_name,
    status,
    role,
    created_at,
    updated_at
) VALUES (
    UUID(),
    'admin@test.com',  -- CHANGE THIS EMAIL
    -- BCrypt hash for password: "admin123"
    -- To generate new hash: https://bcrypt-generator.com/
    '$2a$12$rslTDbWPbRQV.UvGq/63.uyMvjfE1eB8vXRgpaz926..ZjJhVdTD.',
    'System Administrator',  -- CHANGE THIS NAME
    'ACTIVE',
    'ADMIN',
    NOW(),
    NOW()
);

-- Option 2: Create admin with phone (if you prefer phone)
-- INSERT INTO app_user (
--     uuid,
--     phone,
--     password_hash,
--     full_name,
--     status,
--     role,
--     created_at,
--     updated_at
-- ) VALUES (
--     UUID(),
--     '0912345678',  -- CHANGE THIS PHONE
--     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
--     'System Administrator',
--     'ACTIVE',
--     'ADMIN',
--     NOW(),
--     NOW()
-- );

-- Verify admin was created
SELECT
    id,
    uuid,
    email,
    phone,
    full_name,
    status,
    role,
    created_at
FROM app_user
WHERE role = 'ADMIN'
ORDER BY created_at DESC;

