CREATE TABLE IF NOT EXISTS usuario (
    id           UUID         DEFAULT RANDOM_UUID() PRIMARY KEY,
    name         VARCHAR(100) NOT NULL,
    email        VARCHAR(150) NOT NULL UNIQUE,
    password     VARCHAR(255) NOT NULL,
    token        VARCHAR(1000),
    created_at   TIMESTAMP    NOT NULL DEFAULT NOW(),
    modified_at  TIMESTAMP,
    last_login   TIMESTAMP,
    is_active    BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS telefono (
    id           UUID        DEFAULT RANDOM_UUID() PRIMARY KEY,
    usuario_id   UUID        NOT NULL,
    number       VARCHAR(20),
    citycode     VARCHAR(10),
    contrycode   VARCHAR(10)
);
