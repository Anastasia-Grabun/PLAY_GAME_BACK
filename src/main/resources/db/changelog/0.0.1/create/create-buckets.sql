CREATE TYPE bucket_type AS ENUM ('BUYLIST', 'WISHLIST');

CREATE TABLE buckets (
                         id BIGSERIAL PRIMARY KEY,
                         account_id BIGSERIAL NOT NULL REFERENCES accounts(id),
                         date_added TIMESTAMP NOT NULL DEFAULT NOW(),
                         type bucket_type NOT NULL,
                         CONSTRAINT unique_account_type UNIQUE (account_id, type)
);