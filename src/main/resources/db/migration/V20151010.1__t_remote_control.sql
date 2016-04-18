create table t_remote_control(
id bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
uid int(11) NOT NULL COMMENT 'uid',
session_id varchar(15) NOT NULL COMMENT'application+eventid拼接成的会话id',
vin varchar(18)  NOT NULL COMMENT 'vin',
sending_time DATETIME NOT NULL COMMENT '发送时间',
control_type smallint(1) NOT NULL COMMENT '控制类别  0：远程启动发动机  1：远程关闭发动机  2：车门上锁  3：车门解锁  4：空调开启  5：空调关闭  6：座椅加热  7：座椅停止加热  8：远程发动机限制  9：远程发动机限制关闭  10：远程寻车',
ac_temperature smallint(6) NOT NULL COMMENT '空调温度(配合type=4)',
status smallint(1)  COMMENT '状态变化 0已发预命令  1不符合条件主动终止 2返回无效 3返回执行成功 4返回执行失败',
latitude double NOT NULL COMMENT '发起命令APP的纬度',
longitude double NOT NULL COMMENT '发起命令APP经度',
remark varchar(200) COMMENT '备注',
available smallint(1) NOT NULL DEFAULT 1 COMMENT '是否存在, 0 失效 1 有效 默认为1',
PRIMARY KEY (id),
 FOREIGN KEY (uid) REFERENCES t_user(Id)
) DEFAULT CHARSET=utf8 COMMENT='远程控制指令表';