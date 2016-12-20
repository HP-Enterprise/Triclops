alter table t_data_realtime_report add trip_a float DEFAULT 0 COMMENT '小计里程A';
alter table t_data_realtime_report add trip_b float DEFAULT 0 COMMENT '小计里程B';
alter table t_data_realtime_report add seatbelt_fl varchar(1) DEFAULT NULL COMMENT '前左（主驾）安全带 0没系 1系了 2保留 3信号异常';
alter table t_data_realtime_report add seatbelt_fr varchar(1) DEFAULT NULL COMMENT '前右（副驾）安全带 0没系 1系了 2保留 3信号异常';
alter table t_data_realtime_report add seatbelt_rl varchar(1) DEFAULT NULL COMMENT '后左安全带 0没系 1系了 2保留 3信号异常';
alter table t_data_realtime_report add seatbelt_rm varchar(1) DEFAULT NULL COMMENT '后中安全带 0没系 1系了 2保留 3信号异常';
alter table t_data_realtime_report add seatbelt_rr varchar(1) DEFAULT NULL COMMENT '后右安全带 0没系 1系了 2保留 3信号异常';
