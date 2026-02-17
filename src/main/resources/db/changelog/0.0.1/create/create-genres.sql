CREATE TABLE genres (
                        id BIGSERIAL PRIMARY KEY,
                        genre_name VARCHAR(30) UNIQUE NOT NULL,
                        description TEXT NOT NULL,
                        parent_genre_id BIGINT REFERENCES genres(id) ON DELETE SET NULL
);