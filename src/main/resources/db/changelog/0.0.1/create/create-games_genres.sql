CREATE TABLE games_genres (
                              game_id BIGSERIAL REFERENCES games(id),
                              genre_id BIGSERIAL REFERENCES genres(id),
                              PRIMARY KEY (game_id, genre_id)
);