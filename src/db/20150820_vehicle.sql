create table t_vehicle
(
  vin varchar(50) NOT NULL COMMENT '���ܺ�',
  vendor varchar(100) COMMENT '����',
  model varchar(100) COMMENT '�ͺ�',
  t_flag int COMMENT '--0 ��Ȼ���� 1 ������ѹ',
  displacement varchar(20) COMMENT '����',
  license_plate varchar(10) COMMENT '���ƺ�',
  product_date datetime COMMENT '��������',
  PRIMARY KEY (vin)
) DEFAULT CHARSET=utf8 COMMENT='������';

create table sysdict
(
  dictid int(11) NOT NULL AUTO_INCREMENT COMMENT '�ֵ�ID',
  type int(3) COMMENT '�ֵ�����',
  dictname varchar(500) COMMENT '�ֵ�����',
  remark varchar(500)  COMMENT '��ע',
  PRIMARY KEY (dictid)
) DEFAULT CHARSET=utf8 COMMENT='�ֵ��';

