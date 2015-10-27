CREATE TABLE IF NOT EXISTS t_vehicle_tbox
(
    Id int(11) NOT NULL AUTO_INCREMENT,
    vid int(11) NOT NULL COMMENT '车辆Id',
    tid int(11) NOT NULL COMMENT 'TBoxId',
    PRIMARY KEY (Id),
    FOREIGN KEY (vid) REFERENCES t_vehicle(Id),
    FOREIGN KEY (tid) REFERENCES t_tbox(id)
) DEFAULT CHARSET=utf8 COMMENT='车辆和TBox关系表';