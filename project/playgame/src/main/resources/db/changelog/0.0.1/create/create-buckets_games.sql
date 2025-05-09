CREATE TABLE buckets_games (
                               bucket_id BIGSERIAL REFERENCES buckets(id),
                               game_id BIGSERIAL REFERENCES games(id),
                               PRIMARY KEY (bucket_id, game_id)
);
