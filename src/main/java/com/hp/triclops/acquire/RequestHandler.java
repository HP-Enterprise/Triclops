package com.hp.triclops.acquire;

import com.hp.data.bean.tbox.*;
import com.hp.data.core.Conversion;
import com.hp.data.core.DataPackage;
import com.hp.data.util.PackageEntityManager;
import com.hp.triclops.entity.DiagnosticData;
import com.hp.triclops.entity.RemoteControl;
import com.hp.triclops.entity.TBoxParmSet;
import com.hp.triclops.redis.SocketRedis;
import com.hp.triclops.repository.DiagnosticDataRepository;
import com.hp.triclops.repository.TBoxParmSetRepository;
import com.hp.triclops.service.OutputHexService;
import com.hp.triclops.service.TboxService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    private Logger _logger = LoggerFactory.getLogger(RequestHandler.class);


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
            _logger.info("Active Request>>>>>");
            ByteBuffer bb= PackageEntityManager.getByteBuffer(reqString);
            DataPackage dp=conversionTBox.generate(bb);
            ActiveReq bean=dp.loadBean(ActiveReq.class);
            //请求解析到bean
            //远程唤醒响应
           _logger.info(bean.getVin() + "|" + bean.getSerialNumber());
            boolean activeResult=tboxService.activationTBox(bean.getVin(),bean.getSerialNumber());//true成功 false失败
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
            _logger.info("Active Result>>>>> no response");
                  }
        return null;
    }


    /**
     *
     * @param reqString 远程唤醒请求hex
     * @return 远程唤醒响应hex
     */
    public String getRemoteWakeUpResp(String reqString){
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
        resp.setRegisterResult((short) 0);//0唤醒成功 1唤醒失败

        DataPackage dpw=new DataPackage("8995_20_2");
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
        int result=bean.getCanActionTest()+bean.getFarmTest()+bean.getGprsTest()+bean.getGpsTest()+bean.getLedTest()+bean.getResetBatteryMapArrayTest()+bean.getSdTest()+bean.getServerCommTest();
        //电检响应
        DiaResp resp=new DiaResp();
        resp.setHead(bean.getHead());
        resp.setTestFlag(bean.getTestFlag());
        resp.setSendingTime((long) dataTool.getCurrentSeconds());
        resp.setApplicationID(bean.getApplicationID());
        resp.setMessageID((short) 2);
        resp.setEventID(bean.getEventID());
        resp.setDiaReportResp(result > 0 ? (short) 1 : (short) 0);

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
    public String getRegisterResp(String reqString,boolean checkRegister){
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
        //注册响应
        DataPackage dpw=new DataPackage("8995_19_2");
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
    public void handleRemoteControlRequest(String reqString,String vin){
        //处理远程控制上行的16进制字符串
        byte[] bytes=dataTool.getBytesFromByteBuf(dataTool.getByteBuf(reqString));
        byte messageId=dataTool.getMessageId(bytes);
        if(messageId==0x02){
            _logger.info("RemoteControlPreconditionResp handle...");
            //RemoteControlPreconditionResp
            //根据预处理响应判断是否下发控制指令
            ByteBuffer bb= PackageEntityManager.getByteBuffer(reqString);
            DataPackage dp=conversionTBox.generate(bb);
            RemoteControlPreconditionResp bean=dp.loadBean(RemoteControlPreconditionResp.class);
            //已经收到响应 变更消息状态 不会当作失败而重试
            String statusKey=DataTool.msgCurrentStatus_preStr+vin+"-"+bean.getApplicationID()+"-"+bean.getEventID();
            String statusValue=String.valueOf(bean.getMessageID());
            socketRedis.saveValueString(statusKey, statusValue, -1);
            _logger.info("update statusValue "+"statusKey:"+statusKey+"|"+"statusValue"+statusValue);
            if(verifyRemoteControlPreconditionResp(bean)){
                //符合控制逻辑 从redis取出远程控制参数 生成控制指令 save redis
                long eventId=bean.getEventID();
                RemoteControl _valueRc=outputHexService.getRemoteCmdValueFromRedis(vin,eventId);
                //取出redis暂存的控制参数 生成指令
                if(_valueRc==null){
                    _logger.info("get RemoteCmd Value From Redis return null...");
                    return;
                }
                String cmdByteString=outputHexService.getRemoteControlCmdHex(_valueRc,eventId);
                _logger.info("verify RemoteControl PreconditionResp success,we will send RemoteCommand:"+cmdByteString);
                outputHexService.saveCmdToRedis(vin, cmdByteString);
            }else{
                 outputHexService.handleRemoteControlPreconditionResp(vin,bean.getEventID());
                _logger.info("verify RemoteControl PreconditionResp failed,we will not send RemoteCommand");
            }
        }else if(messageId==0x04){
            _logger.info("RemoteControlAck handle...");
            //RemoteControlAck
            ByteBuffer bb= PackageEntityManager.getByteBuffer(reqString);
            DataPackage dp=conversionTBox.generate(bb);
            RemoteControlAck bean=dp.loadBean(RemoteControlAck.class);
            //请求解析到bean
            _logger.info("RemoteControlAck>>>>>>>>>>>" + bean.getRemoteControlAck());//0：无效 1：命令已接收
            //变更消息状态 不会当作失败而重试
            String statusKey=DataTool.msgCurrentStatus_preStr+vin+"-"+bean.getApplicationID()+"-"+bean.getEventID();
            String statusValue=String.valueOf(bean.getMessageID());
            socketRedis.saveValueString(statusKey, statusValue, -1);
            //远程控制命令执行结束，此处进一步持久化或者通知到外部接口
            outputHexService.handleRemoteControlAck(vin,bean.getEventID(),bean.getRemoteControlAck());
            _logger.info("handle remote Control Ack finished:"+bean.getApplicationID()+"-"+bean.getEventID()+" >"+bean.getRemoteControlAck());
        }else if(messageId==0x05){
            _logger.info("RemoteControlRst handle...");
            //RemoteControlRst
            ByteBuffer bb= PackageEntityManager.getByteBuffer(reqString);
            DataPackage dp=conversionTBox.generate(bb);
            RemoteControlRst bean=dp.loadBean(RemoteControlRst.class);
            //请求解析到bean
            _logger.info("RemoteControlRst>>>>>>>>>>>" + bean.getRemoteControlAck());//0：成功 1：失败
            String key="Result:"+vin+"-"+bean.getApplicationID()+"-"+bean.getEventID()+"-"+bean.getMessageID();
            //变更消息状态
            String statusKey=DataTool.msgCurrentStatus_preStr+vin+"-"+bean.getApplicationID()+"-"+bean.getEventID();
            String statusValue=String.valueOf(bean.getMessageID());
            socketRedis.saveValueString(statusKey, statusValue,-1);
            socketRedis.saveSetString(key, String.valueOf(bean.getRemoteControlAck()), -1);
            //远程控制命令执行结束，此处进一步持久化或者通知到外部接口
            outputHexService.handleRemoteControlRst(vin,bean.getEventID(),bean.getRemoteControlAck());
            _logger.info("handle remote Control Rst finished:"+bean.getApplicationID()+"-"+bean.getEventID()+" >"+bean.getRemoteControlAck());
        }else{
            _logger.info("remote control data error");
        }
    }

    /**
     * 校验,是否发送远程指令
     * @param remoteControlPreconditionResp 数据bean
     * @return 是否通过
     */
    public boolean verifyRemoteControlPreconditionResp(RemoteControlPreconditionResp remoteControlPreconditionResp){
        //目前逻辑 车速低于5km/h=上传数据500
        boolean re=false;
        if(remoteControlPreconditionResp!=null){
            if(remoteControlPreconditionResp.getVehicleSpeed()<500){
                re=true;
            }
        }
        return re;
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
        socketRedis.saveValueString(statusKey, statusValue, -1);

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
        socketRedis.saveValueString(statusKey, statusValue, -1);
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
