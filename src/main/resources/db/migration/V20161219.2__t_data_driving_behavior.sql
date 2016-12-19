CREATE TABLE IF NOT EXISTS t_data_driving_behavior (
  id bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  vin varchar(50) NOT NULL COMMENT 'vin',
  imei varchar(50) NOT NULL COMMENT 'imei',
  application_id int(11) NOT NULL COMMENT 'application id',
  message_id int(11) NOT NULL COMMENT 'message id',
  sending_time DATETIME NOT NULL COMMENT '发送时间',
  lateral_acceleration VARCHAR(500) NULL COMMENT '横向加速度信息',
  drive_acceleration VARCHAR(500) NULL COMMENT '行驶方向加速度信息',
  brake VARCHAR(500) NULL COMMENT '紧急刹车信息',
  speed VARCHAR(500) NULL COMMENT '车速信息',
  lws VARCHAR(500) NULL COMMENT '方向盘转角信息',
  bcvol VARCHAR(500) NULL COMMENT '方向盘开关信息',
  cruise VARCHAR(500) NULL COMMENT '车辆加速减速信息',
  PRIMARY KEY (id),
  INDEX idx_vin(vin),
  INDEX idx_sending_time(sending_time)
) DEFAULT CHARSET=utf8 COMMENT='实时数据表';

