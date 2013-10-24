-- Reports! schemas

-- enterprise schema
DROP SCHEMA IF EXISTS enterprise;
CREATE SCHEMA enterprise;

USE enterprise;

CREATE TABLE reports (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    startDate DATE NOT NULL,
    endDate DATE,
    performer VARCHAR(255) NOT NULL,
    activity VARCHAR(255) NOT NULL,
    KEY performer_idx (performer),
    PRIMARY KEY(id) 
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- indices on date fields of 'reports' table
CREATE INDEX startDate_idx ON reports (startDate);
CREATE INDEX endDate_idx ON reports(endDate);

-- views

-- prever type (VARCHAR -> CLOB) conversion
SET group_concat_max_len = 512;
-- date format string for using in virtual tables
SET @_reports_date_format = '%M %e,%Y';

CREATE VIEW reports_by_performer
AS 
SELECT performer, GROUP_CONCAT(CONCAT(id, DATE_FORMAT(startDate, '%M %e,%Y'), DATE_FORMAT(endDate, '%M %e,%Y'),
        activity)) AS 'reports', COUNT(performer) AS 'count'
FROM reports
GROUP BY performer
ORDER BY performer;

CREATE VIEW reports_by_activity
AS
SELECT activity, GROUP_CONCAT(
    CONCAT(id, DATE_FORMAT(startDate, '%M %e,%Y'), DATE_FORMAT(endDate, '%M %e,%Y'),
        performer)
    SEPARATOR ',') AS 'reports', COUNT(activity) AS 'count'
FROM reports
GROUP BY activity
ORDER BY activity;

CREATE OR REPLACE VIEW activity_by_performer
AS
SELECT performer, GROUP_CONCAT(DISTINCT activity SEPARATOR ', ') as 'activities'
FROM reports 
GROUP BY performer
ORDER BY performer;

CREATE OR REPLACE VIEW performer_by_activity
AS
SELECT activity, GROUP_CONCAT(DISTINCT performer SEPARATOR ', ') as 'performers'
FROM reports 
GROUP BY activity
ORDER BY activity;

CREATE VIEW performers
AS 
SELECT DISTINCT performer 
FROM reports
ORDER BY performer;

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

-- dumps

USE enterprise;

SET AUTOCOMMIT = 0;
DELETE FROM enterprise.reports;

INSERT INTO reports VALUES
    (1,DATE('2012-01-30'), DATE('2012-02-12'),'John Travolta', 'laundering'),
    (2,DATE('2012-02-01'), DATE('2012-02-27'),'John Travolta', 'acting'),
    (3,DATE('2012-05-30'), DATE('2012-06-12'),'John Travolta', 'acting'),
    (4,DATE('2012-01-25'), DATE('2012-02-18'),'Leonardo DiCaprio', 'acting'),
    (5,DATE('2012-03-30'), DATE('2012-07-12'),'Leonardo DiCaprio', 'acting'),
    (6,DATE('2012-08-30'), DATE('2012-08-12'),'Leonardo DiCaprio', 'acting'),
    (7,DATE('2012-01-30'), DATE('2012-04-12'),'Ben Affleck', 'directing'),
    (8,DATE('2013-03-30'), DATE('2013-05-22'),'Ben Affleck', 'acting'),
    (9,DATE('2013-01-30'), DATE('2013-02-12'),'Ben Affleck', 'scripting'),
    (10,DATE('2012-11-28'), DATE('2012-12-19'),'Ben Affleck', 'acting'),
    (11,DATE('2012-01-23'), DATE('2012-02-12'),'Matt Damon', 'scripting'),
    (12,DATE('2012-02-21'), DATE('2012-03-01'),'Matt Damon', 'actinging'),
    (13,DATE('2012-03-25'), DATE('2012-04-02'),'Matt Damon', 'chilling'),
    (14,DATE('2012-04-24'), DATE('2012-05-03'),'Matt Damon', 'acting'),
    (15,DATE('2012-05-24'), DATE('2012-06-04'),'George Clooney', 'acting'),
    (16,DATE('2012-06-27'), DATE('2012-07-05'),'George Clooney', 'promoting'),
    (17,DATE('2012-07-28'), DATE('2012-08-06'),'George Clooney', 'promoting'),
    (18,DATE('2012-08-29'), DATE('2012-09-07'),'George Clooney', 'acting'),
    (19,DATE('2012-09-13'), DATE('2012-10-08'),'Anne Hathaway', 'scoring'),
    (20,DATE('2012-10-14'), DATE('2012-11-09'),'Anne Hathaway', 'scoring'),
    (21,DATE('2012-11-14'), DATE('2012-12-15'),'Anne Hathaway', 'promoting'),
    (22,DATE('2012-12-11'), DATE('2013-02-03'),'Anne Hathaway', 'acting'),
    (23,DATE('2013-01-11'), DATE('2013-01-11'),'Brad Pitt', 'scripting'),
    (24,DATE('2013-01-23'), DATE('2013-02-15'),'Brad Pitt', 'acting'),
    (25,DATE('2013-02-23'), DATE('2013-03-16'),'Brad Pitt', 'scoring'),
    (26,DATE('2013-03-02'), DATE('2013-04-17'),'Brad Pitt', 'promoting'),
    (27,DATE('2013-01-12'), DATE('2013-02-25'),'Denzel Washington', 'acting'),
    (28,DATE('2013-01-13'), DATE('2013-02-26'),'Denzel Washington', 'acting'),
    (29,DATE('2013-02-14'), DATE('2013-04-27'),'Denzel Washington', 'producing'),
    (30,DATE('2013-03-15'), DATE('2013-05-28'),'Denzel Washingtom', 'promoting'),
    (31,DATE('2013-04-16'), DATE('2013-06-29'),'Zooye Dechannel', 'acting'),
    (32,DATE('2013-05-17'), DATE('2013-06-30'),'Zooye Dechannel', 'singing'),
    (33,DATE('2013-06-18'), DATE('2013-06-30'),'Zooye Dechannel', 'acting'),
    (34,DATE('2013-01-19'), DATE('2013-06-30'),'Robert De Niro', 'acting'),
    (35,DATE('2013-02-16'), DATE('2013-03-29'),'Robert De Niro', 'chilling'),
    (36,DATE('2013-03-13'), DATE('2013-04-28'),'Al Pachino', 'acting'),
    (37,DATE('2013-04-12'), DATE('2013-05-27'),'Al Pachino', 'acting'),
    (38,DATE('2013-05-11'), DATE('2013-06-26'),'Dustin Hoffman', 'producing'),
    (39,DATE('2013-06-02'), DATE('2013-06-25'),'Dustin Hoffman', 'acting'),
    (40,DATE('2013-01-05'), DATE('2013-02-24'),'Tom Hanks', 'acting'),
    (41,DATE('2013-02-06'), DATE('2013-03-23'),'Tom Hanks', 'acting'),
    (42,DATE('2013-03-07'), DATE('2013-04-22'),'Bruce Willis', 'acting'),
    (43,DATE('2013-04-03'), DATE('2013-05-14'),'Bruce Willis', 'producing'),
    (44,DATE('2013-05-11'), DATE('2013-06-15'),'Kevin Costner', 'scripting'),
    (45,DATE('2013-06-14'), DATE('2013-06-16'),'Kevin Costner', 'actinging'),
    (46,DATE('2013-01-16'), DATE('2013-02-17'),'John Travolta', 'laundering'),
    (47,DATE('2013-02-19'), DATE('2013-03-18'),'Will Smith', 'scripting'),
    (48,DATE('2013-03-21'), DATE('2013-04-19'),'Will Smith', 'acting'),
    (49,DATE('2013-03-03'), DATE('2013-05-13'),'Will Smith', 'producing'),
    (50,DATE('2013-04-23'), DATE('2013-05-24'),'Will Smith', 'promoting');

COMMIT;

-- dump data 

use registry;

DELETE FROM users;
INSERT INTO users VALUES (1, 'Dan Sammerson', 'manager', 'aareports@mail.ru'),
        (2, 'Kathy LaFael', 'secretary', 'sareports@mail.ru'),
        (3, 'Peter Norton', 'director', 'dareports@mail.ru');

DELETE FROM ops;
INSERT INTO ops VALUES (1, 'INSERT', 'data insertion into enterprise db'),
        (2, 'DELETE', 'deletion from enterprise db'),
        (3, 'UPDATE', 'data updates in enterprise db'),
        (4, 'READ', 'data reading from enterprise db');

DELETE FROM registers;
INSERT INTO registers VALUES (1,1,1,TIMESTAMP('2013-01-21 14:11:09')),
        (2,2,2,TIMESTAMP('2013-03-21 13:11:09')),
        (3,3,3,TIMESTAMP('2013-04-21 12:11:01')),
        (4,1,4,TIMESTAMP('2013-05-23 06:21:02')),
        (5,2,1,TIMESTAMP('2013-06-03 07:31:06')),
        (6,3,2,TIMESTAMP('2013-07-30 08:41:05')),
        (7,1,3,TIMESTAMP('2013-02-17 09:51:04'));

COMMIT;
-- dump data 

use userdetails;

DELETE FROM users;
INSERT INTO users VALUES ('admin', 'admin', true, 3),
        ('user', 'user', true, 2),
        ('anonymous', 'anonymous', true, 1);

DELETE FROM authorities;
INSERT INTO authorities VALUES ('admin', 'ROLE_ADMIN'),
        ('admin', 'ROLE_USER'),
        ('user', 'ROLE_USER'),
        ('anonymous', 'ROLE_ANONYMOUS');

COMMIT;

-- user & authorities

REVOKE ALL ON *.* FROM 'netbeans'@'localhost';
DROP USER 'netbeans'@'localhost';
CREATE USER 'netbeans'@'localhost' IDENTIFIED BY 'netbeans';
GRANT ALL PRIVILEGES ON enterprise.* TO 'netbeans'@'localhost';
GRANT ALL PRIVILEGES ON registry.* TO 'netbeans'@'localhost';
GRANT ALL PRIVILEGES ON userdetails.* TO 'netbeans'@'localhost';