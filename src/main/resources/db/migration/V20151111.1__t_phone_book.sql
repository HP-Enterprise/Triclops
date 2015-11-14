CREATE TABLE IF NOT EXISTS t_phone_book (
  id int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  uid int(11) NOT NULL COMMENT '用户ID',
  name varchar(50) NOT NULL COMMENT '联系人名称',
  phone varchar(11) COMMENT '联系人电话',
  isuser int(1) NOT NULL COMMENT '是否为系统用户 1:是 0：不是',
  PRIMARY KEY (id),
  KEY idx_uid (uid)
) DEFAULT CHARSET=utf8 COMMENT='用户通讯录表';