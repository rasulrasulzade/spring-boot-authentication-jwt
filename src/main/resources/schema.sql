use testdb;

DROP TABLE IF EXISTS security_user;

CREATE TABLE security_user
(
    id       varchar(45) NOT NULL,
    name     varchar(45)  DEFAULT NULL,
    surname  varchar(45)  DEFAULT NULL,
    email    varchar(45)  DEFAULT NULL UNIQUE,
    password varchar(65) DEFAULT NULL,
    PRIMARY KEY (id)
)
