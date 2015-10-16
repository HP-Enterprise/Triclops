CREATE TABLE IF NOT EXISTS t_message (
  id int(11) NOT NULL AUTO_INCREMENT,
  source_id int(11)  COMMENT '发送用户id',
  resource_from int(1)  COMMENT '数据来源 1 手机,2 车机',
  target_id int(11)  COMMENT '目标对象id userid or organizeid',
  resource_to int(1) COMMENT '目标类型 1 手机,2 车机,3 短信,4 手机&车机&短信',
  content_type int(1)  COMMENT '发送内容类型 1表示text，2表示文件',
  text_content varchar(4000) COMMENT '发送内容文本',
  file_content longblob COMMENT '发送内容文件',
  send_time datetime COMMENT '发送时间',
  filetype varchar(100) COMMENT '文件名称',
  messagefrom varchar(100) COMMENT '发送名称',
  messagetarget varchar(100) COMMENT '目标名称',
  targettype int(1) COMMENT '目标对象类型 1 user 2 organize',
  PRIMARY KEY (id)
)DEFAULT CHARSET=utf8 COMMENT = '消息表';

