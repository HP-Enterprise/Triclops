CREATE TABLE IF NOT EXISTS t_data_driving_behavior (
  id bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  vin varchar(50) NOT NULL COMMENT 'vin',
  imei varchar(50) NOT NULL COMMENT 'imei',
  application_id int(11) NOT NULL COMMENT 'application id',
  message_id int(11) NOT NULL COMMENT 'message id',
  sending_time DATETIME NOT NULL COMMENT '发送时间',
  receive_time DATETIME NOT NULL COMMENT '接收时间',
  speed_up smallint(5) DEFAULT 0 COMMENT '急加速次数',
  speed_down smallint(5) DEFAULT 0 COMMENT '急减速次数',
  speed_turn smallint(5) DEFAULT 0 COMMENT '急转弯次数',
  PRIMARY KEY (id),
  INDEX idx_vin(vin),
  INDEX idx_sending_time(sending_time)
) DEFAULT CHARSET=utf8 COMMENT='急加速急减速急转弯数据表';

