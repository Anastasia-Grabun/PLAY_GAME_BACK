CREATE TABLE credentials_roles (
                                   role_id BIGSERIAL REFERENCES roles(id),
                                   credentials_login VARCHAR(32) REFERENCES credentials(login),
                                   PRIMARY KEY (role_id, credentials_login)
);
