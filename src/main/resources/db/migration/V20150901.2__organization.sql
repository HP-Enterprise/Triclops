CREATE TABLE t_organization (
  Id int(11) NOT NULL AUTO_INCREMENT COMMENT '组织的id',
  org_name varchar(128) NOT NULL UNIQUE COMMENT '组织名称',
  bre_code varchar(8) UNIQUE NOT NULL COMMENT '组织简码',
  type_key int(1) NOT NULL COMMENT '组织类型',
  PRIMARY KEY (Id)
) DEFAULT CHARSET=utf8 COMMENT='组织表';

alter table t_organization add UNIQUE KEY org_name (org_name);
alter table t_organization add INDEX bre_code (bre_code);
alter table t_organization add INDEX type_key (type_key);
