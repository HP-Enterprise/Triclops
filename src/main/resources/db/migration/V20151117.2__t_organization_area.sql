CREATE TABLE IF NOT EXISTS t_organization_area(
  id int(11) NOT NULL AUTO_INCREMENT COMMENT '车辆绑定id',
  oid int NOT NULL COMMENT '组织ID',
  areaid int(11) NOT NULL COMMENT '区域id',
  PRIMARY KEY (id),
  FOREIGN KEY (oid) REFERENCES t_organization(Id),
  FOREIGN KEY (areaid) REFERENCES t_administrative_division(id)
) DEFAULT CHARSET=utf8 COMMENT='组织地区关系表';

INSERT INTO  t_organization_area(oid,areaid) VALUES (1,1);