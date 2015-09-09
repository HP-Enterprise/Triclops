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

alter table t_vehicle add UNIQUE KEY vin_key (vin);
alter table t_vehicle add INDEX vendor_key (vendor);
alter table t_vehicle add INDEX model_key (model);
alter table t_vehicle add INDEX flag_displacement (t_flag,displacement);

create table sysdict
(
  dictid int(11) NOT NULL AUTO_INCREMENT COMMENT '字典ID',
  type int(3) COMMENT '字典类型',
  dictname varchar(500) COMMENT '字典名称',
  remark varchar(500)  COMMENT '备注',
  PRIMARY KEY (dictid)
) DEFAULT CHARSET=utf8 COMMENT='字典表';

alter table sysdict add INDEX type_sysdict (type);
alter table sysdict add INDEX dictname_sysdict (dictname);
alter table sysdict add INDEX dictname_sremark (dictname,remark);

