create table t_remote_control(
id bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
uid int(11) COMMENT 'uid',
vin varchar(18)  COMMENT 'vin',
sending_time DATETIME NOT NULL COMMENT '发送时间',
control_type smallint(1) COMMENT '操作类别  0：远程启动发动机 1：远程关闭发动机 2：车门上锁 3：车门解锁 4：空调开启 5：空调关闭 6：座椅加热 7：座椅停止加热 8：远程鸣笛 9：远程鸣笛关闭 10：远程开灯 11：远程关闭 12：远程发动机限制 13：远程发动机限制关闭 14：远程寻车 15：设置空调温度',
ac_temperature smallint(6) COMMENT '空调温度(配合type15)',
status smallint(1)  COMMENT '状态 0正在执行 1成功 2失败',
remark varchar(200) COMMENT '备注',
PRIMARY KEY (id)
 FOREIGN KEY (uid) REFERENCES t_user(Id)
) DEFAULT CHARSET=utf8 COMMENT='远程控制指令表';