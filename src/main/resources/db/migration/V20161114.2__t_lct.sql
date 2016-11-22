CREATE TABLE IF NOT EXISTS t_lct(
    id bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    model  VARCHAR(50) COMMENT '设备型号',
    odm_model VARCHAR(50) COMMENT '品牌型号',
    imei VARCHAR(50) COMMENT 'IMEI号',
    imsi VARCHAR(50) COMMENT 'IMSI号',
    hw_ver VARCHAR(50) COMMENT '硬件版本号',
    sw_ver VARCHAR(50) COMMENT '软件内部版本号',
    odm_sw_ver VARCHAR(50) COMMENT '软件外部版本号',
    wifi_mac VARCHAR(50) COMMENT 'WIFI MAC地址',
    bt_mac VARCHAR(50) COMMENT '蓝牙 MAC地址',
    vendor VARCHAR(50) COMMENT '生产厂商',
    brand_name VARCHAR(50) COMMENT '品牌名称',
    receive_time DATETIME COMMENT '接收时间',
    PRIMARY KEY (id),
    UNIQUE key (imei)
) COMMENT='后视镜信息表';