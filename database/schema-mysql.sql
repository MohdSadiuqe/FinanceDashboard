-- MySQL 8+ DDL (optional manual setup). The app normally creates/updates schema via JPA (ddl-auto=update).

DROP TABLE IF EXISTS financial_records;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    password VARCHAR(255) NOT NULL,
    role VARCHAR(32) NOT NULL,
    is_active TINYINT(1) DEFAULT 1,
    CONSTRAINT uk_users_email UNIQUE (email),
    CONSTRAINT chk_users_role CHECK (role IN ('ADMIN', 'ANALYST', 'VIEWER'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE financial_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    amount DOUBLE NOT NULL,
    type VARCHAR(16) NOT NULL,
    category VARCHAR(255) NOT NULL,
    record_date DATE NOT NULL,
    note VARCHAR(255),
    user_id BIGINT,
    CONSTRAINT chk_financial_records_amount CHECK (amount >= 1),
    CONSTRAINT chk_financial_records_type CHECK (type IN ('INCOME', 'EXPENSE')),
    CONSTRAINT fk_financial_records_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_financial_records_user_id ON financial_records (user_id);
CREATE INDEX idx_financial_records_record_date ON financial_records (record_date);
CREATE INDEX idx_financial_records_type ON financial_records (type);
