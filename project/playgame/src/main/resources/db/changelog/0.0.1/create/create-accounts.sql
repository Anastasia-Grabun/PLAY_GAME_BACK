CREATE TABLE accounts (
                          id BIGSERIAL PRIMARY KEY,
                          username VARCHAR(32) UNIQUE NOT NULL,
                          email VARCHAR UNIQUE NOT NULL,
                          created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                          balance DECIMAL(20, 2) DEFAULT 0.00
);
