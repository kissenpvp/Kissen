CREATE TABLE IF NOT EXISTS ksvp_identity (
    linkId VARCHAR(36) NOT NULL,
    PRIMARY KEY (linkId)
);

CREATE TABLE IF NOT EXISTS ksvp_player (
    id VARCHAR(36) NOT NULL,
    linkId VARCHAR(36) NOT NULL,
    username VARCHAR(16) NOT NULL UNIQUE,
    first_login DATETIME NOT NULL,
    last_login DATETIME NOT NULL,
    time_played BIGINT NOT NULL DEFAULT 0,
    locale VARCHAR(5) NOT NULL DEFAULT 'en_US',
    PRIMARY KEY (id),
    FOREIGN KEY (linkId) REFERENCES ksvp_identity(linkId)
);

CREATE TABLE IF NOT EXISTS ksvp_operators (
    id VARCHAR(36) NOT NULL,
    username VARCHAR(16) NOT NULL,
    op_level INT NOT NULL,
    can_bypass_player_limit BOOLEAN NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (id) REFERENCES ksvp_player(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS ksvp_player_data (
    id VARCHAR(36) NOT NULL,
    plugin VARCHAR(255) NOT NULL,
    content JSON NOT NULL,
    PRIMARY KEY (id, plugin),
    FOREIGN KEY (id) REFERENCES ksvp_player(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS ksvp_punishment (
    id INT PRIMARY KEY NOT NULL,
    punishment_type TINYINT NOT NULL,
    time_span BIGINT NULL DEFAULT NULL,
    message JSON NULL DEFAULT NULL
);

CREATE TABLE IF NOT EXISTS ksvp_punishment_subscription (
    id VARCHAR(8) NOT NULL,
    linkId VARCHAR(36) NOT NULL,
    parent_id INT NULL,
    parent_signature INT NOT NULL,
    start_time DATETIME NOT NULL,
    expiry BIGINT NULL DEFAULT NULL,
    expected_expiry BIGINT NULL DEFAULT NULL,
    message JSON NULL DEFAULT NULL,
    PRIMARY KEY (id, linkId),
    FOREIGN KEY (linkId) REFERENCES ksvp_identity(linkId),
    FOREIGN KEY (parent_id) REFERENCES ksvp_punishment(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS ksvp_rank (
    id VARCHAR(20) NOT NULL PRIMARY KEY,
    priority INT NOT NULL DEFAULT 99999
);

CREATE TABLE IF NOT EXISTS ksvp_rank_subscription (
    id VARCHAR(8) NOT NULL,
    rank_id VARCHAR(20) NOT NULL,
    player_id VARCHAR(36) NOT NULL,
    start_time DATETIME NOT NULL,
    expiry BIGINT NULL DEFAULT NULL,
    expected_expiry BIGINT NULL DEFAULT NULL,
    PRIMARY KEY (id, rank_id, player_id),
    FOREIGN KEY (player_id) REFERENCES ksvp_player_data(id),
    FOREIGN KEY (rank_id) REFERENCES ksvp_rank(id)
);