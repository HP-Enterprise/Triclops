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
import com.hp.triclops.utils.HttpRequestTool;
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

    private Logger _logger = LoggerFactory.getLogger(OutputHexService.class);

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
        remoteControlCmd.setRemoteControlType(remoteControl.getControlType().intValue());
        remoteControlCmd.setAcTemperature(remoteControl.getAcTemperature());
        remoteControlCmd.setApplicationID((short) 49);
        remoteControlCmd.setMessageID((short) 3);
        remoteControlCmd.setEventID(eventId);
        remoteControlCmd.setSendingTime((long)dataTool.getCurrentSeconds());
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
     */
    public void getWarningMessageAndPush(String vin,String msg){
        Vehicle vehicle=vehicleRepository.findByVin(vin);
        List<UserVehicleRelatived> uvr=userVehicleRelativedRepository.findByVid(vehicle);
        //一个车对应多个uid
        if(uvr.size()>0) {
            Iterator<UserVehicleRelatived> iterator = uvr.iterator();
            while (iterator.hasNext()) {
                //int uid = iterator.next().getUid().getId();
                User user = iterator.next().getUid();

                Map<String,Object> pushMsg=getWarningMessageForPush(vin, msg, user);
                pushWarningOrFailureMessage(vin,pushMsg);
            }
        }
    }

    /**
     * 根据补发报警hex信息生成文本性质的报警提示 并push到对应user
     * @param vin vin
     * @param msg 16进制报警信息
     */
    public void getResendWarningMessageAndPush(String vin,String msg){
        Vehicle vehicle=vehicleRepository.findByVin(vin);
        List<UserVehicleRelatived> uvr=userVehicleRelativedRepository.findByVid(vehicle);
        //一个车对应多个uid
        if(uvr.size()>0) {
            Iterator<UserVehicleRelatived> iterator = uvr.iterator();
            while (iterator.hasNext()) {
                User user = iterator.next().getUid();
                Map<String,Object> pushMsg=getResendWarningMessageForPush(vin, msg, user);
                pushWarningOrFailureMessage(vin,pushMsg);
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
        pushWarningOrFailureMessage(vin, pushMsg);
    }

    /**
     * 根据补发故障hex信息生成文本性质的故障提示 并push到对应user
     * @param vin vin
     * @param msg 16进制报警信息
     */
    public void getResendFailureMessageAndPush(String vin,String msg){
        Map<String,Object> pushMsg=getResendFailureMessageForPush(vin, msg);
        pushWarningOrFailureMessage(vin,pushMsg);
    }


    /**
     * 根报警提示push到对应user
     * @param vin vin
     * @param pushMsg 文本报警信息
     */
    public void pushWarningOrFailureMessage(String vin,Map<String,Object> pushMsg){
        _logger.info("push message:"+pushMsg);
        Vehicle vehicle=vehicleRepository.findByVin(vin);
        List<UserVehicleRelatived> uvr=userVehicleRelativedRepository.findByVid(vehicle);
        //一个车对应多个uid 报警消息都push过去
        if(uvr.size()>0){
            Iterator<UserVehicleRelatived> iterator=uvr.iterator();
            while (iterator.hasNext()){
                int uid=iterator.next().getUid().getId();
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
        }else{
            _logger.info("can not push  message,because no user found for vin:"+vin);
        }
    }

    /**
     * 根据报警hex信息生成文本性质的报警提示
     * @param vin vin
     * @param msg 16进制报警信息
     * @param user user
     * @return 根据报警hex信息生成文本性质的报警提示
     */
    public Map<String,Object> getWarningMessageForPush(String vin,String msg,User user){
        //报警数据保存
        _logger.info(">>get WarningMessage For Push:"+msg);
        ByteBuffer bb= PackageEntityManager.getByteBuffer(msg);
        DataPackage dp=conversionTBox.generate(bb);
        WarningMessage bean=dp.loadBean(WarningMessage.class);
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
        wd.setAtaWarning(dataTool.getWarningInfoFromByte(bean.getAtaWarning()));

        wd.setSafetyBeltCount(bean.getSafetyBeltCount());
        wd.setVehicleHitSpeed(dataTool.getHitSpeed(bean.getVehicleSpeedLast()));

        List<RealTimeReportData> rdList = realTimeReportDataRespository.findLatestOneByVin(vin);
        RealTimeReportData rd=null;
        if(rdList!=null&&rdList.size()>0) {
            rd = rdList.get(0);
        }

        //生成报警信息
        Map<String,Object> warningMessage=buildWarningString(wd,user,rd);
        return warningMessage;
    }

    /**
     * 根据补发报警hex信息生成文本性质的报警提示
     * @param vin vin
     * @param msg 16进制报警信息
     * @return 根据报警hex信息生成文本性质的报警提示
     */
    public Map<String,Object> getResendWarningMessageForPush(String vin,String msg,User user){
        //报警数据保存
        _logger.info(">>get Resend WarningMessage For Push:"+msg);
        ByteBuffer bb= PackageEntityManager.getByteBuffer(msg);
        DataPackage dp=conversionTBox.generate(bb);
        DataResendWarningMes bean=dp.loadBean(DataResendWarningMes.class);
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
        wd.setAtaWarning(dataTool.getWarningInfoFromByte(bean.getAtaWarning()));

        wd.setSafetyBeltCount(bean.getSafetyBeltCount());
        wd.setVehicleHitSpeed(dataTool.getHitSpeed(bean.getVehicleSpeedLast()));

        List<RealTimeReportData> rdList = realTimeReportDataRespository.findLatestOneByVin(vin);
        RealTimeReportData rd=null;
        if(rdList!=null&&rdList.size()>0) {
            rd = rdList.get(0);
        }
        //生成报警信息
        Map<String,Object> warningMessage=buildWarningString(wd, user, rd);
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

        wd.setInfo1((short) (bean.getInfo1().shortValue() & 0xFF));
        wd.setInfo2((short) (bean.getInfo2().shortValue() & 0xFF));
        wd.setInfo3((short) (bean.getInfo3().shortValue() & 0xFF));
        wd.setInfo4((short) (bean.getInfo4().shortValue() & 0xFF));
        wd.setInfo5((short) (bean.getInfo5().shortValue() & 0xFF));
        wd.setInfo6((short) (bean.getInfo6().shortValue() & 0xFF));
        wd.setInfo7((short) (bean.getInfo7().shortValue() & 0xFF));
        wd.setInfo8((short) (bean.getInfo8().shortValue() & 0xFF));

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

        wd.setInfo1((short) (bean.getInfo1().shortValue() & 0xFF));
        wd.setInfo2((short) (bean.getInfo2().shortValue() & 0xFF));
        wd.setInfo3((short) (bean.getInfo3().shortValue() & 0xFF));
        wd.setInfo4((short) (bean.getInfo4().shortValue() & 0xFF));
        wd.setInfo5((short) (bean.getInfo5().shortValue() & 0xFF));
        wd.setInfo6((short) (bean.getInfo6().shortValue() & 0xFF));
        wd.setInfo7((short) (bean.getInfo7().shortValue() & 0xFF));
        wd.setInfo8((short) (bean.getInfo8().shortValue() & 0xFF));

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
     * @return 便于阅读的报警消息
     */
    public Map<String,Object> buildWarningString(WarningMessageData wd,User user,RealTimeReportData realTimeReportData){
        Map<String,Object> dataMap = new HashMap<String,Object>();
       // StringBuilder sb=new StringBuilder() ;
        Map<String,Object> jsonMap = new HashMap<String,Object>();
        Map<String,String> positionMap = new HashMap<String,String>();

        //sb.append("车辆报警信息: ");
        if(wd.getIsLocation()==(short)0){
            //0有效 1无效
/*          sb.append("当前位置:");
            sb.append("经度:").append(wd.getLongitude()).append(wd.getEastWest()).append(",");
            sb.append("纬度").append(wd.getLatitude()).append(wd.getNorthSouth()).append(";");
            sb.append("速度:").append(wd.getSpeed()).append("km/h;");
            sb.append("方向:").append(wd.getHeading()).append(";");*/
            positionMap.put("longitude", new StringBuilder().append(wd.getLongitude()).append(wd.getEastWest()).toString());
            positionMap.put("latitude",new StringBuilder().append(wd.getLatitude()).append(wd.getNorthSouth()).toString());
            positionMap.put("speed",new StringBuilder().append(wd.getSpeed()).append("km/h;").toString());
            positionMap.put("heading",new StringBuilder().append(wd.getHeading()).toString());

        }else{
            positionMap.put("longitude","");
            positionMap.put("latitude","");
            positionMap.put("speed","");
            positionMap.put("heading","");
        }
        if(wd.getSrsWarning()==(short)1){
            //安全气囊报警 0未触发 1触发
/*            sb.append("安全气囊报警触发,");
            sb.append("背扣安全带数量:").append(wd.getSafetyBeltCount()).append(",");
            sb.append("碰撞速度:").append(wd.getVehicleHitSpeed()).append("km/h;");*/
            jsonMap.put("srs_warning",true);
            jsonMap.put("safety_belt_count",wd.getSafetyBeltCount());
            jsonMap.put("vehicle_hit_speed",new StringBuilder().append(wd.getVehicleHitSpeed()).append("km/h;").toString());
            dataMap.put("pType", 8);

            String contactsPhone ="";
            if(user!=null){
                contactsPhone =  user.getContactsPhone();
            }
            jsonMap.put("contacts_phone",contactsPhone);
        }else if(wd.getAtaWarning()==(short)1){
            //防盗报警 0未触发 1触发
           // sb.append("车辆防盗报警触发");
            jsonMap.put("ata_warning",true);
            dataMap.put("pType", 9);

            String leftFrontDoorInformation = "";
            String leftRearDoorInformation = "";
            String rightFrontDoorInformation = "";
            String rightRearDoorInformation = "";
            String engineCoverState = "";
            String trunkLidState = "";
            if(realTimeReportData!=null){
                leftFrontDoorInformation = realTimeReportData.getLeftFrontDoorInformation();
                leftRearDoorInformation = realTimeReportData.getLeftRearDoorInformation();
                rightFrontDoorInformation = realTimeReportData.getRightFrontDoorInformation();
                rightRearDoorInformation = realTimeReportData.getRightRearDoorInformation();
                engineCoverState = realTimeReportData.getEngineCoverState();
                trunkLidState = realTimeReportData.getTrunkLidState();
            }
            jsonMap.put("leftFrontDoorInformation",leftFrontDoorInformation);
            jsonMap.put("leftRearDoorInformation",leftRearDoorInformation);
            jsonMap.put("rightFrontDoorInformation",rightFrontDoorInformation);
            jsonMap.put("rightRearDoorInformation",rightRearDoorInformation);
            jsonMap.put("engineCoverState",engineCoverState);
            jsonMap.put("trunkLidState",trunkLidState);
        }
        jsonMap.put("position", positionMap);
        String contextJson= JSON.toJSONString(jsonMap);

        dataMap.put("textContent",contextJson);
        return dataMap;
    }

    /**
     * 根据故障消息类生成故障告警消息
     * @param wd 故障消息实体类
     * @return 便于阅读的消息
     */
    public Map<String,Object> buildFailureString(FailureMessageData wd,RealTimeReportData realTimeReportData){
        Map<String,Object> dataMap = new HashMap<String,Object>();

        Map<String,Object> jsonMap = new HashMap<String,Object>();
       // Map<String,String> positionMap = new HashMap<String,String>();
        List<String> failInfo = new ArrayList<String>();

        int drivingRange = 0;
        int drivingTime = 0;
        if(realTimeReportData!=null){
            drivingRange = realTimeReportData.getDrivingRange();
            drivingTime = realTimeReportData.getDrivingTime();
        }
        jsonMap.put("driving_range",drivingRange);
        jsonMap.put("driving_time",drivingTime);

        int count = 0;
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
        HashMap<Short,String> messages=dataTool.messageIteratorToMap(iterator);
        String info1=messages.get(wd.getInfo1());
        if(info1!=null){
            //sb.append(info1+";");
            failInfo.add(info1);
            count++;
        }
        String info2=messages.get(wd.getInfo2());
        if(info2!=null){
            //sb.append(info2+";");
            failInfo.add(info2);
            count++;
        }
        String info3=messages.get(wd.getInfo3());
        if(info3!=null){
            //sb.append(info3+";");
            failInfo.add(info3);
            count++;
        }
        String info4=messages.get(wd.getInfo4());
        if(info4!=null){
           // sb.append(info4+";");
            failInfo.add(info4);
            count++;
        }
        String info5=messages.get(wd.getInfo5());
        if(info5!=null){
            //sb.append(info5+";");
            failInfo.add(info5);
            count++;
        }
        String info6=messages.get(wd.getInfo6());
        if(info6!=null){
            //sb.append(info6+";");
            failInfo.add(info6);
            count++;
        }
        String info7=messages.get(wd.getInfo7());
        if(info7!=null){
            //sb.append(info7+";");
            failInfo.add(info7);
            count++;
        }
        String info8=messages.get(wd.getInfo8());
        if(info8!=null){
            //sb.append(info8+";");
            failInfo.add(info8);
            count++;
        }
        //jsonMap.put("position",positionMap);
        jsonMap.put("failure_info",failInfo);
        jsonMap.put("failure_num",count);
        String contextJson= JSON.toJSONString(jsonMap);

        dataMap.put("messageNums",count);
        dataMap.put("textContent",contextJson);
        dataMap.put("pType",2);

        return dataMap;
    }

    /**
     * 远程控制参数暂存redis
     * @param vin vin
     * @param eventId eventId
     * @param rc 封装远程控制参数的RemoteControl对象
     */
    public  void saveRemoteCmdValueToRedis(String vin,long eventId,RemoteControl rc){
        String valueStr=rc.getControlType()+","+rc.getAcTemperature();//类型和温度值 15,25
        socketRedis.saveValueString(dataTool.remote_cmd_value_preStr +"-"+ vin+"-"+eventId, valueStr, DataTool.remote_cmd_value_ttl);
        //控制参数暂存redis
    }

    /**
     * 从redis取出暂存的远程控制参数
     * @param vin vin
     * @param eventId eventId
     * @return 封装远程控制参数的RemoteControl对象
     */
    public  RemoteControl getRemoteCmdValueFromRedis(String vin,long eventId){
        String cmdValueKey=dataTool.remote_cmd_value_preStr +"-"+ vin+"-"+eventId;
        String valueStr=socketRedis.getValueString(cmdValueKey);
        socketRedis.delValueString(cmdValueKey);
        if(!valueStr.equalsIgnoreCase("null")&&valueStr.length()>0){
            String[] values=valueStr.split(",");
            RemoteControl  rc=new RemoteControl();
            rc.setControlType(Short.parseShort(values[0]));
            rc.setAcTemperature(Short.parseShort(values[1]));
            return rc;
        }
        return null;
        //取出暂存redis控制参数
    }

    /**
     * 处理远程控制Ack上行（持久化 push）
     * @param vin vin
     * @param eventId eventId
     */
    public void handleRemoteControlPreconditionResp(String vin,long eventId){
        String sessionId=49+"-"+eventId;
        Short dbResult=1;//参考建表sql 1不符合条件主动终止 2返回无效 3返回执行成功 4返回执行失败
        RemoteControl rc=remoteControlRepository.findByVinAndSessionId(vin,sessionId);
        if (rc == null) {
            _logger.info("No RemoteControl found in db,vin:"+vin+"|eventId:"+eventId);
        }else{
            //持久化远程控制记录状态，push to sender
            _logger.info("RemoteControl PreconditionResp persistence and push start");
            //返回无效才更新db记录
            rc.setStatus(dbResult);
            remoteControlRepository.save(rc);
            String pushMsg="行驶中，不能远程控制:"+sessionId;
            try{
                this.mqService.pushToUser(rc.getUid(), pushMsg);
            }catch (RuntimeException e){_logger.info(e.getMessage());}

            _logger.info("RemoteControl PreconditionResp persistence and push success");
              }
    }

    /**
     * 处理远程控制Ack上行（持久化 push）
     * @param vin vin
     * @param eventId eventId
     * @param result Ack响应结果
     */
    public void handleRemoteControlAck(String vin,long eventId,Short result){
        String sessionId=49+"-"+eventId;
        Short dbResult=(result==(short)0)?(short)2:(short)-1;//参考建表sql 1不符合条件主动终止 2返回无效 3返回执行成功 4返回执行失败  Rst 0：无效 1：命令已接收
        RemoteControl rc=remoteControlRepository.findByVinAndSessionId(vin,sessionId);
        if (rc == null) {
            _logger.info("No RemoteControl found in db,vin:"+vin+"|eventId:"+eventId+"|result:"+result);
        }else{
            //持久化远程控制记录状态，push to sender
           if(dbResult==(short)2){
               _logger.info("RemoteControl Ack persistence and push start");
               //返回无效才更新db记录 不阻塞
               rc.setStatus(dbResult);
               remoteControlRepository.save(rc);
               String pushMsg="远程命令无效:"+sessionId;
               try{
               this.mqService.pushToUser(rc.getUid(), pushMsg);
               }catch (RuntimeException e){_logger.info(e.getMessage());}
               _logger.info("RemoteControl Ack persistence and push success");
           }

        }
    }

    /**
     * 处理远程控制结果上行（持久化 push）
     * @param vin vin
     * @param eventId eventId
     * @param result Rst响应结果
     */
    public void handleRemoteControlRst(String vin,long eventId,Short result){
        String sessionId=49+"-"+eventId;
        Short dbResult=(short)(result+(short)3);//参考建表sql  1不符合条件主动终止 2返回无效 3返回执行成功 4返回执行失败,  Rst 0：成功 1：失败
        RemoteControl rc=remoteControlRepository.findByVinAndSessionId(vin,sessionId);
        if (rc == null) {
            _logger.info("No RemoteControl found in db,vin:"+vin+"|eventId:"+eventId+"|result:"+result);
        }else{
            //持久化远程控制记录状态，push to sender
            _logger.info("RemoteControl Rst persistence and push start");
            rc.setStatus(dbResult);
            remoteControlRepository.save(rc);
            String pushMsg=(result==(short)0)?"远程命令执行成功:":"远程命令执行失败:";
            pushMsg=pushMsg+sessionId;
            try{
            this.mqService.pushToUser(rc.getUid(), pushMsg);
            }catch (RuntimeException e){_logger.info(e.getMessage());}
            _logger.info("RemoteControl Rst persistence and push success");
        }
    }

    public  void saveCmdToRedis(String vin,String hexStr){
        socketRedis.saveSetString(dataTool.out_cmd_preStr + vin, hexStr, -1);//代发命令的TTL为-1 由处理程序取出
        //保存到redis


    }

}
