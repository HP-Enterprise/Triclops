DROP TABLE  t_organization_vehicle;
CREATE TABLE t_organization_vehicle (
  Id int(11) NOT NULL AUTO_INCREMENT,
  oid int(11) NOT NULL DEFAULT '0' COMMENT '��֯id',
  vin varchar(50) NOT NULL DEFAULT '' COMMENT '��vin��',
  PRIMARY KEY (Id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='��֯������ϵ��';