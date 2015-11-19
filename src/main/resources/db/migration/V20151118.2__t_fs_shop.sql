CREATE TABLE IF NOT EXISTS t_fs_shop (
  id int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  oid int(11) NOT NULL COMMENT '组织ID',
  fsname varchar(50) NOT NULL COMMENT '4s店名称',
  fsphone varchar(11) COMMENT '联系电话',
  fsaddress varchar(50) COMMENT '4s店地址',
  city varchar(50) COMMENT '城市',
  cityid int(11) NOT NULL COMMENT '城市id',
  PRIMARY KEY (id)

) DEFAULT CHARSET=utf8 COMMENT='4s店信息表';
INSERT INTO t_fs_shop(id,oid,fsname,fsphone,fsaddress,city,cityid) values('1','1','华晨汽车4s店','3244754','关山大道155号','武汉','1');