CREATE USER 'gasstation'@'%' IDENTIFIED BY 'z1i@af313';
CREATE DATABASE gasstation CHARACTER SET utf8 COLLATE utf8_unicode_ci;
GRANT ALL PRIVILEGES ON gasstation.* TO 'gasstation'@'%';