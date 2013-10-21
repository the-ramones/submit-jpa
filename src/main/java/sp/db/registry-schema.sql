-- db chema definition
DROP SCHEMA IF EXISTS registry;
CREATE SCHEMA registry;

use registry;

CREATE TABLE ops (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    title VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    UNIQUE KEY (title),
    PRIMARY KEY (id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE users(
    id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    fullname VARCHAR(255) NOT NULL,
    job VARCHAR(255),
    email VARCHAR(255),
    UNIQUE KEY (fullname),
    PRIMARY KEY(id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE registers (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    user_id INT UNSIGNED NOT NULL,
    op_id INT UNSIGNED NOT NULL,
    record_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    CONSTRAINT user_id_fk FOREIGN KEY (user_id) REFERENCES users(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT op_id_fk FOREIGN KEY (op_id) REFERENCES ops(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    PRIMARY KEY (id, user_id, op_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE INDEX users_job_idx ON users (job);
CREATE INDEX ops_title_idx ON ops (title);
CREATE INDEX registers_record_time_idx ON registers (record_time);


