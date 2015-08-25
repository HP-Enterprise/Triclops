CREATE TABLE t_account (
  account_id int(11) NOT NULL AUTO_INCREMENT ,
  account_name varchar(50) NOT NULL COMMENT '姓名',
  account_pwd char(32) NOT NULL COMMENT '密码',
  first_create DATETIME NOT NULL COMMENT '创建时间',
  identify_salt char(4) NOT NULL COMMENT 'salt',
  last_update DATETIME NOT NULL COMMENT '最后一次更新',
  PRIMARY KEY (account_id),
  INDEX t_account_index(account_name,identify_salt,account_pwd)
)DEFAULT CHARSET=utf8  COMMENT = 'account表' ;