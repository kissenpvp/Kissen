CREATE TABLE IF NOT EXISTS ksvp_identity (
    link_id UUID NOT NULL,
    PRIMARY KEY (link_id)
);

CREATE TABLE IF NOT EXISTS ksvp_player (
    id UUID NOT NULL,
    link_id UUID NOT NULL,
    username VARCHAR(16) NOT NULL UNIQUE,
    PRIMARY KEY (id),
    FOREIGN KEY (link_id) REFERENCES ksvp_identity(link_id)
);

CREATE TABLE IF NOT EXISTS ksvp_player_data (
    id UUID NOT NULL,
    player_data BLOB NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (id) REFERENCES ksvp_player(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS ksvp_operators (
    id UUID NOT NULL,
    operator_level INT NOT NULL,
    can_bypass_player_limit BOOLEAN NOT NULL,
    PRIMARY KEY (id),
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
    link_id UUID NOT NULL,
    parent_id INT NULL,
    parent_signature INT NOT NULL,
    operator_id UUID NULL,
    start_time DATETIME NOT NULL,
    expiry DATETIME NULL DEFAULT NULL,
    expected_expiry DATETIME NULL DEFAULT NULL,
    message JSON NULL DEFAULT NULL,
    PRIMARY KEY (id, link_id),
    FOREIGN KEY (link_id) REFERENCES ksvp_identity(link_id),
    FOREIGN KEY (parent_id) REFERENCES ksvp_punishment(id) ON DELETE SET NULL,
    FOREIGN KEY (operator_id) REFERENCES ksvp_operators(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS ksvp_rank (
    id VARCHAR(20) NOT NULL PRIMARY KEY,
    priority INT NOT NULL DEFAULT 99999
);

CREATE TABLE IF NOT EXISTS ksvp_rank_subscription (
    id VARCHAR(8) NOT NULL,
    rank_id VARCHAR(20) NOT NULL,
    player_id UUID NOT NULL,
    start_time DATETIME NOT NULL,
    expiry DATETIME NULL DEFAULT NULL,
    expected_expiry DATETIME NULL DEFAULT NULL,
    PRIMARY KEY (id, rank_id, player_id),
    FOREIGN KEY (player_id) REFERENCES ksvp_player_data(id),
    FOREIGN KEY (rank_id) REFERENCES ksvp_rank(id)
);