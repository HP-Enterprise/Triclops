CREATE TABLE IF NOT EXISTS t_data_original_driving_behavior (
  id bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  vin varchar(50) NOT NULL COMMENT 'vin',
  imei varchar(50) NOT NULL COMMENT 'imei',
  hex_string longtext NOT NULL COMMENT '报文16进制字符串',
  receive_time DATETIME NOT NULL COMMENT '接收时间',
  PRIMARY KEY (id),
  INDEX idx_vin(vin),
  INDEX idx_receive_time(receive_time)
) DEFAULT CHARSET=utf8 COMMENT='急加速急减速急转弯原始报文数据表';

