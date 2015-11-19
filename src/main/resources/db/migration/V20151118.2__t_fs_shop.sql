CREATE TABLE IF NOT EXISTS t_fs_shop (
  id int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  oid int(11) NOT NULL COMMENT '组织ID',
  fSname varchar(50) NOT NULL COMMENT '4s店名称',
  fSphone varchar(11) COMMENT '联系电话',
  fSaddress varchar(50) COMMENT '4s店地址',
  city varchar(50) COMMENT '城市',
  cityid int(11) NOT NULL COMMENT '城市id',
  PRIMARY KEY (id),
) DEFAULT CHARSET=utf8 COMMENT='4s店信息表';