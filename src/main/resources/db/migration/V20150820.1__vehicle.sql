create table t_vehicle
(
  id int(11) NOT NULL AUTO_INCREMENT COMMENT '车辆ID',
  vin varchar(50) NOT NULL COMMENT '车架号',
  vendor varchar(100) COMMENT '厂家',
  model varchar(100) COMMENT '型号',
  t_flag int COMMENT '--0 自然吸气 1 涡轮增压',
  displacement varchar(20) COMMENT '排量',
  license_plate varchar(10) COMMENT '车牌号',
  product_date datetime COMMENT '生产日期',
  PRIMARY KEY (id)
) DEFAULT CHARSET=utf8 COMMENT='车辆表';

create table sysdict
(
  dictid int(11) NOT NULL AUTO_INCREMENT COMMENT '字典ID',
  type int(3) COMMENT '字典类型',
  dictname varchar(500) COMMENT '字典名称',
  remark varchar(500)  COMMENT '备注',
  PRIMARY KEY (dictid)
) DEFAULT CHARSET=utf8 COMMENT='字典表';

create table t_user_vehicle_relatived(
  id int(11) NOT NULL AUTO_INCREMENT COMMENT '车辆绑定id',
  userid int not null COMMENT '用户ID',
  vin varchar(30) not null COMMENT '车辆vin码',
  iflag int not null COMMENT '车主标识 1 车主 0 朋友' ,
  parent_user_id int COMMENT '车主id',
  PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COMMENT='车辆绑定关系表';

