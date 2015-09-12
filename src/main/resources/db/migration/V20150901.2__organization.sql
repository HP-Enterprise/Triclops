CREATE TABLE IF NOT EXISTS t_organization (
  Id int(11) NOT NULL AUTO_INCREMENT COMMENT '组织的id',
  org_name varchar(128) NOT NULL COMMENT '组织名称',
  bre_code varchar(8) UNIQUE NOT NULL COMMENT '组织简码',
  type_key int(1) NOT NULL COMMENT '组织类型',
  PRIMARY KEY (Id),
  UNIQUE KEY (org_name),
  KEY idx_typekey (type_key)
) DEFAULT CHARSET=utf8 COMMENT='组织表';

INSERT INTO  t_organization(org_name, bre_code, type_key) VALUES ("超级管理后台","SUPPER",1)