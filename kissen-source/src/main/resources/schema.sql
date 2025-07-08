CREATE TABLE IF NOT EXISTS ksvp_punishment (
    id INT PRIMARY KEY NOT NULL,
    punishment_type TINYINT NOT NULL,
    time_span BIGINT NULL DEFAULT NULL,
    message JSON NULL DEFAULT NULL
);

CREATE TABLE IF NOT EXISTS ksvp_punishment_subscription (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    parent_id INT NULL,
    parent_signature INT NOT NULL,
    start_time DATETIME NOT NULL,
    time_span BIGINT NULL DEFAULT NULL,
    message JSON NULL DEFAULT NULL,
    PRIMARY KEY (parent_id) REFERENCES ksvp_punishment(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS ksvp_player_data (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    linkId VARCHAR(36) NOT NULL,
    username VARCHAR(16) NOT NULL UNIQUE,
    first_join DATETIME NOT NULL,
    last_join DATETIME NOT NULL,
    time_played BIGINT NOT NULL DEFAULT 0,
    operator BOOLEAN NOT NULL DEFAULT FALSE,
    locale VARCHAR(5) NOT NULL DEFAULT 'en_US'
)