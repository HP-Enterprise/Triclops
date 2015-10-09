CREATE TABLE IF NOT EXISTS  t_data_warning_message (
  id bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  vin varchar(50) NOT NULL COMMENT '车架号',
  imei varchar(50) NOT NULL COMMENT 'imei',
  application_id int(11) NOT NULL COMMENT 'application id',
  message_id int(11) NOT NULL COMMENT 'message id',
  sending_time DATETIME NOT NULL COMMENT '发送时间',
  is_location smallint(6) NOT NULL COMMENT '是否定位 0有效 1无效',
  north_south varchar(1) NOT NULL COMMENT '南北纬',
  east_west varchar(1) NOT NULL COMMENT '东西经',
  latitude varchar(11) NOT NULL COMMENT '纬度',
  longitude varchar(11) NOT NULL COMMENT '经度',
  speed float NOT NULL COMMENT '速度',
  heading int(11) NOT NULL COMMENT '方向',
  bcm1 varchar(8) NOT NULL COMMENT 'BCM',
  ems varchar(8) NOT NULL COMMENT 'EMS',
  tcu varchar(8) NOT NULL COMMENT 'TCU',
  ic varchar(8) NOT NULL COMMENT 'IC',
  abs varchar(8) NOT NULL COMMENT 'ABS',
  pdc varchar(8) NOT NULL COMMENT 'PDC',
  bcm2 varchar(8) NOT NULL COMMENT 'BCM',
  PRIMARY KEY (id)
) DEFAULT CHARSET=utf8 COMMENT='报警数据表';

