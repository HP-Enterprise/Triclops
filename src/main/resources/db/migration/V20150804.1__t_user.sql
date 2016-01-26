CREATE TABLE IF NOT EXISTS t_user (
  Id int(11) NOT NULL AUTO_INCREMENT,
  name varchar(50) NOT NULL DEFAULT '' COMMENT '账号',
  gender int(3) DEFAULT NULL COMMENT '性别',
  nick varchar(50) NOT NULL DEFAULT '' COMMENT '显示给其它人看的名称',
  phone varchar(11) DEFAULT NULL COMMENT '电话号码',
  is_verified  int(1) DEFAULT 0 COMMENT '0表示未验证，1表示已验证',
  contacts varchar(50) DEFAULT NULL COMMENT '紧急联系人的姓名',
  contacts_phone varchar(11) DEFAULT NULL COMMENT '紧急联系人的电话',
  icon varchar(100) DEFAULT  '/api/downloadIconFile?icon=default.png' COMMENT 'API路径 ICON为上传到sftp文件名',
  PRIMARY KEY (Id),
  UNIQUE KEY unique_name (name),
  KEY idx_phone (phone)
) DEFAULT CHARSET=utf8 COMMENT='用户表';

INSERT INTO t_user(Id,name,gender,nick,phone) values('1','admin','1','admin4096','13XXXXXXXXX');
