CREATE TABLE t_user (
  Id int(11) NOT NULL AUTO_INCREMENT,
  name varchar(50) NOT NULL DEFAULT '' COMMENT '�˺�',
  gender int(3) DEFAULT NULL COMMENT '�Ա�',
  nick varchar(50) NOT NULL DEFAULT '' COMMENT '��ʾ�������˿�������',
  phone varchar(11) DEFAULT NULL COMMENT '�绰����'
  PRIMARY KEY (Id)
) DEFAULT CHARSET=utf8 COMMENT='�û���';
