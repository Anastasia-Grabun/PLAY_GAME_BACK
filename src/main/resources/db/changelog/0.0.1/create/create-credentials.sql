CREATE TABLE credentials (
                             login VARCHAR(32) PRIMARY KEY,
                             password VARCHAR(64) NOT NULL,
                             account_id BIGSERIAL UNIQUE NOT NULL REFERENCES accounts(id)
);