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
  latitude double NOT NULL COMMENT '纬度',
  longitude double NOT NULL COMMENT '经度',
  speed float NOT NULL COMMENT '速度',
  heading int(11) NOT NULL COMMENT '方向',
  srs_warning smallint(1) NOT NULL COMMENT '安全气囊报警 0未触发 1触发',
  ata_warning smallint(1) NOT NULL COMMENT '车辆防盗报警 0未触发 1触发',
  safety_belt_count smallint(1) DEFAULT 0 COMMENT '安全带',
  vehicle_hit_speed int(11) NOT NULL COMMENT '碰撞速度',
  PRIMARY KEY (id)
) DEFAULT CHARSET=utf8 COMMENT='报警数据表';

