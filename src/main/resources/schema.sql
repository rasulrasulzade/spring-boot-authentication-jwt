DROP SCHEMA IF EXISTS spring_auth_example;

CREATE SCHEMA spring_auth_example;

use spring_auth_example;

DROP TABLE IF EXISTS security_user_role;
DROP TABLE IF EXISTS security_user;
DROP TABLE IF EXISTS role;

CREATE TABLE user
(
    id       varchar(45) NOT NULL,
    name     varchar(45) DEFAULT NULL,
    surname  varchar(45) DEFAULT NULL,
    email    varchar(45) DEFAULT NULL UNIQUE,
    password varchar(65) DEFAULT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE role
(
    id   bigint(11) NOT NULL AUTO_INCREMENT,
    name varchar(45),
    PRIMARY KEY (id)
);

CREATE TABLE user_role
(
    id               bigint(11) NOT NULL AUTO_INCREMENT,
    user_id varchar(45),
    role_id          bigint(11),
    PRIMARY KEY (id),
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT fk_role FOREIGN KEY (role_id) REFERENCES role (id) ON DELETE NO ACTION ON UPDATE NO ACTION
)


