CREATE TABLE top_ups (
                         id BIGSERIAL PRIMARY KEY,
                         account_id BIGSERIAL NOT NULL REFERENCES accounts(id),
                         amount DECIMAL(20, 2) NOT NULL,
                         top_up_date TIMESTAMP NOT NULL DEFAULT NOW()
);