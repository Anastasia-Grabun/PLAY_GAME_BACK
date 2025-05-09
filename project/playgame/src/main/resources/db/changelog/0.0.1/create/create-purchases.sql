CREATE TABLE purchases (
                           id BIGSERIAL PRIMARY KEY,
                           owner_id BIGSERIAL NOT NULL REFERENCES accounts(id),
                           game_id BIGSERIAL NOT NULL REFERENCES games(id),
                           purchase_date TIMESTAMP DEFAULT NOW(),
                           transaction_id BIGSERIAL NOT NULL REFERENCES transactions(id)
);
