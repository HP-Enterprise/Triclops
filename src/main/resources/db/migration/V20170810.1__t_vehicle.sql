 alter table t_vehicle add column real_name_authentication int(1) DEFAULT 0 COMMENT '实名认证状态 0未实名 1已实名 2审核中 3认证失败';