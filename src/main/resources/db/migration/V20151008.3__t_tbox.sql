create table t_tbox(
id int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
t_sn varchar(15) COMMENT 'TBOX序列号',
vin varchar(18)  COMMENT 'TBOX序列号',
is_activated int(1) default 0 COMMENT '激活标识 0 未激活 1 激活',
activation_time DATETIME COMMENT '激活时间',
imei varchar(15) COMMENT 'IMEI',
mobile varchar(15)  COMMENT '手机号码',
remark varchar(200) COMMENT '备注',
PRIMARY KEY (id)
) DEFAULT CHARSET=utf8 COMMENT='TBOX表';