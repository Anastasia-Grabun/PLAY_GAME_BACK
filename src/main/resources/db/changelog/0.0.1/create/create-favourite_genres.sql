CREATE TABLE favourite_genres (
                                  account_id BIGSERIAL NOT NULL,
                                  genre_id BIGSERIAL NOT NULL,
                                  CONSTRAINT fk_account FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE,
                                  CONSTRAINT fk_game FOREIGN KEY (genre_id) REFERENCES genres(id) ON DELETE CASCADE,
                                  CONSTRAINT uq_account_genre PRIMARY KEY (account_id, genre_id)
);





