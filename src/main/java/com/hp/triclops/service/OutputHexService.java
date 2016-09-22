package com.hp.triclops.service;

/**
 * Created by luj on 2015/10/12.
 */

import com.alibaba.fastjson.JSON;
import com.hp.data.bean.tbox.*;
import com.hp.data.core.Conversion;
import com.hp.data.core.DataPackage;
import com.hp.data.util.PackageEntityManager;
import com.hp.triclops.acquire.DataTool;
import com.hp.triclops.entity.*;
import com.hp.triclops.redis.SocketRedis;
import com.hp.triclops.repository.*;
import com.hp.triclops.utils.DateUtil;
import com.hp.triclops.utils.GpsTool;
import com.hp.triclops.utils.HttpRequestTool;
import com.hp.triclops.utils.SMSHttpTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.util.*;

/**
 * 生成下行数据包hex 并写入redis
 */
@Service
public class OutputHexService {

    @Value("${com.hp.push.url}")
    private String urlLink;

    @Value("${com.hp.web.server.host}")
    private String webserverHost;

    @Autowired
    Conversion conversionTBox;
    @Autowired
    DataTool dataTool;
    @Autowired
    SocketRedis socketRedis;
    @Autowired
    TBoxParmSetRepository tBoxParmSetRepository;
    @Autowired
    RemoteControlRepository remoteControlRepository;
    @Autowired
    UserVehicleRelativedRepository userVehicleRelativedRepository;
    @Autowired
    VehicleRepository vehicleRepository;
    @Autowired
    WarningMessageConversionRepository warningMessageConversionRepository;
    @Autowired
    MQService mqService;
    @Autowired
    HttpRequestTool httpRequestTool;
    @Autowired
    RealTimeReportDataRespository realTimeReportDataRespository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    GpsTool  gpsTool;
    @Autowired
    SMSHttpTool smsHttpTool;
    @Autowired
    WarningMessageDataRespository warningMessageDataRespository;

    private final int SRSFIRST=1;
    private final int CRASHFIRST=2;
    private final int ATAFIRST=3;


    private Logger _logger = LoggerFactory.getLogger(OutputHexService.class);

    public String getRemoteControlSettingReqHex(RemoteControlSettingShow show,long eventId){
        //产生远程控制设置指令
        RemoteSettingReq hr=new RemoteSettingReq();
        hr.setTestFlag((short) 0);
        hr.setSendingTime((long) dataTool.getCurrentSeconds());
        hr.setApplicationID((short) 50);//>>>
        hr.setMessageID((short) 1);//>>>
        hr.setEventID(eventId);
        int value=show.getStartEngine()+ 2*show.getCentralLock() +4* show.getFindCar() +8*show.getAc()
                +16 *show.getSeatHeating() +32* show.getRemindFailure() +64 *show.getLocation() +128 *show.getSms();
        value=value>255?255:value;
        hr.setRemoteFunction(value);//01111111
        DataPackage dpw=new DataPackage("8995_50_1");//>>>
        dpw.fillBean(hr);
        ByteBuffer bbw=conversionTBox.generate(dpw);
        String byteStr=PackageEntityManager.getByteString(bbw);
        return byteStr;
    }

    public String getRemoteControlPreHex(RemoteControl remoteControl,long eventId){
        //产生远程控制预指令hex
        RemoteControlPreconditionReq remoteControlCmd=new RemoteControlPreconditionReq();
        remoteControlCmd.setApplicationID((short) 49);
        remoteControlCmd.setMessageID((short) 1);
        remoteControlCmd.setEventID((long) eventId);
        remoteControlCmd.setSendingTime((long)dataTool.getCurrentSeconds());
        remoteControlCmd.setTestFlag((short) 0);

        DataPackage dpw=new DataPackage("8995_49_1");//>>>
        dpw.fillBean(remoteControlCmd);
        ByteBuffer bbw=conversionTBox.generate(dpw);
        String byteStr= PackageEntityManager.getByteString(bbw);

        return byteStr;
    }

    public String getRemoteControlCmdHex(RemoteControl remoteControl,long eventId){
        //产生远程控制指令hex
        RemoteControlCmd  remoteControlCmd=new RemoteControlCmd();
        int _cType=-1;
        byte _remoteStartEngine=(byte)0;
        byte[] _remoteFindCar={(byte)0,(byte)0,(byte)0,(byte)0};
        byte _remoteLock=(byte)0;
        byte[] _remoteAc={(byte)0,(byte)0,(byte)0,(byte)0};
        byte[] _remoteHeating={(byte)0,(byte)0};
        short _masterL=0;
        short _slaveL=0;

        short _ac=0;//空调关
        short _recir=0;
        short _compr=0;
        short _model=0;

        double _sendTemp=0;
        Integer _sendTempInt=0;
        switch(remoteControl.getControlType().intValue())
        {
            case 0://
               _cType=0;
                //todo
                _remoteStartEngine=(byte)0;
                break;
            case 1://远程关闭发动机
                _cType=1;
                break;
            case 2://上锁
                _cType=3;
                _remoteLock=(byte)1;
                break;
            case 3://解锁
                _cType=3;
                _remoteLock=(byte)2;
                break;
            case 4://空调开启
                //todo
                _cType=4;
                _sendTemp=(remoteControl.getAcTemperature()-15.5)*2;
                _sendTempInt=(int)_sendTemp;
                if(_sendTempInt<0){
                    _sendTempInt=0;
                }
                if(_sendTempInt>0x22){
                    _sendTempInt=0x22;
                }
                _remoteAc[0]=_sendTempInt.byteValue();
                //Bit 0 – bit 1:     0x00:Deactivate climatization
                                    //0x01: Active Climatization in Manual Mode(CCUon)
                                    //0x02: Active Climatization in Auto mode (CCUon)
                //Bit 2 – bit 3:   0x01:recirulation on   0x02:recirulation off
                //Bit 4 – bit 5:   0x01:compressor on     0x02:compressor off

                _ac=1;//0x00:关 0x01: 开（手动模式） 0x02: 开（自动模式）
                if(remoteControl.getAutoMode()!=null){
                    if(remoteControl.getAutoMode()==(short)1){
                        _ac=2;
                    }
                }
                if(remoteControl.getRecirMode()!=null){
                    if(remoteControl.getRecirMode()==1){//外循环
                        _recir=2;
                    }else if(remoteControl.getRecirMode()==0){//内循环
                        _recir=1;
                    }
                }
                if(remoteControl.getAcMode()!=null){
                    if(remoteControl.getAcMode().intValue()==1){//压缩机开
                        _compr=1;
                    }else if(remoteControl.getAcMode().intValue()==0){//关
                        _compr=2;
                    }
                }
                _remoteAc[1]=(byte)(_ac+_recir*4+_compr*16);

                //BYTE[2]:0x00 not used 1 Body 2 Body + Feet 3 Feet 4 Defrost + Feet 5 Defrost
                if(remoteControl.getMode()!=null){
                    if(remoteControl.getMode()==1){//1除雾
                        _model=5;
                    }else if(remoteControl.getMode()==2){//2前玻璃除雾+吹脚
                        _model=4;
                    }else if(remoteControl.getMode()==3){//3 吹脚
                        _model=3;
                    }else if(remoteControl.getMode()==4){//4吹身体+吹脚
                        _model=2;
                    }else if(remoteControl.getMode()==5){//5吹身体
                        _model=1;
                    }
                }
                _remoteAc[2]=(byte)_model;//吹风模式

                //BYTE[3]:0x00 off 0x01–0x07 : 1-7 fan speed
                if(remoteControl.getFan()!=null){
                    if(remoteControl.getFan()>=1&&remoteControl.getFan()<=7){
                        _remoteAc[3]=remoteControl.getFan().byteValue();//风速1-7 不在此范围则为0 off
                    }
                }

                break;
            case 5://空调关闭
                //todo
                 _cType=4;
                 _sendTemp=(remoteControl.getAcTemperature()-15.5)*2; //真实温度 15.5~32.5
                 _sendTempInt=(int)_sendTemp;
                _remoteAc[0]=_sendTempInt.byteValue();//0x00~0x23
                _ac=0;//空调关
                _remoteAc[1]=(byte)(_ac+_recir*4+_compr*16);
                break;
            case 6://座椅加热
                //todo ok
                //Bit0 – bit1:0x00:Deactivate passengers seat heating 0x01:Active passenger Seat Heating
                //Bit2 – Bit3:0x00:Deactivate drivers seat heating    0x01:Active drivers seat heating
                _cType=5;
                int _masterSTAT=0;
                int _slaveSTAT=0;
                if(remoteControl.getMasterStat()!=null){
                    if(remoteControl.getMasterStat()==1){
                        //主开
                        _masterSTAT=1;
                    }
                }
                if(remoteControl.getSlaveStat()!=null){
                    if(remoteControl.getSlaveStat()==1) {
                        //副开
                        _slaveSTAT = 1;
                    }
                }
                _remoteHeating[0]=(byte)(_masterSTAT*4+_slaveSTAT);
                if(remoteControl.getMasterLevel()!=null){
                    if(remoteControl.getMasterLevel()>=1&& remoteControl.getMasterLevel()<=3){
                        _masterL=remoteControl.getMasterLevel();
                    }
                }
                if(remoteControl.getSlaveLevel()!=null){
                    if(remoteControl.getSlaveLevel()>=1&& remoteControl.getSlaveLevel()<=3){
                        _slaveL=(short)(remoteControl.getSlaveLevel().shortValue()+3);
                    }
                }
                _remoteHeating[1]=(byte)(_slaveL*16+_masterL);//BYTE[1]:  Bit 0–Bit 3:  0x01–0x03 Driver level Bit 4–Bit7: 0x04–0x06   Passenger level
                break;
            case 7://停止座椅加热
                //todo ok
                _cType=5;
                _masterSTAT=0;
                _slaveSTAT=0;
                _remoteHeating[0]=(byte)(_masterSTAT*4+_slaveSTAT);

                _remoteHeating[1]=(byte)(_slaveL*16+_masterL);//BYTE[1]:  Bit 0–Bit 3:  0x01–0x03 Driver level Bit 4–Bit7: 0x04–0x06   Passenger level
                break;
            case 8://远程发动机限制  协议不支持
                //_cType=0;
                break;
            case 9://远程发动机限制关闭 协议不支持
                //_cType=0;
                break;
            case 10://远程寻车 通过闪灯鸣笛次数判断是属于哪种模式 （闪灯鸣笛/仅闪灯/仅鸣笛）
                //todo 明确时间 byte[2]时间 byte[3]模式 开关 ok
                _cType=2;
                _remoteFindCar[0]=remoteControl.getLightNum().byteValue();
                _remoteFindCar[1]=remoteControl.getHornNum().byteValue();
                Integer _actTime=(int)(remoteControl.getActTime()*10);//0.2~0.5-> 0x02~0x05
                if(_actTime<2){
                    _actTime=2;
                }
                if(_actTime>5){
                    _actTime=5;
                }
                _remoteFindCar[2]=_actTime.byteValue();
                //BYTE[3]:
                //Bit0 – bit1: 0x00:horn and lights to be activated 0x01:lights only tobe activated 0x02: horn only tobe activated
                //Bit2 – bit3: 0x00:function activation 0x01:function deactivation
                int _remoteFindA=0;//0 闪灯鸣笛都开 1 仅闪灯 2 仅鸣笛
                int _remoteFindB=1;// 0开 1关
                if(remoteControl.getLightNum()==null){
                    remoteControl.setLightNum((short)0);
                }
                if(remoteControl.getHornNum()==null){
                    remoteControl.setHornNum((short) 0);
                }
                if(remoteControl.getLightNum().intValue()>0 && remoteControl.getHornNum().intValue()==0){
                    //1 仅闪灯
                    _remoteFindA=1;
                }
                if(remoteControl.getLightNum().intValue()==0 && remoteControl.getHornNum().intValue()>0){
                    //2 仅鸣笛
                    _remoteFindA=2;
                }
                if(remoteControl.getDeActive()!=null){
                    if(remoteControl.getDeActive()==(short)1){
                        _remoteFindB=0;
                    }
                }
                _remoteFindCar[3]=(byte)(_remoteFindB*4+_remoteFindA);
                break;
            default:
                _logger.info("unknown cType"+remoteControl.getControlType().intValue());
                break;
        }
        remoteControlCmd.setRemoteControlType(_cType);
        remoteControlCmd.setRemoteStartEngine(_remoteStartEngine);
        remoteControlCmd.setRemoteFindCar(_remoteFindCar);
        remoteControlCmd.setRemoteLock(_remoteLock);
        remoteControlCmd.setRemoteAc(_remoteAc);
        remoteControlCmd.setRemoteHeating(_remoteHeating);

        remoteControlCmd.setApplicationID((short) 49);
        remoteControlCmd.setMessageID((short) 3);
        remoteControlCmd.setEventID(eventId);
        remoteControlCmd.setSendingTime((long) dataTool.getCurrentSeconds());
        remoteControlCmd.setTestFlag((short) 0);

        DataPackage dpw=new DataPackage("8995_49_3");//>>>
        dpw.fillBean(remoteControlCmd);
        ByteBuffer bbw=conversionTBox.generate(dpw);
        String byteStr= PackageEntityManager.getByteString(bbw);

        return byteStr;
    }

    /**
     * TBox注册成功后 检查是否有参数设置未下发 如果有则下发处理
     * @param vin vin
     */
    public void  sendParmSetAfterRegister(String vin){
        //注册通过后下发参数设置
        List<TBoxParmSet> tpss=tBoxParmSetRepository.getLatestOneByVin(vin);
        if(tpss.size()>0){
            TBoxParmSet tps=tpss.get(0);
            String byteString=getParmSetCmdHex(tps);
            _logger.info("ParmSet for TBox:"+vin+" will be send>"+byteString);
            saveCmdToRedis(vin,byteString);
            tps.setStatus((short)1);//参数设置指令向tbox发出 消息状态由0->1
            tBoxParmSetRepository.save(tps);
        }else{
            _logger.info("No ParmSet for TBox:"+vin+" will be send>");
        }

    }
    public String getParmSetCmdHex(TBoxParmSet tps){
        PramSetCmd pramSetCmd=new PramSetCmd();
        pramSetCmd.setApplicationID((short) 82);//>>>
        pramSetCmd.setMessageID((short) 1);//>>>
        pramSetCmd.setEventID(tps.getEventId());
        pramSetCmd.setTestFlag((short) 0);
        pramSetCmd.setSendingTime((long) dataTool.getCurrentSeconds());
        pramSetCmd.setPramSetNumber((short) 14);
        pramSetCmd.setPramSetID1((byte) 1);
        pramSetCmd.setFrequencySaveLocalMedia(tps.getFrequencySaveLocalMedia());
        pramSetCmd.setPramSetID2((byte) 2);
        pramSetCmd.setFrequencyForReport(tps.getFrequencyForReport());
        pramSetCmd.setPramSetID3((byte) 3);
        pramSetCmd.setFrequencyForWarningReport(tps.getFrequencyForWarningReport());
        pramSetCmd.setPramSetID4((byte) 4);
        pramSetCmd.setFrequencyHeartbeat(tps.getFrequencyHeartbeat());
        pramSetCmd.setPramSetID5((byte) 5);
        pramSetCmd.setTimeOutForTerminalSearch(tps.getTimeOutForTerminalSearch());
        pramSetCmd.setPramSetID6((byte) 6);
        pramSetCmd.setTimeOutForServerSearch(tps.getTimeOutForServerSearch());
        pramSetCmd.setPramSetID7((byte) 7);
        pramSetCmd.setLicensePlate(dataTool.getLengthString(tps.getLicensePlate(),8));
        pramSetCmd.setPramSetID8((byte) 8);
        pramSetCmd.setUploadType(tps.getUploadType());
        pramSetCmd.setPramSetID9((byte) 9);
        pramSetCmd.setEnterpriseBroadcastAddress1(dataTool.getIpBytes(tps.getEnterpriseBroadcastAddress1()));
        pramSetCmd.setPramSetID10((byte) 10);
        pramSetCmd.setEnterpriseBroadcastPort1(tps.getEnterpriseBroadcastPort1());
        pramSetCmd.setPramSetID11((byte) 11);
        pramSetCmd.setEnterpriseBroadcastAddress2(dataTool.getIpBytes(tps.getEnterpriseBroadcastAddress2()));
        pramSetCmd.setPramSetID12((byte) 12);
        pramSetCmd.setEnterpriseBroadcastPort2(tps.getEnterpriseBroadcastPort2());
        pramSetCmd.setPramSetID13((byte) 13);
        pramSetCmd.setEnterpriseDomainNameSize(tps.getEnterpriseDomainNameSize());
        pramSetCmd.setPramSetID14((byte) 14);
        pramSetCmd.setEnterpriseDomainName(tps.getEnterpriseDomainName());

        DataPackage dpw=new DataPackage("8995_82_1");//>>>
        dpw.fillBean(pramSetCmd);
        ByteBuffer bbw=conversionTBox.generate(dpw);
        String byteStr= PackageEntityManager.getByteString(bbw);
        ////////////////////////////////
    /*    ByteBuffer bb=PackageEntityManager.getByteBuffer(byteStr);
        DataPackage dp=conversionTBox.generate(bb);
        PramSetCmd bean=dp.loadBean(PramSetCmd.class);
        PackageEntityManager.printEntity(bean);*/


        return byteStr;
    }




    /**
     * 生成远程诊断的下行hex
     * @param diagnosticData diagnosticData实体类
     * @return 下行hex
     */
    public String getDiagCmdHex(DiagnosticData diagnosticData){
        DiagnosticCommandCmd diagnosticCommandCmd=new DiagnosticCommandCmd();
        diagnosticCommandCmd.setTestFlag((short) 0);
        diagnosticCommandCmd.setSendingTime((long) dataTool.getCurrentSeconds());
        diagnosticCommandCmd.setApplicationID((short) 66);//>>>
        diagnosticCommandCmd.setMessageID((short) 1);//>>>
        diagnosticCommandCmd.setEventID(diagnosticData.getEventId());


        DataPackage dpw=new DataPackage("8995_66_1");//>>>
        dpw.fillBean(diagnosticCommandCmd);
        ByteBuffer bbw=conversionTBox.generate(dpw);
        String byteStr=PackageEntityManager.getByteString(bbw);
        return byteStr;
    }



    /**
     * 根据报警hex信息生成文本性质的报警提示 并push到对应user
     * @param vin vin
     * @param msg 16进制报警信息
     *  @param oneFirst 气囊/碰撞/防盗优先
     */
    public void getWarningMessageAndPush(String vin,String msg,int oneFirst){
        Vehicle vehicle=vehicleRepository.findByVin(vin);
        List<UserVehicleRelatived> uvr=userVehicleRelativedRepository.findByVid(vehicle);
        //一个车对应多个uid
        if(uvr.size()>0) {
            Iterator<UserVehicleRelatived> iterator = uvr.iterator();
            while (iterator.hasNext()) {
                //int uid = iterator.next().getUid().getId();
               // User user = iterator.next().getUid();
                UserVehicleRelatived userVehicleRelatived =  iterator.next();
                if(userVehicleRelatived.getVflag()==1){
                    User user = userVehicleRelatived.getUid();
                    Map<String,Object> pushMsg=getWarningMessageForPush(vin, msg, user,oneFirst);
                    pushMessageToUser(vin, pushMsg);
                }

            }
        }else{
            _logger.info("no user to push message");
        }
    }

    /**
     * 根据报警hex信息生成文本性质的短信提示 并短信到对应手机
     * @param vin vin
     * @param msg 16进制报警信息
     */
    public void getWarningMessageAndSms(String vin,String msg,int oneFirst){
        Vehicle vehicle=vehicleRepository.findByVin(vin);
        List<UserVehicleRelatived> uvr=userVehicleRelativedRepository.findOwnerByVid(vehicle);//找到车主
        //一个车对应多个uid
        if(uvr.size()>0) {
        User u=userRepository.findById(uvr.get(0).getUid().getId());
            if(u!=null&&u.getContactsPhone()!=null){
                //取到紧急联系人电话
                //发送短信
                  String phone=u.getPhone();//防盗报警车主电话，碰撞报警紧急联系人电话
                if(oneFirst==SRSFIRST){//
                    phone=u.getContactsPhone();
                }
                _logger.info("try send  waring sms to"+phone+"|oneFirst:"+oneFirst);
                sendWarningMessageSms(vin, msg, phone,oneFirst);
            }

        }
    }

    /**
     * 根据补发报警hex信息生成文本性质的报警提示 并push到对应user
     * @param vin vin
     * @param msg 16进制报警信息
     * @param oneFirst 气囊/碰撞/防盗优先
     */
    public void getResendWarningMessageAndPush(String vin,String msg,int oneFirst){
        Vehicle vehicle=vehicleRepository.findByVin(vin);
        List<UserVehicleRelatived> uvr=userVehicleRelativedRepository.findByVid(vehicle);
        //一个车对应多个uid
        if(uvr.size()>0) {
            Iterator<UserVehicleRelatived> iterator = uvr.iterator();
            while (iterator.hasNext()) {
                UserVehicleRelatived userVehicleRelatived =  iterator.next();
                if(userVehicleRelatived.getVflag()==1){
                    User user = userVehicleRelatived.getUid();
                    Map<String,Object> pushMsg=getResendWarningMessageForPush(vin, msg, user,oneFirst);
                    pushMessageToUser(vin,pushMsg);
                }
            }
        }else{
            _logger.info("no user to push message");
        }

    }

    /**
     * 根据报警hex信息生成文本性质的短信提示 并短信到对应手机
     * @param vin vin
     * @param msg 16进制报警信息
     */
    public void getResendWarningMessageAndSms(String vin,String msg,int oneFirst){
        Vehicle vehicle=vehicleRepository.findByVin(vin);
        List<UserVehicleRelatived> uvr=userVehicleRelativedRepository.findOwnerByVid(vehicle);//找到车主
        //一个车对应多个uid
        if(uvr.size()>0) {
            User u=userRepository.findById(uvr.get(0).getUid().getId());
            if(u!=null&&u.getContactsPhone()!=null){
                //取到紧急联系人电话
                //发送短信
                String phone=u.getPhone();//防盗报警车主电话，碰撞报警紧急联系人电话
                if(oneFirst==SRSFIRST){
                    phone=u.getContactsPhone();
                }
                _logger.info("try send srs waring sms to"+phone+"|oneFirst:"+oneFirst);
                sendResendWarningMessageSms(vin, msg, phone,oneFirst);
            }

        }
    }

    /**
     * 根据故障hex信息生成文本性质的故障提示 并push到对应user
     * @param vin vin
     * @param msg 16进制报警信息
     */
    public void getFailureMessageAndPush(String vin,String msg){
        Map<String,Object> pushMsg=getFailureMessageForPush(vin, msg);
        pushMessageToUser(vin, pushMsg);
    }

    /**
     * 根据补发故障hex信息生成文本性质的故障提示 并push到对应user
     * @param vin vin
     * @param msg 16进制报警信息
     */
    public void getResendFailureMessageAndPush(String vin,String msg){
        Map<String,Object> pushMsg=getFailureMessageForPush(vin, msg);
        pushMessageToUser(vin, pushMsg);
    }


    /**
     * 处理远程控制结果推送
     * @param vin target vin
     * @param result 1成功 0是失败
     * @param message remark
     */
    public void pushRemoteControlResult(String vin,int result,String message){
        Map<String,Object> dataMap = new HashMap<String,Object>();
        dataMap.put("pType", 1);
        dataMap.put("rs", result);
        dataMap.put("textContent",message);
        pushMessageToUser(vin,dataMap);
    }

    /**
     * 根报警提示push到对应user
     * @param vin vin
     * @param pushMsg 文本报警信息
     */
    public void pushMessageToUser(String vin,Map<String,Object> pushMsg){
        _logger.info("push message:"+pushMsg);
        if(pushMsg==null){
            return;
        }
        Vehicle vehicle=vehicleRepository.findByVin(vin);
        List<UserVehicleRelatived> uvr=userVehicleRelativedRepository.findByVid(vehicle);
        //一个车对应多个uid 报警消息都push过去
        if(uvr.size()>0){
            Iterator<UserVehicleRelatived> iterator=uvr.iterator();
            while (iterator.hasNext()){
                UserVehicleRelatived userVehicleRelatived = iterator.next();
                if(userVehicleRelatived.getVflag()==1){
                int uid = userVehicleRelatived.getUid().getId();
                //int uid=iterator.next().getUid().getId();
                _logger.info("push to:"+uid+":"+pushMsg);
                try {
                    //this.mqService.pushToUser(uid, pushMsg);
/*                    * @param sourceId    发送用户id<br>
                    * @param resourceFrom  数据来源,1 手机,2 车机<br>
                    * @param targetType 目标对象类型 ,1 user ,2 organize <br>
                    * @param targetId 目标对象id<br>
                    * @param resourceTo 目标类型 ,1 手机,2 车机,3 短信,4 手机,车机 ,短信<br>1
                    * @param funType 消息类型 ,1 ,即时通讯 ,2 ,推送消息<br>2
                    * @param pType 推送消息类型,1 远程控制,2 故障提醒,3 保养提醒,4 位置分享,5 智能寻车,6 位置共享,7 发送位置,8 气囊报警,9 防盗报警<br>289
                    * @param textContent 发送内容文本、经纬度<br>
                    * @param contentType 发送内容类型 ,1 文本,2 音乐,3 图片,41 位置信息,42 位置共享开始,43 位置共享结束<br>
                    * @param messageNums 信息条数 count
                    * @param cleanFlag 消息推送消除标志，仅针对 气囊报警、防盗报警,0报警,1消除
                    * @param file 上传的附件*/
                    pushMsg.put("targetType",1);
                    pushMsg.put("targetId",uid);
                    pushMsg.put("resourceTo",1);
                    pushMsg.put("funType", 2);

                    httpRequestTool.doHttp(urlLink,pushMsg);

                }catch (RuntimeException e){_logger.info(e.getMessage());} catch (Exception e) {
                    _logger.info(e.getMessage());
                }
                }
            }
        }else{
            _logger.info("can not push  message,because no user found for vin:"+vin);
        }
    }

    /**
     * 根据报警hex信息生成文本性质的报警提示
     * @param vin vin
     * @param msg 16进制报警信息
     * @param user user
     * @param onefirst 气囊/碰撞/ATA 优先
     * @return 根据报警hex信息生成文本性质的报警提示
     */
    public Map<String,Object> getWarningMessageForPush(String vin,String msg,User user,int onefirst){
        //报警数据保存
        _logger.info(">>get WarningMessage For Push:"+msg);
        WarningMessage bean=dataTool.decodeWarningMessage(msg);
        WarningMessageData wd=new WarningMessageData();
        wd.setVin(vin);
        wd.setImei(bean.getImei());
        wd.setApplicationId(bean.getApplicationID());
        wd.setMessageId(bean.getMessageID());
        wd.setSendingTime(dataTool.seconds2Date(bean.getSendingTime()));
        //分解IsIsLocation信息
        char[] location=dataTool.getBitsFromShort(bean.getIsLocation());
        wd.setIsLocation(location[7] == '0' ? (short) 0 : (short) 1);//bit0 0有效定位 1无效定位
        wd.setNorthSouth(location[6] == '0' ? "N" : "S");//bit1 0北纬 1南纬
        wd.setEastWest(location[5] == '0' ? "E" : "W");//bit2 0东经 1西经
        wd.setLatitude(dataTool.getTrueLatAndLon(bean.getLatitude()));
        wd.setLongitude(dataTool.getTrueLatAndLon(bean.getLongitude()));
        wd.setSpeed(dataTool.getTrueSpeed(bean.getSpeed()));
        wd.setHeading(bean.getHeading());

        wd.setSrsWarning(dataTool.getWarningInfoFromByte(bean.getSrsWarning()));
        wd.setCrashWarning(dataTool.getWarningInfoFromByte(bean.getCrashWarning()));
        wd.setAtaWarning(dataTool.getWarningInfoFromByte(bean.getAtaWarning()));

        wd.setSafetyBeltCount(bean.getSafetyBeltCount());
        wd.setVehicleHitSpeed(dataTool.getHitSpeed(bean.getVehicleSpeedLast()));

        List<RealTimeReportData> rdList = realTimeReportDataRespository.findLatestOneByVin(vin);
        RealTimeReportData rd=null;
        if(rdList!=null&&rdList.size()>0) {
            rd = rdList.get(0);
        }

        //生成报警信息
        Map<String,Object> warningMessage=buildWarningString(wd,user,rd,onefirst);
        return warningMessage;
    }

    /**
     * 根据报警hex信息生成文本性质的报警短信
     * @param vin vin
     * @param msg 16进制报警信息
     * @param phone 接收短信号码
     * @return 根据报警hex信息生成文本性质的报警短信
     */
    public void sendWarningMessageSms(String vin,String msg,String phone,int oneFirst){
        //报警数据保存
        _logger.info(">>get WarningMessage For SMS:"+msg);
        WarningMessage bean=dataTool.decodeWarningMessage(msg);
        WarningMessageData wd=new WarningMessageData();
        wd.setVin(vin);
        wd.setImei(bean.getImei());
        wd.setApplicationId(bean.getApplicationID());
        wd.setMessageId(bean.getMessageID());
        wd.setSendingTime(dataTool.seconds2Date(bean.getSendingTime()));
        //分解IsIsLocation信息
        char[] location=dataTool.getBitsFromShort(bean.getIsLocation());
        wd.setIsLocation(location[7] == '0' ? (short) 0 : (short) 1);//bit0 0有效定位 1无效定位
        wd.setNorthSouth(location[6] == '0' ? "N" : "S");//bit1 0北纬 1南纬
        wd.setEastWest(location[5] == '0' ? "E" : "W");//bit2 0东经 1西经
        wd.setLatitude(dataTool.getTrueLatAndLon(bean.getLatitude()));
        wd.setLongitude(dataTool.getTrueLatAndLon(bean.getLongitude()));
        wd.setSpeed(dataTool.getTrueSpeed(bean.getSpeed()));
        wd.setHeading(bean.getHeading());

        wd.setSrsWarning(dataTool.getWarningInfoFromByte(bean.getSrsWarning()));
        wd.setCrashWarning(dataTool.getWarningInfoFromByte(bean.getCrashWarning()));
        wd.setAtaWarning(dataTool.getWarningInfoFromByte(bean.getAtaWarning()));

        wd.setSafetyBeltCount(bean.getSafetyBeltCount());
        wd.setVehicleHitSpeed(dataTool.getHitSpeed(bean.getVehicleSpeedLast()));

        Short lastSrsWarning=0;
        Short lastCrashWarning=0;
        Short lastAtaWarning=0;
        WarningMessageData wmd = this.getWarningMessageData(vin);
        if(wmd!=null){
            lastSrsWarning=wmd.getSrsWarning();
            lastCrashWarning=wmd.getCrashWarning();
            lastAtaWarning=wmd.getAtaWarning();
        }
        _logger.info("lastSrsWarning" + lastSrsWarning + "---lastCrashWarning" + lastCrashWarning+ "---lastAtaWarning" + lastAtaWarning);
        _logger.info("nowSrsWarning"+wd.getSrsWarning() + "---nowCrashWarning"+wd.getCrashWarning() + "---nowAtaWarning"+wd.getAtaWarning());

        if(oneFirst==SRSFIRST){
            if(lastSrsWarning==(short)0 && wd.getSrsWarning()==(short)1){
                GpsData gpsData=new GpsData();
                gpsData.setLatitude(wd.getLatitude());
                gpsData.setLongitude(wd.getLongitude());
                //GpsData baiduGpsData= gpsTool.getDataFromBaidu(gpsData);
                StringBuilder sb = new StringBuilder();
                StringBuilder longU = new StringBuilder();
                String srs = "";
                try{
                    srs="【华晨汽车Bri-Air】尊敬的用户，您的爱车于"+ DateUtil.formatDateTime(new Date())+"发生车辆碰撞，建议您尽快确认车辆状态。";
                    srs = java.net.URLEncoder.encode(srs, "UTF-8");
                }catch(Exception e){
                    _logger.info(e.getMessage());
                }
                sb.append(srs);
            /*    longU.append("http://");
                longU.append(webserverHost);
                longU.append("/baiduMap.html?lon=");
                longU.append(baiduGpsData.getLongitude());
                longU.append("%26");
                //sb.append("&");
                longU.append("lat=");
                longU.append(baiduGpsData.getLatitude());
                String shortUrl =   smsHttpTool.getShortUrl(longU.toString());
                sb.append(shortUrl);*/
                String smsStr = sb.toString();
                _logger.info("send sms:" + phone + ":" + smsStr);
                //调用工具类发起 http请求
                smsHttpTool.doHttp(phone, smsStr);
            }
        }else   if(oneFirst==CRASHFIRST){
            if(lastCrashWarning==(short)0 && wd.getCrashWarning()==(short)1){
                GpsData gpsData=new GpsData();
                gpsData.setLatitude(wd.getLatitude());
                gpsData.setLongitude(wd.getLongitude());
                //GpsData baiduGpsData= gpsTool.getDataFromBaidu(gpsData);
                StringBuilder sb = new StringBuilder();
                StringBuilder longU = new StringBuilder();
                String srs = "";
                try{
                    srs="【华晨汽车Bri-Air】尊敬的用户，您的爱车于"+ DateUtil.formatDateTime(new Date())+"发生车辆碰撞，建议您尽快确认车辆状态。";
                    srs = java.net.URLEncoder.encode(srs, "UTF-8");
                }catch(Exception e){
                    _logger.info(e.getMessage());
                }
                sb.append(srs);
            /*    longU.append("http://");
                longU.append(webserverHost);
                longU.append("/baiduMap.html?lon=");
                longU.append(baiduGpsData.getLongitude());
                longU.append("%26");
                //sb.append("&");
                longU.append("lat=");
                longU.append(baiduGpsData.getLatitude());
                String shortUrl =   smsHttpTool.getShortUrl(longU.toString());
                sb.append(shortUrl);*/
                String smsStr = sb.toString();
                _logger.info("send sms:" + phone + ":" + smsStr);
                //调用工具类发起 http请求
                smsHttpTool.doHttp(phone, smsStr);
            }
        }else if(oneFirst==ATAFIRST){
            if(lastAtaWarning==(short)0 &&wd.getAtaWarning()==(short)1){
                /*GpsData gpsData=new GpsData();
                gpsData.setLatitude(wd.getLatitude());
                gpsData.setLongitude(wd.getLongitude());
                GpsData baiduGpsData= gpsTool.getDataFromBaidu(gpsData);*/
                StringBuilder sb = new StringBuilder();
                StringBuilder longU = new StringBuilder();
                String srs = "";
                try{
                    srs="【华晨汽车Bri-Air】尊敬的用户，您的爱车于"+ DateUtil.formatDateTime(new Date())+"车门被异常开启，建议您尽快确认车辆状态。";
                    srs = java.net.URLEncoder.encode(srs, "UTF-8");
                }catch(Exception e){
                    _logger.info(e.getMessage());
                }
                sb.append(srs);
             /*   longU.append("http://");
                longU.append(webserverHost);
                longU.append("/baiduMap.html?lon=");
                longU.append(baiduGpsData.getLongitude());
                longU.append("%26");
                //sb.append("&");
                longU.append("lat=");
                longU.append(baiduGpsData.getLatitude());
                String shortUrl =   smsHttpTool.getShortUrl(longU.toString());
                sb.append(shortUrl);*/
                String smsStr = sb.toString();
                _logger.info("send sms:" + phone + ":" + smsStr);
                //调用工具类发起 http请求
                smsHttpTool.doHttp(phone, smsStr);
            }

        }

    }

    /**
     * 根据报警hex信息生成文本性质的报警短信
     * @param vin vin
     * @param msg 16进制报警信息
     * @param phone 接收短信号码
     * @return 根据报警hex信息生成文本性质的报警短信
     */
    public void sendResendWarningMessageSms(String vin,String msg,String phone,int oneFirst){
        //报警数据保存
        _logger.info(">>get WarningMessage For SMS:"+msg);
        DataResendWarningMes bean=dataTool.decodeResendWarningMessage(msg);
        WarningMessageData wd=new WarningMessageData();

/*        WarningMessage bean=dp.loadBean(WarningMessage.class);
          WarningMessageData wd=new WarningMessageData();*/
        wd.setVin(vin);
        wd.setImei(bean.getImei());
        wd.setApplicationId(bean.getApplicationID());
        wd.setMessageId(bean.getMessageID());
        wd.setSendingTime(dataTool.seconds2Date(bean.getSendingTime()));
        //分解IsIsLocation信息
        char[] location=dataTool.getBitsFromShort(bean.getIsLocation());
        wd.setIsLocation(location[7] == '0' ? (short) 0 : (short) 1);//bit0 0有效定位 1无效定位
        wd.setNorthSouth(location[6] == '0' ? "N" : "S");//bit1 0北纬 1南纬
        wd.setEastWest(location[5] == '0' ? "E" : "W");//bit2 0东经 1西经
        wd.setLatitude(dataTool.getTrueLatAndLon(bean.getLatitude()));
        wd.setLongitude(dataTool.getTrueLatAndLon(bean.getLongitude()));
        wd.setSpeed(dataTool.getTrueSpeed(bean.getSpeed()));
        wd.setHeading(bean.getHeading());

        wd.setSrsWarning(dataTool.getWarningInfoFromByte(bean.getSrsWarning()));
        wd.setCrashWarning(dataTool.getWarningInfoFromByte(bean.getCrashWarning()));
        wd.setAtaWarning(dataTool.getWarningInfoFromByte(bean.getAtaWarning()));

        wd.setSafetyBeltCount(bean.getSafetyBeltCount());
        wd.setVehicleHitSpeed(dataTool.getHitSpeed(bean.getVehicleSpeedLast()));


        Short lastSrsWarning=0;
        Short lastAtaWarning=0;
        Short lastCrashWarning=0;
        WarningMessageData wmd = this.getWarningMessageData(vin);
        if(wmd!=null){
            lastSrsWarning=wmd.getSrsWarning();
            lastCrashWarning=wmd.getCrashWarning();
            lastAtaWarning=wmd.getAtaWarning();
        }
        _logger.info("lastSrsWarning" + lastSrsWarning + "---lastCrashWarning" + lastCrashWarning+ "---lastAtaWarning" + lastAtaWarning);
        _logger.info("nowSrsWarning"+wd.getSrsWarning() + "---nowCrashWarning"+wd.getCrashWarning() + "---nowAtaWarning"+wd.getAtaWarning());

        if(oneFirst==SRSFIRST){
            if(lastSrsWarning==(short)0 && wd.getSrsWarning()==(short)1){
              /*  GpsData gpsData=new GpsData();
                gpsData.setLatitude(wd.getLatitude());
                gpsData.setLongitude(wd.getLongitude());
                GpsData baiduGpsData= gpsTool.getDataFromBaidu(gpsData);*/
                StringBuilder sb = new StringBuilder();
                StringBuilder longU = new StringBuilder();
                String srs = "";
                try{
                    srs="【华晨汽车Bri-Air】尊敬的用户，您的爱车于"+ DateUtil.formatDateTime(new Date())+"发生车辆碰撞，建议您尽快确认车辆状态。";
                    srs = java.net.URLEncoder.encode(srs, "UTF-8");
                }catch(Exception e){
                    _logger.info(e.getMessage());
                }
                sb.append(srs);
              /*  longU.append("http://");
                longU.append(webserverHost);
                longU.append("/baiduMap.html?lon=");
                longU.append(baiduGpsData.getLongitude());
                longU.append("%26");
                //sb.append("&");
                longU.append("lat=");
                longU.append(baiduGpsData.getLatitude());
                String shortUrl =   smsHttpTool.getShortUrl(longU.toString());
                sb.append(shortUrl);*/
                String smsStr = sb.toString();
                _logger.info("send sms:" + phone + ":" + smsStr);
                //调用工具类发起 http请求
                smsHttpTool.doHttp(phone, smsStr);
            }
        }else  if(oneFirst==CRASHFIRST){
            if(lastCrashWarning==(short)0 && wd.getCrashWarning()==(short)1){
              /*  GpsData gpsData=new GpsData();
                gpsData.setLatitude(wd.getLatitude());
                gpsData.setLongitude(wd.getLongitude());
                GpsData baiduGpsData= gpsTool.getDataFromBaidu(gpsData);*/
                StringBuilder sb = new StringBuilder();
                StringBuilder longU = new StringBuilder();
                String srs = "";
                try{
                    srs="【华晨汽车Bri-Air】尊敬的用户，您的爱车于"+ DateUtil.formatDateTime(new Date())+"发生车辆碰撞，建议您尽快确认车辆状态。";
                    srs = java.net.URLEncoder.encode(srs, "UTF-8");
                }catch(Exception e){
                    _logger.info(e.getMessage());
                }
                sb.append(srs);
              /*  longU.append("http://");
                longU.append(webserverHost);
                longU.append("/baiduMap.html?lon=");
                longU.append(baiduGpsData.getLongitude());
                longU.append("%26");
                //sb.append("&");
                longU.append("lat=");
                longU.append(baiduGpsData.getLatitude());
                String shortUrl =   smsHttpTool.getShortUrl(longU.toString());
                sb.append(shortUrl);*/
                String smsStr = sb.toString();
                _logger.info("send sms:" + phone + ":" + smsStr);
                //调用工具类发起 http请求
                smsHttpTool.doHttp(phone, smsStr);
            }
        }else if(oneFirst==ATAFIRST){
            if(lastAtaWarning==(short)0 && wd.getAtaWarning()==(short)1){
            /*    GpsData gpsData=new GpsData();
                gpsData.setLatitude(wd.getLatitude());
                gpsData.setLongitude(wd.getLongitude());
                GpsData baiduGpsData= gpsTool.getDataFromBaidu(gpsData);*/
                StringBuilder sb = new StringBuilder();
                StringBuilder longU = new StringBuilder();
                String srs = "";
                try{
                    srs="【华晨汽车Bri-Air】尊敬的用户，您的爱车于"+ DateUtil.formatDateTime(new Date())+"车门被异常开启，建议您尽快确认车辆状态。";
                    srs = java.net.URLEncoder.encode(srs, "UTF-8");
                }catch(Exception e){
                    _logger.info(e.getMessage());
                }
                sb.append(srs);
               /* longU.append("http://");
                longU.append(webserverHost);
                longU.append("/baiduMap.html?lon=");
                longU.append(baiduGpsData.getLongitude());
                longU.append("%26");
                //sb.append("&");
                longU.append("lat=");
                longU.append(baiduGpsData.getLatitude());
                String shortUrl =   smsHttpTool.getShortUrl(longU.toString());
                sb.append(shortUrl);*/
                String smsStr = sb.toString();
                _logger.info("send sms:" + phone + ":" + smsStr);
                //调用工具类发起 http请求
                smsHttpTool.doHttp(phone, smsStr);
            }

        }

    }

    /**
     * 根据补发报警hex信息生成文本性质的报警提示
     * @param vin vin
     * @param msg 16进制报警信息
     * @param user user
     * @param oneFirst 气囊/碰撞/防盗优先
     * @return 根据报警hex信息生成文本性质的报警提示
     */
    public Map<String,Object> getResendWarningMessageForPush(String vin,String msg,User user,int oneFirst){
        //报警数据保存
        _logger.info(">>get Resend WarningMessage For Push:"+msg);
        DataResendWarningMes bean=dataTool.decodeResendWarningMessage(msg);
        WarningMessageData wd=new WarningMessageData();
        wd.setVin(vin);
        wd.setImei(bean.getImei());
        wd.setApplicationId(bean.getApplicationID());
        wd.setMessageId(bean.getMessageID());
        wd.setSendingTime(dataTool.seconds2Date(bean.getSendingTime()));
        //分解IsIsLocation信息
        char[] location=dataTool.getBitsFromShort(bean.getIsLocation());
        wd.setIsLocation(location[7] == '0' ? (short) 0 : (short) 1);//bit0 0有效定位 1无效定位
        wd.setNorthSouth(location[6] == '0' ? "N" : "S");//bit1 0北纬 1南纬
        wd.setEastWest(location[5] == '0' ? "E" : "W");//bit2 0东经 1西经
        wd.setLatitude(dataTool.getTrueLatAndLon(bean.getLatitude()));
        wd.setLongitude(dataTool.getTrueLatAndLon(bean.getLongitude()));
        wd.setSpeed(dataTool.getTrueSpeed(bean.getSpeed()));
        wd.setHeading(bean.getHeading());

        wd.setSrsWarning(dataTool.getWarningInfoFromByte(bean.getSrsWarning()));
        wd.setCrashWarning(dataTool.getWarningInfoFromByte(bean.getCrashWarning()));
        wd.setAtaWarning(dataTool.getWarningInfoFromByte(bean.getAtaWarning()));

        wd.setSafetyBeltCount(bean.getSafetyBeltCount());
        wd.setVehicleHitSpeed(dataTool.getHitSpeed(bean.getVehicleSpeedLast()));

        List<RealTimeReportData> rdList = realTimeReportDataRespository.findLatestOneByVin(vin);
        RealTimeReportData rd=null;
        if(rdList!=null&&rdList.size()>0) {
            rd = rdList.get(0);
        }
        //生成报警信息
        Map<String,Object> warningMessage=buildWarningString(wd, user, rd, oneFirst);
        return warningMessage;
    }

    /**
     * 根据故障hex信息生成文本性质的报警提示
     * @param vin vin
     * @param msg 16进制报警信息
     * @return 根据故障hex信息生成文本性质的报警提示
     */
    public Map<String,Object> getFailureMessageForPush(String vin,String msg){
        //故障数据
        _logger.info(">>get FailureMessage For Push:"+msg);
        ByteBuffer bb= PackageEntityManager.getByteBuffer(msg);
        DataPackage dp=conversionTBox.generate(bb);
        FailureMessage bean=dp.loadBean(FailureMessage.class);
        FailureMessageData wd=new FailureMessageData();
        wd.setVin(vin);
        wd.setImei(bean.getImei());
        wd.setApplicationId(bean.getApplicationID());
        wd.setMessageId(bean.getMessageID());
        wd.setSendingTime(dataTool.seconds2Date(bean.getSendingTime()));
        //分解IsIsLocation信息
        char[] location=dataTool.getBitsFromShort(bean.getIsLocation());
        wd.setIsLocation(location[7] == '0' ? (short) 0 : (short) 1);//bit0 0有效定位 1无效定位
        wd.setNorthSouth(location[6] == '0' ? "N" : "S");//bit1 0北纬 1南纬
        wd.setEastWest(location[5] == '0' ? "E" : "W");//bit2 0东经 1西经
        wd.setLatitude(dataTool.getTrueLatAndLon(bean.getLatitude()));
        wd.setLongitude(dataTool.getTrueLatAndLon(bean.getLongitude()));
        wd.setSpeed(dataTool.getTrueSpeed(bean.getSpeed()));
        wd.setHeading(bean.getHeading());

        wd.setInfo(dataTool.getFailureMesId(bean));


        List<RealTimeReportData> rdList = realTimeReportDataRespository.findLatestOneByVin(vin);
        RealTimeReportData rd=null;
        if(rdList!=null&&rdList.size()>0) {
            rd = rdList.get(0);
        }
        //RealTimeDataShow realTimeDataShow = vehicleDataService.getRealTimeData(vin);
        //生成故障信息
        Map<String,Object> failureString=buildFailureString(wd, rd);
        return failureString;
    }

    /**
     * 根据补发故障hex信息生成文本性质的报警提示
     * @param vin vin
     * @param msg 16进制故障信息
     * @return 根据故障hex信息生成文本性质的故障提示
     */
    public Map<String,Object> getResendFailureMessageForPush(String vin,String msg){
        //报警数据保存
        _logger.info(">>get Resend FailureMessage For Push:"+msg);
        ByteBuffer bb= PackageEntityManager.getByteBuffer(msg);
        DataPackage dp=conversionTBox.generate(bb);
        DataResendFailureData bean=dp.loadBean(DataResendFailureData.class);
        FailureMessageData wd=new FailureMessageData();
        wd.setVin(vin);
        wd.setImei(bean.getImei());
        wd.setApplicationId(bean.getApplicationID());
        wd.setMessageId(bean.getMessageID());
        wd.setSendingTime(dataTool.seconds2Date(bean.getSendingTime()));
        //分解IsIsLocation信息
        char[] location=dataTool.getBitsFromShort(bean.getIsLocation());
        wd.setIsLocation(location[7] == '0' ? (short) 0 : (short) 1);//bit0 0有效定位 1无效定位
        wd.setNorthSouth(location[6] == '0' ? "N" : "S");//bit1 0北纬 1南纬
        wd.setEastWest(location[5] == '0' ? "E" : "W");//bit2 0东经 1西经
        wd.setLatitude(dataTool.getTrueLatAndLon(bean.getLatitude()));
        wd.setLongitude(dataTool.getTrueLatAndLon(bean.getLongitude()));
        wd.setSpeed(dataTool.getTrueSpeed(bean.getSpeed()));
        wd.setHeading(bean.getHeading());

        wd.setInfo(dataTool.getDataResendFailureMesId(bean));


        List<RealTimeReportData> rdList = realTimeReportDataRespository.findLatestOneByVin(vin);
        RealTimeReportData rd=null;
        if(rdList!=null&&rdList.size()>0) {
            rd = rdList.get(0);
        }
        //生成故障信息
        Map<String,Object> failureString=buildFailureString(wd, rd);
        return failureString;
    }

    /**
     * 根据报警消息类生成报警消息
     * @param wd 报警消息实体类
     * @param user user
     * @param realTimeReportData 实时数据实体类
     * @param oneFirst 气囊优先
     * @return 便于阅读的报警消息
     */
    public Map<String,Object> buildWarningString(WarningMessageData wd,User user,RealTimeReportData realTimeReportData,int  oneFirst){
        Map<String,Object> dataMap = new HashMap<String,Object>();
       // StringBuilder sb=new StringBuilder() ;
        Map<String,Object> jsonMap = new HashMap<String,Object>();
        Map<String,String> positionMap = new HashMap<String,String>();

        //获取上一条报警消息
        String vin = wd.getVin();
        Short lastSrsWarning=0;
        Short lastCrashWarning=0;
        Short lastAtaWarning=0;
        WarningMessageData wmd = this.getWarningMessageData(vin);
        if(wmd!=null){
            lastSrsWarning=wmd.getSrsWarning();
            lastCrashWarning=wmd.getCrashWarning();
            lastAtaWarning=wmd.getAtaWarning();
        }
        _logger.info("lastSrsWarning" + lastSrsWarning + "---lastCrashWarning" + lastCrashWarning+ "---lastAtaWarning" + lastAtaWarning);
        _logger.info("nowSrsWarning"+wd.getSrsWarning() + "---nowCrashWarning"+wd.getCrashWarning() + "---nowAtaWarning"+wd.getAtaWarning());
        //sb.append("车辆报警信息: ");
        if(wd.getIsLocation()==(short)0){
            //0有效 1无效
/*          sb.append("当前位置:");
            sb.append("经度:").append(wd.getLongitude()).append(wd.getEastWest()).append(",");
            sb.append("纬度").append(wd.getLatitude()).append(wd.getNorthSouth()).append(";");
            sb.append("速度:").append(wd.getSpeed()).append("km/h;");
            sb.append("方向:").append(wd.getHeading()).append(";");*/
            positionMap.put("longitude", new StringBuilder().append(wd.getLongitude()).toString());
            positionMap.put("latitude",new StringBuilder().append(wd.getLatitude()).toString());
            positionMap.put("speed",new StringBuilder().append(wd.getSpeed()).toString());
            positionMap.put("heading",new StringBuilder().append(wd.getHeading()).toString());

        }else{
            positionMap.put("longitude", "");
            positionMap.put("latitude","");
            positionMap.put("speed","");
            positionMap.put("heading","");
        }
        if(oneFirst==SRSFIRST){
            _logger.info("handle srs warning only");
            //仅处理气囊
            if(wd.getSrsWarning()==(short)1){
                //安全气囊报警 0未触发 1触发
/*            sb.append("安全气囊报警触发,");
            sb.append("背扣安全带数量:").append(wd.getSafetyBeltCount()).append(",");
            sb.append("碰撞速度:").append(wd.getVehicleHitSpeed()).append("km/h;");*/
                jsonMap.put("srs_warning","1");
                jsonMap.put("safety_belt_count",wd.getSafetyBeltCount());
                jsonMap.put("vehicle_hit_speed",new StringBuilder().append(wd.getVehicleHitSpeed()).toString());
                dataMap.put("pType", 8);
                dataMap.put("cleanFlag", "0");

                String contactsPhone ="";
                if(user!=null){
                    contactsPhone =  user.getContactsPhone();
                }
                jsonMap.put("contacts_phone",contactsPhone);
            }
            if(wd.getSrsWarning()==(short)0 && lastSrsWarning==1){//srs1--0 碰撞解除
                //推srs解除
                jsonMap.put("srs_warning","0");
                jsonMap.put("safety_belt_count",wd.getSafetyBeltCount());
                jsonMap.put("vehicle_hit_speed",new StringBuilder().append(wd.getVehicleHitSpeed()).toString());
                dataMap.put("pType", 8);
                dataMap.put("cleanFlag", "1");

                String contactsPhone ="";
                if(user!=null){
                    contactsPhone =  user.getContactsPhone();
                }
                jsonMap.put("contacts_phone",contactsPhone);
            }
            if(wd.getSrsWarning()==(short)0 && lastSrsWarning==0) {//碰撞一直没有
                return null;
            }
        }else   if(oneFirst==CRASHFIRST){
            _logger.info("handle crash warning only");
            //仅处理气囊
            if(wd.getCrashWarning()==(short)1){
                //安全气囊报警 0未触发 1触发
/*            sb.append("安全气囊报警触发,");
            sb.append("背扣安全带数量:").append(wd.getSafetyBeltCount()).append(",");
            sb.append("碰撞速度:").append(wd.getVehicleHitSpeed()).append("km/h;");*/
                jsonMap.put("srs_warning","1");
                jsonMap.put("safety_belt_count",wd.getSafetyBeltCount());
                jsonMap.put("vehicle_hit_speed",new StringBuilder().append(wd.getVehicleHitSpeed()).toString());
                dataMap.put("pType", 8);
                dataMap.put("cleanFlag", "0");

                String contactsPhone ="";
                if(user!=null){
                    contactsPhone =  user.getContactsPhone();
                }
                jsonMap.put("contacts_phone",contactsPhone);
            }
            if(wd.getCrashWarning()==(short)0 && lastCrashWarning==1){//srs1--0 碰撞解除
                //推srs解除
                jsonMap.put("srs_warning","0");
                jsonMap.put("safety_belt_count",wd.getSafetyBeltCount());
                jsonMap.put("vehicle_hit_speed",new StringBuilder().append(wd.getVehicleHitSpeed()).toString());
                dataMap.put("pType", 8);
                dataMap.put("cleanFlag", "1");

                String contactsPhone ="";
                if(user!=null){
                    contactsPhone =  user.getContactsPhone();
                }
                jsonMap.put("contacts_phone",contactsPhone);
            }
            if(wd.getCrashWarning()==(short)0 && lastCrashWarning==0) {//碰撞一直没有
                return null;
            }
        }else if(oneFirst==ATAFIRST){
            _logger.info("handle ata warning only");
            //仅处理防盗
            if(wd.getAtaWarning()==(short)1){
                //防盗报警 0未触发 1触发
                // sb.append("车辆防盗报警触发");
                jsonMap.put("ata_warning","1");
                dataMap.put("pType", 9);
                dataMap.put("cleanFlag", "0");
                String leftFrontDoorInformation = "1";
                String leftRearDoorInformation = "1";
                String rightFrontDoorInformation = "1";
                String rightRearDoorInformation = "1";
                String engineCoverState = "1";
                String trunkLidState = "1";
                if(realTimeReportData!=null){
                    if(realTimeReportData.getLeftFrontDoorInformation().equals("1")){
                        leftFrontDoorInformation="0";
                    }
                    if(realTimeReportData.getLeftRearDoorInformation().equals("1")){
                        leftRearDoorInformation="0";
                    }
                    if(realTimeReportData.getRightFrontDoorInformation().equals("1")){
                        rightFrontDoorInformation="0";
                    }
                    if(realTimeReportData.getRightRearDoorInformation().equals("1")){
                        rightRearDoorInformation="0";
                    }
                    if(realTimeReportData.getEngineCoverState().equals("1")){
                        engineCoverState="0";
                    }
                    if(realTimeReportData.getTrunkLidState().equals("1")){
                        trunkLidState="0";
                    }
                }
                jsonMap.put("leftFrontDoorInformation",leftFrontDoorInformation);
                jsonMap.put("leftRearDoorInformation",leftRearDoorInformation);
                jsonMap.put("rightFrontDoorInformation",rightFrontDoorInformation);
                jsonMap.put("rightRearDoorInformation",rightRearDoorInformation);
                jsonMap.put("engineCoverState",engineCoverState);
                jsonMap.put("trunkLidState",trunkLidState);
            }
            if(wd.getAtaWarning()==(short)0 && lastAtaWarning==1 ){//atr1---0 防盗解除
                //推ata解除
                jsonMap.put("ata_warning","0");
                dataMap.put("pType", 9);
                dataMap.put("cleanFlag", "1");
                String leftFrontDoorInformation = "1";
                String leftRearDoorInformation = "1";
                String rightFrontDoorInformation = "1";
                String rightRearDoorInformation = "1";
                String engineCoverState = "1";
                String trunkLidState = "1";
                if(realTimeReportData!=null){
                    if(realTimeReportData.getLeftFrontDoorInformation().equals("1")){
                        leftFrontDoorInformation="0";
                    }
                    if(realTimeReportData.getLeftRearDoorInformation().equals("1")){
                        leftRearDoorInformation="0";
                    }
                    if(realTimeReportData.getRightFrontDoorInformation().equals("1")){
                        rightFrontDoorInformation="0";
                    }
                    if(realTimeReportData.getRightRearDoorInformation().equals("1")){
                        rightRearDoorInformation="0";
                    }
                    if(realTimeReportData.getEngineCoverState().equals("1")){
                        engineCoverState="0";
                    }
                    if(realTimeReportData.getTrunkLidState().equals("1")){
                        trunkLidState="0";
                    }
                }
                jsonMap.put("leftFrontDoorInformation",leftFrontDoorInformation);
                jsonMap.put("leftRearDoorInformation",leftRearDoorInformation);
                jsonMap.put("rightFrontDoorInformation",rightFrontDoorInformation);
                jsonMap.put("rightRearDoorInformation",rightRearDoorInformation);
                jsonMap.put("engineCoverState",engineCoverState);
                jsonMap.put("trunkLidState",trunkLidState);
            }
            if(wd.getAtaWarning()==(short)0 && lastAtaWarning==0 ) {//atr1---0 防盗一直没有
            return null;
            }
        }

        if(jsonMap.size()==0){
        return null;
        }
        jsonMap.put("position", positionMap);
        String contextJson= JSON.toJSONString(jsonMap);

        dataMap.put("textContent",contextJson);
        return dataMap;
    }

    public WarningMessageData getWarningMessageData(String vin){
        List<WarningMessageData> list = warningMessageDataRespository.findTop1ByVinOrderByReceiveTimeDesc(vin);
        WarningMessageData wmd =null;
        if(list!=null && list.size()>0){
            wmd =  list.get(0);
        }
        return wmd;
    }

    /**
     * 根据故障消息类生成故障告警消息
     * @param wd 故障消息实体类
     * @param realTimeReportData 实时数据实体类
     * @return 便于阅读的消息
     */
    public Map<String,Object> buildFailureString(FailureMessageData wd,RealTimeReportData realTimeReportData){
        Map<String,Object> dataMap = new HashMap<String,Object>();

        Map<String,Object> jsonMap = new HashMap<String,Object>();
       // Map<String,String> positionMap = new HashMap<String,String>();
        HashSet<String> failInfo = new HashSet<String>();

        int drivingRange = 0;
        int drivingTime = 0;
        if(realTimeReportData!=null){
            drivingRange = realTimeReportData.getDrivingRange();
            drivingTime = realTimeReportData.getDrivingTime();
        }
        jsonMap.put("driving_range",drivingRange);
        jsonMap.put("driving_time",dataTool.getCurrentDate());

         //StringBuilder sb=new StringBuilder() ;
        //sb.append("车辆故障信息: ");
    /*    if(wd.getIsLocation()==(short)0){
            //0有效 1无效
            sb.append("当前位置:");
            sb.append("经度:").append(wd.getLongitude()).append(wd.getEastWest()).append(",");
            sb.append("纬度").append(wd.getLatitude()).append(wd.getNorthSouth()).append(";");
            sb.append("速度:").append(wd.getSpeed()).append("km/h;");
            sb.append("方向:").append(wd.getHeading()).append(";");
            positionMap.put("longitude", new StringBuilder().append(wd.getLongitude()).append(wd.getEastWest()).toString());
            positionMap.put("latitude", new StringBuilder().append(wd.getLatitude()).append(wd.getNorthSouth()).toString());
            positionMap.put("speed", new StringBuilder().append(wd.getSpeed()).append("km/h;").toString());
            positionMap.put("heading", new StringBuilder().append(wd.getHeading()).toString());
        }*/
        Iterator<WarningMessageConversion> iterator=warningMessageConversionRepository.findAll().iterator();
        HashMap<String,String> messages=dataTool.messageIteratorToMap(iterator);

        String[] failureId=wd.getIdArray();//包含故障信息ID的数组

        for (int i = 0; i <failureId.length ; i++) {
            String _info=messages.get(failureId[i]);
            if(_info!=null){
                failInfo.add(_info);
            }
        }

        //jsonMap.put("position",positionMap);
        jsonMap.put("failure_info",failInfo);
        jsonMap.put("failure_num",failInfo.size());
        String contextJson= JSON.toJSONString(jsonMap);

        dataMap.put("messageNums",failInfo.size());
        dataMap.put("textContent",contextJson);
        dataMap.put("pType",2);

        return dataMap;
    }

   /* *//**
     * 远程控制参数暂存redis
     * @param vin vin
     * @param eventId eventId
     * @param rc 封装远程控制参数的RemoteControl对象
     *//*
    public  void saveRemoteCmdValueToRedis(String vin,long eventId,RemoteControl rc){
        String valueStr=rc.getControlType()+","+rc.getAcTemperature();//类型和温度值 15,25
        socketRedis.saveValueString(dataTool.remote_cmd_value_preStr +"-"+ vin+"-"+eventId, valueStr, DataTool.remote_cmd_value_ttl);
        //控制参数暂存redis
    }*/

    /**
     * 从数据库取出暂存的远程控制参数
     * @param vin vin
     * @param eventId eventId
     * @return 封装远程控制参数的RemoteControl对象
     */
    public  RemoteControl getRemoteCmdValueFromDb(String vin,long eventId){
        String sessionId=49+"-"+eventId;
        RemoteControl rc=remoteControlRepository.findByVinAndSessionId(vin, sessionId);
        if (rc == null) {
            _logger.info("No RemoteControl found in db,vin:"+vin+"|eventId:"+eventId);
            return null;
        }else{
            return rc;
        }
        //取出暂存redis控制参数
    }

    /**
     * 更新远程控制记录的eventId
     * @param rc 远程控制对象
     * @return 封装远程控制参数的RemoteControl对象
     */
    public  RemoteControl modifyRemoteControl(RemoteControl rc){
        String newSessionId=49+"-"+dataTool.getCurrentSeconds();
        rc.setRefId(-2l);
        rc.setSessionId(newSessionId);
        RemoteControl retRc=remoteControlRepository.save(rc);
        return rc;

    }


    /**
     * 生成一个简单remoteControl 用于生成启动发动机命令,无需数据库存储
     * @param vin vin
     * @return 封装远程控制参数的RemoteControl对象
     */
    public  RemoteControl getStartEngineRemoteControl(int uid,String vin,long eventId,long refId){
        RemoteControl remoteControl=new RemoteControl();
        String sessionId="49-"+eventId;
        remoteControl.setUid(uid);
        remoteControl.setSendingTime(new Date());
        remoteControl.setVin(vin);
        remoteControl.setSessionId(sessionId);
        remoteControl.setRefId(refId);
        remoteControl.setControlType((short) 0);
        remoteControl.setAcTemperature(0.0);
        remoteControl.setLightNum((short) 0);
        remoteControl.setHornNum((short) 0);
        remoteControl.setActTime(0.0);
        remoteControl.setDeActive((short) 0);
        remoteControl.setAutoMode((short)0);
        remoteControl.setRecirMode((short) 0);
        remoteControl.setAcMode((short) 0);
        remoteControl.setFan((short) 0);
        remoteControl.setMode((short) 0);
        remoteControl.setMasterStat((short) 0);
        remoteControl.setMasterLevel((short) 0);
        remoteControl.setSlaveStat((short) 0);
        remoteControl.setSlaveLevel((short) 0);
        remoteControl.setStatus((short) 2);//处理中
        remoteControl.setRemark("命令下发成功，处理中");
        remoteControl.setRemarkEn("sending command");
        remoteControl.setAvailable((short) 0);
        remoteControlRepository.save(remoteControl);
        return remoteControl;
    }

    /**
     * 从数据库取出暂存的远程控制参数
     * @param id id
     * @return 封装远程控制参数的RemoteControl对象
     */
    public  RemoteControl getRemoteCmdValueFromDb(long id){
        RemoteControl rc=remoteControlRepository.findOne(id);
        if (rc == null) {
            _logger.info("No RemoteControl found in db,id:"+id);
            return null;
        }else{
            return rc;
        }
    }


    /**
     * 处理远程控制Ack上行（持久化 push）
     * @param vin vin
     * @param eventId eventId
     */
    public void handleRemoteControlPreconditionResp(String vin,long eventId,String message,String messageEn,boolean push){
        String sessionId=49+"-"+eventId;
        RemoteControl rc=remoteControlRepository.findByVinAndSessionId(vin, sessionId);
        if (rc == null) {
            _logger.info("No RemoteControl found in db,vin:"+vin+"|eventId:"+eventId);
        }else{
            //持久化远程控制记录状态，push to sender
            _logger.info("RemoteControl PreconditionResp persistence and push start");
            //返回无效才更新db记录
            rc.setRemark(message);
            rc.setRemarkEn(messageEn);
            rc.setStatus((short)0);
            remoteControlRepository.save(rc);
            String pushMsg=message;
            _logger.info("RemoteControl PreconditionResp  push message>:"+pushMsg);
            if(push){
                try{
                    this.pushRemoteControlResult(rc.getVin(), 0, pushMsg);
                }catch (RuntimeException e){_logger.info(e.getMessage());}
            }
            _logger.info("RemoteControl PreconditionResp persistence and push success");
              }
    }

    /**
     * 处理远程控制Ack上行（持久化 push）
     * @param vin vin
     * @param eventId eventId
     * @param result Ack响应结果  0：无效 1：命令已接收
     */
    public void handleRemoteControlAck(String vin,long eventId,Short result,boolean push){
        String sessionId=49+"-"+eventId;
        //  Rst 0：无效 1：命令已接收
        RemoteControl rc=remoteControlRepository.findByVinAndSessionId(vin, sessionId);
        if (rc == null) {
            _logger.info("No RemoteControl found in db,vin:"+vin+"|eventId:"+eventId+"|result:"+result);
        }else{
            //持久化远程控制记录状态，push to sender
           if(result==(short)0){
               _logger.info("RemoteControl Ack persistence and push start");
               //返回无效才更新db记录 不阻塞
               rc.setRemark("TBOX提示命令无效");
               rc.setRemarkEn("TBOX prompt invalid command");
               rc.setStatus((short) 0);
               remoteControlRepository.save(rc);
               String pushMsg="TBOX提示命令无效";
               if(push){
                   try {
                       this.pushRemoteControlResult(rc.getVin(),0,pushMsg);
                   }catch (RuntimeException e){_logger.info(e.getMessage());}
               }
               _logger.info("RemoteControl Ack persistence and push success");
           }

        }
    }

    /**
     * 处理远程控制Ack上行（持久化 push）本方法只有引用远程启动的情况才会被调用
     * @param id vin
     * @param result Ack响应结果  0：无效 1：命令已接收
     */
    public void handleRefRemoteControlAck(long id,Short result){
        RemoteControl rc=remoteControlRepository.findOne(id);
        if (rc == null) {
            _logger.info("No RemoteControl found in db id:"+id+"  "+"result:"+ result);
        }else{
            //持久化远程控制记录状态，push to sender
            if(result==(short)0){//Rst 0：无效 1：命令已接收
                _logger.info("RemoteControl Ack persistence and push start");
                //返回无效才更新db记录 不阻塞
                rc.setRemark("命令执行失败,依赖的远程启动发动机命令执行未能成功:TBOX提示命令无效");
                rc.setRemarkEn("Command execution failed, dependent remote start engine command execution failed: TBOX prompt command is invalid");
                rc.setStatus((short) 0);
                remoteControlRepository.save(rc);
                String pushMsg="命令执行失败,依赖的远程启动发动机命令执行未能成功:TBOX提示命令无效";
                try {
                    this.pushRemoteControlResult(rc.getVin(),0,pushMsg);
                }catch (RuntimeException e){_logger.info(e.getMessage());}
                _logger.info("RemoteControl Ack persistence and push success");
            }

        }
    }


    /**
     * 返回远程控制记录
     * @param vin vin
     * @param eventId eventId
     */
    public RemoteControl getRemoteControlRecord(String vin,long eventId){
        String sessionId=49+"-"+eventId;
        RemoteControl rc=remoteControlRepository.findByVinAndSessionId(vin,sessionId);
        return rc;
    }


    /**
     * 处理远程控制结果上行（持久化 push）
     * @param vin vin
     * @param eventId eventId
     * @param result Rst响应结果 0：成功 1：失败
     */
    public void handleRemoteControlRst(String vin,long eventId,Short result,boolean push){
        String sessionId=49+"-"+eventId;
        Short dbResult=0;//参考建表sql  0失败1成功   ,  Rst 0：成功 1：失败
        if(result==(short)0){
            dbResult=1;
        }
        RemoteControl rc=remoteControlRepository.findByVinAndSessionId(vin, sessionId);
        if (rc == null) {
            _logger.info("No RemoteControl found in db,vin:"+vin+"|eventId:"+eventId+"|result:"+result);
        }else{
            //持久化远程控制记录状态，push to sender
            _logger.info("RemoteControl Rst persistence and push start");
            rc.setStatus(dbResult);
            Vehicle vehicle=vehicleRepository.findByVin(vin);
            if(vehicle!=null){
                int currentCount=0;
                if(vehicle.getRemoteCount()!=null){
                    currentCount=vehicle.getRemoteCount();
                }
                vehicle.setRemoteCount(currentCount+1);
                vehicleRepository.save(vehicle);
            }

            String pushMsg="";//参考PDF0621 page55
            String pushMsgEn="";
            if(result==(short)0){
                pushMsg="远程命令执行成功";
                pushMsgEn="Remote command execution success";
            }else if(result==(short)1){
                pushMsg="远程命令执行失败";
                pushMsgEn="Remote command execution failed";
            }else if(result==(short)0x20){
                pushMsg="请求未完成";
                pushMsgEn="Request not completed";
            }else if(result==(short)0x21){
                pushMsg="请求的CRC错误";
                pushMsgEn="Requested CRC error";
            }else if(result==(short)0x22){
                pushMsg="请求的身份验证错误";
                pushMsgEn="Requested authentication error";
            }else if(result==(short)0x23){
                pushMsg="请求无效";
                pushMsgEn="Request message order error";
            }else if(result==(short)0x24){
                pushMsg="请求消息顺序错误";
                pushMsgEn="请求的CRC错误";
            }else if(result==(short)0x30){
                pushMsg="请求不能执行";
                pushMsgEn="Request cannot be executed";
            }else if(result==(short)0x31){
                pushMsg="请求先决条件无效";
                pushMsgEn="Request prerequisites are invalid";
            }else if(result==(short)0x40){
                pushMsg="本地用户终止请求";
                pushMsgEn="Local user termination request";
            }else if(result==(short)0x50){
                pushMsg="请求超时失效";
                pushMsgEn="Request timeout";
            }else if(result==(short)0x51){
                pushMsg="请求次数超过3次";
                pushMsgEn="More than 3 times the number of requests";
            }else if(result==(short)0x60){
                pushMsg="功能无效，请求被忽略";
                pushMsgEn="Function is not valid, the request is ignored.";
            }else if(result==(short)0x80){
                pushMsg="等待响应中，指定时间后再请求";
                pushMsgEn="Wait for the response, after the specified time to request";
            }else if(result==(short)0x81){
                pushMsg="响应等待下次车辆启动";
                pushMsgEn="In response to waiting for the next vehicle to start";
            }
            String _dbReMark=pushMsg;
            rc.setRemark(_dbReMark);
            rc.setRemarkEn(pushMsgEn);
            remoteControlRepository.save(rc);
            if(push){
                try{
                    this.pushRemoteControlResult(rc.getVin(),rc.getStatus(),pushMsg);
                }catch (RuntimeException e){_logger.info(e.getMessage());}
            }
            _logger.info("RemoteControl Rst persistence and push success");
        }
    }

    /**
     * 处理远程控制结果上行（持久化 push） 仅仅只处理失败的引用情况
     * @param id vin
     * @param result Rst响应结果 0：成功 1：失败  ...
     */
    public void handleRefRemoteControlRst(long id,Short result){
        RemoteControl rc=remoteControlRepository.findOne(id);
        if (rc == null) {
            _logger.info("No RemoteControl found in db id:"+id+"  "+"result:"+ result);
        }else{
            //持久化远程控制记录状态，push to sender
            _logger.info("RemoteControl Rst persistence and push start");
            Vehicle vehicle=vehicleRepository.findByVin(rc.getVin());
            if(vehicle!=null){
                int currentCount=0;
                if(vehicle.getRemoteCount()!=null){
                    currentCount=vehicle.getRemoteCount();
                }
                vehicle.setRemoteCount(currentCount+1);
                vehicleRepository.save(vehicle);
            }
            String pushMsg="";//参考PDF0621 page55
            String pushMsgEn="";
            if(result==(short)0){
                pushMsg="远程命令执行成功";
                pushMsgEn="Remote command execution success";
            }else if(result==(short)1){
                pushMsg="远程命令执行失败";
                pushMsgEn="Remote command execution failed";
            }else if(result==(short)0x20){
                pushMsg="请求未完成";
                pushMsgEn="Request not completed";
            }else if(result==(short)0x21){
                pushMsg="请求的CRC错误";
                pushMsgEn="Requested CRC error";
            }else if(result==(short)0x22){
                pushMsg="请求的身份验证错误";
                pushMsgEn="Requested authentication error";
            }else if(result==(short)0x23){
                pushMsg="请求无效";
                pushMsgEn="Request message order error";
            }else if(result==(short)0x24){
                pushMsg="请求消息顺序错误";
                pushMsgEn="请求的CRC错误";
            }else if(result==(short)0x30){
                pushMsg="请求不能执行";
                pushMsgEn="Request cannot be executed";
            }else if(result==(short)0x31){
                pushMsg="请求先决条件无效";
                pushMsgEn="Request prerequisites are invalid";
            }else if(result==(short)0x40){
                pushMsg="本地用户终止请求";
                pushMsgEn="Local user termination request";
            }else if(result==(short)0x50){
                pushMsg="请求超时失效";
                pushMsgEn="Request timeout";
            }else if(result==(short)0x51){
                pushMsg="请求次数超过3次";
                pushMsgEn="More than 3 times the number of requests";
            }else if(result==(short)0x60){
                pushMsg="功能无效，请求被忽略";
                pushMsgEn="Function is not valid, the request is ignored.";
            }else if(result==(short)0x80){
                pushMsg="等待响应中，指定时间后再请求";
                pushMsgEn="Wait for the response, after the specified time to request";
            }else if(result==(short)0x81){
                pushMsg="响应等待下次车辆启动";
                pushMsgEn="In response to waiting for the next vehicle to start";
            }
            String _dbReMark="命令执行失败,依赖的远程启动发动机命令执行未能成功:"+pushMsg;
            rc.setRemark(_dbReMark);
            rc.setRemarkEn(pushMsgEn);
            if(result==(short)0){
                rc.setStatus((short)1);
            }else{
                rc.setStatus((short)0);
            }
            remoteControlRepository.save(rc);
            pushMsg=_dbReMark;
            try{
                this.pushRemoteControlResult(rc.getVin(), rc.getStatus(), pushMsg);
            }catch (RuntimeException e){_logger.info(e.getMessage());}
            _logger.info("RemoteControl Rst persistence and push success");
        }
    }

    /**
     * update RefController Remark
     * @param id id
     */
    public void updateRefRemoteControlRst(long id,String remark,String remaerEn) {
        RemoteControl rc = remoteControlRepository.findOne(id);
        rc.setRemark(remark);
        rc.setRemarkEn(remaerEn);
        rc.setStatus((short) 0);
        remoteControlRepository.save(rc);
        String pushMsg=remark;
        try{
            this.pushRemoteControlResult(rc.getVin(), rc.getStatus(), pushMsg);
        }catch (RuntimeException e){_logger.info(e.getMessage());}
        _logger.info("RemoteControl Rst persistence and push success");
    }


    public  void saveCmdToRedis(String vin,String hexStr){
        socketRedis.saveSetString(dataTool.out_cmd_preStr + vin, hexStr, -1);//代发命令的TTL为-1 由处理程序取出
        //保存到redis


    }

}
