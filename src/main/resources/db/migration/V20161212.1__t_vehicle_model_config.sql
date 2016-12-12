CREATE TABLE IF NOT EXISTS t_vehicle_model_config
(
  id int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  model_id  SMALLINT (5) NOT NULL COMMENT '车型代号，值对应TBox协议DispacherMessage的VehicleModel字段',
  model_name  varchar(20) NOT NULL COMMENT '车型名称，如F60',
  funcation  varchar(50) NOT NULL COMMENT '功能',
  PRIMARY KEY (id),
  UNIQUE key (model_id)
) DEFAULT CHARSET=utf8 COMMENT='车型具备功能参数表';

INSERT INTO t_vehicle_model_config VALUES ('1', '0', 'M82', '101010');
INSERT INTO t_vehicle_model_config VALUES ('2', '1', 'M82', '101010');
INSERT INTO t_vehicle_model_config VALUES ('3', '2', 'M85', '001001');
INSERT INTO t_vehicle_model_config VALUES ('4', '3', 'F60', '010101');
INSERT INTO t_vehicle_model_config VALUES ('5', '4', 'F70', '111111');
INSERT INTO t_vehicle_model_config VALUES ('6', '5', 'F60电动车', '000000');