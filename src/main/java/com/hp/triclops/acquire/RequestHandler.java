package com.hp.triclops.acquire;

import com.hp.data.bean.tbox.*;
import com.hp.data.core.Conversion;
import com.hp.data.core.DataPackage;
import com.hp.data.util.PackageEntityManager;
import com.hp.triclops.redis.SocketRedis;
import com.hp.triclops.service.TboxService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;

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

    private Logger _logger = LoggerFactory.getLogger(RequestHandler.class);


    /**
     * 处理激活数据，包括激活请求和激活结果，上行messageId 1或3 ，对于1下行2，对于3只接收无下行
     * @param reqString
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
        resp.setRegisterResult((short)1);

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
        short registerResult = checkRegister ? (short)1 : (short)0;
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
     * @param reqString 远程控制响应hex
     * @param vin vin码
     */
    public void getRemoteControlAck(String reqString,String vin){
        //根据心跳请求的16进制字符串，生成响应的16进制字符串
        ByteBuffer bb= PackageEntityManager.getByteBuffer(reqString);
        DataPackage dp=conversionTBox.generate(bb);
        RemoteControlAck bean=dp.loadBean(RemoteControlAck.class);
        //请求解析到bean
        _logger.info("RemoteControlAck>>>>>>>>>>>" + bean.getRemoteControlAck());//0：成功 1：失败
        String key="Result:"+vin+"-"+bean.getApplicationID()+"-"+bean.getEventID()+"-"+bean.getMessageID();
        //变更消息状态
        String statusKey=DataTool.msgCurrentStatus_preStr+vin+"-"+bean.getApplicationID()+"-"+bean.getEventID();
        String statusValue=String.valueOf(bean.getMessageID());
        socketRedis.saveValueString(statusKey, statusValue,-1);
        socketRedis.saveSetString(key,String.valueOf(bean.getRemoteControlAck()),-1);
        //远程控制命令执行结束，此处进一步持久化或者通知到外部接口
    }


}
