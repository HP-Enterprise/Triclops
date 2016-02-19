CREATE TABLE t_warning_message_conversion (
  id int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  message_id varchar(5) NOT NULL COMMENT '报警消息id',
  message_en varchar(150) NOT NULL COMMENT '英文消息',
  message_zh varchar(150) NOT NULL COMMENT '中文消息',
  PRIMARY KEY (id)
 ) DEFAULT CHARSET=utf8 COMMENT='报警消息表';

insert into t_warning_message_conversion(message_id,message_en,message_zh) values('50','PDC System not available because of malfunction','PDC系统故障');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('51','BSW System not available because of malfunction','BSW系统故障');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('52','Continious Seat belt warning for unbelted driver','司机未系安全带持续警报');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('53','Blinking Seat belt warning for unbelted driver','司机未系安全带持续闪烁警报灯');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('54','Continious Seat belt warning for unbelted front passenger','前排乘客没有系安全带的安全带持续预警');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('55','Blinking Seat belt warning for unbelted front passenger','前排乘客没有系安全带持续闪烁警报灯');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('1','Airbag or Restraint sytem malfunction','安全气囊或者座椅安全带系统失灵');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('120','passenger air bag off','乘客安全气囊关闭');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('2','Airbag or Restraint sytem malfunction','安全气囊或者座椅安全带系统失灵');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('56','Seat belt system malfunction','座椅安全带系统故障');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('57','Immobilizer key acceptance failed','防盗钥匙接受失败');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('58','Immobilizer communication failure','防盗器通信故障');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('3','steering lock defect, steering wheel is not able to be moved','转向锁缺陷，转向盘不能移动');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('59','BCM lost communication with PDC','BCM与PDC失去连接');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('60','Steering lock failed to unlock because of blocking','转向锁因为阻塞未能解锁');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('61','engine start / stop button defect','发动机起动/停止按钮缺陷');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('62','Press Brake padel for engine start','发动机启动机制动踏板');

insert into t_warning_message_conversion(message_id,message_en,message_zh) values('63','Gear not in position P or N so engine start not possible','发动机不能启动，因为齿轮不在P或N位置');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('64','Press Clutch padel for engine start','发动机启动机离合器踏板');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('65',"No valid key found","没有检测到有效的钥匙");
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('121','Identified Smart Key has a low battery status','确定了智能钥匙具有低的电池状态');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('122','Feedback sound of Seat memory setting activation','座椅记忆激活的反馈声音');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('123','Feedback sound of Seat memory storage','座椅记忆存储的反馈声音');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('66','Activated Turn indicator left','激活左转向指示器');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('67','Activated Turn indicator right','激活右转向指示器');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('68','Activated Hazard blinking','激活危险闪烁灯');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('69','Activated High Beam','激活远光灯');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('70','Activated Fog lights','激活雾灯');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('71','Activated Rear Fog lights','激活后雾灯');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('72','Activated Position Lights','激活位置灯');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('73','Position lights are not deactivated during driver door opened','在驾驶门打开时位置灯不被激活');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('124','Information about activated Follow me home light','激活伴我回家功能');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('125','Energy Saving or Transport mode activated','激活节能或运输方式');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('4','Gateway in BCM not working so general Electric fault in the vehicle','网关在BCM不工作，车辆通用电气故障');

insert into t_warning_message_conversion(message_id,message_en,message_zh) values('5','Gateway and BCM not working so general Electric fault in the vehicle','网关和BCM不工作，车辆通用电气故障');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('74','Malfunction in light system','光系统故障');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('126','Malfunction in light system','光系统故障');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('127','Malfunction in light system','光系统故障');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('128','Malfunction in light system','光系统故障');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('129','Malfunction in light system','光系统故障');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('130','Malfunction in light system','光系统故障');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('131','Malfunction in light system','光系统故障');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('132','Malfunction in light system','光系统故障');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('133','Malfunction in light system','光系统故障');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('134','Malfunction in light system','光系统故障');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('135','automatic headlamp activated','激活自动前照灯');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('136','automatic headlamp deactivated','自动关闭大灯');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('137','automatic wiper activated','自动激活雨刮器');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('138','automatic wiper deactivated','自动关闭雨刮器');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('75','during driving one or more doors or flaps are open','在驾驶期间一个或多个门或挡泥板是开着的');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('76','BCM lost communication with SCM and activates wiper durable until communication is working again','BCM失去与单片机和激活雨刷通信直至通信恢复工作');

insert into t_warning_message_conversion(message_id,message_en,message_zh) values('139','Rain light sensor malfunction','雨光传感器故障');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('6','ESC System not working','ESC系统不工作');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('7','ESC System not working','ESC系统不工作');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('8','ECS Control active because of instable driving situation','ECS因为不稳定的驾驶状况控制主动');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('77','ESC switched off on drivers demand','ESC关闭驾驶员需求');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('9','ABS System not working','ABS防抱死系统故障');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('10','ABS System not working','ABS防抱死系统故障');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('11','Brake fluid level to low','制动液液位低');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('12','Brake system malfunction','制动系统故障');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('13','Brake system malfunction','制动系统故障');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('14','Tyre is loosing pressure very quick','轮胎在很快的失去压力');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('15','Tyre is loosing pressure under a certain level','轮胎压力过低');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('16','Tyre has a high pressure over a certain level','轮胎压力过高');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('78','Flat tyre warning system not active','轮胎警告系统不活跃');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('79','One or more sensors not connected to TPMS System','一个或多个传感器未连接到TPMS系统');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('140','One or more sensors have a low battery status','一个或多个传感器电量过低');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('17','High temperatur measured in one or more tyres','一个或多个轮胎温度过高');

insert into t_warning_message_conversion(message_id,message_en,message_zh) values('80','Parking break activated','激活刹车');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('81','Driving with activated Parking Break','主动刹车');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('82','Autohold activated and working','激活自动驻车功能和工作');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('83','Electric park brake malfunction','电动停车制动故障');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('18','Electric power steering malfunction','电动助力转向故障');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('84','Electric power steering limited function','电动助力转向功能');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('85','Hill decent control activated','激活坡路缓降系统');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('86','Hill decent control active','坡路缓降系统活动');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('87','Hill decent control malfunction','坡路缓降系统故障');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('19','Forward collision warning because of critical distance to vehicle in front','正向碰撞警告，因为在前面的车辆的临界距离');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('88','Forward collision warning malfunction','正向碰撞警告故障');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('89','Cruise Control System activated','激活巡航控制系统');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('141','Cruise Control System cancelled','取消巡航控制系统');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('90','Contact Service to check Cruise Control','联系服务检查巡航控制系统');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('91','Start Stop System not available','启动停止系统不可用');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('142','Engine Stopped because of Start Stop System','发动机停止，因为启动停止系统');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('92','Start Stop System did not start the engine because of not fulfilled criteria','启动停止系统没有启动发动机，因为不符合标准');

insert into t_warning_message_conversion(message_id,message_en,message_zh) values('93','Start Stop System did not stop the engine because of not fulfilled criteria','启动停止系统没有启动发动机，因为不符合标准');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('94','Start Stop system deactivated for EOL Testing','启停系统停用EOL测试');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('143','T- Box Batterie for emergency call is empty and needs to be replaced','T-Box电量耗尽，需要更换');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('20','Low Oil Pressure','低油压力');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('21','Coolant temperature too high Engine overheated','冷却液温度过高发动机过热');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('95','Coolant temperature high','冷却液温度高');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('22','Critical Malfunction of Transmission System','传输系统故障');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('96','Malfunction of Transmission System','传动系统故障');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('97','Charging of the battery is not working','电池充电不工作');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('98',"'Increased emissions Check at next Service'",'在下一个服务增加排放检查');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('23','Engine fault Check at next Service','在下一个服务发动机故障检查');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('99','Low fuel warning','低燃油警告');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('100','Icy Road warning at temperatures <= +3°C','道路低温警告< = 3°C');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('101','Speed Limit set up in IPC exceeded','在IPC限速设置');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('144','T- Box Batterie for emergency call is empty and needs to be replaced','T-Box电量耗尽，需要更换');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('145','Driver mode-Sport','运动模式');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('146','Driver mode-Winter','冬季模式');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('147','Symbol shows that one or more Warnings are active','符号显示一个或多个警告是活动的');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('148','Transmission over heated(7 DCT)','变速器过热（7 DCT）');
insert into t_warning_message_conversion(message_id,message_en,message_zh) values('149','Service Information','服务指南');

