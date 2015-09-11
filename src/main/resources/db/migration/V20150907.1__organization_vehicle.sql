CREATE TABLE t_organization_vehicle (
  Id int(11) NOT NULL AUTO_INCREMENT,
  oid int(11) NOT NULL COMMENT '组织id',
  vid int(11) NOT NULL COMMENT '车vid',
  PRIMARY KEY (Id)
) DEFAULT CHARSET=utf8 COMMENT='组织、车关系表';