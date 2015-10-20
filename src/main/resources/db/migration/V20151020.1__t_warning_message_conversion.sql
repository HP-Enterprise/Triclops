CREATE TABLE t_warning_message_conversion (
  id int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  message_id smallint(3) NOT NULL COMMENT '报警消息id',
  message_en varchar(50) NOT NULL COMMENT '英文消息',
  message_zh varchar(50) NOT NULL COMMENT '中文消息',
  PRIMARY KEY (id)
 ) DEFAULT CHARSET=utf8 COMMENT='报警消息表';
