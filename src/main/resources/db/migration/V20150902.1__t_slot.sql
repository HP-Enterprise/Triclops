CREATE TABLE t_slot (
  id bigint(12) NOT NULL AUTO_INCREMENT COMMENT 'id',
  uid varchar(50) NOT NULL COMMENT '用户id',
  slotkey varchar(50) NOT NULL COMMENT 'slotkey',
  slot longblob NOT NULL COMMENT 'slot二进制数据',
  PRIMARY KEY (id)
)  DEFAULT CHARSET=utf8 COMMENT='通讯录表';

alter table t_slot add INDEX uid_slot (uid);
alter table t_slot add INDEX slotkey_slot (slotkey);