CREATE USER hsdb IDENTIFIED BY 'hsdbpass';

CREATE DATABASE hstestdb;

USE hstestdb;

CREATE TABLE HTML(
	uuid CHAR(36) NOT NULL,
    content LONG VARCHAR,
    CONSTRAINT PK_UUID PRIMARY KEY(uuid)
);

CREATE TABLE XML(
	uuid CHAR(36) NOT NULL,
    content LONG VARCHAR,
    CONSTRAINT PK_UUID PRIMARY KEY(uuid)
);

CREATE TABLE XSD(
	uuid CHAR(36) NOT NULL,
    content LONG VARCHAR,
    CONSTRAINT PK_UUID PRIMARY KEY(uuid)
);

CREATE TABLE XSLT(
	uuid CHAR(36) NOT NULL,
    content LONG VARCHAR,
	xsd CHAR(36) NOT NULL,
    CONSTRAINT PK_UUID PRIMARY KEY(uuid)
);

CREATE DATABASE hstestdb1;

USE hstestdb1;

CREATE TABLE HTML(
	uuid CHAR(36) NOT NULL,
    content LONG VARCHAR,
    CONSTRAINT PK_UUID PRIMARY KEY(uuid)
);

CREATE TABLE XML(
	uuid CHAR(36) NOT NULL,
    content LONG VARCHAR,
    CONSTRAINT PK_UUID PRIMARY KEY(uuid)
);

CREATE TABLE XSD(
	uuid CHAR(36) NOT NULL,
    content LONG VARCHAR,
    CONSTRAINT PK_UUID PRIMARY KEY(uuid)
);

CREATE TABLE XSLT(
	uuid CHAR(36) NOT NULL,
    content LONG VARCHAR,
	xsd CHAR(36) NOT NULL,
    CONSTRAINT PK_UUID PRIMARY KEY(uuid)
);

CREATE DATABASE hstestdb2;

USE hstestdb2;

CREATE TABLE HTML(
	uuid CHAR(36) NOT NULL,
    content LONG VARCHAR,
    CONSTRAINT PK_UUID PRIMARY KEY(uuid)
);

CREATE TABLE XML(
	uuid CHAR(36) NOT NULL,
    content LONG VARCHAR,
    CONSTRAINT PK_UUID PRIMARY KEY(uuid)
);

CREATE TABLE XSD(
	uuid CHAR(36) NOT NULL,
    content LONG VARCHAR,
    CONSTRAINT PK_UUID PRIMARY KEY(uuid)
);

CREATE TABLE XSLT(
	uuid CHAR(36) NOT NULL,
    content LONG VARCHAR,
	xsd CHAR(36) NOT NULL,
    CONSTRAINT PK_UUID PRIMARY KEY(uuid)
);

CREATE DATABASE hstestdb3;

USE hstestdb3;

CREATE TABLE HTML(
	uuid CHAR(36) NOT NULL,
    content LONG VARCHAR,
    CONSTRAINT PK_UUID PRIMARY KEY(uuid)
);

CREATE TABLE XML(
	uuid CHAR(36) NOT NULL,
    content LONG VARCHAR,
    CONSTRAINT PK_UUID PRIMARY KEY(uuid)
);

CREATE TABLE XSD(
	uuid CHAR(36) NOT NULL,
    content LONG VARCHAR,
    CONSTRAINT PK_UUID PRIMARY KEY(uuid)
);

CREATE TABLE XSLT(
	uuid CHAR(36) NOT NULL,
    content LONG VARCHAR,
	xsd CHAR(36) NOT NULL,
    CONSTRAINT PK_UUID PRIMARY KEY(uuid)
);

CREATE DATABASE hstestdb4;

USE hstestdb4;

CREATE TABLE HTML(
	uuid CHAR(36) NOT NULL,
    content LONG VARCHAR,
    CONSTRAINT PK_UUID PRIMARY KEY(uuid)
);

CREATE TABLE XML(
	uuid CHAR(36) NOT NULL,
    content LONG VARCHAR,
    CONSTRAINT PK_UUID PRIMARY KEY(uuid)
);

CREATE TABLE XSD(
	uuid CHAR(36) NOT NULL,
    content LONG VARCHAR,
    CONSTRAINT PK_UUID PRIMARY KEY(uuid)
);

CREATE TABLE XSLT(
	uuid CHAR(36) NOT NULL,
    content LONG VARCHAR,
	xsd CHAR(36) NOT NULL,
    CONSTRAINT PK_UUID PRIMARY KEY(uuid)
);
