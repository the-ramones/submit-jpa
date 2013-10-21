-- db chema definition
DROP SCHEMA IF EXISTS userdetails;
CREATE SCHEMA userdetails;

use userdetails;

CREATE TABLE users(
      username VARCHAR(255) NOT NULL PRIMARY KEY,
      password VARCHAR(255) NOT NULL,
      enabled BOOLEAN NOT NULL,
      app_user INT NOT NULL
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE authorities (
    username VARCHAR(255) NOT NULL,
    authority VARCHAR(255) NOT NULL,
    CONSTRAINT fk_authorities_users FOREIGN KEY(username) REFERENCES users(username)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE UNIQUE INDEX ix_auth_username ON authorities (username, authority);
