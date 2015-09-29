CREATE TABLE IF NOT EXISTS t_data_gps (
  id bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  vin varchar(17) NOT NULL COMMENT '车架号',
  serial_number varchar(14) NOT NULL COMMENT 'TBOX序列号',
  imei varchar(15) NOT NULL COMMENT 'imei',
  application_id int(2) NOT NULL COMMENT '应用类型id',
  message_id int(1) NOT NULL COMMENT '消息id',
  sending_time int(10) NOT NULL COMMENT '发送时间',
  is_location smallint(1) NOT NULL COMMENT '是否定位',
  latitude bigint(9) NOT NULL COMMENT '纬度',
  longitude bigint(9) NOT NULL COMMENT '经度',
  speed int(4) NOT NULL COMMENT '速度',
  heading int(3) NOT NULL COMMENT '方向',
  PRIMARY KEY (id)
) DEFAULT CHARSET=utf8 COMMENT='GPS数据表';