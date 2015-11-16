CREATE TABLE IF NOT EXISTS t_data_realtime_report (
  id bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  vin varchar(50) NOT NULL COMMENT 'vin',
  imei varchar(50) NOT NULL COMMENT 'imei',
  application_id int(11) NOT NULL COMMENT 'application id',
  message_id int(11) NOT NULL COMMENT 'message id',
  sending_time DATETIME NOT NULL COMMENT '发送时间',
  fuel_oil float NOT NULL COMMENT '燃油量',
  avg_oil_a float NOT NULL COMMENT '平均油耗A',
  avg_oil_b float NOT NULL COMMENT '平均油耗B',
  service_intervall int(11) NOT NULL COMMENT '保养里程',
  left_front_tire_pressure float NOT NULL COMMENT '左前轮胎压',
  left_rear_tire_pressure float NOT NULL COMMENT '左后轮胎压',
  right_front_tire_pressure float NOT NULL COMMENT '右前轮胎压',
  right_rear_tire_pressure float NOT NULL COMMENT '右后轮胎压',
  left_front_window_information varchar(1) NOT NULL COMMENT '左前车窗信息1开0关',
  left_rear_window_information varchar(1) NOT NULL COMMENT '左后车窗信息1开0关',
  right_front_window_information varchar(1) NOT NULL COMMENT '右前车窗信息1开0关',
  right_rear_window_information varchar(1) NOT NULL COMMENT '右后车窗信息1开0关',
  vehicle_temperature smallint(6) NOT NULL COMMENT '车内温度',
  vehicle_outer_temperature smallint(6) NOT NULL COMMENT '车外温度',
  left_front_door_information varchar(1) NOT NULL COMMENT '左前车门信息1开0关',
  left_rear_door_information varchar(1) NOT NULL COMMENT '左后车门信息1开0关',
  right_front_door_information varchar(1) NOT NULL COMMENT '右前车门信息1开0关',
  right_rear_door_information varchar(1) NOT NULL COMMENT '右后车门信息1开0关',

  PRIMARY KEY (id)
) DEFAULT CHARSET=utf8 COMMENT='实时数据表';

