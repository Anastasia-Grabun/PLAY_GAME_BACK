CREATE TABLE games (
                       id BIGSERIAL PRIMARY KEY,
                       name VARCHAR(60) UNIQUE NOT NULL,
                       description TEXT NOT NULL,
                       release_date TIMESTAMP NOT NULL DEFAULT NOW(),
                       developers_id BIGSERIAL REFERENCES accounts(id),
                       price DECIMAL(20, 2) NOT NULL,
                       rating DECIMAL(3, 2) DEFAULT 0.00
);
