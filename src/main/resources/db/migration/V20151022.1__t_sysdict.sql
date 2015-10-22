CREATE TABLE IF NOT EXISTS t_sysdict
(
  dictid int(11) NOT NULL  COMMENT '字典ID',
  type int(3) COMMENT '字典类型',
  dictname varchar(50) COMMENT '字典名称',
  remark varchar(100)  COMMENT '备注',
  PRIMARY KEY (dictid),
  KEY idex_type (type)
) DEFAULT CHARSET=utf8 COMMENT='字典表';

INSERT INTO t_sysdict(dictid, type, dictname) VALUES (1,1, "华晨汽车");
INSERT INTO t_sysdict(dictid, type, dictname) VALUES (2,1, "经销商");
INSERT INTO t_sysdict(dictid, type, dictname) VALUES (3,1, "车友群");