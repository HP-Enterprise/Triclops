CREATE TABLE t_organization (
  o_Id int(11) NOT NULL AUTO_INCREMENT COMMENT '组织的id',
  org_name varchar(128) NOT NULL COMMENT '组织名称',
  bre_code varchar(8) UNIQUE NOT NULL COMMENT '组织简码',
  type_key int(1) NOT NULL COMMENT '组织类型',
  sub_oid int(11) NOT NULL COMMENT '子组织id',
  par_oid int(11) NOT NULL COMMENT '父组织id',
  PRIMARY KEY (O_Id)
) DEFAULT CHARSET=utf8 COMMENT='组织表';
