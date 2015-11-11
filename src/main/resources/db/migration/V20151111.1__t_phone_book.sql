CREATE TABLE IF NOT EXISTS t_slot (
  id bigint(12) NOT NULL AUTO_INCREMENT COMMENT 'id',
  uid int(11) NOT NULL COMMENT '用户id',
  slotkey varchar(50) NOT NULL COMMENT 'slotkey',
  slot longtext NOT NULL COMMENT 'slot数据base64',
  PRIMARY KEY (id),
  FOREIGN KEY (uid) REFERENCES t_user(Id)
) DEFAULT CHARSET=utf8 COMMENT='通讯录表';