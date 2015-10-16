CREATE TABLE IF NOT EXISTS t_user_device (
  id          INT(11) NOT NULL AUTO_INCREMENT ,
  user_id     INT(11) NOT NULL COMMENT '用户Id',
  device_type INT(2) NOT NULL COMMENT '设备类型',
  device_id   VARCHAR(200) NOT NULL COMMENT '设备唯一标识',
  active      INT DEFAULT 1 COMMENT '是否激活，1激活0未激活',
  PRIMARY KEY (id),
  KEY t_user_device_index(user_id)
)DEFAULT CHARSET=utf8  COMMENT = '用户设备表' ;
