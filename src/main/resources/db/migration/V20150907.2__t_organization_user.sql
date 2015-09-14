CREATE TABLE IF NOT EXISTS t_organization_user (
  Id int(11) NOT NULL AUTO_INCREMENT COMMENT '关系id',
  oid int(11) NOT NULL COMMENT '组织Id',
  uid int(11) NOT NULL COMMENT '用户Id',
  PRIMARY KEY (Id),
  FOREIGN KEY (oid) REFERENCES t_organization(Id),
  FOREIGN KEY (uid) REFERENCES t_user(Id)
) DEFAULT CHARSET=utf8 COMMENT='组织用户关系表';