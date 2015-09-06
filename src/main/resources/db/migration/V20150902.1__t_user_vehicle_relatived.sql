create table t_user_vehicle_relatived(
  id int(11) NOT NULL AUTO_INCREMENT COMMENT '车辆绑定id',
  userid int(11) not null COMMENT '用户ID',
  vin varchar(30) not null COMMENT '车辆vin码',
  iflag int not null COMMENT '车主标识 1 车主 0 朋友' ,
  parent_user_id int(11) COMMENT '车主id',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='车辆绑定关系表';