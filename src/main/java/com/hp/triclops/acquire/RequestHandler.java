package com.hp.triclops.acquire;

import com.hp.data.bean.tbox.*;
import com.hp.data.core.Conversion;
import com.hp.data.core.DataPackage;
import com.hp.data.util.PackageEntityManager;
import com.hp.triclops.entity.*;
import com.hp.triclops.redis.SocketRedis;
import com.hp.triclops.repository.*;
import com.hp.triclops.service.OutputHexService;
import com.hp.triclops.service.TboxService;
import com.hp.triclops.service.VehicleDataService;
import com.hp.triclops.utils.GpsTool;
import com.hp.triclops.utils.MD5;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.util.Date;
import java.util.List;

/**
 * Created by luj on 2015/9/24.
 */
@Component
public class RequestHandler {
    //处理直接回应的请求
    @Autowired
    Conversion conversionTBox;
    @Autowired
    DataTool dataTool;
    @Autowired
    SocketRedis socketRedis;
    @Autowired
    TboxService tboxService;
    @Autowired
    OutputHexService outputHexService;
    @Autowired
    TBoxParmSetRepository tBoxParmSetRepository;
    @Autowired
    DiagnosticDataRepository diagnosticDataRepository;
    @Autowired
    GpsDataRepository gpsDataRepository;
    @Autowired
    VehicleDataService vehicleDataService;
    @Autowired
    GpsTool gpsTool;
    @Autowired
    TBoxRepository tBoxRepository;
    @Value("${com.hp.acquire.serverId}")
    private String _serverId;//serverId集群依赖这个值

    private Logger _logger = LoggerFactory.getLogger(RequestHandler.class);


    /**
     * 注册使用的aeskey来时
     * @param imei
     * @return
     */
    public String getRegAesKeyByImei(String imei){
        String key=null;
        TBox tBox=tBoxRepository.findByImei(imei);
        if(tBox!=null){
            key=tBox.getT_sn()+""+tBox.getVin();
            key= MD5.MD5To16UpperCase(key);
        }
        return key;
    }

    /**
     * @param reqString 处理激活数据，包括激活请求和激活结果，上行messageId 1或3 ，对于1下行2，对于3只接收无下行
     * @return messageId=1返回处理后的resp,messageId=1返回null
     */

    public String getActiveHandle(String reqString){
        //根据激活请求的16进制字符串，生成响应的16进制字符串
        byte[] bytes=dataTool.getBytesFromByteBuf(dataTool.getByteBuf(reqString));
        byte messageId=0;
        if(bytes!=null){
            if(bytes.length>10) {
                messageId=bytes[10];
            }
        }
        if(messageId==0x01){
            //Active Request
            _logger.info("[0x12]收到激活请求>>>>>");
            ByteBuffer bb= PackageEntityManager.getByteBuffer(reqString);
            DataPackage dp=conversionTBox.generate(bb);
            ActiveReq bean=dp.loadBean(ActiveReq.class);
            //请求解析到bean
            //远程唤醒响应
           _logger.info(bean.getVin() + "|" + bean.getSerialNumber());
            boolean activeResult=tboxService.activationTBox(bean.getVin(),bean.getVehicleModel(),bean.getSerialNumber(),bean.getImei(),bean.getIccid());//true成功 false失败
            short tBoxStatus=1; //0激活成功 1激活失败
            if(activeResult){
                tBoxStatus=0;
            }
            ActiveResp resp=new ActiveResp();
            resp.setHead(bean.getHead());
            resp.setTestFlag(bean.getTestFlag());
            resp.setSendingTime((long) dataTool.getCurrentSeconds());
            resp.setApplicationID(bean.getApplicationID());
            resp.setMessageID((short) 2);
            resp.setEventID(bean.getEventID());
            resp.settBoxStatus(tBoxStatus);
            resp.setVin(bean.getVin());

            DataPackage dpw=new DataPackage("8995_18_2");
            dpw.fillBean(resp);
            ByteBuffer bbw=conversionTBox.generate(dpw);
            String byteStr=PackageEntityManager.getByteString(bbw);
            return byteStr;
        }else if(messageId==0x03){
            //Active Result
            _logger.info("[0x12]收到激活结果>>>>> 无需回复");
                  }
        return null;
    }

    /**
     *
     * @param reqString 远程唤醒请求hex
     * @return 远程唤醒响应hex
     */
    public String getFlowResp(String reqString){

        //根据远程唤醒请求的16进制字符串，生成响应的16进制字符串
        ByteBuffer bb= PackageEntityManager.getByteBuffer(reqString);
        DataPackage dp=conversionTBox.generate(bb);
        FlowReq bean=dp.loadBean(FlowReq.class);
        //请求解析到bean
        //远程唤醒响应
        FlowResp resp=new FlowResp();
        resp.setHead(bean.getHead());
        resp.setTestFlag(bean.getTestFlag());
        resp.setSendingTime((long) dataTool.getCurrentSeconds());
        resp.setApplicationID(bean.getApplicationID());
        resp.setMessageID((short) 2);
        resp.setEventID(bean.getEventID());
        resp.setTotalSize(500l);
        resp.setUsedSize(123l);
        DataPackage dpw=new DataPackage("8995_21_2");
        dpw.fillBean(resp);
        ByteBuffer bbw=conversionTBox.generate(dpw);
        String byteStr=PackageEntityManager.getByteString(bbw);
        return byteStr;
    }



    /**
     *
     * @param reqString 电检请求hex
     * @return 电检响应hex
     */
    public String getDiagResp(String reqString){
        //根据电检请求的16进制字符串，生成响应的16进制字符串
        ByteBuffer bb= PackageEntityManager.getByteBuffer(reqString);
        DataPackage dp=conversionTBox.generate(bb);
        DiaRequest bean=dp.loadBean(DiaRequest.class);
        //请求解析到bean
        //单项为0成功，全部为0测试成功返回0否则失败返回1
       // int result=bean.getCanActionTest()+bean.getFarmTest()+bean.getGprsTest()+bean.getGpsTest()+bean.getLedTest()+bean.getResetBatteryMapArrayTest()+bean.getSdTest()+bean.getServerCommTest();
        //电检响应
        DiaResp resp=new DiaResp();
        resp.setHead(bean.getHead());
        resp.setTestFlag(bean.getTestFlag());
        resp.setSendingTime((long) dataTool.getCurrentSeconds());
        resp.setApplicationID(bean.getApplicationID());
        resp.setMessageID((short) 2);
        resp.setEventID(bean.getEventID());
      //  resp.setDiaReportResp(result > 0 ? (short) 1 : (short) 0);

        DataPackage dpw=new DataPackage("8995_17_2");
        dpw.fillBean(resp);
        ByteBuffer bbw=conversionTBox.generate(dpw);
        String byteStr=PackageEntityManager.getByteString(bbw);
        return byteStr;
    }

    /**
     *
     * @param reqString 注册请求数据hex
     * @param checkRegister 注册是否通过
     * @return 响应hex
     */
    public String getRegisterResp(String reqString,String vin,boolean checkRegister){
        //根据注册请求的16进制字符串，生成响应的16进制字符串
        ByteBuffer bb= PackageEntityManager.getByteBuffer(reqString);
        DataPackage dp=conversionTBox.generate(bb);
        RegisterReq bean=dp.loadBean(RegisterReq.class);
        //请求解析到bean
        RegisterResp resp=new RegisterResp();
        resp.setHead(bean.getHead());
        resp.setTestFlag(bean.getTestFlag());
        resp.setSendingTime((long) dataTool.getCurrentSeconds());
        resp.setApplicationID(bean.getApplicationID());
        resp.setMessageID((short) 2);
        resp.setEventID(bean.getEventID());
        short registerResult = checkRegister ? (short)0 : (short)1;
        resp.setRegisterResult(registerResult);
        resp.setTotalSize(500l);
        resp.setUsedSize(123l);
        String randomKey="0123456789abcdef";
       // randomKey=dataTool.getRandomString(16);
        if(checkRegister) {
            _logger.info("[0x13]注册时给vin:" + vin+"生成的AES key:"+randomKey);
            socketRedis.saveHashString(dataTool.tboxkey_hashmap_name, vin, randomKey, -1);
        }
        resp.setKeyInfo(randomKey.getBytes());
        //注册响应
        DataPackage dpw=new DataPackage("8995_19_2");
        dpw.fillBean(resp);
        ByteBuffer bbw=conversionTBox.generate(dpw);
        String byteStr=PackageEntityManager.getByteString(bbw);
        return byteStr;
    }

    /**
     *
     * @param reqString 远程唤醒请求hex
     * @param checkVinAndSerNumWake 唤醒校验
     * @return 远程唤醒响应hex
     */
    public String getRemoteWakeUpResp(String reqString,String vin,boolean checkVinAndSerNumWake){

        //根据远程唤醒请求的16进制字符串，生成响应的16进制字符串
        ByteBuffer bb= PackageEntityManager.getByteBuffer(reqString);
        DataPackage dp=conversionTBox.generate(bb);
        RemoteWakeUpReq bean=dp.loadBean(RemoteWakeUpReq.class);
        //请求解析到bean
        //远程唤醒响应
        RemoteWakeUpResp resp=new RemoteWakeUpResp();
        resp.setHead(bean.getHead());
        resp.setTestFlag(bean.getTestFlag());
        resp.setSendingTime((long) dataTool.getCurrentSeconds());
        resp.setApplicationID(bean.getApplicationID());
        resp.setMessageID((short) 2);
        resp.setEventID(bean.getEventID());
        short wakeUpResult = checkVinAndSerNumWake ? (short)0 : (short)1;
        resp.setRegisterResult(wakeUpResult);//0唤醒成功 1唤醒失败
        resp.setTotalSize(500l);
        resp.setUsedSize(123l);
        String randomKey="0123456789abcdef";
        // randomKey=dataTool.getRandomString(16);
        if(checkVinAndSerNumWake) {
            _logger.info("[0x13]注册时给vin:" + vin+"生成的AES key:"+randomKey);
            socketRedis.saveHashString(dataTool.tboxkey_hashmap_name, vin, randomKey, -1);
        }
        resp.setKeyInfo(randomKey.getBytes());

        DataPackage dpw=new DataPackage("8995_20_2");
        dpw.fillBean(resp);
        ByteBuffer bbw=conversionTBox.generate(dpw);
        String byteStr=PackageEntityManager.getByteString(bbw);
        return byteStr;
    }

    /**
     *
     * @param reqString 心跳请求hex
     * @return 心跳响应hex
     */
    public String getHeartbeatResp(String reqString){
        //根据心跳请求的16进制字符串，生成响应的16进制字符串
        ByteBuffer bb= PackageEntityManager.getByteBuffer(reqString);
        DataPackage dp=conversionTBox.generate(bb);
        HeartbeatReq bean=dp.loadBean(HeartbeatReq.class);
        //请求解析到bean
        HeartbeatResp resp=new HeartbeatResp();
        resp.setHead(bean.getHead());
        resp.setTestFlag(bean.getTestFlag());
        resp.setSendingTime((long) dataTool.getCurrentSeconds());
        resp.setApplicationID(bean.getApplicationID());
        resp.setMessageID((short) 2);
        resp.setEventID(bean.getEventID());
        //响应
        DataPackage dpw=new DataPackage("8995_38_2");
        dpw.fillBean(resp);
        ByteBuffer bbw=conversionTBox.generate(dpw);
        String byteStr=PackageEntityManager.getByteString(bbw);
        return byteStr;
    }

    /**
     *
     * @return 解密失败报告hex
     */
    public String getInvalidReport(){
        InvalidReport report=new InvalidReport();
        report.setTestFlag((short) 0);
        report.setSendingTime((long)dataTool.getCurrentSeconds());
        report.setApplicationID((short) 97);//>>>
        report.setMessageID((short) 1);//>>>
        report.setEventID(report.getSendingTime());
        //响应
        DataPackage dpw=new DataPackage("8995_97_1");
        dpw.fillBean(report);
        ByteBuffer bbw=conversionTBox.generate(dpw);
        String byteStr=PackageEntityManager.getByteString(bbw);
        return byteStr;
    }

    /**
     *
     * @param reqString 实时数据请求hex
     * @return 实时数据响应hex
     */
    public String getRealTimeDataResp(String reqString){
        //根据心跳请求的16进制字符串，生成响应的16进制字符串
        ByteBuffer bb= PackageEntityManager.getByteBuffer(reqString);
        DataPackage dp=conversionTBox.generate(bb);
        RealTimeReportMes bean=dp.loadBean(RealTimeReportMes.class);
        //请求解析到bean
        RealTimeReportMesResp resp=new RealTimeReportMesResp();
        resp.setHead(bean.getHead());
        resp.setTestFlag(bean.getTestFlag());
        resp.setSendingTime((long) dataTool.getCurrentSeconds());
        resp.setApplicationID(bean.getApplicationID());
        resp.setMessageID((short) 2);
        resp.setEventID(bean.getEventID());
        //响应
        DataPackage dpw=new DataPackage("8995_34_2");
        dpw.fillBean(resp);
        ByteBuffer bbw=conversionTBox.generate(dpw);
        String byteStr=PackageEntityManager.getByteString(bbw);
        return byteStr;
    }

    /**
     *
     * @param reqString 补发实时数据请求hex
     * @return 补发实时数据响应hex
     */
    public String getResendRealTimeDataResp(String reqString){
        //根据心跳请求的16进制字符串，生成响应的16进制字符串
        ByteBuffer bb= PackageEntityManager.getByteBuffer(reqString);
        DataPackage dp=conversionTBox.generate(bb);
        DataResendRealTimeMes bean=dp.loadBean(DataResendRealTimeMes.class);
        //请求解析到bean
        DataResendRealTimeMesResp resp=new DataResendRealTimeMesResp();
        resp.setHead(bean.getHead());
        resp.setTestFlag(bean.getTestFlag());
        resp.setSendingTime((long) dataTool.getCurrentSeconds());
        resp.setApplicationID(bean.getApplicationID());
        resp.setMessageID((short) 2);
        resp.setEventID(bean.getEventID());
        //响应
        DataPackage dpw=new DataPackage("8995_35_2");
        dpw.fillBean(resp);
        ByteBuffer bbw=conversionTBox.generate(dpw);
        String byteStr=PackageEntityManager.getByteString(bbw);
        return byteStr;
    }

    /**
     *
     * @param reqString 报警数据请求hex
     * @return 报警数据响应hex
     */
    public String getWarningMessageResp(String reqString){
        //根据心跳请求的16进制字符串，生成响应的16进制字符串
        ByteBuffer bb= PackageEntityManager.getByteBuffer(reqString);
        WarningMessage bean=dataTool.decodeWarningMessage(reqString);
        //请求解析到bean
        WarningMessageResp resp=new WarningMessageResp();
        resp.setHead(bean.getHead());
        resp.setTestFlag(bean.getTestFlag());
        resp.setSendingTime((long) dataTool.getCurrentSeconds());
        resp.setApplicationID(bean.getApplicationID());
        resp.setMessageID((short) 2);
        resp.setEventID(bean.getEventID());
        //响应
        DataPackage dpw=new DataPackage("8995_36_2");
        dpw.fillBean(resp);
        ByteBuffer bbw=conversionTBox.generate(dpw);
        String byteStr=PackageEntityManager.getByteString(bbw);
        return byteStr;
    }

    /**
     *
     * @param reqString 补发报警数据请求hex
     * @return 补发报警数据响应hex
     */
    public String getDataResendWarningDataResp(String reqString){
        //根据心跳请求的16进制字符串，生成响应的16进制字符串
        ByteBuffer bb= PackageEntityManager.getByteBuffer(reqString);
        DataResendWarningMes bean=dataTool.decodeResendWarningMessage(reqString);
       //请求解析到bean
        DataResendWarningMesResp resp=new DataResendWarningMesResp();
        resp.setHead(bean.getHead());
        resp.setTestFlag(bean.getTestFlag());
        resp.setSendingTime((long) dataTool.getCurrentSeconds());
        resp.setApplicationID(bean.getApplicationID());
        resp.setMessageID((short) 2);
        resp.setEventID(bean.getEventID());
        //响应
        DataPackage dpw=new DataPackage("8995_37_2");
        dpw.fillBean(resp);
        ByteBuffer bbw=conversionTBox.generate(dpw);
        String byteStr=PackageEntityManager.getByteString(bbw);
        return byteStr;
    }


    /**
     *
     * @param reqString 故障数据请求hex
     * @return 故障数据响应hex
     */
    public String getFailureDataResp(String reqString){
        //根据心跳请求的16进制字符串，生成响应的16进制字符串
        ByteBuffer bb= PackageEntityManager.getByteBuffer(reqString);
        DataPackage dp=conversionTBox.generate(bb);
        FailureMessage bean=dp.loadBean(FailureMessage.class);
        //请求解析到bean
        FailureMessageResp resp=new FailureMessageResp();
        resp.setHead(bean.getHead());
        resp.setTestFlag(bean.getTestFlag());
        resp.setSendingTime((long) dataTool.getCurrentSeconds());
        resp.setApplicationID(bean.getApplicationID());
        resp.setMessageID((short) 2);
        resp.setEventID(bean.getEventID());
        //响应
        DataPackage dpw=new DataPackage("8995_40_2");
        dpw.fillBean(resp);
        ByteBuffer bbw=conversionTBox.generate(dpw);
        String byteStr=PackageEntityManager.getByteString(bbw);
        return byteStr;
    }

    /**
     *
     * @param reqString 补发故障数据请求hex
     * @return 补发故障数据响应hex
     */
    public String getResendFailureDataResp(String reqString){
        //根据心跳请求的16进制字符串，生成响应的16进制字符串
        ByteBuffer bb= PackageEntityManager.getByteBuffer(reqString);
        DataPackage dp=conversionTBox.generate(bb);
        DataResendFailureData bean=dp.loadBean(DataResendFailureData.class);
        //请求解析到bean
        DataResendFailureDataResp resp=new DataResendFailureDataResp();
        resp.setHead(bean.getHead());
        resp.setTestFlag(bean.getTestFlag());
        resp.setSendingTime((long) dataTool.getCurrentSeconds());
        resp.setApplicationID(bean.getApplicationID());
        resp.setMessageID((short) 2);
        resp.setEventID(bean.getEventID());
        //响应
        DataPackage dpw=new DataPackage("8995_41_2");
        dpw.fillBean(resp);
        ByteBuffer bbw=conversionTBox.generate(dpw);
        String byteStr=PackageEntityManager.getByteString(bbw);
        return byteStr;
    }

    /**
     *
     * @param reqString 行为数据请求hex
     * @return 补发故障数据响应hex
     */
    public String getDrivingBehaviorMesResp(String reqString){
        //根据心跳请求的16进制字符串，生成响应的16进制字符串

        DrivingBehaviorMes bean=dataTool.decodeDrivingBehaviorMes(reqString);
        //请求解析到bean
        DrivingBehaviorAck resp=new DrivingBehaviorAck();
        resp.setHead(bean.getHead());
        resp.setTestFlag(bean.getTestFlag());
        resp.setSendingTime((long) dataTool.getCurrentSeconds());
        resp.setApplicationID(bean.getApplicationID());
        resp.setMessageID((short) 2);
        resp.setEventID(bean.getEventID());
        //响应
        DataPackage dpw=new DataPackage("8995_42_2");
        dpw.fillBean(resp);
        ByteBuffer bbw=conversionTBox.generate(dpw);
        String byteStr=PackageEntityManager.getByteString(bbw);
        return byteStr;
    }

    /**
     *
     * @param reqString 休眠请求hex
     * @return 休眠响应hex
     */
    public String getSleepResp(String reqString){
        //根据心跳请求的16进制字符串，生成响应的16进制字符串
        ByteBuffer bb= PackageEntityManager.getByteBuffer(reqString);
        DataPackage dp=conversionTBox.generate(bb);
        SleepReq bean=dp.loadBean(SleepReq.class);
        //请求解析到bean
        SleepResp resp=new SleepResp();
        resp.setHead(bean.getHead());
        resp.setTestFlag(bean.getTestFlag());
        resp.setSendingTime((long) dataTool.getCurrentSeconds());
        resp.setApplicationID(bean.getApplicationID());
        resp.setMessageID((short) 2);
        resp.setEventID(bean.getEventID());
        //响应
        DataPackage dpw=new DataPackage("8995_39_2");
        dpw.fillBean(resp);
        ByteBuffer bbw=conversionTBox.generate(dpw);
        String byteStr=PackageEntityManager.getByteString(bbw);
        return byteStr;
    }

    /**
     *
     * @param reqString 远程控制上行hex mid=2,4,5
     * @param vin vin码
     */
    public void handleRemoteControlRequest(String reqString,String vin,int maxDistance){
        //处理远程控制上行的16进制字符串
        byte[] bytes=dataTool.getBytesFromByteBuf(dataTool.getByteBuf(reqString));
        byte messageId=dataTool.getMessageId(bytes);
        if(messageId==0x02){
            _logger.info("[0x31]远程控制Precondition响应处理...");
            //RemoteControlPreconditionResp
            //根据预处理响应判断是否下发控制指令
            ByteBuffer bb= PackageEntityManager.getByteBuffer(reqString);
            DataPackage dp=conversionTBox.generate(bb);
            RemoteControlPreconditionResp bean=dp.loadBean(RemoteControlPreconditionResp.class);
            //已经收到响应 变更消息状态 不会当作失败而重试
            String statusKey=DataTool.msgCurrentStatus_preStr+vin+"-"+bean.getApplicationID()+"-"+bean.getEventID();
            String statusValue=String.valueOf(bean.getMessageID());
            socketRedis.saveValueString(statusKey, statusValue,DataTool.msgCurrentStatus_ttl);
            RemoteControl dbRc=outputHexService.getRemoteControlRecord(vin, bean.getEventID());
            if(dbRc==null){
                _logger.info("[0x31]通过vin和EventId没有找到对应的远程控制记录..."+vin+"--"+bean.getEventID());
                return;
            }
            _logger.info("[0x31] "+dbRc.getId()+":变更状态标记 "+"statusKey:"+statusKey+"|"+"statusValue"+statusValue);
            //取出redis暂存的控制参数 生成指令
            long currentRefId=dbRc.getRefId();
            _logger.info("[0x31][debug] 引用记录ID:"+currentRefId);
            int preconditionRespCheck=0;
            if(currentRefId!=-2){//普通报文才做check ,-2 check直接通过
               preconditionRespCheck=verifyRemoteControlPreconditionResp(vin,bean,dbRc.getControlType());
            }
            _logger.info("[0x31][debug] precondition响应校验结果:"+preconditionRespCheck);
            //0 通过   1~6 各种异常
            //boolean distanceCheck=verifyRemoteControlDistance(vin, bean.getEventID(),maxDistance);//app与tbox距离校验
            if(preconditionRespCheck==0){
                //符合控制逻辑 从redis取出远程控制参数 生成控制指令 save redis
                long eventId=bean.getEventID();
                //RemoteControl _valueRc=outputHexService.getRemoteCmdValueFromRedis(vin,eventId);
                String cmdByteString=outputHexService.getRemoteControlCmdHex(dbRc,eventId);
                _logger.info("[0x31]Precondition响应校验通过,即将下发控制指令:" + cmdByteString);
                outputHexService.saveCmdToRedis(_serverId,vin, cmdByteString);
            }else{
                String msg="";
                String msgEn="";
                if(preconditionRespCheck==1){
                    msg="远程启动发动机条件不符合";
                    msgEn="emote start engine conditions do not meet";
                }
                if(preconditionRespCheck==2){
                    msg="远程关闭发动机失败,必须是远程启动发动机才能远程关闭";
                    msgEn="Remote shutdown of the engine fails, it must be remote start the engine to remote shutdown";
                }
                if(preconditionRespCheck==3){
                    msg="操作条件不符合，请检查车辆状态";
                    msgEn="Operating conditions do not meet, please check the state of the vehicle";
                }
                if(preconditionRespCheck==4){
                    //todo 生成启动发动机命令,建立关联关系
                    if(currentRefId==-1){
                        long refId=dbRc.getId();
                       //更新原来的命令eventId，后续通过id找回,
                        outputHexService.modifyRemoteControl(dbRc);
                        RemoteControl rc=outputHexService.getStartEngineRemoteControl(dbRc.getUid(),vin, bean.getEventID(),refId);
                        String cmdByteString=outputHexService.getRemoteControlCmdHex(rc,bean.getEventID());
                        _logger.info("[0x31]即将发送一条关联的启动发动机命令:" + cmdByteString);
                        outputHexService.saveCmdToRedis(_serverId,vin, cmdByteString);
                    }else{
                        _logger.info("[0x31]命令已经存在关联的远程控制记录->"+currentRefId);
                    }
                }
                if(preconditionRespCheck==5){
                    //todo 生成启动发动机命令,建立关联关系
                    if(currentRefId==-1){
                        long refId=dbRc.getId();
                        //更新原来的命令eventId，后续通过id找回,
                        outputHexService.modifyRemoteControl(dbRc);
                        RemoteControl rc=outputHexService.getStartEngineRemoteControl(dbRc.getUid(),vin, bean.getEventID(),refId);
                        String cmdByteString=outputHexService.getRemoteControlCmdHex(rc,bean.getEventID());
                        _logger.info("[0x31]即将发送一条关联的启动发动机命令:" + cmdByteString);
                        outputHexService.saveCmdToRedis(_serverId,vin, cmdByteString);
                    }
                }
                if(preconditionRespCheck==6){
                    //todo 生成启动发动机命令,建立关联关系
                    if(currentRefId==-1){
                        long refId=dbRc.getId();
                        //更新原来的命令eventId，后续通过id找回,
                        outputHexService.modifyRemoteControl(dbRc);
                        RemoteControl rc=outputHexService.getStartEngineRemoteControl(dbRc.getUid(),vin, bean.getEventID(),refId);
                        String cmdByteString=outputHexService.getRemoteControlCmdHex(rc,bean.getEventID());
                        _logger.info("[0x31]即将发送一条关联的启动发动机命令:" + cmdByteString);
                        outputHexService.saveCmdToRedis(_serverId,vin, cmdByteString);
                    }
                }
                if(preconditionRespCheck==7){
                    //todo 生成启动发动机命令,建立关联关系
                    if(currentRefId==-1){
                        long refId=dbRc.getId();
                        //更新原来的命令eventId，后续通过id找回,
                        outputHexService.modifyRemoteControl(dbRc);
                        RemoteControl rc=outputHexService.getStartEngineRemoteControl(dbRc.getUid(),vin, bean.getEventID(),refId);
                        String cmdByteString=outputHexService.getRemoteControlCmdHex(rc,bean.getEventID());
                        _logger.info("[0x31]即将发送一条关联的启动发动机命令:" + cmdByteString);
                        outputHexService.saveCmdToRedis(_serverId,vin, cmdByteString);
                    }
                }
                if(preconditionRespCheck==10){
                    msg="远程寻车失败，操作条件不满足";
                    msgEn="Remote search failed, the operating conditions are not satisfied";
                }
                if(preconditionRespCheck==4||preconditionRespCheck==5||preconditionRespCheck==6||preconditionRespCheck==7){
                    if(currentRefId<=0){
                        _logger.info("[0x31]正在尝试启动发动机...");
                    }
                }else{//除了4 5 6 7之外的失败会导致流程结束，而4 5 6 7会尝试启动发动机
                    if(currentRefId>0) {//存在ref记录
                        outputHexService.handleRemoteControlPreconditionResp(vin,bean.getEventID(),msg,msgEn,false);//关联子操作 不推送
                        String pre="依赖的操作失败:";
                        String preEn="Dependent operation failure :";
                        outputHexService.updateRefRemoteControlRst(currentRefId,pre+msg,preEn+msgEn);//原始记录推送
                    }else{
                        outputHexService.handleRemoteControlPreconditionResp(vin,bean.getEventID(),msg,msgEn,true);//普通单条，推送
                    }

                    _logger.info("[0x31]Precondition响应校验未通过,无法下发控制指令。");
                }
            }
        }else if(messageId==0x04){
            _logger.info("[0x31]收到远程控制应答,开始处理...");
            //RemoteControlAck
            ByteBuffer bb= PackageEntityManager.getByteBuffer(reqString);
            DataPackage dp=conversionTBox.generate(bb);
            RemoteControlAck bean=dp.loadBean(RemoteControlAck.class);
            //请求解析到bean
            _logger.info("[0x31]远程控制应答:>" + bean.getRemoteControlAck()+" (参考值 0:无效 1:命令已接收)");//0：无效 1：命令已接收
            //变更消息状态 不会当作失败而重试
            String statusKey=DataTool.msgCurrentStatus_preStr+vin+"-"+bean.getApplicationID()+"-"+bean.getEventID();
            String statusValue=String.valueOf(bean.getMessageID());
            socketRedis.saveValueString(statusKey, statusValue, DataTool.msgCurrentStatus_ttl);
            //todo 《失败时》需要判断是否存在ref控制指令（常见ref：远程启动空调需要远程启动发动机），如果存在这种情况，需要找到原始指令，更新失败原因
            RemoteControl rc=outputHexService.getRemoteControlRecord(vin,bean.getEventID());
            if(rc==null){
                _logger.info("[0x31]通过vin和eventId没有在数据库找到控制记录..."+vin+"|"+bean.getEventID());
                return;
            }
            long refId=rc.getRefId();
            if(bean.getRemoteControlAck()==(short)0){//无效
                //远程控制命令执行结束，此处进一步持久化或者通知到外部接口
                if(refId>0) {//存在ref记录
                    outputHexService.handleRemoteControlAck(vin, bean.getEventID(), bean.getRemoteControlAck(),false);
                    outputHexService.handleRefRemoteControlAck(refId, bean.getRemoteControlAck());
                }else{
                    outputHexService.handleRemoteControlAck(vin, bean.getEventID(), bean.getRemoteControlAck(),true);
                }
            }
            _logger.info("[0x31]远程控制应答处理结束:"+bean.getApplicationID()+"-"+bean.getEventID()+" >"+bean.getRemoteControlAck());
        }else if(messageId==0x05){
            _logger.info("[0x31]收到远程控制结果,开始处理...");
            //RemoteControlRst
            ByteBuffer bb= PackageEntityManager.getByteBuffer(reqString);
            DataPackage dp=conversionTBox.generate(bb);
            RemoteControlRst bean=dp.loadBean(RemoteControlRst.class);
            //请求解析到bean
            _logger.info("[0x31]收到远程控制结果：>" + bean.getRemoteControlAck()+" (参考值 0：成功 1：失败 ...大于1:其它文档中定义的原因)");//0：成功 1：失败
            String key="Result:"+vin+"-"+bean.getApplicationID()+"-"+bean.getEventID()+"-"+bean.getMessageID();
            //变更消息状态
            String statusKey=DataTool.msgCurrentStatus_preStr+vin+"-"+bean.getApplicationID()+"-"+bean.getEventID();
            String statusValue=String.valueOf(bean.getMessageID());
            socketRedis.saveValueString(statusKey, statusValue, DataTool.msgCurrentStatus_ttl);
            //socketRedis.saveSetString(key, String.valueOf(bean.getRemoteControlAck()), -1);
            //远程控制命令执行结束，此处进一步持久化或者通知到外部接口
            //todo 需要判断是否存在ref控制指令（常见ref：远程启动空调需要远程启动发动机），如果存在这种情况，需要找到原始指令，参照0x02 resp处理下发
            //todo 存在ref  成功:找到remote记录，下发0x03命令  失败：持久化失败消息
            //todo 不存在ref 成功或者失败 持久化消息
            RemoteControl rc=outputHexService.getRemoteControlRecord(vin,bean.getEventID());
            if(rc==null){
                _logger.info("[0x31]通过vin和eventId没有在数据库找到控制记录..."+vin+"|"+bean.getEventID());
                return;
            }
            long refId=rc.getRefId();
            if(bean.getRemoteControlAck()==(short)0){//成功了
                    //符合控制逻辑 从redis取出远程控制参数 生成控制指令 save redis
                   //RemoteControl _valueRc=outputHexService.getRemoteCmdValueFromRedis(vin,eventId);
                    //取出redis暂存的控制参数 生成指令
                    if(refId>0){
                        outputHexService.handleRemoteControlRst(vin,bean.getEventID(), bean.getRemoteControlAck(),false);
                    //存在ref记录
                        //RemoteControl refRc=outputHexService.getRemoteCmdValueFromDb(rc.getRefId());
                       _logger.info("[0x31]关联的启动发动机执行成功，开始执行原始控制指令");
                        new RemoteCommandSender(vehicleDataService,refId,rc.getUid(), vin, null,true).start();
                    }
                else{
                        outputHexService.handleRemoteControlRst(vin,bean.getEventID(), bean.getRemoteControlAck(),true);
                    }
                }else{//各种原因未能成功

                if(refId>0){
                    outputHexService.handleRemoteControlRst(vin,bean.getEventID(), bean.getRemoteControlAck(),false);
                    outputHexService.handleRefRemoteControlRst(refId, bean.getRemoteControlAck());
                }else {
                    outputHexService.handleRemoteControlRst(vin,bean.getEventID(), bean.getRemoteControlAck(),true);
                }
                }
            _logger.info("[0x31]远程控制结果处理结束:"+bean.getApplicationID()+"-"+bean.getEventID()+" >"+bean.getRemoteControlAck());
        }
    }


    /**
     *
     * @param reqString 远程控制上行hex mid=2,4,5
     * @param vin vin码
     */
    public void handleRemoteControlSettingRequest(String reqString,String vin) {
        byte[] bytes=dataTool.getBytesFromByteBuf(dataTool.getByteBuf(reqString));
        byte messageId=dataTool.getMessageId(bytes);
        if(messageId==0x02){
            //todo 记录远程控制设置结果
            ByteBuffer bb=PackageEntityManager.getByteBuffer(reqString);
            DataPackage dp=conversionTBox.generate(bb);
            RemoteSettingResp bean=dp.loadBean(RemoteSettingResp.class);
            String key=vin+"-"+bean.getEventID();
            String val=String.valueOf(bean.getResponse());
            _logger.info("handle RemoteControl Setting resp"+key+":"+val);
            socketRedis.saveHashString(dataTool.remoteControlSet_hashmap_name,key,val,-1);
        }
    }

    /**
     * 校验,是否发送远程指令
     * @param vin
     * @param remoteControlPreconditionResp 数据bean
     * @return 是否通过
     */
    public int verifyRemoteControlPreconditionResp(String vin,RemoteControlPreconditionResp remoteControlPreconditionResp,short controlType){
        //目前逻辑 根据0619协议
        boolean re=false;
        short vehicleModel=remoteControlPreconditionResp.getVehicleModel();//按照协议0628车型编号 0~255 0：默认值(M82)；1：M82；2：M85； 3：F60；4：F70； 5：F60电动车
        boolean isM8X=true;
        if(vehicleModel>(short)2){
            isM8X=false;
        }
        int reint=-1;
        boolean tmpCheck=false;
        boolean clampCheck=false;
        boolean remoteKeyCheck=false;
        boolean hazardLightsCheck=false;
        boolean vehicleSpeedCheck=false;
        boolean transmissionGearPositionCheck=false;
        boolean handBrakeCheck=false;
        boolean sunroofCheck=false;
        boolean windowsCheck=false;
        boolean doorsCheck=false;
        boolean trunkCheck=false;
        boolean bonnetCheck=false;
        boolean centralLockCheck=false;
        boolean crashStatusCheck=false;
        boolean remainingFuelCheck=false;

        RemoteControl rc=outputHexService.getRemoteControlRecord(vin, remoteControlPreconditionResp.getEventID());
        if(rc!=null&&remoteControlPreconditionResp!=null){
            //根据0621协议
            float ambientAirTemperature=dataTool.getInternTrueTmp(remoteControlPreconditionResp.getTempIntern());//偏移40
            if(ambientAirTemperature>-10&&ambientAirTemperature<40){        //温度 -10~40
                tmpCheck=true;
            }
            byte clampStatus=remoteControlPreconditionResp.getSesam_clamp_stat();
            char[] clampStatus_char=dataTool.getBitsFromByte(clampStatus);
            if(clampStatus_char[2]=='0'&&clampStatus_char[3]=='0'){
                clampCheck=true;
            }
            byte remoteKey=remoteControlPreconditionResp.getSesam_hw_status();
            char[] remoteKey_char=dataTool.getBitsFromByte(remoteKey);
            if(isM8X){
                if(remoteKey_char[5]=='0' && remoteKey_char[6]=='0' && remoteKey_char[7]=='1'){
                    remoteKeyCheck=true;//0x1 Key outside Vehicle
                }
                if(remoteKey_char[5]=='0' && remoteKey_char[6]=='1' && remoteKey_char[7]=='0'){
                    remoteKeyCheck=true;//0x2 Key out of range
                }
            }else{
                //尚未定义 false
                if(remoteKey_char[5]=='0' && remoteKey_char[6]=='1' && remoteKey_char[7]=='0'){
                    remoteKeyCheck=true;//0x2: Valid key outside right
                }
                if(remoteKey_char[5]=='0' && remoteKey_char[6]=='1' && remoteKey_char[7]=='1'){
                    remoteKeyCheck=true;//0x3: Valid key outside left
                }
                if(remoteKey_char[5]=='1' && remoteKey_char[6]=='0' && remoteKey_char[7]=='0'){
                    remoteKeyCheck=true;//0x4: Valid key outside trunk
                }
            }



            byte hazardLight=remoteControlPreconditionResp.getBcm_Stat_Central_Lock2();
            char[] hazardLight_char=dataTool.getBitsFromByte(hazardLight);
            if(hazardLight_char[5]=='0' && hazardLight_char[6]=='0' && hazardLight_char[7]=='0'){
                hazardLightsCheck=true;
            }
            //根据0628协议 M8X车速分辨率0.015625，偏移量0，显示范围： 0 ~350kmh 上报数据范围:0~22400  5km/h-->320<上传数据>
            //F60:分辨率0.05625，偏移量0，显示范围： 0 ~296kmh 上报数据范围： 0~5263 缺省值： 0x0000无效值
            if(remoteControlPreconditionResp.getVehicleSpeed()==0){
                if(isM8X) {
                    vehicleSpeedCheck = true;
                }else{
                    vehicleSpeedCheck = true;
                }
            }
            byte transmissionGearPosition=remoteControlPreconditionResp.getTcu_ecu_stat();//要求P挡位 参考0628协议
            char[] transmissionGearPosition_char=dataTool.getBitsFromByte(transmissionGearPosition);
            if(isM8X) {//M8X 0x3 P挡
                if (transmissionGearPosition_char[4] == '0' && transmissionGearPosition_char[5] == '0' && transmissionGearPosition_char[6] == '1' && transmissionGearPosition_char[7] == '1') {
                    transmissionGearPositionCheck = true;
                }
            }else{//F6O 0x1 P挡
                if (transmissionGearPosition_char[4] == '0' && transmissionGearPosition_char[5] == '0' && transmissionGearPosition_char[6] == '0' && transmissionGearPosition_char[7] == '1') {
                    transmissionGearPositionCheck = true;
                }
            }

            byte handBrake=remoteControlPreconditionResp.getEpb_status();
            char[] handBrake_char=dataTool.getBitsFromByte(handBrake);
            if(isM8X){
            if(handBrake_char[6]=='0'&&handBrake_char[7]=='1'){
                handBrakeCheck=true;
            }
            }else{
                if(handBrake_char[6]=='0'&&handBrake_char[7]=='1'){ //0x2
                    handBrakeCheck=true;
                }
            }
            byte sunroof=remoteControlPreconditionResp.getBcm_Stat_window2();
            char[] sunroof_char=dataTool.getBitsFromByte(sunroof);
            //if(sunroof_char[6]=='0'&&sunroof_char[7]=='1'){
                sunroofCheck = true;
            //}

            byte[] windows=remoteControlPreconditionResp.getBcm_Stat_window();
            char[] windows_char = dataTool.getBitsFrom2Byte(windows);
            if(isM8X){
            //if(windows_char[14]=='1'&&windows_char[15]=='0' && windows_char[12]=='1'&&windows_char[13]=='0' && windows_char[10]=='1'&&windows_char[11]=='0' && windows_char[8]=='1'&&windows_char[9]=='0'){
                windowsCheck=true;
            //}
            }else{
                //if(windows_char[6]=='1'&&windows_char[7]=='0' && windows_char[4]=='1'&&windows_char[5]=='0' && windows_char[2]=='1'&&windows_char[3]=='0' && windows_char[0]=='1'&&windows_char[1]=='0'){
                windowsCheck=true;
                //}
            }

            byte[] doors=remoteControlPreconditionResp.getBcm_Stat_Door_Flap();
            char[] doors_char=dataTool.getBitsFrom2Byte(doors);
            if(doors_char[14]=='0'&&doors_char[15]=='0' && doors_char[12]=='0'&&doors_char[13]=='0' && doors_char[10]=='0'&&doors_char[11]=='0' && doors_char[8]=='0'&&doors_char[9]=='0'){
                doorsCheck=true;
            }
            if(doors_char[6]=='0'&&doors_char[7]=='0' ){
                bonnetCheck=true;
            }
            if(doors_char[4]=='0'&&doors_char[5]=='0'){
                trunkCheck=true;
            }
            byte centralLock=remoteControlPreconditionResp.getBcm_Stat_Central_Lock();
            char[] centralLock_char=dataTool.getBitsFromByte(centralLock);
            if(centralLock_char[6]=='0'&&centralLock_char[7]=='1'){
                centralLockCheck=true;
            }
            byte crashStatus=remoteControlPreconditionResp.getAcm_crash_status();
            char[] crashStatus_char=dataTool.getBitsFromByte(crashStatus);
            if(crashStatus_char[6]=='0'&&crashStatus_char[7]=='0'&& crashStatus_char[1]=='0'&&crashStatus_char[0]=='0'){
                crashStatusCheck =true;
            }
            short remainingFuel=remoteControlPreconditionResp.getFuelLevel();
            if(remainingFuel>15){
                remainingFuelCheck=true;
            }
            byte remoteStartestatus=remoteControlPreconditionResp.getStat_remote_start();
            char[] remoteStartStatus_char=dataTool.getBitsFromByte(remoteStartestatus);
            boolean isRemoteStart=false;
            if(remoteStartStatus_char[2]=='0'&&remoteStartStatus_char[3]=='1'){//必须是远程启动发动机才能远程关闭
                isRemoteStart=true;
            }
            /*
            控制类别  0：远程启动发动机  1：远程关闭发动机  2：车门上锁  3：车门解锁  4：空调开启  5：空调关闭  6：座椅加热  7：座椅停止加热  8：远程发动机限制  9：远程发动机限制关闭  10：闪灯 11：鸣笛
            */
            if(controlType==(short)0){//0：远程启动发动机
              /*  re=tmpCheck && clampCheck && remoteKeyCheck && hazardLightsCheck && vehicleSpeedCheck
                        && transmissionGearPositionCheck && handBrakeCheck && sunroofCheck && windowsCheck
                        && doorsCheck && trunkCheck && bonnetCheck && centralLockCheck && crashStatusCheck
                        && remainingFuelCheck;*/
                //todo 20160908屏蔽发动机启动的温度检查
                re= clampCheck && remoteKeyCheck && hazardLightsCheck && vehicleSpeedCheck
                        && transmissionGearPositionCheck && handBrakeCheck && sunroofCheck && windowsCheck
                        && doorsCheck && trunkCheck && bonnetCheck && centralLockCheck && crashStatusCheck
                        && remainingFuelCheck;

                if(re){
                    reint=0;
                }else{
                    reint=1;
                }
            }else if(controlType==(short)1){//1：远程关闭发动机
                if(isRemoteStart){//必须是远程启动发动机才能远程关闭
                    re=true;
                }
                if(re){
                    reint=0;
                }else{
                    reint=2;
                }
            }else if(controlType==(short)2||controlType==(short)3){//2：车门上锁  3：车门解锁
                re=doorsCheck && clampCheck && trunkCheck && bonnetCheck ;
                if(re){
                    reint=0;
                }else{
                    reint=3;
                }
            }else if(controlType==(short)4){//4：空调开启
                if(isRemoteStart){//必须是远程启动发动机才能开启空调
                    re=true;
                }
                if(re){
                    reint=0;
                }else{
                    reint=4;
                }
            }else if(controlType==(short)5){//5：空调关闭
                if(isRemoteStart){//必须是远程启动发动机才能关闭空调
                    re=true;
                }
                if(re){
                    reint=0;
                }else{
                    reint=5;
                }
            }else if(controlType==(short)6){//6：座椅加热
                if(isRemoteStart){//必须是远程启动发动机才能开启座椅加热
                    re=true;
                }
                if(re){
                    reint=0;
                }else{
                    reint=6;
                }
            }else if(controlType==(short)7){//6：座椅加热关闭
                if(isRemoteStart){//必须是远程启动发动机才能关闭座椅加热
                    re=true;
                }
                if(re){
                    reint=0;
                }else{
                    reint=7;
                }
            }else if(controlType==(short)10||controlType==(short)11){//10：远程寻车->10闪灯 11鸣笛
                re=doorsCheck && clampCheck && hazardLightsCheck && trunkCheck && bonnetCheck;
                if(re){
                    reint=0;
                }else{
                    reint=10;
                }
            }
        }
        _logger.info("[0x31]status:tmpCheck-clampCheck-remoteKeyCheck-hazardLightsCheck-vehicleSpeedCheck"
                +"-transmissionGearPositionCheck-handBrakeCheck-sunroofCheck-windowsCheck"
                +"-doorsCheck-trunkCheck-bonnetCheck-centralLockCheck-crashStatusCheck"
                +"-remainingFuelCheck");
        _logger.info("[0x31]status:"+tmpCheck +"-"+ clampCheck +"-"+ remoteKeyCheck +"-"+ hazardLightsCheck +"-"+ vehicleSpeedCheck
                +"-"+ transmissionGearPositionCheck +"-"+ handBrakeCheck +"-"+ sunroofCheck +"-"+ windowsCheck
                +"-"+ doorsCheck +"-"+ trunkCheck +"-"+ bonnetCheck +"-"+ centralLockCheck +"-"+ crashStatusCheck
                +"-"+ remainingFuelCheck);
        reint=0;//临时处理
        _logger.info("[0x31]Precondition响应校验结果:"+reint+" 车型:"+vehicleModel+"(0:M82;1:M82;2:M85;3:F60;4:F70;5:F60电动车) 是否M8X:"+isM8X);
        return reint;
    }
    /**
     * 校验发起指令APP与车之间的距离
     * @param vin
     * @param eventId
     * @return 是否通过
     */
    public boolean verifyRemoteControlDistance(String vin,long eventId,int maxDistance){
        //目前逻辑 app与car距离小于配置的maxDistance 单位m
     /*   boolean re=false;
        RemoteControl rc=outputHexService.getRemoteControlRecord(vin, eventId);
        GpsData gpsData=gpsDataRepository.findTopByVinOrderBySendingTimeDesc(vin);

        if(rc!=null&&gpsData!=null){
            double distance=gpsTool.getDistance(gpsData.getLongitude(),gpsData.getLatitude(),rc.getLongitude(),rc.getLatitude());
            _logger.info("app-car distance:"+distance+"   >gpsData.id"+gpsData.getId()+"|>rc.id"+rc.getId());
            if(distance<=maxDistance){
                re=true;
            }
        }
        _logger.info("verifyRemoteControlDistance result:"+re);*/
        return true;//协议0625 取消此校验

    }



    /**
     *
     * @param reqString 参数设置响应hex
     * @param vin vin码
     */
    public void handleParmSetAck(String reqString,String vin){
        //处理参数设置响应hex
        ByteBuffer bb= PackageEntityManager.getByteBuffer(reqString);
        DataPackage dp=conversionTBox.generate(bb);
        PramSetupAck bean=dp.loadBean(PramSetupAck.class);
        byte[] pramValue=bean.getPramValue();
        //请求解析到bean
        String key="Result:"+vin+"-"+bean.getApplicationID()+"-"+bean.getEventID()+"-"+bean.getMessageID();
        //变更消息状态 不会当作失败而重试
        String statusKey=DataTool.msgCurrentStatus_preStr+vin+"-"+bean.getApplicationID()+"-"+bean.getEventID();
        String statusValue=String.valueOf(bean.getMessageID());
        socketRedis.saveValueString(statusKey, statusValue, DataTool.msgCurrentStatus_ttl);

        List<TBoxParmSet> tpss=tBoxParmSetRepository.findByVinAndEventId(vin, bean.getEventID());
        if(tpss.size()>0){
            TBoxParmSet tps=tpss.get(0);
            if (tps != null) {
                tps.setStatus((short) 2);//标识命令已经响应
                tps.setFrequencySaveLocalMediaResult(pramValue[0] == 0x00 ? (short) 0 : (short) 1);//标识单条参数结果
                tps.setFrequencyForReportResult(pramValue[1] == 0x00 ? (short) 0 : (short) 1);
                tps.setFrequencyForWarningReportResult(pramValue[2] == 0x00 ? (short) 0 : (short) 1);
                tps.setFrequencyHeartbeatResult(pramValue[3] == 0x00 ? (short) 0 : (short) 1);
                tps.setTimeOutForTerminalSearchResult(pramValue[4] == 0x00 ? (short) 0 : (short) 1);
                tps.setTimeOutForServerSearchResult(pramValue[5] == 0x00 ? (short) 0 : (short) 1);
                tps.setUploadTypeResult(pramValue[6] == 0x00 ? (short) 0 : (short) 1);
                tps.setEnterpriseBroadcastAddress1Result(pramValue[7] == 0x00 ? (short) 0 : (short) 1);
                tps.setEnterpriseBroadcastPort1Result(pramValue[8] == 0x00 ? (short) 0 : (short) 1);
                tps.setEnterpriseBroadcastAddress2Result(pramValue[9] == 0x00 ? (short) 0 : (short) 1);
                tps.setEnterpriseBroadcastPort2Result(pramValue[10] == 0x00 ? (short) 0 : (short) 1);
                tps.setEnterpriseDomainNameSizeResult(pramValue[11] == 0x00 ? (short) 0 : (short) 1);
                tps.setEnterpriseDomainNameResult(pramValue[12] == 0x00 ? (short) 0 : (short) 1);
                tBoxParmSetRepository.save(tps);
            }
        }
       //参数设置命令执行结束，此处进一步持久化或者通知到外部接口
        _logger.info("ParmSet Ack finished:"+bean.getApplicationID()+"-"+bean.getEventID()+" >");
    }


    /**
     * 处理远程诊断响应数据
     * @param reqString 响应hex
     * @param vin vin码
     */
    public void handleDiagnosticAck(String reqString,String vin){
        _logger.info("DiagnosticAck:"+reqString);
        //
        ByteBuffer bb= PackageEntityManager.getByteBuffer(reqString);
        DataPackage dp=conversionTBox.generate(bb);
        DiagnosticCommanAck bean=dp.loadBean(DiagnosticCommanAck.class);
        DiagnosticData diagnosticData=diagnosticDataRepository.findByVinAndEventId(vin,bean.getEventID());
        //变更消息状态 不会当作失败而重试
        String statusKey=DataTool.msgCurrentStatus_preStr+vin+"-"+66+"-"+bean.getEventID();//0X42=66 ACK mid=2
        String statusValue=String.valueOf(2);//ACK mid=2
        socketRedis.saveValueString(statusKey, statusValue, DataTool.msgCurrentStatus_ttl);
        if(diagnosticData==null){
            _logger.info("no record found for vin:" + vin + "eventId:" + bean.getEventID());
        }else{
            //按位解析诊断数据
            byte[] bytes=bean.getDiagData();
            char[] datas_1=dataTool.getBitsFromByte(bytes[0]);
            char[] datas_2=dataTool.getBitsFromByte(bytes[1]);
            diagnosticData.setReceiveDate(new Date());
            diagnosticData.setHasAck((short) 1);
            //拆解bit0-bit15
            diagnosticData.setMessage1(datas_2[7] == '0' ? (short) 0 : (short) 1);
            diagnosticData.setMessage2(datas_2[6] == '0' ? (short) 0 : (short) 1);
            diagnosticData.setMessage3(datas_2[5] == '0' ? (short) 0 : (short) 1);
            diagnosticData.setMessage4(datas_2[4] == '0' ? (short) 0 : (short) 1);
            diagnosticData.setMessage5(datas_2[3] == '0' ? (short) 0 : (short) 1);
            diagnosticData.setMessage6(datas_2[2] == '0' ? (short) 0 : (short) 1);
            diagnosticData.setMessage7(datas_2[1] == '0' ? (short) 0 : (short) 1);
            diagnosticData.setMessage8(datas_2[0] == '0' ? (short) 0 : (short) 1);

            diagnosticData.setMessage9(datas_1[7] == '0' ? (short) 0 : (short) 1);
            diagnosticData.setMessage10(datas_1[6] == '0' ? (short) 0 : (short) 1);
            diagnosticData.setMessage11(datas_1[5] == '0' ? (short) 0 : (short) 1);
            diagnosticData.setMessage12(datas_1[4] == '0' ? (short) 0 : (short) 1);
            diagnosticData.setMessage13(datas_1[3] == '0' ? (short) 0 : (short) 1);
            diagnosticData.setMessage14(datas_1[2] == '0' ? (short) 0 : (short) 1);
            diagnosticData.setMessage15(datas_1[1] == '0' ? (short) 0 : (short) 1);
            diagnosticData.setMessage16(datas_1[0] == '0' ? (short) 0 : (short) 1);

            //保存Ack数据
            diagnosticDataRepository.save(diagnosticData);
        }
    }

}
