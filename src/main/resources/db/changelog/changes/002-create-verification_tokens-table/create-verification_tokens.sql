CREATE TABLE verification_tokens (
                                     id       bigserial PRIMARY KEY,
                                     token    varchar(255) NOT NULL UNIQUE,
                                     user_id  bigint NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                                     expires_at timestamp NOT NULL
);
