-- db schema definition

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