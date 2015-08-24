CREATE TABLE t_user (
  Id int(11) NOT NULL AUTO_INCREMENT,
  name varchar(50) NOT NULL DEFAULT '' COMMENT '账号',
  gender int(3) DEFAULT NULL COMMENT '性别',
  nick varchar(50) NOT NULL DEFAULT '' COMMENT '显示给其它人看的名称',
  phone varchar(11) DEFAULT NULL COMMENT '电话号码',
  PRIMARY KEY (Id)
) DEFAULT CHARSET=utf8 COMMENT='用户表';
