alter table t_remote_control_init MODIFY  remote_started_count smallint(1)  NULL DEFAULT 0 COMMENT '已经做过的远程启动发动机次数';
alter table t_remote_control_init MODIFY is_announce smallint(1)  NULL DEFAULT 0 COMMENT '远程启动发动机参数，是否是Announce 0否 1是';
alter table t_remote_control_init MODIFY  available smallint(1)  NULL DEFAULT 1 COMMENT '是否存在, 0 失效 1 有效 默认为1';
alter table t_remote_control_init MODIFY  ref_id bigint(20)  NULL COMMENT 'ref id';
