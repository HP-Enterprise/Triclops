CREATE TABLE t_organization_relatived (
  Id int(11) NOT NULL AUTO_INCREMENT COMMENT '关系id',
  sub_oid int(11) NOT NULL COMMENT '子组织id',
  par_oid int(11) NOT NULL COMMENT '父组织id',
  PRIMARY KEY (Id)
) DEFAULT CHARSET=utf8 COMMENT='组织关系表';