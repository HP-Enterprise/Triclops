CREATE TABLE `t_slot` (
  `id` bigint(12) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `uid` varchar(50) NOT NULL COMMENT '用户id',
  `slotkey` varchar(50) NOT NULL COMMENT 'slotkey',
  `slot` longblob NOT NULL COMMENT 'slot二进制数据',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;