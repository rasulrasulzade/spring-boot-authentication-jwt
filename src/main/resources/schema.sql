use testdb;

DROP TABLE IF EXISTS security_user_role;
DROP TABLE IF EXISTS security_user;
DROP TABLE IF EXISTS role;

CREATE TABLE security_user
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

CREATE TABLE security_user_role
(
    id               bigint(11) NOT NULL,
    security_user_id varchar(45),
    role_id          bigint(11),
    PRIMARY KEY (id),
    CONSTRAINT fk_security_user FOREIGN KEY (security_user_id) REFERENCES security_user (id) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT fk_role FOREIGN KEY (role_id) REFERENCES role (id) ON DELETE NO ACTION ON UPDATE NO ACTION
)


