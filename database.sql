CREATE DATABASE hstestdb;

CREATE USER hsdb IDENTIFIED BY PASSWORD 'hsdbpass';

CREATE TABLE HTML(
	uuid CHAR(36) NOT NULL,
    content LONG VARCHAR,
    CONSTRAINT PK_UUID PRIMARY KEY(uuid)
);