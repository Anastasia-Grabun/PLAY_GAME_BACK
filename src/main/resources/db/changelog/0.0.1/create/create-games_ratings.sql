CREATE TABLE games_ratings (
                               account_id BIGSERIAL NOT NULL,
                               game_id BIGSERIAL NOT NULL,
                               rating DECIMAL(3, 2) NOT NULL,
                               CONSTRAINT fk_account FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE,
                               CONSTRAINT fk_game FOREIGN KEY (game_id) REFERENCES games(id) ON DELETE CASCADE,
                               CONSTRAINT uq_account_game PRIMARY KEY (account_id, game_id)
);
