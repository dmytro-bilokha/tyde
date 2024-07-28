CREATE USER 'user_data'@'%' IDENTIFIED BY 'SET_THE_PASSWORD';
CREATE DATABASE user_data;
GRANT ALL ON user_data.* TO 'user_data'@'%';
USE user_data;

CREATE TABLE app_user
( id BIGINT NOT NULL AUTO_INCREMENT
, login VARCHAR(120) NOT NULL
, password_hash VARCHAR(1024) NOT NULL
, CONSTRAINT app_user_pk PRIMARY KEY (id)
, CONSTRAINT app_user_login_uq UNIQUE (login)
);

CREATE TABLE app_role
( id BIGINT NOT NULL AUTO_INCREMENT
, name VARCHAR(120) NOT NULL
, CONSTRAINT app_role_pk PRIMARY KEY (id)
, CONSTRAINT app_role_name_uq UNIQUE (name)
);

CREATE TABLE app_user_role
( app_user_id BIGINT NOT NULL
, app_role_id BIGINT NOT NULL
, CONSTRAINT app_user_role_pk PRIMARY KEY (app_user_id, app_role_id)
, CONSTRAINT app_user_role_user_fk FOREIGN KEY (app_user_id) REFERENCES app_user (id)
, CONSTRAINT app_user_role_role_fk FOREIGN KEY (app_role_id) REFERENCES app_role (id)
);

CREATE TABLE authentication_token
( id BIGINT NOT NULL AUTO_INCREMENT
, app_user_id BIGINT NOT NULL
, login VARCHAR(120) NOT NULL
, password_hash VARCHAR(1024) NOT NULL
, valid_to TIMESTAMP NOT NULL
, CONSTRAINT authentication_token_pk PRIMARY KEY (id)
, CONSTRAINT authentication_token_login_uq UNIQUE (login)
, CONSTRAINT authentication_token_user_fk FOREIGN KEY (app_user_id) REFERENCES app_user (id)
);

INSERT INTO app_role (name) VALUES ('tyde_user');

