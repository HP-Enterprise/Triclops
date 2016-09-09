CREATE TABLE IF NOT EXISTS t_iccid_phone
(
  id int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  iccid  varchar(20) NOT NULL COMMENT 'iccid',
  phone  varchar(20) NOT NULL COMMENT '手机号码',
  PRIMARY KEY (id),
  UNIQUE key (iccid,phone)
) DEFAULT CHARSET=utf8 COMMENT='iccid与手机号码关系表';