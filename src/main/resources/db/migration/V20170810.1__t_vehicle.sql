CREATE TABLE IF NOT EXISTS t_vehicle
(
  id int(11) NOT NULL AUTO_INCREMENT COMMENT '车辆ID',
  vin varchar(50) NOT NULL COMMENT '车架号',
  tboxsn varchar(50) COMMENT 'tbox码',
  vendor varchar(100) COMMENT '厂家',
  model varchar(100) COMMENT '型号',
  displacement varchar(20) COMMENT '排量',
  product_date datetime NOT NULL DEFAULT '1970-01-01' COMMENT '生产日期',
  vcolor varchar(20) COMMENT '颜色',
  buystore varchar(100) COMMENT '购买4S店',
  buydate date COMMENT '购买时间',
  vpurl varchar(200) COMMENT '车辆图片URL',
  vtype int(1) default 1 COMMENT ' 车辆类型(1:乘用 2:电动)',
  license_plate varchar(10) COMMENT '车牌号',
  t_flag int COMMENT '0 车辆未禁用 1 已禁用',
  security_pwd varchar(32) DEFAULT NULL COMMENT '安防密码',
  security_salt varchar(4) DEFAULT NULL COMMENT 'salt',
  remote_count  int(5) default 0 COMMENT ' 车辆远程控制计数',
  PRIMARY KEY (id),
  UNIQUE key (vin)
--   UNIQUE key (tboxsn)
) DEFAULT CHARSET=utf8 COMMENT='车辆表';

