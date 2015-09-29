CREATE TABLE IF NOT EXISTS t_data_gps (
  id bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  vin varchar(17) NOT NULL COMMENT '���ܺ�',
  serial_number varchar(14) NOT NULL COMMENT 'TBOX���к�',
  imei varchar(15) NOT NULL COMMENT 'imei',
  application_id int(2) NOT NULL COMMENT 'Ӧ������id',
  message_id int(1) NOT NULL COMMENT '��Ϣid',
  sending_time int(10) NOT NULL COMMENT '����ʱ��',
  is_location smallint(1) NOT NULL COMMENT '�Ƿ�λ',
  latitude bigint(9) NOT NULL COMMENT 'γ��',
  longitude bigint(9) NOT NULL COMMENT '����',
  speed int(4) NOT NULL COMMENT '�ٶ�',
  heading int(3) NOT NULL COMMENT '����',
  PRIMARY KEY (id)
) DEFAULT CHARSET=utf8 COMMENT='GPS���ݱ�';