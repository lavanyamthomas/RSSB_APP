CREATE TABLE IF NOT EXISTS user_roles (
    role_code VARCHAR(50) PRIMARY KEY,
    role_name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255)
);

INSERT INTO user_roles (role_code, role_name, description)
VALUES
    ('ROLE_USER', 'ROLE_USER', 'Default application user role'),
    ('ROLE_ADMIN', 'ROLE_ADMIN', 'Administrator role with full access'),
    ('ROLE_VISITOR', 'ROLE_VISITOR', 'Visitor role for basic registration access')
ON CONFLICT (role_code) DO UPDATE
SET role_name = EXCLUDED.role_name,
    description = EXCLUDED.description;
