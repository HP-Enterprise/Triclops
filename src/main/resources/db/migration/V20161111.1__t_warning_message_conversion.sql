delete from t_warning_message_conversion where group_id in(21,22,23,24,25);
insert into t_warning_message_conversion(message_id,message_en,message_zh,group_id,group_message,group_message_en) values('200','','','21','电子换挡器故障','en21');
insert into t_warning_message_conversion(message_id,message_en,message_zh,group_id,group_message,group_message_en) values('201','','','22','电动尾门系统故障','en22');
insert into t_warning_message_conversion(message_id,message_en,message_zh,group_id,group_message,group_message_en) values('202','','','21','电子换挡器故障','en21');

insert into t_warning_message_conversion(message_id,message_en,message_zh,group_id,group_message,group_message_en) values('0','','','23','前方碰撞预警系统故障','en23');
insert into t_warning_message_conversion(message_id,message_en,message_zh,group_id,group_message,group_message_en) values('203','','','24','紧急制动系统故障','en24');
insert into t_warning_message_conversion(message_id,message_en,message_zh,group_id,group_message,group_message_en) values('204','','','25','车道偏离预警系统故障','en25');
