CREATE TABLE IF NOT EXISTS users (
    id          UUID          NOT NULL,
    name        VARCHAR(255)  NOT NULL,
    email       VARCHAR(255)  NOT NULL UNIQUE,
    password    VARCHAR(255)  NOT NULL,
    created     TIMESTAMP     NOT NULL,
    modified    TIMESTAMP     NOT NULL,
    last_login  TIMESTAMP     NOT NULL,
    token       VARCHAR(512)  NOT NULL,
    isactive    BOOLEAN       NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS phones (
    id          UUID          NOT NULL,
    number      VARCHAR(255)  NOT NULL,
    citycode    VARCHAR(255)  NOT NULL,
    contrycode  VARCHAR(255)  NOT NULL,
    user_id     UUID          NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_phones_user FOREIGN KEY (user_id) REFERENCES users(id)
);
