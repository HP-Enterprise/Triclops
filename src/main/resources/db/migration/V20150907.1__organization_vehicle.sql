CREATE TABLE IF NOT EXISTS t_organization_vehicle (
  Id int(11) NOT NULL AUTO_INCREMENT,
  oid int(11) NOT NULL COMMENT '组织id',
  vid int(11) NOT NULL COMMENT '车vid',
  PRIMARY KEY (Id),
  FOREIGN KEY (oid) REFERENCES t_organization(Id),
  FOREIGN KEY (vid) REFERENCES t_vehicle(id)
) DEFAULT CHARSET=utf8 COMMENT='组织、车关系表';