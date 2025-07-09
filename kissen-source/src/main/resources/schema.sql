CREATE TABLE IF NOT EXISTS ksvp_player_data (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    linkId VARCHAR(36) NOT NULL,
    username VARCHAR(16) NOT NULL UNIQUE,
    first_login DATETIME NOT NULL,
    last_login DATETIME NOT NULL,
    time_played BIGINT NOT NULL DEFAULT 0,
    operator BOOLEAN NOT NULL DEFAULT FALSE,
    locale VARCHAR(5) NOT NULL DEFAULT 'en_US',
    FOREIGN KEY (linkId) REFERENCES kissen_player(linkId)
);

CREATE TABLE IF NOT EXISTS ksvp_punishment (
    id INT PRIMARY KEY NOT NULL,
    punishment_type TINYINT NOT NULL,
    time_span BIGINT NULL DEFAULT NULL,
    message JSON NULL DEFAULT NULL
);

CREATE TABLE IF NOT EXISTS ksvp_punishment_subscription (
    id VARCHAR(8) NOT NULL PRIMARY KEY,
    linkId VARCHAR(36) NOT NULL,
    parent_id INT NULL,
    parent_signature INT NOT NULL,
    start_time DATETIME NOT NULL,
    time_span BIGINT NULL DEFAULT NULL,
    message JSON NULL DEFAULT NULL,
    FOREIGN KEY (linkId) REFERENCES ksvp_player_data(linkId),
    FOREIGN KEY (parent_id) REFERENCES ksvp_punishment(id) ON DELETE SET NULL
);
