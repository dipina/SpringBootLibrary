DROP USER IF EXISTS 'dbuser'@'%';
DROP USER IF EXISTS 'dbuser'@'localhost';
CREATE USER IF NOT EXISTS 'dbuser'@'%' IDENTIFIED BY 'dbuser';
CREATE USER IF NOT EXISTS 'dbuser'@'localhost' IDENTIFIED BY 'dbuser';

DROP SCHEMA IF EXISTS libraryapidb;
CREATE SCHEMA libraryapidb;

GRANT ALL ON libraryapidb.* TO 'dbuser'@'%';
GRANT ALL ON libraryapidb.* TO 'dbuser'@'localhost';
FLUSH PRIVILEGES;
