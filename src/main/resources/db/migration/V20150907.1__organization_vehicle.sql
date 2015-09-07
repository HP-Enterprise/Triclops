DROP TABLE  t_organization_vehicle;
CREATE TABLE t_organization_vehicle (
  Id int(11) NOT NULL AUTO_INCREMENT,
  oid int(11) NOT NULL DEFAULT '0' COMMENT '组织id',
  vin varchar(50) NOT NULL DEFAULT '' COMMENT '车vin码',
  PRIMARY KEY (Id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='组织、车关系表';