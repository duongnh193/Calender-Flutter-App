-- Migration V10: Add role column to app_user table
-- Purpose: Role-based authorization (USER, ADMIN)

SET NAMES utf8mb4;

-- Add role column with default value USER
ALTER TABLE app_user 
ADD COLUMN role VARCHAR(20) NOT NULL DEFAULT 'USER' 
COMMENT 'User role: USER or ADMIN'
AFTER status;

-- Add index for role column
CREATE INDEX idx_role ON app_user(role);

-- Create first admin user (optional - uncomment and modify if needed)
-- INSERT INTO app_user (uuid, email, password_hash, full_name, status, role)
-- VALUES (
--     UUID(),
--     'admin@lichvannien.vn',
--     '$2a$10$...', -- BCrypt hash of password
--     'System Admin',
--     'ACTIVE',
--     'ADMIN'
-- );

