
# DROP DATABASE chubot;
create database if not exists `chubot` default character set utf8;

use chubot;

create table if not exists job(
  id int NOT NULL AUTO_INCREMENT COMMENT 'job id',
  job varchar(2048) NOT NULL COMMENT 'job statement',
  time DATETIME NOT NULL COMMENT 'execute time',
  PRIMARY KEY (id)
);

create table if not exists agent(
  id int not NULL COMMENT 'agent id',
  address varchar(128) NOT NULL COMMENT 'address',
  startTime DATETIME NOT NULL COMMENT 'start time',
  endTime DATETIME COMMENT 'end time',
  PRIMARY KEY (id)
);