CREATE TABLE IF NOT EXISTS ksvp_identity (
    link_id UUID NOT NULL,
    PRIMARY KEY (link_id)
) COMMENT = "This table stores a link id which is used to identify players across their accounts.";

CREATE TABLE IF NOT EXISTS ksvp_player (
    id UUID NOT NULL,
    link_id UUID NOT NULL,
    username VARCHAR(16) NOT NULL UNIQUE,
    PRIMARY KEY (id),
    FOREIGN KEY (link_id) REFERENCES ksvp_identity(link_id)
) COMMENT = "This table stores basic account information based on the uid of the player.";

CREATE TABLE IF NOT EXISTS ksvp_player_data (
    server_uid UUID NOT NULL UNIQUE,
    id UUID NOT NULL,
    player_data BLOB NOT NULL,
    PRIMARY KEY (server_uid, id),
    FOREIGN KEY (id) REFERENCES ksvp_player(id) ON DELETE CASCADE
) COMMENT = "This table stores server specific player data such as inventory and stats.";

CREATE TABLE IF NOT EXISTS ksvp_punishment (
    id INT PRIMARY KEY NOT NULL,
    punishment_type TINYINT NOT NULL,
    time_span BIGINT NULL DEFAULT NULL,
    message JSON NULL DEFAULT NULL
) COMMENT = "This table stores punishment templates which can be applied to link ids.";

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
) COMMENT = "This table stores punishment subscriptions which are applied to link ids.";

CREATE TABLE IF NOT EXISTS ksvp_rank (
    id VARCHAR(20) NOT NULL PRIMARY KEY,
    priority INT NOT NULL DEFAULT 99999
) COMMENT = "This table stores ranks which can be applied to players.";

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
) COMMENT = "This table stores the subscriptions of players to ranks.";