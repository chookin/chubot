
# DROP DATABASE chubot;
create database if not exists `chubot` default character set utf8;
grant all on *.* to 'chookin'@'localhost' identified by 'winwin';

use chubot;

DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` int(11) unsigned NOT NULL auto_increment COMMENT '用户唯一标示',
  `email` char(128) default NULL COMMENT '邮箱',
  `phone` int(11) default NULL COMMENT '手机号',
  `name` varchar(50) NOT NULL COMMENT '昵称',
  `password` char(32) default NULL COMMENT '账户密码',
  `role` tinyint(1) default NULL COMMENT '用户角色：1，管理员',
  `ustatus` tinyint(1) default 1 COMMENT '账户状态：0，禁用；1，正常启用',
  `create_time` timestamp default CURRENT_TIMESTAMP COMMENT '账户的创建时间',
  `update_time` DATETIME COMMENT '账户信息的修改时间',
  `last_login_time` DATETIME COMMENT '用户最后一次登录时间',
  `last_login_ip` varchar(40) default NULL COMMENT '用户最后一次登录IP',
  `login_times` int DEFAULT 0 COMMENT '登录次数',
  PRIMARY KEY  (`id`)
);
Insert into user(id, name, email, password,role) values(1,'admin','admin@chu.com', '0c7540eb7e65b553ec1ba6b20de79608',1);

create table if not exists agent(
  id int not NULL COMMENT 'agent id',
  address varchar(128) NOT NULL COMMENT 'address',
  startTime DATETIME NOT NULL COMMENT 'start time',
  endTime DATETIME COMMENT 'end time',
  PRIMARY KEY (id)
);

create table if not exists job(
  id int NOT NULL AUTO_INCREMENT COMMENT 'job id',
  job varchar(2048) NOT NULL COMMENT 'job statement',
  time DATETIME NOT NULL COMMENT 'execute time',
  PRIMARY KEY (id)
);

create table if not exists jobDetail(
  id int NOT NULL AUTO_INCREMENT COMMENT 'job id',
  startTime DATETIME NOT NULL COMMENT 'start time',
  endTime DATETIME COMMENT 'execute time',
  status varchar(12) COMMENT 'status',
  PRIMARY KEY (id)
);