CREATE TABLE t_organization_vehicle (
  Id int(11) NOT NULL AUTO_INCREMENT,
  oid int(11) NOT NULL DEFAULT '0' COMMENT '组织id',
  vin varchar(50) NOT NULL DEFAULT '' COMMENT '车vin码',
  PRIMARY KEY (Id)
) DEFAULT CHARSET=utf8 COMMENT='组织、车关系表';

alter table t_organization_vehicle add INDEX oid_veh (oid);
alter table t_organization_vehicle add INDEX vin_veh (vin);
alter table t_organization_vehicle add INDEX oid_vin (oid,vin);