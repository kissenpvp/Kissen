CREATE TABLE IF NOT EXISTS ksvp_punishment (
    id INT PRIMARY KEY NOT NULL,
    punishment_type TINYINT NOT NULL,
    time_span BIGINT NULL,
    message JSON NULL
);

CREATE TABLE IF NOT EXISTS ksvp_punishment_subscription (
    id VARCHAR(36) NOT NULL,
    parent_id INT NULL,
    parent_signature INT NOT NULL,
    time_span BIGINT NULL,
    message JSON NULL,
    PRIMARY KEY (parent_id) REFERENCES ksvp_punishment(id) ON DELETE SET NULL
);

