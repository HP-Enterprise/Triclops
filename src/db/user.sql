CREATE TABLE t_user (
  Id int(11) NOT NULL AUTO_INCREMENT,
  name varchar(50) NOT NULL DEFAULT '' COMMENT '�˺�',
  gender int(3) DEFAULT NULL COMMENT '�Ա�',
  nick varchar(50) NOT NULL DEFAULT '' COMMENT '��ʾ�������˿�������',
  phone varchar(11) DEFAULT NULL COMMENT '�绰����',
  password varchar(100) DEFAULT '' COMMENT '����',
  PRIMARY KEY ('Id')
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='�û���';
