CREATE TABLE t_organization_user (
  Id int(11) NOT NULL AUTO_INCREMENT COMMENT '关系id',
  oid int(11) NOT NULL COMMENT '组织Id',
  uid int(11) NOT NULL COMMENT '用户Id',
  PRIMARY KEY (Id)
) DEFAULT CHARSET=utf8 COMMENT='组织用户关系表';

alter table t_organization_user add INDEX oid_user (oid);
alter table t_organization_user add INDEX uid_user (uid);
alter table t_organization_user add INDEX oid_uid (oid,uid);