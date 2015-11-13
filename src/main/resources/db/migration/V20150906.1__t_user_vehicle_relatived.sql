CREATE TABLE IF NOT EXISTS t_user_vehicle_relatived(
  id int(11) NOT NULL AUTO_INCREMENT COMMENT '车辆绑定id',
  userid int not null COMMENT '用户ID',
  vid int(11) not null COMMENT '车辆vid',
  vflag int(1) DEFAULT 0 COMMENT '车辆标识 1 默认  0 非默认',
  iflag int not null COMMENT '车主标识 1 车主 0 朋友' ,
  parent_user_id int COMMENT '车主id',
  PRIMARY KEY (id),
  FOREIGN KEY (userid) REFERENCES t_user(Id),
  FOREIGN KEY (vid) REFERENCES t_vehicle(id)
) DEFAULT CHARSET=utf8 COMMENT='车辆绑定关系表';