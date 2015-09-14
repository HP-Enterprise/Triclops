INSERT INTO t_authoritygroup(name) VALUES ('sysadmin');

INSERT INTO t_authoritygroup_relatived(a_id, ag_id) VALUES (0,(SELECT id from t_authoritygroup where name='sysadmin'));

INSERT INTO t_authoritygroup_relatived(a_id, ag_id) VALUES (1,(SELECT id from t_authoritygroup where name='sysadmin'));

INSERT INTO t_authoritygroup_user(u_id,ag_id) VALUES((SELECT id from t_user where name='admin'),(SELECT id from t_authoritygroup where name='sysadmin'));
