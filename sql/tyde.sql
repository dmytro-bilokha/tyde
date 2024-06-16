CREATE USER 'tyde'@'%' IDENTIFIED BY 'SET_THE_PASSWORD';
-- We have foreign key to connect GPS device to users, so have to be able to read it
GRANT REFERENCES ON user_data.app_user TO 'tyde'@'%';
CREATE DATABASE tyde;
GRANT ALL ON tyde.* TO 'tyde'@'%';
USE tyde;

CREATE TABLE gps_device
( id BIGINT NOT NULL AUTO_INCREMENT
, submission_token VARCHAR(120) NOT NULL
, description VARCHAR(1024) NOT NULL
, CONSTRAINT gps_device_pk PRIMARY KEY (id)
, CONSTRAINT gps_device_submission_token_uq UNIQUE (submission_token)
);

CREATE TABLE gps_device_read_user
( gps_device_id BIGINT NOT NULL
, app_user_id BIGINT NOT NULL
, CONSTRAINT gps_device_read_user_pk PRIMARY KEY (gps_device_id, app_user_id)
, CONSTRAINT gps_device_read_user_gps_device_id_fk FOREIGN KEY (gps_device_id) REFERENCES gps_device (id)
, CONSTRAINT gps_device_read_user_app_user_id_fk FOREIGN KEY (app_user_id) REFERENCES user_data.app_user (id)
);

