CREATE TABLE IF NOT EXISTS t_lct_status_data(
id bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
sequence_id varchar(20)  NOT NULL COMMENT '序号',
type smallint(1)  NOT NULL COMMENT '数据类型 1状态信息 2碰撞事件 3 移动事件',
imei varchar(15)  NOT NULL COMMENT 'imei',
longitude double(10,6)  COMMENT 'longitude',
latitude double(10,6)  COMMENT 'latitude',
action_time DATETIME NULL COMMENT '事件时间',
receive_time DATETIME NULL COMMENT '接收时间',
PRIMARY KEY (id),
UNIQUE key (sequence_id,imei)
) DEFAULT CHARSET=utf8 COMMENT='后视镜设备数据表';