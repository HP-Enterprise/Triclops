CREATE TABLE IF NOT EXISTS t_vehicle
(
  id int(11) NOT NULL AUTO_INCREMENT COMMENT '车辆ID',
  vin varchar(50) NOT NULL COMMENT '车架号',
  tboxsn varchar(50) NOT NULL  COMMENT 'tbox码',
  vendor varchar(100) COMMENT '厂家',
  model varchar(100) COMMENT '型号',
  t_flag int COMMENT '--0 车辆未禁用 1 已禁用',
  displacement varchar(20) COMMENT '排量',
  license_plate varchar(10) COMMENT '车牌号',
  product_date datetime NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '生产日期',
  security_pwd varchar(32) DEFAULT NULL COMMENT '安防密码',
  security_salt varchar(4) DEFAULT NULL COMMENT 'salt',
  PRIMARY KEY (id),
  UNIQUE key (vin)
--   UNIQUE key (tboxsn)
) DEFAULT CHARSET=utf8 COMMENT='车辆表';

