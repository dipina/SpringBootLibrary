CREATE DATABASE IF NOT EXISTS libraryapidb;

-- Crear usuario para conexiones remotas
CREATE USER IF NOT EXISTS 'dbuser'@'%' IDENTIFIED BY 'dbuser';

-- Asegurar autenticación con mysql_native_password
ALTER USER 'dbuser'@'%' IDENTIFIED WITH mysql_native_password BY 'dbuser';

-- Conceder privilegios al usuario
GRANT ALL PRIVILEGES ON libraryapidb.* TO 'dbuser'@'%' WITH GRANT OPTION;

-- Aplicar cambios
FLUSH PRIVILEGES;
