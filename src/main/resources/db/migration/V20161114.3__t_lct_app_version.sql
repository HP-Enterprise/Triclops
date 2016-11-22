CREATE TABLE IF NOT EXISTS t_lct_app_version(
    id bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    app_id varchar(20)  NOT NULL COMMENT '应用id',
    version varchar(20)  NOT NULL COMMENT '版本',
    url varchar(100)  NOT NULL COMMENT '下载url',
    app_size int(10)  COMMENT 'app大小 字节',
    md5 varchar(32)  NOT NULL COMMENT 'MD5校验',
    app_desc varchar(200) NULL COMMENT '版本简介',
    publish_time DATETIME COMMENT '发布时间',
    PRIMARY KEY (id),
    UNIQUE key (app_id,version)
) DEFAULT CHARSET=utf8 COMMENT='APP版本信息表';