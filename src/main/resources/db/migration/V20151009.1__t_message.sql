CREATE TABLE IF NOT EXISTS t_message (
  id int(11) NOT NULL AUTO_INCREMENT,
  source_id int(11)  COMMENT '发送用户id',
  resource_from int(1)  COMMENT '数据来源 1 手机,2 车机',
  target_type int(1) COMMENT '目标对象类型 1 user ,2 organize',
  target_id int(11) COMMENT '目标对象id',
  resource_to int(1) COMMENT '目标类型 1 手机,2 车机,3 短信,4 手机 车机 短信',
  fun_type int(1) COMMENT '消息类型 1 即时通讯 2 推送消息',
  p_type int(1) COMMENT '推送消息类型,1 远程控制,2 故障提醒,3 保养提醒,4 位置分享,5 智能寻车,6 位置共享,7 发送位置,8 气囊报警,9 防盗报警',
  content_type int(1) COMMENT '发送内容类型 ,1 文本,2 音乐,3 图片,41 位置信息,42 位置共享开始,43 位置共享结束',
  text_content varchar(1000) COMMENT '发送内容文本、经纬度',
  file_name varchar(100) COMMENT '发送的附件名',
  PRIMARY KEY (id)
)DEFAULT CHARSET=utf8 COMMENT = '消息表';