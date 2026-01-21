CREATE TYPE role_type AS ENUM ('ADMIN', 'USER', 'DEVELOPER');

CREATE TABLE roles (
                       id BIGSERIAL PRIMARY KEY,
                       name role_type NOT NULL
);