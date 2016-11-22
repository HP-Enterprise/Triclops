CREATE TABLE IF NOT EXISTS t_lct_remote_control(
id bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
uid int(11) COMMENT 'uid',
sequence_id varchar(20)  NOT NULL COMMENT '序号',
imei varchar(20)  NOT NULL COMMENT 'imei',
sending_time DATETIME NOT NULL COMMENT '发送时间',
receive_time DATETIME NULL COMMENT '响应时间',
command varchar(100) NOT NULL COMMENT '指令',
params varchar(100) NOT NULL COMMENT '指令参数',
status smallint(1)  COMMENT '状态变化 0：失败 1：成功',
remark varchar(100) COMMENT '备注，错误详细信息',
PRIMARY KEY (id),
UNIQUE key (sequence_id)
) DEFAULT CHARSET=utf8 COMMENT='远程控制指令表';