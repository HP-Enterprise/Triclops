create table t_vehicle
(
  id int(11) NOT NULL AUTO_INCREMENT COMMENT '车辆ID',
  vin varchar(50) NOT NULL COMMENT '车架号',
  vendor varchar(100) COMMENT '厂家',
  model varchar(100) COMMENT '型号',
  t_flag int COMMENT '--0 自然吸气 1 涡轮增压',
  displacement varchar(20) COMMENT '排量',
  license_plate varchar(10) COMMENT '车牌号',
  product_date datetime COMMENT '生产日期',
  PRIMARY KEY (id)
) DEFAULT CHARSET=utf8 COMMENT='车辆表';

create table sysdict
(
  dictid int(11) NOT NULL AUTO_INCREMENT COMMENT '字典ID',
  type int(3) COMMENT '字典类型',
  dictname varchar(500) COMMENT '字典名称',
  remark varchar(500)  COMMENT '备注',
  PRIMARY KEY (dictid)
) DEFAULT CHARSET=utf8 COMMENT='字典表';

INSERT INTO sysdict(dictid, type, dictname, remark) VALUES (1,1,'华晨汽车','华晨汽车');
INSERT INTO sysdict(dictid, type, dictname, remark) VALUES (2,1,'经销商','经销商');
INSERT INTO sysdict(dictid, type, dictname, remark) VALUES (3,1,'车友群','车友群');
ALTER TABLE t_vehicle
ADD tboxsn varchar(50) NOT NULL  COMMENT 'obd码';

