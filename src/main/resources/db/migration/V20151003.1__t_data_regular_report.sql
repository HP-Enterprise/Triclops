CREATE TABLE IF NOT EXISTS  t_data_regular_report (
 id bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
 vin varchar(50) NOT NULL COMMENT '车架号',
 imei varchar(50) NOT NULL COMMENT 'imei',
 application_id int(11) NOT NULL COMMENT 'application id',
 message_id int(11) NOT NULL COMMENT 'message id',
 sending_time DATETIME NOT NULL COMMENT '发送时间',
 frequency_for_realtime_report int(11) NOT NULL COMMENT '实时数据上报周期',
 frequency_for_warning_report int(11) NOT NULL COMMENT '报警数据上报周期',
 frequency_heartbeat int(11) NOT NULL COMMENT '心跳周期',
 timeout_for_terminal_search int(11) NOT NULL COMMENT '终端应答超时',
 timeout_for_server_search int(11) NOT NULL COMMENT '平台应答超时',
 vehicle_type smallint(6) NOT NULL COMMENT '车辆类型',
 vehicle_models int(11) NOT NULL COMMENT '车辆型号',
 max_speed smallint(6) NOT NULL COMMENT '最大速度',
 hardware_version varchar(50) NOT NULL COMMENT '硬件版本',
 software_version varchar(50) NOT NULL COMMENT '软件版本',
 frequency_save_local_media int(11) NOT NULL COMMENT '本地数据保存周期',
 enterprise_broadcast_address varchar(50) NOT NULL COMMENT '平台ip',
 enterprise_broadcast_port int(11) NOT NULL COMMENT '平台端口',
 PRIMARY KEY (id)
) DEFAULT CHARSET=utf8 COMMENT='额定数据表';

