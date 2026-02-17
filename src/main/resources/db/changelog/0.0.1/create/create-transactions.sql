CREATE TYPE transaction_status AS ENUM ('PENDING', 'COMPLETED', 'FAILED');

CREATE TABLE transactions (
                              id BIGSERIAL PRIMARY KEY,
                              account_id BIGSERIAL NOT NULL REFERENCES accounts(id),
                              time TIMESTAMP NOT NULL DEFAULT NOW(),
                              amount DECIMAL(20, 2) NOT NULL,
                              status transaction_status NOT NULL
);
