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
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.ReferenceCountUtil;
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
    VehicleRepository vehicleRepository;
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
    @Autowired
    UploadPackageEntityRepository uploadPackageEntityRepository;
    @Autowired
    FtpSettingRepository ftpSettingRepository;
    @Autowired
    FtpDataRepository ftpDataRepository;
    @Value("${com.hp.acquire.serverId}")
    private String _serverId;//serverId集群依赖这个值
    @Value("${com.hp.web.server.url}")
    private String _serverUrl;//软件/固件升级url

    private Logger _logger = LoggerFactory.getLogger(RequestHandler.class);


    /**
     * 注册使用的aeskey来时
     *
     * @param imei
     * @return
     */
    public String getRegAesKeyByImei(String imei) {
        String key = null;
        TBox tBox = tBoxRepository.findByImei(imei);
        if (tBox != null) {
            key = tBox.getT_sn() + "" + tBox.getVin();
            key = MD5.MD5To16UpperCase(key);
        }
        return key;
    }

    /**
     * @param reqString 处理激活数据，包括激活请求和激活结果，上行messageId 1或3 ，对于1下行2，对于3只接收无下行
     * @return messageId=1返回处理后的resp,messageId=1返回null
     */
    public String getActiveHandle(String reqString) {
        //根据激活请求的16进制字符串，生成响应的16进制字符串
        long startTime = System.currentTimeMillis();
        byte[] bytes = dataTool.getBytesFromByteBuf(dataTool.getByteBuf(reqString));
        byte messageId = 0;
        if (bytes != null) {
            if (bytes.length > 10) {
                messageId = bytes[10];
            }
        }
        if (messageId == 0x01) {
            //Active Request
            _logger.info("[0x12]收到激活请求>>>>>");
            long startTime1 = System.currentTimeMillis();
            ByteBuffer bb = PackageEntityManager.getByteBuffer(reqString);
            DataPackage dp = conversionTBox.generate(bb);
            ActiveReq bean = dp.loadBean(ActiveReq.class);
            long startTime2 = System.currentTimeMillis();
            //请求解析到bean
            //远程唤醒响应
            _logger.info(bean.getVin() + "|" + bean.getSerialNumber());
            boolean activeResult = tboxService.activationTBox(bean.getVin(), bean.getVehicleModel(), bean.getSerialNumber(), bean.getImei(), bean.getIccid());//true成功 false失败
            long middleTime = System.currentTimeMillis();
            short tBoxStatus = 1; //0激活成功 1激活失败
            if (activeResult) {
                tBoxStatus = 0;
            }
            ActiveResp resp = new ActiveResp();
            resp.setHead(bean.getHead());
            resp.setTestFlag(bean.getTestFlag());
            resp.setSendingTime((long) dataTool.getCurrentSeconds());
            resp.setApplicationID(bean.getApplicationID());
            resp.setMessageID((short) 2);
            resp.setEventID(bean.getEventID());
            resp.settBoxStatus(tBoxStatus);
            resp.setVin(bean.getVin());

            DataPackage dpw = new DataPackage("8995_18_2");
            dpw.fillBean(resp);
            ByteBuffer bbw = conversionTBox.generate(dpw);
            String byteStr = PackageEntityManager.getByteString(bbw);
            long endTime = System.currentTimeMillis();
            _logger.info(bean.getVin() + " prepare total Time" + (startTime - startTime1));
            _logger.info(bean.getVin() + " get bean" + (startTime2 - startTime1));
            _logger.info(bean.getVin() + " save data total Time" + (middleTime - startTime2));
            _logger.info(bean.getVin() + " package total Time" + (endTime - middleTime));

            return byteStr;
        } else if (messageId == 0x03) {
            //Active Result
            _logger.info("[0x12]收到激活结果>>>>> 无需回复");
        }
        return null;
    }

    /**
     * @param reqString 处理激活数据，包括激活请求和激活结果，上行messageId 1或3 ，对于1下行2，对于3只接收无下行
     * @return messageId=1返回处理后的resp,messageId=1返回null
     */
    public void handerIccid(String reqString) {
        //根据字符串创建buf
        ByteBuf buf = dataTool.getByteBuf(reqString);
        try {
            //根据激活请求的16进制字符串，生成响应的16进制字符串
            byte[] bytes = dataTool.getBytesFromByteBuf(buf);
            byte messageId = 0;
            if (bytes != null) {
                if (bytes.length > 10) {
                    messageId = bytes[10];
                }
            }
            if (messageId == 0x01) {
                //Active Request
                _logger.info("[0x12]收到激活请求>>>>>");
                long startTime = System.currentTimeMillis();
                ByteBuffer bb = PackageEntityManager.getByteBuffer(reqString);
                DataPackage dp = conversionTBox.generate(bb);
                ActiveReq bean = dp.loadBean(ActiveReq.class);
                //请求解析到bean
                //远程唤醒响应
                boolean activeResult = tboxService.saveIccidPhone(bean.getVin(), bean.getImei(), bean.getIccid());//true成功 false失败
                _logger.info(bean.getVin() + "handerIccid result:" + activeResult);
            }
        } finally {
            //todo 释放buffer
            ReferenceCountUtil.release(buf);
        }
    }

    /**
     * @param reqString 远程唤醒请求hex
     * @return 远程唤醒响应hex
     */
    public String getFlowResp(String reqString) {

        //根据远程唤醒请求的16进制字符串，生成响应的16进制字符串
        ByteBuffer bb = PackageEntityManager.getByteBuffer(reqString);
        DataPackage dp = conversionTBox.generate(bb);
        FlowReq bean = dp.loadBean(FlowReq.class);
        //请求解析到bean
        //远程唤醒响应
        FlowResp resp = new FlowResp();
        resp.setHead(bean.getHead());
        resp.setTestFlag(bean.getTestFlag());
        resp.setSendingTime((long) dataTool.getCurrentSeconds());
        resp.setApplicationID(bean.getApplicationID());
        resp.setMessageID((short) 2);
        resp.setEventID(bean.getEventID());
        resp.setTotalSize(500l);
        resp.setUsedSize(123l);
        DataPackage dpw = new DataPackage("8995_21_2");
        dpw.fillBean(resp);
        ByteBuffer bbw = conversionTBox.generate(dpw);
        String byteStr = PackageEntityManager.getByteString(bbw);
        return byteStr;
    }


    /**
     * @param reqString 电检请求hex
     * @return 电检响应hex
     */
    public String getDiagResp(String reqString) {
        ByteBuf buf = dataTool.getByteBuf(reqString);
        try {
            byte[] bytes = dataTool.getBytesFromByteBuf(buf);
            byte messageId = dataTool.getMessageId(bytes);
            if (messageId == 0x01) {//电检
                _logger.info("[0x11]电检结果上报");
                //根据电检请求的16进制字符串，生成响应的16进制字符串
                ByteBuffer bb = PackageEntityManager.getByteBuffer(reqString);
                DataPackage dp = conversionTBox.generate(bb);
                DiaRequest bean = dp.loadBean(DiaRequest.class);
                //请求解析到bean
                //单项为0成功，全部为0测试成功返回0否则失败返回1
                // int result=bean.getCanActionTest()+bean.getFarmTest()+bean.getGprsTest()+bean.getGpsTest()+bean.getLedTest()+bean.getResetBatteryMapArrayTest()+bean.getSdTest()+bean.getServerCommTest();
                //电检响应
                DiaResp resp = new DiaResp();
                resp.setHead(bean.getHead());
                resp.setTestFlag(bean.getTestFlag());
                resp.setSendingTime((long) dataTool.getCurrentSeconds());
                resp.setApplicationID(bean.getApplicationID());
                resp.setMessageID((short) 2);
                resp.setEventID(bean.getEventID());
                //  resp.setDiaReportResp(result > 0 ? (short) 1 : (short) 0);

                DataPackage dpw = new DataPackage("8995_17_2");
                dpw.fillBean(resp);
                ByteBuffer bbw = conversionTBox.generate(dpw);
                String byteStr = PackageEntityManager.getByteString(bbw);
                return byteStr;
            } else if (messageId == 0x03) {//测试注册
                //根据电检请求的16进制字符串，生成响应的16进制字符串
                ByteBuffer bb = PackageEntityManager.getByteBuffer(reqString);
                DataPackage dp = conversionTBox.generate(bb);
                DiagRegisterRequest bean = dp.loadBean(DiagRegisterRequest.class);

                //请求解析到bean
                DiagRegisterResp resp = new DiagRegisterResp();
                resp.setHead(bean.getHead());
                resp.setTestFlag(bean.getTestFlag());
                resp.setSendingTime((long) dataTool.getCurrentSeconds());
                resp.setApplicationID(bean.getApplicationID());
                resp.setMessageID((short) 4);
                resp.setEventID(bean.getEventID());

                //判断数据是否存在
                boolean checkVinAndSerNum = dataTool.checkVinAndSerialNum(bean.getVin(), bean.getSerialNumber());
                if (checkVinAndSerNum) {
                    _logger.info("[0x11]电检注册成功");
                    resp.setRegisterResult((short) 0);
                } else {
                    _logger.info("[0x11]电检注册失败");
                    resp.setRegisterResult((short) 1);
                }

                DataPackage dpw = new DataPackage("8995_17_4");
                dpw.fillBean(resp);
                ByteBuffer bbw = conversionTBox.generate(dpw);
                String byteStr = PackageEntityManager.getByteString(bbw);

                return byteStr;
            } else {
                return null;
            }
        } finally {
            //todo 释放buffer
            ReferenceCountUtil.release(buf);
        }
    }

    /**
     * @param reqString     注册请求数据hex
     * @param checkRegister 注册是否通过
     * @return 响应hex
     */
    public String getRegisterResp(String reqString, String vin, boolean checkRegister, String model) {
        //根据注册请求的16进制字符串，生成响应的16进制字符串
        ByteBuffer bb = PackageEntityManager.getByteBuffer(reqString);
        DataPackage dp = conversionTBox.generate(bb);
        RegisterReq registerReq;
        RegisterF70Req registerF70Req;
        Integer head;
        Short testFlag;
        Short applicationId;
        Long eventID;
        //F70
        if ("4".equals(model)) {
            registerF70Req = dp.loadBean(RegisterF70Req.class);
            head = registerF70Req.getHead();
            testFlag = registerF70Req.getTestFlag();
            applicationId = registerF70Req.getApplicationID();
            eventID = registerF70Req.getEventID();
        } else {
            registerReq = dp.loadBean(RegisterReq.class);
            head = registerReq.getHead();
            testFlag = registerReq.getTestFlag();
            applicationId = registerReq.getApplicationID();
            eventID = registerReq.getEventID();
        }

        //请求解析到bean
        RegisterResp resp = new RegisterResp();
        resp.setHead(head);
        resp.setTestFlag(testFlag);
        resp.setSendingTime((long) dataTool.getCurrentSeconds());
        resp.setApplicationID(applicationId);
        resp.setMessageID((short) 2);
        resp.setEventID(eventID);
        short registerResult = checkRegister ? (short) 0 : (short) 1;
        resp.setRegisterResult(registerResult);
        resp.setTotalSize(500l);
        resp.setUsedSize(123l);
        String randomKey = "0123456789abcdef";
        // randomKey=dataTool.getRandomString(16);
        if (checkRegister) {
            _logger.info("[0x13]注册时给vin:" + vin + "生成的AES key:" + randomKey);
            socketRedis.saveHashString(dataTool.tboxkey_hashmap_name, vin, randomKey, -1);
            //更新车型信息
            modifyVehicleInfo(vin, Short.parseShort(model));
        }
        resp.setKeyInfo(randomKey.getBytes());
        //注册响应
        DataPackage dpw = new DataPackage("8995_19_2");
        dpw.fillBean(resp);
        ByteBuffer bbw = conversionTBox.generate(dpw);
        String byteStr = PackageEntityManager.getByteString(bbw);
        return byteStr;
    }

    public String getRegisterResp(ByteBuf bb) {
        //请求解析到bean
        RegisterResp resp = new RegisterResp();
        resp.setHead(bb.readUnsignedShort());
        resp.setLength(40);
        bb.skipBytes(2);
        resp.setTestFlag(bb.readUnsignedByte());
        resp.setSendingTime(bb.readUnsignedInt());
        resp.setApplicationID(bb.readUnsignedByte());
        resp.setMessageID(((Integer) 0x02).shortValue());
        bb.skipBytes(1);
        resp.setEventID(bb.readUnsignedInt());
        resp.setRegisterResult((short) 1);
        resp.setTotalSize(500l);
        resp.setUsedSize(123l);
        String randomKey = "0123456789abcdef";
        resp.setKeyInfo(randomKey.getBytes());
        //注册响应
        DataPackage dpw = new DataPackage("8995_19_2");
        dpw.fillBean(resp);
        ByteBuffer bbw = conversionTBox.generate(dpw);
        String byteStr = PackageEntityManager.getByteString(bbw);
        return byteStr;
    }


    public void modifyVehicleInfo(String vin, Short modelId) {
        tboxService.modifyVehicleModelInfo(vin, modelId);
    }

    /**
     * @param reqString             远程唤醒请求hex
     * @param checkVinAndSerNumWake 唤醒校验
     * @return 远程唤醒响应hex
     */
    public String getRemoteWakeUpResp(String reqString, String vin, boolean checkVinAndSerNumWake) {

        //根据远程唤醒请求的16进制字符串，生成响应的16进制字符串
        ByteBuffer bb = PackageEntityManager.getByteBuffer(reqString);
        DataPackage dp = conversionTBox.generate(bb);
        RemoteWakeUpReq bean = dp.loadBean(RemoteWakeUpReq.class);
        //请求解析到bean
        //远程唤醒响应
        RemoteWakeUpResp resp = new RemoteWakeUpResp();
        resp.setHead(bean.getHead());
        resp.setTestFlag(bean.getTestFlag());
        resp.setSendingTime((long) dataTool.getCurrentSeconds());
        resp.setApplicationID(bean.getApplicationID());
        resp.setMessageID((short) 2);
        resp.setEventID(bean.getEventID());
        short wakeUpResult = checkVinAndSerNumWake ? (short) 0 : (short) 1;
        resp.setRegisterResult(wakeUpResult);//0唤醒成功 1唤醒失败
        resp.setTotalSize(500l);
        resp.setUsedSize(123l);
        String randomKey = "0123456789abcdef";
        // randomKey=dataTool.getRandomString(16);
        if (checkVinAndSerNumWake) {
            _logger.info("[0x13]注册时给vin:" + vin + "生成的AES key:" + randomKey);
            socketRedis.saveHashString(dataTool.tboxkey_hashmap_name, vin, randomKey, -1);
        }
        resp.setKeyInfo(randomKey.getBytes());

        DataPackage dpw = new DataPackage("8995_20_2");
        dpw.fillBean(resp);
        ByteBuffer bbw = conversionTBox.generate(dpw);
        String byteStr = PackageEntityManager.getByteString(bbw);
        return byteStr;
    }

    /**
     * @param reqString 心跳请求hex
     * @return 心跳响应hex
     */
    public String getHeartbeatResp(String reqString) {
        //根据心跳请求的16进制字符串，生成响应的16进制字符串
        ByteBuffer bb = PackageEntityManager.getByteBuffer(reqString);
        DataPackage dp = conversionTBox.generate(bb);
        HeartbeatReq bean = dp.loadBean(HeartbeatReq.class);
        //请求解析到bean
        HeartbeatResp resp = new HeartbeatResp();
        resp.setHead(bean.getHead());
        resp.setTestFlag(bean.getTestFlag());
        resp.setSendingTime((long) dataTool.getCurrentSeconds());
        resp.setApplicationID(bean.getApplicationID());
        resp.setMessageID((short) 2);
        resp.setEventID(bean.getEventID());
        //响应
        DataPackage dpw = new DataPackage("8995_38_2");
        dpw.fillBean(resp);
        ByteBuffer bbw = conversionTBox.generate(dpw);
        String byteStr = PackageEntityManager.getByteString(bbw);
        return byteStr;
    }

    /**
     * @return 解密失败报告hex
     */
    public String getInvalidReport() {
        InvalidReport report = new InvalidReport();
        report.setTestFlag((short) 0);
        report.setSendingTime((long) dataTool.getCurrentSeconds());
        report.setApplicationID((short) 97);//>>>
        report.setMessageID((short) 1);//>>>
        report.setEventID(report.getSendingTime());
        //响应
        DataPackage dpw = new DataPackage("8995_97_1");
        dpw.fillBean(report);
        ByteBuffer bbw = conversionTBox.generate(dpw);
        String byteStr = PackageEntityManager.getByteString(bbw);
        return byteStr;
    }

    /**
     * @param reqString 实时数据请求hex
     * @return 实时数据响应hex
     */
    public String getRealTimeDataResp(String reqString) {
        //根据心跳请求的16进制字符串，生成响应的16进制字符串
        ByteBuffer bb = PackageEntityManager.getByteBuffer(reqString);
        DataPackage dp = conversionTBox.generate(bb);
        RealTimeReportMes bean = dp.loadBean(RealTimeReportMes.class);
        //请求解析到bean
        RealTimeReportMesResp resp = new RealTimeReportMesResp();
        resp.setHead(bean.getHead());
        resp.setTestFlag(bean.getTestFlag());
        resp.setSendingTime((long) dataTool.getCurrentSeconds());
        resp.setApplicationID(bean.getApplicationID());
        resp.setMessageID((short) 2);
        resp.setEventID(bean.getEventID());
        //响应
        DataPackage dpw = new DataPackage("8995_34_2");
        dpw.fillBean(resp);
        ByteBuffer bbw = conversionTBox.generate(dpw);
        String byteStr = PackageEntityManager.getByteString(bbw);
        return byteStr;
    }

    /**
     * @param reqString 补发实时数据请求hex
     * @return 补发实时数据响应hex
     */
    public String getResendRealTimeDataResp(String reqString) {
        //根据心跳请求的16进制字符串，生成响应的16进制字符串
        ByteBuffer bb = PackageEntityManager.getByteBuffer(reqString);
        DataPackage dp = conversionTBox.generate(bb);
        DataResendRealTimeMes bean = dp.loadBean(DataResendRealTimeMes.class);
        //请求解析到bean
        DataResendRealTimeMesResp resp = new DataResendRealTimeMesResp();
        resp.setHead(bean.getHead());
        resp.setTestFlag(bean.getTestFlag());
        resp.setSendingTime((long) dataTool.getCurrentSeconds());
        resp.setApplicationID(bean.getApplicationID());
        resp.setMessageID((short) 2);
        resp.setEventID(bean.getEventID());
        //响应
        DataPackage dpw = new DataPackage("8995_35_2");
        dpw.fillBean(resp);
        ByteBuffer bbw = conversionTBox.generate(dpw);
        String byteStr = PackageEntityManager.getByteString(bbw);
        return byteStr;
    }

    /**
     * @param reqString 报警数据请求hex
     * @return 报警数据响应hex
     */
    public String getWarningMessageResp(String reqString) {
        //根据心跳请求的16进制字符串，生成响应的16进制字符串
        ByteBuffer bb = PackageEntityManager.getByteBuffer(reqString);
        WarningMessage bean = dataTool.decodeWarningMessage(reqString);
        //请求解析到bean
        WarningMessageResp resp = new WarningMessageResp();
        resp.setHead(bean.getHead());
        resp.setTestFlag(bean.getTestFlag());
        resp.setSendingTime((long) dataTool.getCurrentSeconds());
        resp.setApplicationID(bean.getApplicationID());
        resp.setMessageID((short) 2);
        resp.setEventID(bean.getEventID());
        //响应
        DataPackage dpw = new DataPackage("8995_36_2");
        dpw.fillBean(resp);
        ByteBuffer bbw = conversionTBox.generate(dpw);
        String byteStr = PackageEntityManager.getByteString(bbw);
        return byteStr;
    }

    /**
     * @param reqString 补发报警数据请求hex
     * @return 补发报警数据响应hex
     */
    public String getDataResendWarningDataResp(String reqString) {
        //根据心跳请求的16进制字符串，生成响应的16进制字符串
        ByteBuffer bb = PackageEntityManager.getByteBuffer(reqString);
        DataResendWarningMes bean = dataTool.decodeResendWarningMessage(reqString);
        //请求解析到bean
        DataResendWarningMesResp resp = new DataResendWarningMesResp();
        resp.setHead(bean.getHead());
        resp.setTestFlag(bean.getTestFlag());
        resp.setSendingTime((long) dataTool.getCurrentSeconds());
        resp.setApplicationID(bean.getApplicationID());
        resp.setMessageID((short) 2);
        resp.setEventID(bean.getEventID());
        //响应
        DataPackage dpw = new DataPackage("8995_37_2");
        dpw.fillBean(resp);
        ByteBuffer bbw = conversionTBox.generate(dpw);
        String byteStr = PackageEntityManager.getByteString(bbw);
        return byteStr;
    }


    /**
     * @param reqString 故障数据请求hex
     * @return 故障数据响应hex
     */
    public String getFailureDataResp(String reqString) {
        //根据心跳请求的16进制字符串，生成响应的16进制字符串
        ByteBuffer bb = PackageEntityManager.getByteBuffer(reqString);
        DataPackage dp = conversionTBox.generate(bb);
        FailureMessage bean = dp.loadBean(FailureMessage.class);
        //请求解析到bean
        FailureMessageResp resp = new FailureMessageResp();
        resp.setHead(bean.getHead());
        resp.setTestFlag(bean.getTestFlag());
        resp.setSendingTime((long) dataTool.getCurrentSeconds());
        resp.setApplicationID(bean.getApplicationID());
        resp.setMessageID((short) 2);
        resp.setEventID(bean.getEventID());
        //响应
        DataPackage dpw = new DataPackage("8995_40_2");
        dpw.fillBean(resp);
        ByteBuffer bbw = conversionTBox.generate(dpw);
        String byteStr = PackageEntityManager.getByteString(bbw);
        return byteStr;
    }

    /**
     * @param reqString 补发故障数据请求hex
     * @return 补发故障数据响应hex
     */
    public String getResendFailureDataResp(String reqString) {
        //根据心跳请求的16进制字符串，生成响应的16进制字符串
        ByteBuffer bb = PackageEntityManager.getByteBuffer(reqString);
        DataPackage dp = conversionTBox.generate(bb);
        DataResendFailureData bean = dp.loadBean(DataResendFailureData.class);
        //请求解析到bean
        DataResendFailureDataResp resp = new DataResendFailureDataResp();
        resp.setHead(bean.getHead());
        resp.setTestFlag(bean.getTestFlag());
        resp.setSendingTime((long) dataTool.getCurrentSeconds());
        resp.setApplicationID(bean.getApplicationID());
        resp.setMessageID((short) 2);
        resp.setEventID(bean.getEventID());
        //响应
        DataPackage dpw = new DataPackage("8995_41_2");
        dpw.fillBean(resp);
        ByteBuffer bbw = conversionTBox.generate(dpw);
        String byteStr = PackageEntityManager.getByteString(bbw);
        return byteStr;
    }

    /**
     * @param reqString 行为数据请求hex
     * @return 补发故障数据响应hex
     */
    public String getDrivingBehaviorMesResp(String reqString) {
        //根据心跳请求的16进制字符串，生成响应的16进制字符串

        DrivingBehaviorMes bean = dataTool.decodeDrivingBehaviorMes(reqString);
        //请求解析到bean
        DrivingBehaviorAck resp = new DrivingBehaviorAck();
        resp.setHead(bean.getHead());
        resp.setTestFlag(bean.getTestFlag());
        resp.setSendingTime((long) dataTool.getCurrentSeconds());
        resp.setApplicationID(bean.getApplicationID());
        resp.setMessageID((short) 2);
        resp.setEventID(bean.getEventID());
        //响应
        DataPackage dpw = new DataPackage("8995_42_2");
        dpw.fillBean(resp);
        ByteBuffer bbw = conversionTBox.generate(dpw);
        String byteStr = PackageEntityManager.getByteString(bbw);
        return byteStr;
    }

    /**
     * @param reqString 休眠请求hex
     * @return 休眠响应hex
     */
    public String getSleepResp(String reqString) {
        //根据心跳请求的16进制字符串，生成响应的16进制字符串
        ByteBuffer bb = PackageEntityManager.getByteBuffer(reqString);
        DataPackage dp = conversionTBox.generate(bb);
        SleepReq bean = dp.loadBean(SleepReq.class);
        //请求解析到bean
        SleepResp resp = new SleepResp();
        resp.setHead(bean.getHead());
        resp.setTestFlag(bean.getTestFlag());
        resp.setSendingTime((long) dataTool.getCurrentSeconds());
        resp.setApplicationID(bean.getApplicationID());
        resp.setMessageID((short) 2);
        resp.setEventID(bean.getEventID());
        //响应
        DataPackage dpw = new DataPackage("8995_39_2");
        dpw.fillBean(resp);
        ByteBuffer bbw = conversionTBox.generate(dpw);
        String byteStr = PackageEntityManager.getByteString(bbw);
        return byteStr;
    }

    /**
     * @param reqString 远程控制上行hex mid=2,4,5
     * @param vin       vin码
     */
    public void handleRemoteControlRequest(String reqString, String vin, int maxDistance) {
        //转换bytebuf
        ByteBuf buf = dataTool.getByteBuf(reqString);
        try {
            //处理远程控制上行的16进制字符串
            byte[] bytes = dataTool.getBytesFromByteBuf(buf);
            byte messageId = dataTool.getMessageId(bytes);
            if (messageId == 0x02) {
                _logger.info("[0x31]远程控制Precondition响应处理...");
                //RemoteControlPreconditionResp
                //根据预处理响应判断是否下发控制指令
                ByteBuffer bb = PackageEntityManager.getByteBuffer(reqString);
                DataPackage dp = conversionTBox.generate(bb);
                RemoteControlPreconditionResp bean = dp.loadBean(RemoteControlPreconditionResp.class);
                //已经收到响应 变更消息状态 不会当作失败而重试
                String statusKey = DataTool.msgCurrentStatus_preStr + vin + "-" + bean.getApplicationID() + "-" + bean.getEventID();
                String statusValue = String.valueOf(bean.getMessageID());
                //todo 检查此事件是否做过对应的处理，防止重复报文造成影响
                String currentStatus = socketRedis.getValueString(statusKey);
                if (currentStatus != null) {
                    if (currentStatus.equals(statusValue)) {
                        //redis记录的该事件当前值等于此报文携带的业务信息，我们认为这是重复报文，不处理
                        _logger.info("[0x31]redis记录的该事件当前状态值等于此报文MID，我们认为这是重复报文，不处理..." + vin + "|" + bean.getEventID());
                        return;
                    }
                }
                socketRedis.saveValueString(statusKey, statusValue, DataTool.msgCurrentStatus_ttl);
                RemoteControl dbRc = outputHexService.getRemoteControlRecord(vin, bean.getEventID());
                if (dbRc == null) {
                    _logger.info("[0x31]通过vin和EventId没有找到对应的远程控制记录..." + vin + "--" + bean.getEventID());
                    return;
                }
                _logger.info("[0x31] " + dbRc.getId() + ":变更状态标记 " + "statusKey:" + statusKey + "|" + "statusValue" + statusValue);
                //取出redis暂存的控制参数 生成指令
                long currentRefId = dbRc.getRefId();
                _logger.info("[0x31][debug] 引用记录ID:" + currentRefId);
                int preconditionRespCheck = 0;

                if (dbRc.getControlType() == 0 && dbRc.getRefId() == -1) {
                    //初始的启动发动机命令，虽然数据库存储的是IsAnnounce=0，但是我们要当作IsAnnounce=1即FD去做preconditionCheck
                    preconditionRespCheck = verifyRemoteControlPreconditionResp(vin, bean, dbRc.getControlType(), (short) 1);
                } else {//所有流程都做check
                    preconditionRespCheck = verifyRemoteControlPreconditionResp(vin, bean, dbRc.getControlType(), dbRc.getIsAnnounce());
                }
                _logger.info("[0x31][debug] precondition响应校验结果:" + preconditionRespCheck);
                //0 通过   1~6 各种异常
                //boolean distanceCheck=verifyRemoteControlDistance(vin, bean.getEventID(),maxDistance);//app与tbox距离校验
                dbRc.setRemoteStartedCount(dataTool.getRemoteStartedCount(bean.getStat_remote_start()));
                outputHexService.modifyOrignalRemoteControl(dbRc);
                String msg = "";
                String msgEn = "";
                boolean fcCheckPass = false;
                short _startedCount = dataTool.getRemoteStartedCount(bean.getStat_remote_start());
                if (preconditionRespCheck == 0 && dbRc.getControlType() != 0) {//远程启动以外的 preconditionRespCheck=0为通过
                    fcCheckPass = true;
                } else if (preconditionRespCheck == 0 && dbRc.getControlType() == 0) {//远程启动以外的 preconditionRespCheck=0为通过
                    if (_startedCount >= 2) {//FD要求检查次数 RefId()==-1 是初始命令
                        fcCheckPass = false;
                        //                    msg="发动机启动次数已超出2次，启动请求无效";
                        //                    msgEn="Engine start number exceeded 2 times, invalid startup request";
                        msg = "远程指令未执行，由于发动机启动已超出允许次数2次。";
                        msgEn = "remote command not implemented, due to engine start number exceeded 2 times.";
                    } else {
                        fcCheckPass = true;//还可以做远程启动发动机
                    }

                }

                if (fcCheckPass) {//todo 判断为precondition检查通过
                    if (dbRc.getControlType() == 0 && currentRefId == -1) {
                        //如果是初始的启动发动机命令，需要在precondition成功后构造一个关联的announce发动操作。
                        long refId = dbRc.getId();//原发动命令
                        //更新原来的命令eventId，后续通过id找回,将原始命令（比如开空调）的refId标识为-2，后续不必再进行precondition检查
                        outputHexService.modifyRemoteControl(dbRc);
                        RemoteControl rc = outputHexService.getStartEngineRemoteControl(dbRc.getUid(), vin, bean.getEventID(), refId, (short) 1);
                        //先发出一条Announce的启动发动机命令
                        String cmdByteString = outputHexService.getRemoteControlCmdHex(rc, bean.getEventID());
                        _logger.info("[0x31]即将发送一条关联的启动发动机命令(announce):" + cmdByteString + ",它完成后的后续任务是id+" + refId);
                        outputHexService.saveCmdToRedis(_serverId, vin, cmdByteString);
                    } else {//其他类别的控制按照之前的逻辑处理即可
                        //符合控制逻辑 从redis取出远程控制参数 生成控制指令 save redis
                        long eventId = bean.getEventID();
                        String cmdByteString = outputHexService.getRemoteControlCmdHex(dbRc, eventId);
                        _logger.info("[0x31]Precondition响应校验通过,即将下发控制指令:" + cmdByteString);
                        outputHexService.saveCmdToRedis(_serverId, vin, cmdByteString);
                    }

                } else {

                    if (preconditionRespCheck == 0x10) {
                        //                    msg="远程启动发动机条件不符合,检查电源开关";
                        //                    msgEn="emote start engine conditions do not meet，Check power switch";
                        msg = "远程指令未执行，由于启动按键未处于OFF档。";
                        msgEn = "remote command not implemented, due to the start button is not in OFF.";
                    } else if (preconditionRespCheck == 0x11) {
                        //                    msg="远程启动发动机条件不符合，检查危险警告灯";
                        //                    msgEn="emote start engine conditions do not meet，Check hazard warning light";
                        msg = "远程指令未执行，由于危险警告灯未关闭。";
                        msgEn = "remote command not implemented, due to the danger warning light not truned off.";
                    } else if (preconditionRespCheck == 0x12) {
                        msg = "远程指令未执行，由于档位未处于P档。";
                        msgEn = "remote command not implemented, due to the gear position is not in P.";
                    } else if (preconditionRespCheck == 0x1A) {
                        //                    msg="远程启动发动机条件不符合，检查档位";
                        //                    msgEn="emote start engine conditions do not meet，Check gear";
                        msg = "远程指令未执行，由于档位未处于N档。";
                        msgEn = "remote command not implemented, due to the gear position is not in N.";
                    } else if (preconditionRespCheck == 0x13) {
                        //                    msg="远程启动发动机条件不符合，检查车门";
                        //                    msgEn="emote start engine conditions do not meet，Check door";
                        msg = "远程指令未执行，由于车门未关闭";
                        msgEn = "remote command not implemented, due to the door is not closed.";
                    } else if (preconditionRespCheck == 0x14) {
                        //                    msg="远程启动发动机条件不符合，检查后备箱";
                        //                    msgEn="emote start engine conditions do not meet，Check trunk";
                        msg = "远程指令未执行，由于后备箱未关闭。";
                        msgEn = "remote command not implemented, due to the trunk is not closed.";
                    } else if (preconditionRespCheck == 0x15) {
                        //                    msg="远程启动发动机条件不符合，检查引擎盖";
                        //                    msgEn="emote start engine conditions do not meet，Check engine cover";
                        msg = "远程指令未执行，由于前舱盖未关闭。";
                        msgEn = "remote command not implemented, due to the bonnet is not closed.";
                    } else if (preconditionRespCheck == 0x16) {
                        //                    msg="远程启动发动机条件不符合，车速不符合";
                        //                    msgEn="emote start engine conditions do not meet，Speed does not match";
                        msg = "远程指令未执行，由于车速不满足条件。";
                        msgEn = "remote command not implemented, due to the speed is not match.";
                    } else if (preconditionRespCheck == 0x17) {
                        //                    msg="远程启动发动机条件不符合，检查中控锁";
                        //                    msgEn="emote start engine conditions do not meet，Check the lock";
                        msg = "远程指令未执行，由于车锁未关闭。";
                        msgEn = "remote command not implemented, due to the central lock is not closed.";
                    } else if (preconditionRespCheck == 0x18) {
                        msg = "远程启动发动机条件不符合，检查手刹";
                        msgEn = "emote start engine conditions do not meet，check parking brake adjustment";
                    } else if (preconditionRespCheck == 0x19) {
                        msg = "远程启动发动机条件不符合，发动机存在故障";
                        msgEn = "emote start engine conditions do not meet，Engine trouble";
                    } else if (preconditionRespCheck == 0x1E) {
                        //                    msg="发动机启动次数已超出2次，启动请求无效";
                        //                    msgEn="Engine start number exceeded 2 times, invalid startup request";
                        msg = "远程指令未执行，由于发动机启动已超出允许次数2次。";
                        msgEn = "remote command not implemented, due to engine start number exceeded 2 times.";
                    } else if (preconditionRespCheck == 0x1F) {
                        msg = "发动机已启动";
                        msgEn = "The engine is started";
                    }

                    if (preconditionRespCheck == 2) {
                        msg = "远程关闭发动机失败,必须是远程启动发动机才能远程关闭";
                        msgEn = "Remote shutdown of the engine fails, it must be remote start the engine to remote shutdown";
                    }
                    //                if(preconditionRespCheck==3){
                    //                    msg="操作条件不符合，请检查车辆状态";
                    //                    msgEn="Operating conditions do not meet, please check the state of the vehicle";
                    //                }
                    if (preconditionRespCheck == 0x31) {
                        msg = "远程指令未执行，由于车门未关闭";
                        msgEn = "remote command not implemented, due to the door is not closed.";
                    } else if (preconditionRespCheck == 0x32) {
                        msg = "远程指令未执行，由于后备箱未关闭。";
                        msgEn = "remote command not implemented, due to the trunk is not closed.";
                    } else if (preconditionRespCheck == 0x33) {
                        msg = "远程指令未执行，由于前舱盖未关闭。";
                        msgEn = "remote command not implemented, due to the bonnet is not closed.";
                    } else if (preconditionRespCheck == 0x34) {
                        msg = "远程指令未执行，由于启动按键未处于OFF档。";
                        msgEn = "remote command not implemented, due to the start button is not in OFF.";
                    } else if (preconditionRespCheck == 0x35) {
                        msg = "远程指令未执行，由于车锁未关闭。";
                        msgEn = "remote command not implemented, due to the central lock is not closed.";
                    }
                    if (preconditionRespCheck == 300) {
                        msg = "发动机已启动，不允许解锁";
                        msgEn = "Engine has been started, not allowed to unlock";
                    }
                    if (preconditionRespCheck == 4 || preconditionRespCheck == 5 || preconditionRespCheck == 6 || preconditionRespCheck == 7) {
                        //todo 生成启动发动机命令,建立关联关系
                        if (currentRefId == -1) {
                            long refId = dbRc.getId();
                            //更新原来的命令eventId，后续通过id找回,将原始命令（比如开空调）的refId标识为-2，后续不必再进行precondition检查
                            outputHexService.modifyRemoteControl(dbRc);
                            RemoteControl rc = outputHexService.getStartEngineRemoteControl(dbRc.getUid(), vin, bean.getEventID(), refId, (short) 1);
                            //先发出一条Announce的启动发动机命令
                            String cmdByteString = outputHexService.getRemoteControlCmdHex(rc, bean.getEventID());
                            _logger.info("[0x31]即将发送一条关联的启动发动机命令(announce):" + cmdByteString);
                            outputHexService.saveCmdToRedis(_serverId, vin, cmdByteString);
                        } else {
                            _logger.info("[0x31]命令已经存在关联的远程控制记录->" + currentRefId);
                        }
                    }

                    //                if(preconditionRespCheck == 10 || preconditionRespCheck == 11){
                    //                    msg="远程寻车失败，操作条件不满足";
                    //                    msgEn="Remote search failed, the operating conditions are not satisfied";
                    //                }
                    if (preconditionRespCheck == 0x101) {
                        msg = "远程指令未执行，由于车门未关闭";
                        msgEn = "remote command not implemented, due to the door is not closed.";
                    } else if (preconditionRespCheck == 0x102) {
                        msg = "远程指令未执行，由于后备箱未关闭。";
                        msgEn = "remote command not implemented, due to the trunk is not closed.";
                    } else if (preconditionRespCheck == 0x103) {
                        msg = "远程指令未执行，由于前舱盖未关闭。";
                        msgEn = "remote command not implemented, due to the bonnet is not closed.";
                    } else if (preconditionRespCheck == 0x104) {
                        msg = "远程指令未执行，由于启动按键未处于OFF档。";
                        msgEn = "remote command not implemented, due to the start button is not in OFF.";
                    } else if (preconditionRespCheck == 0x105) {
                        msg = "远程指令未执行，由于车锁未关闭。";
                        msgEn = "remote command not implemented, due to the central lock is not closed.";
                    }
                    if (preconditionRespCheck == 300) {
                        msg = "发动机已启动，不允许解锁";
                        msgEn = "Engine has been started, not allowed to unlock";
                    }
                    if (preconditionRespCheck == 11) {
                        msg = "远程指令未执行，操作条件不满足";
                        msgEn = "remote command not implemented, the operating conditions are not satisfied";
                    }
                    if (preconditionRespCheck == 12) {
                        msg = "远程指令未执行，操作条件不满足";
                        msgEn = "remote command not implemented, the operating conditions are not satisfied";
                    }
                    if ((preconditionRespCheck == 4 || preconditionRespCheck == 5 || preconditionRespCheck == 6 || preconditionRespCheck == 7) && currentRefId != -2) {
                        if (currentRefId == -1) {//-1的情况
                            _logger.info("[0x31]正在尝试启动发动机...");
                        }
                    } else {//除了4 5 6 7之外的失败会导致流程结束，而4 5 6 7会尝试启动发动机，当然，如果 4 5 6 7已经有尝试过启动发动机记录，则流程结束
                        if (currentRefId > 0) {//存在ref记录
                            outputHexService.handleRemoteControlPreconditionResp(vin, bean.getEventID(), msg, msgEn, false);//关联子操作 不推送
                            String pre = "依赖的操作失败:";
                            String preEn = "Dependent operation failure :";
                            outputHexService.updateRefRemoteControlRst(currentRefId, pre + msg, preEn + msgEn);//原始记录推送
                        } else {
                            outputHexService.handleRemoteControlPreconditionResp(vin, bean.getEventID(), msg, msgEn, true);//普通单条，推送
                        }

                        _logger.info("[0x31]Precondition响应校验未通过,无法下发控制指令。");
                    }
                }
            } else if (messageId == 0x04) {
                _logger.info("[0x31]收到远程控制应答,开始处理...");
                //RemoteControlAck
                ByteBuffer bb = PackageEntityManager.getByteBuffer(reqString);
                DataPackage dp = conversionTBox.generate(bb);
                RemoteControlAck bean = dp.loadBean(RemoteControlAck.class);
                //请求解析到bean
                _logger.info("[0x31]远程控制应答:>" + bean.getRemoteControlAck() + " (参考值 0:无效 1:命令已接收)");//0：无效 1：命令已接收
                //变更消息状态 不会当作失败而重试
                String statusKey = DataTool.msgCurrentStatus_preStr + vin + "-" + bean.getApplicationID() + "-" + bean.getEventID();
                String statusValue = String.valueOf(bean.getMessageID());
                socketRedis.saveValueString(statusKey, statusValue, DataTool.msgCurrentStatus_ttl);
                //todo 《失败时》需要判断是否存在ref控制指令（常见ref：远程启动空调需要远程启动发动机），如果存在这种情况，需要找到原始指令，更新失败原因
                RemoteControl rc = outputHexService.getRemoteControlRecord(vin, bean.getEventID());
                if (rc == null) {
                    _logger.info("[0x31]通过vin和eventId没有在数据库找到控制记录..." + vin + "|" + bean.getEventID());
                    return;
                }
                long refId = rc.getRefId();
                if (bean.getRemoteControlAck() == (short) 0) {//无效
                    //远程控制命令执行结束，此处进一步持久化或者通知到外部接口
                    if (refId > 0) {//存在ref记录
                        outputHexService.handleRemoteControlAck(vin, bean.getEventID(), bean.getRemoteControlAck(), false);
                        outputHexService.handleRefRemoteControlAck(refId, bean.getRemoteControlAck());
                    } else {
                        outputHexService.handleRemoteControlAck(vin, bean.getEventID(), bean.getRemoteControlAck(), true);
                    }
                }
                _logger.info("[0x31]远程控制应答处理结束:" + bean.getApplicationID() + "-" + bean.getEventID() + " >" + bean.getRemoteControlAck());
            } else if (messageId == 0x05) {
                _logger.info("[0x31]收到远程控制结果,开始处理...");
                //RemoteControlRst
                ByteBuffer bb = PackageEntityManager.getByteBuffer(reqString);
                DataPackage dp = conversionTBox.generate(bb);
                RemoteControlRst bean = dp.loadBean(RemoteControlRst.class);
                //请求解析到bean
                _logger.info("[0x31]收到远程控制结果：>" + bean.getRemoteControlAck() + " (参考值 0：成功 1：失败 ...大于1:其它文档中定义的原因)");//0：成功 1：失败
                String key = "Result:" + vin + "-" + bean.getApplicationID() + "-" + bean.getEventID() + "-" + bean.getMessageID();
                //变更消息状态
                String statusKey = DataTool.msgCurrentStatus_preStr + vin + "-" + bean.getApplicationID() + "-" + bean.getEventID();
                String statusValue = String.valueOf(bean.getMessageID());
                //todo 检查此事件是否做过对应的处理，防止重复报文造成影响
                String currentStatus = socketRedis.getValueString(statusKey);
                if (currentStatus != null) {
                    if (currentStatus.equals(statusValue)) {
                        //redis记录的该事件当前值等于此报文携带的业务信息，我们认为这是重复报文，不处理
                        _logger.info("[0x31]redis记录的该事件当前状态值等于此报文MID，我们认为这是重复报文，不处理..." + vin + "|" + bean.getEventID());
                        return;
                    }
                }
                socketRedis.saveValueString(statusKey, statusValue, DataTool.msgCurrentStatus_ttl);
                //socketRedis.saveSetString(key, String.valueOf(bean.getRemoteControlAck()), -1);
                //远程控制命令执行结束，此处进一步持久化或者通知到外部接口
                //todo 需要判断是否存在ref控制指令（常见ref：远程启动空调需要远程启动发动机），如果存在这种情况，需要找到原始指令，参照0x02 resp处理下发
                //todo 存在ref  成功:找到remote记录，下发0x03命令  失败：持久化失败消息
                //todo 不存在ref 成功或者失败 持久化消息
                RemoteControl rc = outputHexService.getRemoteControlRecord(vin, bean.getEventID());
                if (rc == null) {
                    _logger.info("[0x31]通过vin和eventId没有在数据库找到控制记录..." + vin + "|" + bean.getEventID());
                    return;
                }
                long refId = rc.getRefId();
                if (bean.getRemoteControlAck() == (short) 0 || bean.getRemoteControlAck() == 0x80) {//成功了 0x80响应时，暂时判断为发动机启动成功
                    //符合控制逻辑 从redis取出远程控制参数 生成控制指令 save redis
                    //RemoteControl _valueRc=outputHexService.getRemoteCmdValueFromRedis(vin,eventId);
                    //取出redis暂存的控制参数 生成指令
                    if (bean.getRemoteControlAck() == 0x80) {
                        _logger.info("[0x31]0x80响应时，暂时判断为发动机启动成功");
                    }


                    if (refId > 0) {//todo refId大于0的情况是启动发动机或者需要依赖启动发动机的场景
                        outputHexService.handleRemoteControlRst(vin, bean.getEventID(), rc.getControlType(), (short) 0, bean.getRemoteControlTime(), rc.getIsAnnounce(), false);
                        //存在ref记录
                        if (rc.getIsAnnounce() == 1) {//刚刚结束的是1条发动机announce命令，现在我们需要做一个真正的启动发动机,将指向的那条原始记录refId传递给perform命令
                            _logger.info("[0x31]刚刚结束的是1条发动机FC命令，现在我们需要做一个真正的启动发动机（FD）");
                            RemoteControl orginalCommand = outputHexService.getRemoteControlRecord(refId);
                            //todo 这里需要注意了
                            if (orginalCommand.getControlType() == (short) 0) {
                                //todo 如果原始命令是启动发动机，则继续处理原始命令(perform)，判断的标准就是原始命令的refId对应的记录的信息
                                _logger.info("[0x31]关联的启动发动机执行成功，开始执行原始控制指令.id=" + refId);
                                new RemoteCommandSender(vehicleDataService, refId, rc.getUid(), vin, null, true).start();
                            } else {
                                //todo 如果是原始命令是开启空调（座椅加热），接下来需要作的是来一个真正的发动机perform，并将perform的refId指向原始命令
                                //避免重复的eventId
                                RemoteControl _perform = outputHexService.getStartEngineRemoteControl(rc.getUid(), vin, dataTool.getCurrentSeconds() - 1800, refId, (short) 0);
                                //先发出一条perform的启动发动机命令
                                _logger.info("[0x31]即将开始发送一条关联的启动发动机命令(perform):id=" + _perform.getId());
                                new RemoteCommandSender(vehicleDataService, _perform.getId(), rc.getUid(), vin, null, true).start();
                            }
                        } else {
                            //perform命令执行完毕，开始执行真实命令
                            _logger.info("[0x31]关联的启动发动机执行成功，开始执行原始控制指令.id=" + refId);
                            new RemoteCommandSender(vehicleDataService, refId, rc.getUid(), vin, null, true).start();
                        }

                    } else {
                        outputHexService.handleRemoteControlRst(vin, bean.getEventID(), rc.getControlType(), (short) 0, bean.getRemoteControlTime(), rc.getIsAnnounce(), true);
                    }
                } else {//各种原因未能成功

                    if (refId > 0) {
                        outputHexService.handleRemoteControlRst(vin, bean.getEventID(), rc.getControlType(), bean.getRemoteControlAck(), bean.getRemoteControlTime(), rc.getIsAnnounce(), false);
                        outputHexService.handleRefRemoteControlRst(refId, rc.getControlType(), bean.getRemoteControlAck(), bean.getRemoteControlTime());
                    } else {
                        outputHexService.handleRemoteControlRst(vin, bean.getEventID(), rc.getControlType(), bean.getRemoteControlAck(), bean.getRemoteControlTime(), rc.getIsAnnounce(), true);
                    }
                }
                _logger.info("[0x31]远程控制结果处理结束:" + bean.getApplicationID() + "-" + bean.getEventID() + " >" + bean.getRemoteControlAck());
            }
        } finally {
            //todo 释放buffer
            ReferenceCountUtil.release(buf);
        }

    }


    /**
     * @param reqString 远程控制上行hex
     * @param vin       vin码
     */
    public String handleRemoteControlSettingRequest(String reqString, String vin) {
        String byteStr = null;
        ByteBuf buf = dataTool.getByteBuf(reqString);
        try {
            byte[] bytes = dataTool.getBytesFromByteBuf(buf);
            byte messageId = dataTool.getMessageId(bytes);
            if (messageId == 0x01) {
                ByteBuffer bb = PackageEntityManager.getByteBuffer(reqString);
                DataPackage dp = conversionTBox.generate(bb);
                RemoteSettingReq bean = dp.loadBean(RemoteSettingReq.class);
                String key = vin + "-" + bean.getEventID();
                Integer val = bean.getRemoteFunction();
                _logger.info("handle RemoteControl Setting req" + key + ":" + val);
                Vehicle vehicle = vehicleRepository.findByVin(vin);
                if (vehicle != null) {
                    char[] value = dataTool.getBitsFromInteger(val);
                    String activeState = "";
                    if (value.length == 16) {
                        for (int i = 7; i >= 0; i--) {
                            activeState += value[i];
                            _logger.info("value[" + i + "];" + value[i]);
                        }
                        for (int i = 15; i >= 8; i--) {
                            activeState += value[i];
                            _logger.info("value[" + i + "];" + value[i]);
                        }

                    } else {
                        for (int i = value.length - 1; i >= 0; i--) {
                            activeState += value[i];
                            _logger.info("value[" + i + "];" + value[i]);
                        }
                    }
                    _logger.info("activeState value：" + activeState);
                    vehicle.setActiveState(activeState);
                    vehicleRepository.save(vehicle);

                    RemoteSettingResp resp = new RemoteSettingResp();
                    resp.setHead(bean.getHead());
                    resp.setTestFlag(bean.getTestFlag());
                    resp.setSendingTime((long) dataTool.getCurrentSeconds());
                    resp.setApplicationID(bean.getApplicationID());
                    resp.setMessageID((short) 2);
                    resp.setEventID(bean.getEventID());
                    resp.setResponse((short) 0);

                    DataPackage dpw = new DataPackage("8995_50_2");
                    dpw.fillBean(resp);
                    ByteBuffer bbw = conversionTBox.generate(dpw);
                    byteStr = PackageEntityManager.getByteString(bbw);
                }

            }
        } finally {
            //todo 释放buffer
            ReferenceCountUtil.release(buf);
        }

        return byteStr;
//        if(messageId==0x02){
//            //todo 记录远程控制设置结果
//            ByteBuffer bb=PackageEntityManager.getByteBuffer(reqString);
//            DataPackage dp=conversionTBox.generate(bb);
//            RemoteSettingResp bean=dp.loadBean(RemoteSettingResp.class);
//            String key=vin+"-"+bean.getEventID();
//            String val=String.valueOf(bean.getResponse());
//            _logger.info("handle RemoteControl Setting resp"+key+":"+val);
//            socketRedis.saveHashString(dataTool.remoteControlSet_hashmap_name,key,val,-1);
//        }
    }

    /**
     * 校验,是否发送远程指令
     *
     * @param vin
     * @param remoteControlPreconditionResp 数据bean
     * @param controlType                   控制类型
     * @param isAnnounce                    是否是announce(fc)
     * @return 是否通过
     */
    public int verifyRemoteControlPreconditionResp(String vin, RemoteControlPreconditionResp remoteControlPreconditionResp, short controlType, short isAnnounce) {
        //目前逻辑 根据0619协议
        boolean re = false;
        short vehicleModel = remoteControlPreconditionResp.getVehicleModel();//按照协议0628车型编号 0~255 0：默认值(M82)；1：M82；2：M85； 3：F60；4：F70； 5：F60电动车
        boolean isM8X = true;//区分是否是M8X/F60车型
        if (vehicleModel > (short) 2) {
            isM8X = false;
        }
        int reint = -1;
        boolean tmpCheck = false;
        boolean clampCheck = false;//发动机状态
        boolean remoteKeyCheck = false;
        boolean hazardLightsCheck = false;//报警灯
        boolean vehicleSpeedCheck = false;//车速
        boolean transmissionGearPositionCheck = false;//P档(自动)
        boolean handBrakeCheck = false;//手刹
        boolean sunroofCheck = false;//天窗
        boolean windowsCheck = false;//车窗
        boolean doorsCheck = false;//四门
        boolean trunkCheck = false;//后盖
        boolean bonnetCheck = false;//前盖
        boolean centralLockCheck = false;//中控锁
        boolean crashStatusCheck = false;
        boolean remainingFuelCheck = false;//燃料等级
        boolean powerStatusCheck = false;//电源开关OFF(true)
        boolean powerStatusIsOnCheck = false;//电源开关是否在ON挡或者Engine Start信号
        boolean engineFaultCheck = false;//发动机故障
        boolean remoteStartedCountCheck = false;
        boolean remoteStarted = false;//是否是远程启动的，空调/座椅加热需要判断此条件
        boolean emsCheck = false;//是否手动档 true 手动 false 自动
        boolean neutralGearSensorCheck = false;//手动档位空挡信号


        RemoteControl rc = outputHexService.getRemoteControlRecord(vin, remoteControlPreconditionResp.getEventID());
        if (rc != null && remoteControlPreconditionResp != null) {

            //根据0621协议
            float ambientAirTemperature = dataTool.getInternTrueTmp(remoteControlPreconditionResp.getTempIntern());//偏移40
            if (ambientAirTemperature > -10 && ambientAirTemperature < 40) {        //温度 -10~40
                tmpCheck = true;
            }
            byte clampStatus = remoteControlPreconditionResp.getSesam_clamp_stat();
            char[] clampStatus_char = dataTool.getBitsFromByte(clampStatus);
            if (clampStatus_char[5] == '0' && clampStatus_char[6] == '1' && clampStatus_char[7] == '1') {
                clampCheck = true;//19. 0x03 Engine start
            }

            byte remoteStart = remoteControlPreconditionResp.getStat_remote_start();
            char[] remoteStart_char = dataTool.getBitsFromByte(remoteStart);
            if (remoteStart_char[5] == '0' && remoteStart_char[6] == '1') {
                remoteStarted = true;//7.bit 4~5 0x1 remote start announced
            }

            byte remoteKey = remoteControlPreconditionResp.getSesam_hw_status();
            char[] remoteKey_char = dataTool.getBitsFromByte(remoteKey);
            if (isM8X) {
                if (remoteKey_char[5] == '0' && remoteKey_char[6] == '0' && remoteKey_char[7] == '1') {
                    remoteKeyCheck = true;//0x1 Key outside Vehicle
                }
                if (remoteKey_char[5] == '0' && remoteKey_char[6] == '1' && remoteKey_char[7] == '0') {
                    remoteKeyCheck = true;//0x2 Key out of range
                }
            } else {
                //尚未定义 false
                if (remoteKey_char[5] == '0' && remoteKey_char[6] == '1' && remoteKey_char[7] == '0') {
                    remoteKeyCheck = true;//0x2: Valid key outside right
                }
                if (remoteKey_char[5] == '0' && remoteKey_char[6] == '1' && remoteKey_char[7] == '1') {
                    remoteKeyCheck = true;//0x3: Valid key outside left
                }
                if (remoteKey_char[5] == '1' && remoteKey_char[6] == '0' && remoteKey_char[7] == '0') {
                    remoteKeyCheck = true;//0x4: Valid key outside trunk
                }
            }


            byte hazardLight = remoteControlPreconditionResp.getBcm_Stat_Central_Lock2();
            //协议变更 BCM_Stat_Central_Lock改为BCM_SWITCH_STATUS
            if (hazardLight == 0) {
                hazardLightsCheck = true;
            }
//            char[] hazardLight_char=dataTool.getBitsFromByte(hazardLight);
//            if(hazardLight_char[5]=='0' && hazardLight_char[6]=='0' && hazardLight_char[7]=='0'){
//                hazardLightsCheck=true;
//            }
            //根据0628协议 M8X车速分辨率0.015625，偏移量0，显示范围： 0 ~350kmh 上报数据范围:0~22400  5km/h-->320<上传数据>
            //F60:分辨率0.05625，偏移量0，显示范围： 0 ~296kmh 上报数据范围： 0~5263 缺省值： 0x0000无效值
            //新增有效值0xFFFF
            if (remoteControlPreconditionResp.getVehicleSpeed() < 3 || remoteControlPreconditionResp.getVehicleSpeed() == 0xFFFF) {
//                if(isM8X) {
//                    vehicleSpeedCheck = true;
//                }else{
//                    vehicleSpeedCheck = true;
//                }
                vehicleSpeedCheck = true;
            }

            //0636新增字段
            byte ems = remoteControlPreconditionResp.getEms1_n_vehicleID();
            //F60才有此信号 是否手动档
            if (!isM8X) {
                if (vehicleModel == 4) {
                    if (ems == 0x01 || ems == 0x02 || ems == 0x03) {
                        emsCheck = true;
                    }
                } else if (ems == 0x02 || ems == 0x04 || ems == 0x05) {
                    emsCheck = true;
                }
            }

            //0636新增字段
            byte neutralGearSensor = remoteControlPreconditionResp.getNeutralGearSensor();
            //F60才有此信号 是否N档(手动档)
            if (!isM8X && emsCheck) {
                if (neutralGearSensor == 0x01) {
                    neutralGearSensorCheck = true;
                }
            }

            byte transmissionGearPosition = remoteControlPreconditionResp.getTcu_ecu_stat();//要求P挡位 参考0628协议
            char[] transmissionGearPosition_char = dataTool.getBitsFromByte(transmissionGearPosition);
            if (isM8X) {//M8X 0x3 P挡
                if (transmissionGearPosition_char[4] == '0' && transmissionGearPosition_char[5] == '0' && transmissionGearPosition_char[6] == '1' && transmissionGearPosition_char[7] == '1') {
                    transmissionGearPositionCheck = true;
                }
            } else {//F6O 0xB P挡
                if (!emsCheck) {//自动档
                    if (transmissionGearPosition_char[4] == '1' && transmissionGearPosition_char[5] == '0' && transmissionGearPosition_char[6] == '1' && transmissionGearPosition_char[7] == '1') {
                        transmissionGearPositionCheck = true;
                    }
                }
            }
            /*else{//F6O 0xB P挡
                if (transmissionGearPosition_char[4] == '1' && transmissionGearPosition_char[5] == '0' && transmissionGearPosition_char[6] == '1' && transmissionGearPosition_char[7] == '1') {
                    transmissionGearPositionCheck = true;
                }
            }*/

            byte handBrake = remoteControlPreconditionResp.getEpb_status();
            char[] handBrake_char = dataTool.getBitsFromByte(handBrake);
            if (isM8X) {
                //M8X车型不检查手刹
//                if(handBrake_char[6]=='0'&&handBrake_char[7]=='1'){
                handBrakeCheck = true;
//                }
            } else {
                if (handBrake_char[6] == '1' && handBrake_char[7] == '0') { //0x2
                    handBrakeCheck = true;
                }
            }

//            byte sunroof=remoteControlPreconditionResp.getBcm_Stat_window2();
//            char[] sunroof_char=dataTool.getBitsFromByte(sunroof);
//            if(sunroof_char[6]=='0'&&sunroof_char[7]=='1'){
//                sunroofCheck = true;
//            }


//            byte[] windows=remoteControlPreconditionResp.getBcm_Stat_window();
//            char[] windows_char = dataTool.getBitsFrom2Byte(windows);
//            if(isM8X){
            //todo 关于车窗全部屏蔽检查，后续实车加上
//                if(windows_char[14]=='1'&&windows_char[15]=='0' && windows_char[12]=='1'&&windows_char[13]=='0' && windows_char[10]=='1'&&windows_char[11]=='0' && windows_char[8]=='1'&&windows_char[9]=='0'){
//                    windowsCheck=true;
//                }
//            }else{
//                if(windows_char[6]=='1'&&windows_char[7]=='0' && windows_char[4]=='1'&&windows_char[5]=='0' && windows_char[2]=='1'&&windows_char[3]=='0' && windows_char[0]=='1'&&windows_char[1]=='0'){
//                    windowsCheck=true;
//                }
//            }

            byte[] doors = remoteControlPreconditionResp.getBcm_Stat_Door_Flap();
            char[] doors_char = dataTool.getBitsFrom2Byte(doors);
//            if(doors_char[14]=='0'&&doors_char[15]=='0' && doors_char[12]=='0'&&doors_char[13]=='0' && doors_char[10]=='0'&&doors_char[11]=='0' && doors_char[8]=='0'&&doors_char[9]=='0'){
//                doorsCheck=true;
//            }
            //四门判断条件修改为0-7位
            if (doors_char[0] == '0' && doors_char[1] == '0' && doors_char[2] == '0' && doors_char[3] == '0' && doors_char[4] == '0' && doors_char[5] == '0' && doors_char[6] == '0' && doors_char[7] == '0') {
                doorsCheck = true;
            }
            if (doors_char[14] == '0' && doors_char[15] == '0') {
                bonnetCheck = true;
            }
            if (doors_char[12] == '0' && doors_char[13] == '0') {
                trunkCheck = true;
            }
            byte centralLock = remoteControlPreconditionResp.getBcm_Stat_Central_Lock();
            char[] centralLock_char = dataTool.getBitsFromByte(centralLock);
            if (centralLock_char[6] == '0' && centralLock_char[7] == '1') {
                centralLockCheck = true;
            }
            byte crashStatus = remoteControlPreconditionResp.getAcm_crash_status();
            char[] crashStatus_char = dataTool.getBitsFromByte(crashStatus);
            if (crashStatus_char[6] == '0' && crashStatus_char[7] == '0' && crashStatus_char[1] == '0' && crashStatus_char[0] == '0') {
                crashStatusCheck = true;
            }
            short remainingFuel = remoteControlPreconditionResp.getFuelLevel();
            if (remainingFuel > 15) {
                remainingFuelCheck = true;
            }
            byte remoteStartestatus = remoteControlPreconditionResp.getStat_remote_start();
            char[] remoteStartStatus_char = dataTool.getBitsFromByte(remoteStartestatus);
            boolean isRemoteStart = false;
            if (remoteStartStatus_char[2] == '0' && remoteStartStatus_char[3] == '1') {//必须是远程启动发动机才能远程关闭
                isRemoteStart = true;
            }

            byte sesam_clamp_stat2 = remoteControlPreconditionResp.getSesam_clamp_stat2();
            char[] sesam_clamp_stat2_char = dataTool.getBitsFromByte(sesam_clamp_stat2);//bit0-3 0x0 0FF
            if (sesam_clamp_stat2_char[5] == '0' && sesam_clamp_stat2_char[6] == '0' && sesam_clamp_stat2_char[7] == '0') {//OFF
                powerStatusCheck = true;
            }

            if (sesam_clamp_stat2_char[5] == '0' && sesam_clamp_stat2_char[6] == '1' && sesam_clamp_stat2_char[7] == '0') {//0x2 ON
                powerStatusIsOnCheck = true;//ON
            }
            if (sesam_clamp_stat2_char[5] == '0' && sesam_clamp_stat2_char[6] == '1' && sesam_clamp_stat2_char[7] == '1') {//0x3 Engine start
                powerStatusIsOnCheck = true;//Engine start
            }

            //车窗、天窗
            if (doorsCheck && trunkCheck && bonnetCheck && centralLockCheck && powerStatusCheck) {
                windowsCheck = true;
                sunroofCheck = true;
            }

            //不再检查M8X
            if (1 != 1) {
                FailureMessageData f = outputHexService.getLatestFailureMessage(vin);
                if (f != null) {
                    _logger.info("[0x31]获取到的最新故障数据，id:" + f.getId() + " info:" + f.getInfo());
                    if (f.getInfo() != null) {
                        String[] ids = f.getInfo().split(",");
                        for (int i = 0; i < ids.length; i++) {
                            if (ids[i].equals("20") || ids[i].equals("21") || ids[i].equals("23") || ids[i].equals("95") || ids[i].equals("145")) {
                                engineFaultCheck = true;//true有故障
                                break;
                            }
                        }
                    }
                }
            } else {
                engineFaultCheck = false;//F60车型无需判断发动机故障
            }

            short _startedCount = dataTool.getRemoteStartedCount(remoteControlPreconditionResp.getStat_remote_start());
            if (_startedCount < 2) {
                remoteStartedCountCheck = true;//小于2，还可以做远程启动发动机
            }

            /*
            控制类别  0：远程启动发动机  1：远程关闭发动机  2：车门上锁  3：车门解锁  4：空调开启  5：空调关闭  6：座椅加热  7：座椅停止加热  8：远程发动机限制  9：远程发动机限制关闭  10：闪灯 11：鸣笛
            */
            if (controlType == (short) 0) {//0：远程启动发动机
              /*  re=tmpCheck && clampCheck && remoteKeyCheck && hazardLightsCheck && vehicleSpeedCheck
                        && transmissionGearPositionCheck && handBrakeCheck && sunroofCheck && windowsCheck
                        && doorsCheck && trunkCheck && bonnetCheck && centralLockCheck && crashStatusCheck
                        && remainingFuelCheck;*/
                //todo 20160908屏蔽发动机启动的温度检查
                /*re= clampCheck && remoteKeyCheck && hazardLightsCheck && vehicleSpeedCheck
                        && transmissionGearPositionCheck && handBrakeCheck && sunroofCheck && windowsCheck
                        && doorsCheck && trunkCheck && bonnetCheck && centralLockCheck && crashStatusCheck
                        && remainingFuelCheck;*/
                //todo 启动发动机检查要区分是否是fc 区别处理
                if (isAnnounce == 1) {
                    //Initial Check 检查条件:电源档位、危险警告灯、档位、车门、天窗、后备箱、引擎盖、车速、中控锁、车窗
                    re = powerStatusCheck && hazardLightsCheck && doorsCheck && trunkCheck && bonnetCheck && vehicleSpeedCheck && centralLockCheck;//车门 后备箱 引擎盖 车速
                    _logger.info("[0x31]启动发动机precondition检查，是否是FC:" + isAnnounce + " 检查条件:电源档位/危险警告灯/车门/后备箱/引擎盖/车速/中控锁--" + powerStatusCheck + "/" + hazardLightsCheck + "/" + doorsCheck + "/" + trunkCheck + "/" + bonnetCheck + "/" + vehicleSpeedCheck + "/" + centralLockCheck + " 检查结果:" + re);

                    if (!powerStatusCheck) {
                        reint = 0x10;
                    } else if (!hazardLightsCheck) {
                        reint = 0x11;
                    } else if (!doorsCheck) {
                        reint = 0x13;
                    } else if (!trunkCheck) {
                        reint = 0x14;
                    } else if (!bonnetCheck) {
                        reint = 0x15;
                    } else if (!vehicleSpeedCheck) {
                        reint = 0x16;
                    } else if (!centralLockCheck) {
                        reint = 0x17;
                    }
                } else {
                    String msg = "";
                    boolean gearSensorCheck = false;
                    if (emsCheck) {//手动
                        msg = "手动档N档位";
                        if (neutralGearSensorCheck) {
                            gearSensorCheck = true;
                        }
                    } else {//自动
                        msg = "自动档P挡位";
                        if (transmissionGearPositionCheck) {
                            gearSensorCheck = true;
                        }
                    }
                    //Final Check  危险警告灯、档位、车门、天窗、后备箱、引擎盖、车速、中控锁、车窗、手刹、发动机无故障
                    re = hazardLightsCheck && gearSensorCheck && doorsCheck && trunkCheck && bonnetCheck && vehicleSpeedCheck && centralLockCheck && handBrakeCheck && (!engineFaultCheck) && remoteStartedCountCheck;//
                    _logger.info("[0x31]启动发动机precondition检查，是否是FC:" + isAnnounce + " 检查条件:危险警告灯/" + msg + "/车门/后备箱/引擎盖/车速/中控锁/手刹/发动机无故障/启动次数是否小于2--" + hazardLightsCheck + "/" + transmissionGearPositionCheck + "/" + doorsCheck + "/" + trunkCheck + "/" + bonnetCheck + "/" + vehicleSpeedCheck + "/" + centralLockCheck + "/" + handBrakeCheck + "/" + (!engineFaultCheck) + "/" + remoteStartedCountCheck + " 检查结果:" + re);
                    if (!hazardLightsCheck) {
                        reint = 0x11;
                    } else if (!gearSensorCheck) {
                        if (emsCheck) {//手动
                            if (!neutralGearSensorCheck) {
                                reint = 0x1A;
                            }
                        } else {
                            if (!transmissionGearPositionCheck) {
                                reint = 0x12;
                            }
                        }
                    } else if (!doorsCheck) {
                        reint = 0x13;
                    } else if (!trunkCheck) {
                        reint = 0x14;
                    } else if (!bonnetCheck) {
                        reint = 0x15;
                    } else if (!vehicleSpeedCheck) {
                        reint = 0x16;
                    } else if (!centralLockCheck) {
                        reint = 0x17;
                    } else if (!handBrakeCheck) {
                        reint = 0x18;
                    } else if (engineFaultCheck) {//true表示存在发动机故障
                        reint = 0x19;
                    } else if (!remoteStartedCountCheck) {
                        reint = 0x1E;//起动次数超过2
                    }
                }

                if (re) {
                    reint = 0;
                }
                if (clampCheck && transmissionGearPositionCheck) {//P挡位 && 发动机启动
                    reint = 0x1F;//专门标识发动机运行中
                }
            } else if (controlType == (short) 1) {//1：远程关闭发动机
                if (isRemoteStart) {//必须是远程启动发动机才能远程关闭
                    re = true;
                }
                if (re) {
                    reint = 0;
                } else {
                    reint = 2;
                }
            } else if (controlType == (short) 2) {//2：车门上锁
                re = doorsCheck && trunkCheck && bonnetCheck && powerStatusCheck;
                if (re) {
                    reint = 0;
                } else {
                    if (!doorsCheck) {
                        reint = 0x31;
                    } else if (!trunkCheck) {
                        reint = 0x32;
                    } else if (!bonnetCheck) {
                        reint = 0x33;
                    } else if (!powerStatusCheck) {
                        reint = 0x34;
                    }
//                    reint = 3;
                }
            } else if (controlType == (short) 3) {//3：车门解锁
                re = centralLockCheck && powerStatusCheck;
                if (re) {
                    reint = 0;
                } else {
                    if (!powerStatusCheck) {
                        reint = 0x34;
                    }
                    if (!centralLockCheck) {
                        reint = 0x35;
                    }
//                    reint = 3;
                }
//                if(controlType==(short)3 && clampCheck){//车门解锁,判断发动机是否已经启动,如果已经启动，会有特别的提示消息
//                    reint=300;
//                }
            } else if (controlType == (short) 4) {//4：空调开启
                if (powerStatusIsOnCheck) {//必须是远程启动发动机才能开启空调
                    re = true;
                }
                if (re) {
                    reint = 0;
                } else {
                    reint = 4;
                }
            } else if (controlType == (short) 5) {//5：空调关闭
                // if(clampCheck){//必须是远程启动发动机才能关闭空调，2017.1.10取消此要求
                re = true;
                //  }
                if (re) {
                    reint = 0;
                } else {
                    reint = 5;
                }
            } else if (controlType == (short) 6) {//6：座椅加热
                if (powerStatusIsOnCheck) {//必须是远程启动发动机才能开启座椅加热
                    re = true;
                }
                if (re) {
                    reint = 0;
                } else {
                    reint = 6;
                }
            } else if (controlType == (short) 7) {//6：座椅加热关闭
                //  if(clampCheck){//必须是远程启动发动机才能关闭座椅加热，2017.1.10取消此要求
                re = true;
                //  }
                if (re) {
                    reint = 0;
                } else {
                    reint = 7;
                }
            } else if (controlType == (short) 10) {//10：远程寻车->10闪灯
//                re = doorsCheck  && hazardLightsCheck && trunkCheck && bonnetCheck;
                re = doorsCheck && trunkCheck && bonnetCheck && powerStatusCheck && centralLockCheck;
                if (re) {
                    reint = 0;
                } else {
                    if (!doorsCheck) {
                        reint = 0x101;
                    } else if (!trunkCheck) {
                        reint = 0x102;
                    } else if (!bonnetCheck) {
                        reint = 0x103;
                    } else if (!powerStatusCheck) {
                        reint = 0x104;
                    } else if (!centralLockCheck) {
                        reint = 0x105;
                    }
//                    reint = 10;
                }
            }
//            else if(controlType==(short)11){//10：远程寻车->11鸣笛
//                re = doorsCheck  && hazardLightsCheck && trunkCheck && bonnetCheck;
//                if(re){
//                    reint = 0;
//                }else{
//                    reint = 11;
//                }
//            }
            //0638协议
            else if (controlType == (short) 11) {//11：远程控制车窗
                re = powerStatusCheck && doorsCheck && trunkCheck && bonnetCheck && centralLockCheck;//车门 后备箱 引擎盖 车速
                _logger.info("[0x31]车窗检查，是否:电源档位/车门/后备箱/引擎盖/中控锁--" + powerStatusCheck + "/" + doorsCheck + "/" + trunkCheck + "/" + bonnetCheck + "/" + centralLockCheck + " 检查结果:" + re);
                if (re) {
                    reint = 0;
                } else {
                    if (!powerStatusCheck) {
                        reint = 0x10;
                    } else if (!doorsCheck) {
                        reint = 0x13;
                    } else if (!trunkCheck) {
                        reint = 0x14;
                    } else if (!bonnetCheck) {
                        reint = 0x15;
                    } else if (!centralLockCheck) {
                        reint = 0x17;
                    }
                }
            } else if (controlType == (short) 12) {//12：远程控制天窗
                re = powerStatusCheck && doorsCheck && trunkCheck && bonnetCheck && centralLockCheck;//车门 后备箱 引擎盖 车速
                _logger.info("[0x31]天窗检查，是否:电源档位/车门/后备箱/引擎盖/中控锁--" + powerStatusCheck + "/" + doorsCheck + "/" + trunkCheck + "/" + bonnetCheck + "/" + centralLockCheck + " 检查结果:" + re);
                if (re) {
                    reint = 0;
                } else {
                    if (!powerStatusCheck) {
                        reint = 0x10;
                    } else if (!doorsCheck) {
                        reint = 0x13;
                    } else if (!trunkCheck) {
                        reint = 0x14;
                    } else if (!bonnetCheck) {
                        reint = 0x15;
                    } else if (!centralLockCheck) {
                        reint = 0x17;
                    }
                }
            }
        }
        _logger.info("[0x31]status:tmpCheck-clampCheck-remoteKeyCheck-hazardLightsCheck-vehicleSpeedCheck"
                + "-transmissionGearPositionCheck-handBrakeCheck-sunroofCheck-windowsCheck"
                + "-doorsCheck-trunkCheck-bonnetCheck-centralLockCheck-crashStatusCheck"
                + "-remainingFuelCheck");
        _logger.info("[0x31]status:" + tmpCheck + "-" + clampCheck + "-" + remoteKeyCheck + "-" + hazardLightsCheck + "-" + vehicleSpeedCheck
                + "-" + transmissionGearPositionCheck + "-" + handBrakeCheck + "-" + sunroofCheck + "-" + windowsCheck
                + "-" + doorsCheck + "-" + trunkCheck + "-" + bonnetCheck + "-" + centralLockCheck + "-" + crashStatusCheck
                + "-" + remainingFuelCheck);
        if (controlType == (short) 0 || controlType == (short) 4 || controlType == (short) 5 || controlType == (short) 6 || controlType == (short) 7 || controlType == (short) 2 || controlType == (short) 3 || controlType == (short) 10 || controlType == (short) 11 || controlType == (short) 12) {

        } else {
            _logger.info("除启动发动机、空调、座椅加热、远程上锁、远程解锁、远程寻车、车窗控制、天窗控制外的操作，Precondition校验会直接通过。");
            reint = 0;//临时处理
        }
        _logger.info("[0x31]Precondition响应校验结果:" + reint + " 车型:" + vehicleModel + "(0:M82;1:M82;2:M85;3:F60;4:F70;5:F60电动车) 是否M8X:" + isM8X);
        return reint;
    }

    /**
     * 校验发起指令APP与车之间的距离
     *
     * @param vin
     * @param eventId
     * @return 是否通过
     */
    public boolean verifyRemoteControlDistance(String vin, long eventId, int maxDistance) {
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
     * @param reqString 参数设置响应hex
     * @param vin       vin码
     */
    public void handleParmSetAck(String reqString, String vin) {
        //处理参数设置响应hex
        ByteBuffer bb = PackageEntityManager.getByteBuffer(reqString);
        DataPackage dp = conversionTBox.generate(bb);
        PramSetupAck bean = dp.loadBean(PramSetupAck.class);
        byte[] pramValue = bean.getPramValue();
        //请求解析到bean
        String key = "Result:" + vin + "-" + bean.getApplicationID() + "-" + bean.getEventID() + "-" + bean.getMessageID();
        //变更消息状态 不会当作失败而重试
        String statusKey = DataTool.msgCurrentStatus_preStr + vin + "-" + bean.getApplicationID() + "-" + bean.getEventID();
        String statusValue = String.valueOf(bean.getMessageID());
        socketRedis.saveValueString(statusKey, statusValue, DataTool.msgCurrentStatus_ttl);

        List<TBoxParmSet> tpss = tBoxParmSetRepository.findByVinAndEventId(vin, bean.getEventID());
        if (tpss.size() > 0) {
            TBoxParmSet tps = tpss.get(0);
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
        _logger.info("ParmSet Ack finished:" + bean.getApplicationID() + "-" + bean.getEventID() + " >");
    }


    /**
     * 处理远程诊断响应数据
     *
     * @param reqString 响应hex
     * @param vin       vin码
     */
    public void handleDiagnosticAck(String reqString, String vin) {
        _logger.info("DiagnosticAck:" + reqString);
        //
        ByteBuffer bb = PackageEntityManager.getByteBuffer(reqString);
        DataPackage dp = conversionTBox.generate(bb);
        DiagnosticCommanAck bean = dp.loadBean(DiagnosticCommanAck.class);
        DiagnosticData diagnosticData = diagnosticDataRepository.findByVinAndEventId(vin, bean.getEventID());
        //变更消息状态 不会当作失败而重试
        String statusKey = DataTool.msgCurrentStatus_preStr + vin + "-" + 66 + "-" + bean.getEventID();//0X42=66 ACK mid=2
        String statusValue = String.valueOf(2);//ACK mid=2
        socketRedis.saveValueString(statusKey, statusValue, DataTool.msgCurrentStatus_ttl);
        if (diagnosticData == null) {
            _logger.info("no record found for vin:" + vin + "eventId:" + bean.getEventID());
        } else {
            //按位解析诊断数据
            byte[] bytes = bean.getDiagData();
            char[] datas_1 = dataTool.getBitsFromByte(bytes[0]);
            char[] datas_2 = dataTool.getBitsFromByte(bytes[1]);
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

    /**
     * T-Box Ftp 远程软件升级
     *
     * @param reqString
     * @param vin
     */
    public String handleSoftUpdate(String reqString, String vin) {
        String result = "";

        FtpData ftpData = new FtpData();
        ByteBuf buffer = dataTool.getByteBuf(reqString);
        //下行
        ByteBuf buf = Unpooled.buffer();

        try {
            //处理远程软件升级上行的16进制字符串
            byte[] bytes = dataTool.getBytesFromByteBuf(buffer);
            byte messageId = dataTool.getMessageId(bytes);
            if (messageId == 0x01) {
                //上行
                ByteBuffer bb = PackageEntityManager.getByteBuffer(reqString);
                DataPackage dp = conversionTBox.generate(bb);
                SoftFtpUpdateCheck bean = dp.loadBean(SoftFtpUpdateCheck.class);

                Vehicle vehicle = vehicleRepository.findByVin(vin);
                UploadPackageEntity uploadPackageEntity = uploadPackageEntityRepository.findByModelAndVersion(3, vehicle.getSoftVersion());


                buf.writeShort(0x2323);//Short((short) 0x2323);
                buf.markWriterIndex();
                buf.writeShort(0x0000);//消息长度
                buf.writeByte(0x0);//Test Flag
                buf.writeInt(dataTool.getCurrentSeconds());//Sending Time
                buf.writeByte(0x54);//Application Id
                buf.writeByte(0x02);//Message Id

                //MID2 - TBOX_FtpUpdateCheckResp
                buf.writeInt(bean.getEventID().intValue());
                buf.writeByte(3);
                String versionStr = vehicle.getSoftVersion();

                if (versionStr == null) {
                    for (int i = 0; i < 5; i++) {
                        buf.writeByte(0x00);
                    }
                } else {
                    if (versionStr.length() >= 5) {
                        buf.writeBytes(versionStr.substring(0, 5).getBytes());
                    } else if (versionStr.length() < 5) {
                        buf.writeBytes(versionStr.getBytes());
                        for (int i = 0; i < 5 - versionStr.length(); i++) {
                            buf.writeByte(0x00);
                        }
                    }
                }

                String fileName = "";
                if (uploadPackageEntity == null) {
                    for (int i = 0; i < 10; i++) {
                        buf.writeByte(0x00);
                    }
                } else {
                    fileName = uploadPackageEntity.getFileName();
                    if (fileName == null) {
                        for (int i = 0; i < 10; i++) {
                            buf.writeByte(0x00);
                        }
                    } else if (fileName.length() >= 10) {
                        buf.writeBytes(fileName.substring(0, 10).getBytes());
                    } else if (fileName.length() < 10) {
                        buf.writeBytes(fileName.getBytes());
                        for (int i = 0; i < 10 - fileName.length(); i++) {
                            buf.writeByte(0x00);
                        }
                    }
                }

                Integer isUpdate = 1;
                if (vehicle.getIsUpdate() != null) {
                    isUpdate = vehicle.getIsUpdate();
                }
                buf.writeByte(isUpdate);
                String url = _serverUrl + "/api/download/" + fileName + "?uploadName=" + fileName;
                Integer len = 27 + url.length();
                buf.writeBytes(url.getBytes());
                buf.writeByte(dataTool.getCheckSum(DataTool.getBytesFromByteBuf(buf)));

                int index = buf.writerIndex();
                buf.resetWriterIndex();
                buf.writeShort(len);
                buf.writerIndex(index);

                //返回结果
                result = dataTool.bytes2hex(DataTool.getBytesFromByteBuf(buf));

                //保存记录
                ftpData.setVin(vin);
                ftpData.setModel(3);
                ftpData.setVersion(vehicle.getSoftVersion());
                ftpData.setIsUpdate(vehicle.getIsUpdate());
                ftpData.setUpdateStep(0);
                ftpData.setCreateTime(new Date());
                ftpDataRepository.save(ftpData);
            } else if (messageId == 0x03) {
                //上行
                ByteBuffer bb = PackageEntityManager.getByteBuffer(reqString);
                DataPackage dp = conversionTBox.generate(bb);
                SoftFtpUpdateFileDownLoadRst bean = dp.loadBean(SoftFtpUpdateFileDownLoadRst.class);

                //下行
                SoftFtpUpdateFileDownLoadAck resp = new SoftFtpUpdateFileDownLoadAck();
                resp.setHead(bean.getHead());
                resp.setTestFlag(bean.getTestFlag());
                resp.setSendingTime((long) dataTool.getCurrentSeconds());
                resp.setApplicationID(bean.getApplicationID());
                resp.setMessageID((short) 4);
                resp.setEventID(bean.getEventID());
                //响应
                DataPackage dpw = new DataPackage("8995_84_4");
                dpw.fillBean(resp);
                ByteBuffer bbw = conversionTBox.generate(dpw);

                //返回结果
                result = PackageEntityManager.getByteString(bbw);

                //保存记录
                ftpData.setVin(vin);
                ftpData.setModel(3);
                ftpData.setVersion(bean.getDestVersion());
                ftpData.setUpdateStep(bean.getUpdateStep().intValue());
                ftpData.setDownloadResult(bean.getDownloadResult().intValue());
                ftpData.setErrorCode(bean.getErrorCode().intValue());
                ftpData.setCreateTime(new Date());
                ftpDataRepository.save(ftpData);
            } else if (messageId == 0x05) {
                //上行
                ByteBuffer bb = PackageEntityManager.getByteBuffer(reqString);
                DataPackage dp = conversionTBox.generate(bb);
                SoftFtpUpdateResult bean = null;
                try {
                    bean = dp.loadBean(SoftFtpUpdateResult.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //下行
                SoftFtpUpdateResultAck resp = new SoftFtpUpdateResultAck();
                resp.setHead(bean.getHead());
                resp.setTestFlag(bean.getTestFlag());
                resp.setSendingTime((long) dataTool.getCurrentSeconds());
                resp.setApplicationID(bean.getApplicationID());
                resp.setMessageID((short) 6);
                resp.setEventID(bean.getEventID());
                //响应
                DataPackage dpw = new DataPackage("8995_84_6");
                dpw.fillBean(resp);
                ByteBuffer bbw = conversionTBox.generate(dpw);

                //返回结果
                result = PackageEntityManager.getByteString(bbw);

                //保存记录
                ftpData.setVin(vin);
                ftpData.setModel(3);
                ftpData.setVersion(bean.getDestVersion());
                ftpData.setUpdateStep(bean.getUpdateStep().intValue());
                ftpData.setDownloadResult(bean.getUpdateResult().intValue());
                ftpData.setErrorCode(bean.getErrorCode().intValue());
                ftpData.setCreateTime(new Date());
                ftpDataRepository.save(ftpData);

                if (bean.getUpdateResult() == 0) {
                    //更新升级标志
                    Vehicle vehicle = vehicleRepository.findByVin(vin);
                    vehicle.setIsUpdate(0);
                    vehicleRepository.save(vehicle);
                }
            }
        } finally {
            //todo 释放buffer
            ReferenceCountUtil.release(buf);
            ReferenceCountUtil.release(buffer);
        }
        return result;
    }

    /**
     * T-Box Ftp 远程固件升级
     *
     * @param reqString
     * @param vin
     */
    public String handleHardUpdate(String reqString, String vin) {
        String result = "";

        FtpData ftpData = new FtpData();
        ByteBuf buf = dataTool.getByteBuf(reqString);

        try {
            //处理远程固件升级上行的16进制字符串
            byte[] bytes = dataTool.getBytesFromByteBuf(buf);
            byte messageId = dataTool.getMessageId(bytes);
            if (messageId == 0x01) {
                //上行
                ByteBuffer bb = PackageEntityManager.getByteBuffer(reqString);
                DataPackage dp = conversionTBox.generate(bb);
                HardFtpUpdateCheck bean = dp.loadBean(HardFtpUpdateCheck.class);

                Vehicle vehicle = vehicleRepository.findByVin(vin);
                UploadPackageEntity uploadPackageEntity = uploadPackageEntityRepository.findByModelAndVersion(5, vehicle.getHardVersion());
                FtpSetting ftpSetting = ftpSettingRepository.findById(1);

                //下行
                HardFtpUpdateCheckResp resp = new HardFtpUpdateCheckResp();
                resp.setHead(bean.getHead());
                resp.setTestFlag(bean.getTestFlag());
                resp.setSendingTime((long) dataTool.getCurrentSeconds());
                resp.setApplicationID(bean.getApplicationID());
                resp.setMessageID((short) 2);
                resp.setEventID(bean.getEventID());

                Integer model = 5;
                resp.setModel(model.shortValue());

                String hwVersion = vehicle.getHardVersion();
                if (hwVersion == null) {
                    byte[] tempBytes = new byte[20];
                    for (int i = 0; i < 20; i++) {
                        tempBytes[i] = 0x00;
                    }
                    resp.setFwDestVersion(new String(tempBytes));
                } else if (hwVersion.length() >= 20) {
                    resp.setFwDestVersion(hwVersion.substring(0, 20));
                } else {
                    byte[] tempBytes = new byte[20 - hwVersion.length()];
                    for (int i = 0; i < 20 - hwVersion.length(); i++) {
                        tempBytes[i] = 0x00;
                    }
                    resp.setFwDestVersion(hwVersion + new String(tempBytes));
                }

                String fileName = null;
                if (uploadPackageEntity != null) {
                    fileName = uploadPackageEntity.getFileName();
                }
                if (fileName == null) {
                    byte[] tempBytes = new byte[50];
                    for (int i = 0; i < 50; i++) {
                        tempBytes[i] = 0x00;
                    }
                    resp.setFileName(new String(tempBytes));
                } else if (fileName.length() >= 50) {
                    resp.setFileName(fileName.substring(0, 20));
                } else {
                    byte[] tempBytes = new byte[50 - fileName.length()];
                    for (int i = 0; i < 50 - fileName.length(); i++) {
                        tempBytes[i] = 0x00;
                    }
                    resp.setFileName(fileName + new String(tempBytes));
                }

                Short isUpdate = 1;
                if (vehicle.getHwisUpdate() != null) {
                    isUpdate = vehicle.getHwisUpdate().shortValue();
                }
                resp.setIsUpdate(isUpdate);

                String srcVersion = null;
                if (fileName != null) {
                    String[] first = fileName.split("_");
                    if (first != null && first.length > 0) {
                        srcVersion = first[0];
                    }
                }
                if (srcVersion != null && !"".equals(srcVersion)) {
                    if (srcVersion.length() >= 20) {
                        resp.setFwSrcVersion(srcVersion.substring(0, 20));
                    } else {
                        byte[] tempBytes = new byte[20 - srcVersion.length()];
                        for (int i = 0; i < 20 - srcVersion.length(); i++) {
                            tempBytes[i] = 0x00;
                        }
                        resp.setFwSrcVersion(srcVersion + new String(tempBytes));
                    }
                } else {
                    byte[] versionBytes = new byte[20];
                    for (int i = 0; i < 20; i++) {
                        versionBytes[i] = 0x0;
                    }
                    resp.setFwSrcVersion(new String(versionBytes));
                }

                String ftpIp = ftpSetting.getFtpIp();
                if (ftpIp != null && !"".equals(ftpIp)) {
                    String[] ips = ftpIp.split("\\.");
                    byte[] ipBytes = new byte[4];
                    if (ips != null && ips.length == 4) {
                        Integer ip0 = Integer.parseInt(ips[0]);
                        ipBytes[0] = ip0.byteValue();
                        Integer ip1 = Integer.parseInt(ips[1]);
                        ipBytes[1] = ip1.byteValue();
                        Integer ip2 = Integer.parseInt(ips[2]);
                        ipBytes[2] = ip2.byteValue();
                        Integer ip3 = Integer.parseInt(ips[3]);
                        ipBytes[3] = ip3.byteValue();
                    } else {
                        ipBytes[0] = 0;
                        ipBytes[1] = 0;
                        ipBytes[2] = 0;
                        ipBytes[3] = 0;
                    }
                    resp.setFtpIp(ipBytes);
                } else {
                    byte[] ipBytes = new byte[4];
                    ipBytes[0] = 0;
                    ipBytes[1] = 0;
                    ipBytes[2] = 0;
                    ipBytes[3] = 0;
                    resp.setFtpIp(ipBytes);
                }
                resp.setFtpPort(ftpSetting.getFtpPort());
                String dialUserName = ftpSetting.getDialUserName();
                if (dialUserName == null) {
                    byte[] tempBytes = new byte[40];
                    for (int i = 0; i < 40; i++) {
                        tempBytes[i] = 0x00;
                    }
                    resp.setDialUserNumber(new String(dialUserName));
                } else if (dialUserName.length() >= 40) {
                    resp.setDialUserNumber(dialUserName.substring(0, 40));
                } else {
                    byte[] tempBytes = new byte[40 - dialUserName.length()];
                    for (int i = 0; i < 40 - dialUserName.length(); i++) {
                        tempBytes[i] = 0x00;
                    }
                    resp.setDialUserNumber(dialUserName + new String(tempBytes));
                }
                String dialPin = ftpSetting.getDialPin();
                if (dialPin == null) {
                    byte[] tempBytes = new byte[40];
                    for (int i = 0; i < 40; i++) {
                        tempBytes[i] = 0x00;
                    }
                    resp.setDialPin(new String(dialUserName));
                } else if (dialPin.length() >= 40) {
                    resp.setDialPin(dialPin.substring(0, 40));
                } else {
                    byte[] tempBytes = new byte[40 - dialPin.length()];
                    for (int i = 0; i < 40 - dialPin.length(); i++) {
                        tempBytes[i] = 0x00;
                    }
                    resp.setDialPin(dialPin + new String(tempBytes));
                }

                //响应
                DataPackage dpw = new DataPackage("8995_85_2");
                dpw.fillBean(resp);
                ByteBuffer bbw = conversionTBox.generate(dpw);

                //返回结果
                result = PackageEntityManager.getByteString(bbw);

                //保存记录
                ftpData.setVin(vin);
                ftpData.setModel(5);
                ftpData.setVersion(vehicle.getHardVersion());
                ftpData.setIsUpdate(vehicle.getHwisUpdate());
                ftpData.setUpdateStep(0);
                ftpData.setCreateTime(new Date());
                ftpDataRepository.save(ftpData);
            } else if (messageId == 0x03) {
                //上行
                ByteBuffer bb = PackageEntityManager.getByteBuffer(reqString);
                DataPackage dp = conversionTBox.generate(bb);
                HardFtpUpdateFileDownLoadRst bean = dp.loadBean(HardFtpUpdateFileDownLoadRst.class);

                //下行
                HardFtpUpdateFileDownLoadAck resp = new HardFtpUpdateFileDownLoadAck();
                resp.setHead(bean.getHead());
                resp.setTestFlag(bean.getTestFlag());
                resp.setSendingTime((long) dataTool.getCurrentSeconds());
                resp.setApplicationID(bean.getApplicationID());
                resp.setMessageID((short) 4);
                resp.setEventID(bean.getEventID());
                //响应
                DataPackage dpw = new DataPackage("8995_85_4");
                dpw.fillBean(resp);
                ByteBuffer bbw = conversionTBox.generate(dpw);

                //返回结果
                result = PackageEntityManager.getByteString(bbw);

                //保存记录
                ftpData.setVin(vin);
                ftpData.setModel(5);
                ftpData.setVersion(bean.getDestVersion());
                ftpData.setUpdateStep(bean.getUpdateStep().intValue());
                ftpData.setDownloadResult(bean.getDownloadResult().intValue());
                ftpData.setErrorCode(bean.getErrorCode().intValue());
                ftpData.setCreateTime(new Date());
                ftpDataRepository.save(ftpData);
            } else if (messageId == 0x05) {
                //上行
                ByteBuffer bb = PackageEntityManager.getByteBuffer(reqString);
                DataPackage dp = conversionTBox.generate(bb);
                HardFtpUpdateResult bean = dp.loadBean(HardFtpUpdateResult.class);

                //下行
                HardFtpUpdateResultAck resp = new HardFtpUpdateResultAck();
                resp.setHead(bean.getHead());
                resp.setTestFlag(bean.getTestFlag());
                resp.setSendingTime((long) dataTool.getCurrentSeconds());
                resp.setApplicationID(bean.getApplicationID());
                resp.setMessageID((short) 6);
                resp.setEventID(bean.getEventID());
                //响应
                DataPackage dpw = new DataPackage("8995_85_6");
                dpw.fillBean(resp);
                ByteBuffer bbw = conversionTBox.generate(dpw);

                //返回结果
                result = PackageEntityManager.getByteString(bbw);

                //保存记录
                ftpData.setVin(vin);
                ftpData.setModel(5);
                ftpData.setVersion(bean.getDestVersion());
                ftpData.setUpdateStep(bean.getUpdateStep().intValue());
                ftpData.setDownloadResult(bean.getUpdateResult().intValue());
                ftpData.setErrorCode(bean.getErrorCode().intValue());
                ftpData.setCreateTime(new Date());
                ftpDataRepository.save(ftpData);

                if (bean.getUpdateResult() == 0) {
                    //更新版本号及升级标志
                    Vehicle vehicle = vehicleRepository.findByVin(vin);
                    vehicle.setHwisUpdate(0);
                    vehicle.setSrcVersion(bean.getDestVersion());
                    vehicleRepository.save(vehicle);
                }

            }
        } finally {
            //todo  释放buffer
            ReferenceCountUtil.release(buf);
        }
        return result;
    }

}
