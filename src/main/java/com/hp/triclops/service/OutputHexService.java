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

import java.io.UnsupportedEncodingException;
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
    GpsTool gpsTool;
    @Autowired
    SMSHttpTool smsHttpTool;
    @Autowired
    WarningMessageDataRespository warningMessageDataRespository;
    @Autowired
    MessageRepository messageRepository;
    @Autowired
    FailureMessageDataRespository failureMessageDataRespository;

    @Value("${com.hp.acquire.serverId}")
    private String _serverId;//serverId集群依赖这个值

    private final int SRSFIRST = 1;
    private final int CRASHFIRST = 2;
    private final int ATAFIRST = 3;
    private final int TOWRST = 4;


    private Logger _logger = LoggerFactory.getLogger(OutputHexService.class);

    public String getRemoteControlSettingReqHex(RemoteControlSettingShow show, long eventId) {
        //产生远程控制设置指令
        RemoteSettingReq hr = new RemoteSettingReq();
        hr.setTestFlag((short) 0);
        hr.setSendingTime((long) dataTool.getCurrentSeconds());
        hr.setApplicationID((short) 50);//>>>
        hr.setMessageID((short) 1);//>>>
        hr.setEventID(eventId);
        int value = show.getStartEngine() + 2 * show.getCentralLock() + 4 * show.getFindCar() + 8 * show.getAc()
                + 16 * show.getSeatHeating() + 32 * show.getRemindFailure() + 64 * show.getLocation() + 128 * show.getSms();
        value = value > 255 ? 255 : value;
        hr.setRemoteFunction(value);//01111111
        DataPackage dpw = new DataPackage("8995_50_1");//>>>
        dpw.fillBean(hr);
        ByteBuffer bbw = conversionTBox.generate(dpw);
        String byteStr = PackageEntityManager.getByteString(bbw);
        return byteStr;
    }

    public String getRemoteControlPreHex(long eventId) {
        //产生远程控制预指令hex
        RemoteControlPreconditionReq remoteControlCmd = new RemoteControlPreconditionReq();
        remoteControlCmd.setApplicationID((short) 49);
        remoteControlCmd.setMessageID((short) 1);
        remoteControlCmd.setEventID(eventId);
        remoteControlCmd.setSendingTime((long) dataTool.getCurrentSeconds());
        remoteControlCmd.setTestFlag((short) 0);

        DataPackage dpw = new DataPackage("8995_49_1");//>>>
        dpw.fillBean(remoteControlCmd);
        ByteBuffer bbw = conversionTBox.generate(dpw);
        String byteStr = PackageEntityManager.getByteString(bbw);

        return byteStr;
    }

    public String getRemoteControlCmdHex(RemoteControl remoteControl, long eventId) {
        //产生远程控制指令hex
        RemoteControlCmd remoteControlCmd = new RemoteControlCmd();
        int _cType = -1;
        byte _remoteStartEngine = (byte) 0;
        byte[] _remoteFindCar = {(byte) 0, (byte) 0, (byte) 0, (byte) 0};
        byte _remoteLock = (byte) 0;
        byte[] _remoteAc = {(byte) 0, (byte) 0, (byte) 0, (byte) 0};
        byte[] _remoteHeating = {(byte) 0, (byte) 0};
        short _masterL = 0;
        short _slaveL = 0;

        short _ac = 0;//空调关
        short _recir = 0;
        short _compr = 0;
        short _model = 0;

        double _sendTemp = 0;
        Integer _sendTempInt = 0;
        switch (remoteControl.getControlType().intValue()) {
            case 0://
                _cType = 0;
                //todo
                //0x0 announce      0x1 perform remote start
                if (remoteControl.getIsAnnounce() == 1) {//此条命令是否为Announce
                    _remoteStartEngine = (byte) 0;
                } else {
                    _remoteStartEngine = (byte) 1;
                }
                break;
            case 1://远程关闭发动机
                _cType = 1;
                break;
            case 2://上锁
                _cType = 3;
                _remoteLock = (byte) 1;
                break;
            case 3://解锁
                _cType = 3;
                _remoteLock = (byte) 2;
                break;
            case 4://空调开启
                //todo
                _cType = 4;
                _sendTemp = (remoteControl.getAcTemperature() - 15.5) * 2;
                _sendTempInt = (int) _sendTemp;
                if (_sendTempInt < 0) {
                    _sendTempInt = 0;
                }
                if (_sendTempInt > 0x22) {
                    _sendTempInt = 0x22;
                }
                _remoteAc[0] = _sendTempInt.byteValue();
                //Bit 0 – bit 1:     0x00:Deactivate climatization
                //0x01: Active Climatization in Manual Mode(CCUon)
                //0x02: Active Climatization in Auto mode (CCUon)
                //Bit 2 – bit 3:   0x01:recirulation on   0x02:recirulation off
                //Bit 4 – bit 5:   0x01:compressor on     0x02:compressor off

                _ac = 1;//0x00:关 0x01: 开（手动模式） 0x02: 开（自动模式）
                if (remoteControl.getAutoMode() != null) {
                    if (remoteControl.getAutoMode() == (short) 1) {
                        _ac = 2;
                    }
                }
                if (remoteControl.getRecirMode() != null) {
                    if (remoteControl.getRecirMode() == 1) {//外循环
                        _recir = 2;
                    } else if (remoteControl.getRecirMode() == 0) {//内循环
                        _recir = 1;
                    }
                }
                if (remoteControl.getAcMode() != null) {
                    if (remoteControl.getAcMode().intValue() == 1) {//压缩机开
                        _compr = 1;
                    } else if (remoteControl.getAcMode().intValue() == 0) {//关
                        _compr = 2;
                    }
                }
                _remoteAc[1] = (byte) (_ac + _recir * 4 + _compr * 16);

                //BYTE[2]:0x00 not used 1 Body 2 Body + Feet 3 Feet 4 Defrost + Feet 5 Defrost
                if (remoteControl.getMode() != null) {
                    if (remoteControl.getMode() == 1) {//1除雾
                        _model = 5;
                    } else if (remoteControl.getMode() == 2) {//2前玻璃除雾+吹脚
                        _model = 4;
                    } else if (remoteControl.getMode() == 3) {//3 吹脚
                        _model = 3;
                    } else if (remoteControl.getMode() == 4) {//4吹身体+吹脚
                        _model = 2;
                    } else if (remoteControl.getMode() == 5) {//5吹身体
                        _model = 1;
                    }
                }
                _remoteAc[2] = (byte) _model;//吹风模式

                //BYTE[3]:0x00 off 0x01–0x07 : 1-7 fan speed
                if (remoteControl.getFan() != null) {
                    if (remoteControl.getFan() >= 1 && remoteControl.getFan() <= 7) {
                        _remoteAc[3] = remoteControl.getFan().byteValue();//风速1-7 不在此范围则为0 off
                    }
                }

                break;
            case 5://空调关闭
                //todo
                _cType = 4;
                _remoteAc[0] = (byte) 1;//0x00~0x23
                _ac = 0;//空调关
                _remoteAc[1] = (byte) (_ac + _recir * 4 + _compr * 16);
                break;
            case 6://座椅加热
                //todo ok
                //Bit0 – bit1:0x00:Deactivate passengers seat heating 0x01:Active passenger Seat Heating
                //Bit2 – Bit3:0x00:Deactivate drivers seat heating    0x01:Active drivers seat heating
                _cType = 5;
                int _masterSTAT = 0;
                int _slaveSTAT = 0;
                if (remoteControl.getMasterStat() != null) {
                    if (remoteControl.getMasterStat() == 1) {
                        //主开
                        _masterSTAT = 1;
                    }
                }
                if (remoteControl.getSlaveStat() != null) {
                    if (remoteControl.getSlaveStat() == 1) {
                        //副开
                        _slaveSTAT = 1;
                    }
                }
                _remoteHeating[0] = (byte) (_masterSTAT * 4 + _slaveSTAT);
                if (remoteControl.getMasterLevel() != null) {
                    if (remoteControl.getMasterLevel() >= 1 && remoteControl.getMasterLevel() <= 3) {
                        _masterL = remoteControl.getMasterLevel();
                    }
                }
                if (remoteControl.getSlaveLevel() != null) {
                    if (remoteControl.getSlaveLevel() >= 1 && remoteControl.getSlaveLevel() <= 3) {
                        _slaveL = (short) (remoteControl.getSlaveLevel().shortValue() + 3);
                    }
                }
                _remoteHeating[1] = (byte) (_slaveL * 16 + _masterL);//BYTE[1]:  Bit 0–Bit 3:  0x01–0x03 Driver level Bit 4–Bit7: 0x04–0x06   Passenger level
                break;
            case 7://停止座椅加热
                //todo ok
                _cType = 5;
                _masterSTAT = 0;
                _slaveSTAT = 0;
                _remoteHeating[0] = (byte) (_masterSTAT * 4 + _slaveSTAT);

                _remoteHeating[1] = (byte) (_slaveL * 16 + _masterL);//BYTE[1]:  Bit 0–Bit 3:  0x01–0x03 Driver level Bit 4–Bit7: 0x04–0x06   Passenger level
                break;
            case 8://远程发动机限制  协议不支持
                //_cType=0;
                break;
            case 9://远程发动机限制关闭 协议不支持
                //_cType=0;
                break;
            case 10://远程寻车 通过闪灯鸣笛次数判断是属于哪种模式 （闪灯鸣笛/仅闪灯/仅鸣笛）
                //todo 明确时间 byte[2]时间 byte[3]模式 开关 ok
                _cType = 2;

                Integer _actTime = (int) (remoteControl.getActTime() * 10);//0.2~0.5-> 0x02~0x05
                if (_actTime < 2) {
                    _actTime = 2;
                }
                if (_actTime > 5) {
                    _actTime = 5;
                }
                _remoteFindCar[2] = _actTime.byteValue();
                //BYTE[3]:
                //Bit0 – bit1: 0x00:horn and lights to be activated 0x01:lights only tobe activated 0x02: horn only tobe activated
                //Bit2 – bit3: 0x00:function activation 0x01:function deactivation
                int _remoteFindA = 0;//0 闪灯鸣笛都开 1 仅闪灯 2 仅鸣笛
                int _remoteFindB = 1;// 0开 1关
                if (remoteControl.getLightNum() == null) {
                    remoteControl.setLightNum((short) 0);
                }
                if (remoteControl.getHornNum() == null) {
                    remoteControl.setHornNum((short) 0);
                }
                if (remoteControl.getLightNum().intValue() > 0 && remoteControl.getHornNum().intValue() == 0) {
                    //1 仅闪灯
                    _remoteFindA = 1;
                }
                if (remoteControl.getLightNum().intValue() == 0 && remoteControl.getHornNum().intValue() > 0) {
                    //2 仅鸣笛
                    _remoteFindA = 2;
                }
                short lNum = remoteControl.getLightNum();
                short hNum = remoteControl.getHornNum();
                lNum = lNum < 0x01 ? 0x01 : lNum;
                lNum = lNum > 0x1E ? 0x1E : lNum;
                hNum = hNum < 0x01 ? 0x01 : hNum;
                hNum = hNum > 0x1E ? 0x1E : hNum;
                _remoteFindCar[0] = (byte) lNum;
                _remoteFindCar[1] = (byte) hNum;

                if (remoteControl.getDeActive() != null) {
                    if (remoteControl.getDeActive() == (short) 1) {
                        _remoteFindB = 0;
                    }
                }
                _remoteFindCar[3] = (byte) (_remoteFindB * 4 + _remoteFindA);
                break;
            case 11://远程控制车窗
                _cType = 6;
                Short stat = remoteControl.getWindowStat();
                if (stat == 1) {//开
                    _remoteStartEngine = (byte) 85;//01 01 01 01
                } else if (stat == 0) {//关
                    _remoteStartEngine = (byte) 170;//10 10 10 10
                }
                break;
            case 12://远程控制天窗窗
                _cType = 7;
                stat = remoteControl.getWindowStat();
                if (stat == 1) {//开
                    _remoteStartEngine = (byte) 1;//
                } else if (stat == 0) {//关
                    _remoteStartEngine = (byte) 2;//
                }
                break;
            default:
                _logger.info("[0x31]未知的远程控制类别" + remoteControl.getControlType().intValue());
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

        DataPackage dpw = new DataPackage("8995_49_3");//>>>
        dpw.fillBean(remoteControlCmd);
        ByteBuffer bbw = conversionTBox.generate(dpw);
        String byteStr = PackageEntityManager.getByteString(bbw);

        return byteStr;
    }

    /**
     * TBox注册成功后 检查是否有参数设置未下发 如果有则下发处理
     *
     * @param vin vin
     */
    public void sendParmSetAfterRegister(String vin) {
        //注册通过后下发参数设置
        List<TBoxParmSet> tpss = tBoxParmSetRepository.getLatestOneByVin(vin);
        if (tpss.size() > 0) {
            TBoxParmSet tps = tpss.get(0);
            String byteString = getParmSetCmdHex(tps);
            _logger.info("即将下发参数设置给vin:" + vin + " >" + byteString);
            saveCmdToRedis(_serverId, vin, byteString);
            tps.setStatus((short) 1);//参数设置指令向tbox发出 消息状态由0->1
            tBoxParmSetRepository.save(tps);
        } else {
            _logger.info("没有vin:" + vin + " 的参数设置需要下发>");
        }

    }

    public String getParmSetCmdHex(TBoxParmSet tps) {
        PramSetCmd pramSetCmd = new PramSetCmd();
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
        pramSetCmd.setLicensePlate(dataTool.getLengthString(tps.getLicensePlate(), 8));
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

        DataPackage dpw = new DataPackage("8995_82_1");//>>>
        dpw.fillBean(pramSetCmd);
        ByteBuffer bbw = conversionTBox.generate(dpw);
        String byteStr = PackageEntityManager.getByteString(bbw);
        ////////////////////////////////
    /*    ByteBuffer bb=PackageEntityManager.getByteBuffer(byteStr);
        DataPackage dp=conversionTBox.generate(bb);
        PramSetCmd bean=dp.loadBean(PramSetCmd.class);
        PackageEntityManager.printEntity(bean);*/


        return byteStr;
    }


    /**
     * 生成远程诊断的下行hex
     *
     * @param diagnosticData diagnosticData实体类
     * @return 下行hex
     */
    public String getDiagCmdHex(DiagnosticData diagnosticData) {
        DiagnosticCommandCmd diagnosticCommandCmd = new DiagnosticCommandCmd();
        diagnosticCommandCmd.setTestFlag((short) 0);
        diagnosticCommandCmd.setSendingTime((long) dataTool.getCurrentSeconds());
        diagnosticCommandCmd.setApplicationID((short) 66);//>>>
        diagnosticCommandCmd.setMessageID((short) 1);//>>>
        diagnosticCommandCmd.setEventID(diagnosticData.getEventId());


        DataPackage dpw = new DataPackage("8995_66_1");//>>>
        dpw.fillBean(diagnosticCommandCmd);
        ByteBuffer bbw = conversionTBox.generate(dpw);
        String byteStr = PackageEntityManager.getByteString(bbw);
        return byteStr;
    }

    /**
     * 根据报警hex信息生成文本性质的报警提示 并push or send到对应user
     *
     * @param vin      vin
     * @param msg      16进制报警信息
     * @param oneFirst 气囊/碰撞/防盗优先
     */
    public void getWarningMessage(String vin, String msg, int oneFirst) {

        //推送消息
        Map<String, Object> pushMsg = getWarningMessageForPush(vin, msg, null, oneFirst);
        pushMessageToUser(vin, pushMsg);

        //发送短信
        if (pushMsg != null) {
            String cleanFlag = (String) pushMsg.get("cleanFlag");
            //0报警 1清除
            if ("0".equals(cleanFlag)) {
                Vehicle vehicle = vehicleRepository.findByVin(vin);
                List<UserVehicleRelatived> uvr = userVehicleRelativedRepository.findOwnerByVid(vehicle);//找到车主

                StringBuilder sb = new StringBuilder();
                String srs = "";
                try {
                    if (oneFirst == SRSFIRST) {
                        srs = "[华晨汽车Bri-Air]尊敬的用户，您的爱车于" + DateUtil.formatDateByFormat(new Date(), "yyyy-MM-dd,HH:mm:ss") + "发生车辆碰撞，建议您尽快确认车辆状态。";
                        srs = java.net.URLEncoder.encode(srs, "UTF-8");

                    }
                    if (oneFirst == CRASHFIRST) {
                        srs = "[华晨汽车Bri-Air]尊敬的用户，您的爱车于" + DateUtil.formatDateByFormat(new Date(), "yyyy-MM-dd,HH:mm:ss") + "发生车辆碰撞，建议您尽快确认车辆状态。";
                        srs = java.net.URLEncoder.encode(srs, "UTF-8");
                    }
                    if (oneFirst == ATAFIRST) {
                        srs = "[华晨汽车Bri-Air]尊敬的用户，您的爱车于" + DateUtil.formatDateByFormat(new Date(), "yyyy-MM-dd,HH:mm:ss") + "车门被异常开启，建议您尽快确认车辆状态。";
                        srs = java.net.URLEncoder.encode(srs, "UTF-8");
                    }
                    if (oneFirst == TOWRST) {
                        //TODO
                        srs = "[华晨汽车Bri-Air]尊敬的用户，您的爱车于" + DateUtil.formatDateByFormat(new Date(), "yyyy-MM-dd,HH:mm:ss") + "被拖动或者轻微刮碰，建议您尽快确认车辆状态。";
                        srs = java.net.URLEncoder.encode(srs, "UTF-8");
                    }
                } catch (UnsupportedEncodingException e) {
                    _logger.info(e.getMessage());
                }
                sb.append(srs);
                String smsStr = sb.toString();
                //一个车对应多个uid
                if (uvr.size() > 0) {
                    User u = userRepository.findById(uvr.get(0).getUid().getId());
                    if (u != null) {
                        //取到紧急联系人电话
                        //发送短信
                        String phone = u.getPhone();//防盗报警车主电话，碰撞报警紧急联系人电话
                        if (oneFirst == SRSFIRST || oneFirst == CRASHFIRST) {//
                            if (u.getContactsPhone() != null && !"".equals(u.getContactsPhone())) {
                                String contactPhone = u.getContactsPhone();
                                _logger.info("[0x24]准备发送报警短信给紧急联系人" + contactPhone + "|oneFirst:" + oneFirst);
                                _logger.info("[0x24]发送短信:" + contactPhone + ":" + smsStr);
                                //调用工具类发起 http请求
                                smsHttpTool.doHttp(contactPhone, smsStr);
                            } else {
                                _logger.info("[0x24]关联用户的紧急联系人手机号为空，无法发送短信");
                            }
                        }
                        _logger.info("[0x24]准备发送报警短信给" + phone + "|oneFirst:" + oneFirst);
                        _logger.info("[0x24]发送短信:" + phone + ":" + smsStr);
                        //调用工具类发起 http请求
                        smsHttpTool.doHttp(phone, smsStr);
                    } else {
                        _logger.info("[0x24]没有找到关联用户，无法发送短信");
                    }
                } else {
                    _logger.info("[0x24]没有找到关联用户，无法发送短信");
                }
            }
        }
    }


    /**
     * 根据报警hex信息生成文本性质的报警提示 并push到对应user
     *
     * @param vin      vin
     * @param msg      16进制报警信息
     * @param oneFirst 气囊/碰撞/防盗优先
     */
    public void getWarningMessageAndPush(String vin, String msg, int oneFirst) {
        Vehicle vehicle = vehicleRepository.findByVin(vin);
        List<UserVehicleRelatived> uvr = userVehicleRelativedRepository.findByVid(vehicle);
        //一个车对应多个uid
        if (uvr.size() > 0) {
            Iterator<UserVehicleRelatived> iterator = uvr.iterator();
            while (iterator.hasNext()) {
                //int uid = iterator.next().getUid().getId();
                // User user = iterator.next().getUid();
                UserVehicleRelatived userVehicleRelatived = iterator.next();
                if (userVehicleRelatived.getVflag() == 1) {
                    User user = userVehicleRelatived.getUid();
                    Map<String, Object> pushMsg = getWarningMessageForPush(vin, msg, user, oneFirst);
                    pushMessageToUser(vin, pushMsg);
                }

            }
        } else {
            _logger.info("[0x24]没有找到关联用户，无法推送消息");
        }
    }

    /**
     * 根据报警hex信息生成文本性质的短信提示 并短信到对应手机
     *
     * @param vin vin
     * @param msg 16进制报警信息
     */
    public void getWarningMessageAndSms(String vin, String msg, int oneFirst) {
        Vehicle vehicle = vehicleRepository.findByVin(vin);
        List<UserVehicleRelatived> uvr = userVehicleRelativedRepository.findOwnerByVid(vehicle);//找到车主
        //一个车对应多个uid
        if (uvr.size() > 0) {
            User u = userRepository.findById(uvr.get(0).getUid().getId());
            if (u != null) {
                //取到紧急联系人电话
                //发送短信
                String phone = u.getPhone();//防盗报警车主电话，碰撞报警紧急联系人电话
                if (oneFirst == SRSFIRST || oneFirst == CRASHFIRST) {//
                    if (u.getContactsPhone() != null && !"".equals(u.getContactsPhone())) {
                        String contactPhone = u.getContactsPhone();
                        sendWarningMessageSms(vin, msg, contactPhone, oneFirst);
                    } else {
                        _logger.info("[0x24]关联用户的紧急联系人手机号为空，无法发送短信");
                    }
                }
                _logger.info("[0x24]准备发送报警短信给" + phone + "|oneFirst:" + oneFirst);
                sendWarningMessageSms(vin, msg, phone, oneFirst);
            } else {
                _logger.info("[0x24]没有找到关联用户，无法发送短信");
            }
        } else {
            _logger.info("[0x24]没有找到关联用户，无法发送短信");
        }
    }

    /**
     * 根据补发报警hex信息生成文本性质的报警提示 并push or send到对应user
     *
     * @param vin      vin
     * @param msg      16进制报警信息
     * @param oneFirst 气囊/碰撞/防盗优先
     */
    public void getResendWarningMessage(String vin, String msg, int oneFirst) {
        //推送消息
        Map<String, Object> pushMsg = getResendWarningMessageForPush(vin, msg, null, oneFirst);
        pushMessageToUser(vin, pushMsg);

        //发送短信
        if (pushMsg != null) {
            String cleanFlag = (String) pushMsg.get("cleanFlag");
            //0报警 1清除
            if ("0".equals(cleanFlag)) {
                Vehicle vehicle = vehicleRepository.findByVin(vin);
                List<UserVehicleRelatived> uvr = userVehicleRelativedRepository.findOwnerByVid(vehicle);//找到车主

                StringBuilder sb = new StringBuilder();
                String srs = "";
                try {
                    if (oneFirst == SRSFIRST) {
                        srs = "[华晨汽车Bri-Air]尊敬的用户，您的爱车于" + DateUtil.formatDateByFormat(new Date(), "yyyy-MM-dd,HH:mm:ss") + "发生车辆碰撞，建议您尽快确认车辆状态。";
                        srs = java.net.URLEncoder.encode(srs, "UTF-8");

                    }
                    if (oneFirst == CRASHFIRST) {
                        srs = "[华晨汽车Bri-Air]尊敬的用户，您的爱车于" + DateUtil.formatDateByFormat(new Date(), "yyyy-MM-dd,HH:mm:ss") + "发生车辆碰撞，建议您尽快确认车辆状态。";
                        srs = java.net.URLEncoder.encode(srs, "UTF-8");
                    }
                    if (oneFirst == ATAFIRST) {
                        srs = "[华晨汽车Bri-Air]尊敬的用户，您的爱车于" + DateUtil.formatDateByFormat(new Date(), "yyyy-MM-dd,HH:mm:ss") + "车门被异常开启，建议您尽快确认车辆状态。";
                        srs = java.net.URLEncoder.encode(srs, "UTF-8");
                    }
                } catch (UnsupportedEncodingException e) {
                    _logger.info(e.getMessage());
                }
                sb.append(srs);
                String smsStr = sb.toString();
                //一个车对应多个uid
                if (uvr.size() > 0) {
                    User u = userRepository.findById(uvr.get(0).getUid().getId());
                    if (u != null) {
                        //取到紧急联系人电话
                        //发送短信
                        String phone = u.getPhone();//防盗报警车主电话，碰撞报警紧急联系人电话
                        if (oneFirst == SRSFIRST || oneFirst == CRASHFIRST) {//
                            if (u.getContactsPhone() != null && !"".equals(u.getContactsPhone())) {
                                String contactPhone = u.getContactsPhone();
                                _logger.info("[0x24]准备发送报警短信给紧急联系人:" + contactPhone + "|oneFirst:" + oneFirst);
                                _logger.info("[0x24]发送短信:" + contactPhone + ":" + smsStr);
                                //调用工具类发起 http请求
                                smsHttpTool.doHttp(contactPhone, smsStr);
                            } else {
                                _logger.info("[0x24]关联用户的紧急联系人手机号为空，无法发送短信");
                            }
                        }
                        _logger.info("[0x24]准备发送报警短信给" + phone + "|oneFirst:" + oneFirst);
                        _logger.info("[0x24]发送短信:" + phone + ":" + smsStr);
                        //调用工具类发起 http请求
                        smsHttpTool.doHttp(phone, smsStr);
                    } else {
                        _logger.info("[0x24]没有找到关联用户，无法发送短信");
                    }
                } else {
                    _logger.info("[0x24]没有找到关联用户，无法发送短信");
                }
            }
        }
    }

    /**
     * 根据补发报警hex信息生成文本性质的报警提示 并push到对应user
     *
     * @param vin      vin
     * @param msg      16进制报警信息
     * @param oneFirst 气囊/碰撞/防盗优先
     */
    public void getResendWarningMessageAndPush(String vin, String msg, int oneFirst) {
        Vehicle vehicle = vehicleRepository.findByVin(vin);
        List<UserVehicleRelatived> uvr = userVehicleRelativedRepository.findByVid(vehicle);
        //一个车对应多个uid
        if (uvr.size() > 0) {
            Iterator<UserVehicleRelatived> iterator = uvr.iterator();
            while (iterator.hasNext()) {
                UserVehicleRelatived userVehicleRelatived = iterator.next();
                if (userVehicleRelatived.getVflag() == 1) {
                    User user = userVehicleRelatived.getUid();
                    Map<String, Object> pushMsg = getResendWarningMessageForPush(vin, msg, user, oneFirst);
                    pushMessageToUser(vin, pushMsg);
                }
            }
        } else {
            _logger.info("[0x25]没有找到关联用户，无法推送消息");
        }
    }

    /**
     * 根据报警hex信息生成文本性质的短信提示 并短信到对应手机
     *
     * @param vin vin
     * @param msg 16进制报警信息
     */
    public void getResendWarningMessageAndSms(String vin, String msg, int oneFirst) {
        Vehicle vehicle = vehicleRepository.findByVin(vin);
        List<UserVehicleRelatived> uvr = userVehicleRelativedRepository.findOwnerByVid(vehicle);//找到车主
        //一个车对应多个uid
        if (uvr.size() > 0) {
            User u = userRepository.findById(uvr.get(0).getUid().getId());
            if (u != null && u.getContactsPhone() != null) {
                //取到紧急联系人电话
                //发送短信
                String phone = u.getPhone();//防盗报警车主电话，碰撞报警紧急联系人电话
                if (oneFirst == SRSFIRST) {
                    phone = u.getContactsPhone();
                }
                _logger.info("[0x25]准备发送报警短信给" + phone + "|oneFirst:" + oneFirst);
                sendResendWarningMessageSms(vin, msg, phone, oneFirst);
            } else {
                _logger.info("[0x25]没有找到关联用户，无法发送短信");
            }
        } else {
            _logger.info("[0x25]没有找到关联用户，无法发送短信");
        }
    }

    /**
     * 根据故障hex信息生成文本性质的故障提示 并push到对应user
     *
     * @param vin vin
     * @param msg 16进制报警信息
     */
    public void getFailureMessageAndPush(String vin, String msg) {
        Map<String, Object> pushMsg = getFailureMessageForPush(vin, msg);
        pushMessageToUser(vin, pushMsg);
    }

    /**
     * 根据补发故障hex信息生成文本性质的故障提示 并push到对应user
     *
     * @param vin vin
     * @param msg 16进制报警信息
     */
    public void getResendFailureMessageAndPush(String vin, String msg) {
        Map<String, Object> pushMsg = getFailureMessageForPush(vin, msg);
        pushMessageToUser(vin, pushMsg);
    }


    /**
     * 处理远程控制结果推送
     *
     * @param vin     target vin
     * @param result  1成功 0是失败
     * @param message remark
     */
    public void pushRemoteControlResult(long id, String vin, String eventId, int result, String message) {
        /*todo
        *如果一条记录进入终结状态，
        需要判断他是否有指向它的启动发动机命令，
        因为这条启动发动机的命令对应的eventId才是前端监听的key
        在pushRemoteControlResult修改，
        查找对应vin-eventId的记录，看看是否有refId=this id
        如果有使用后者的eventId更新到redis
         */
        /*RemoteControl originalRemoteControl=remoteControlRepository.findByRefId(id);//判断他是否有指向它的启动发动机命令
        if(originalRemoteControl!=null){
            eventId=originalRemoteControl.getSessionId();
        }*/
        String key = vin + "-" + id;
        String value = String.valueOf(id);
        socketRedis.saveHashString(dataTool.remoteControl_hashmap_name, key, value, dataTool.remoteControlResult_ttl);
        Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("pType", 1);
        dataMap.put("rs", result);
        dataMap.put("textContent", message);
        // pushMessageToUser(vin,dataMap); 协议0627远程控制改为API阻塞式响应后取消推送
    }

    /**
     * 根报警提示push到对应user
     *
     * @param vin     vin
     * @param pushMsg 文本报警信息
     */
    public void pushMessageToUser(String vin, Map<String, Object> pushMsg) {
        _logger.info("[0x24][0x25][0x28][0x29]准备推送消息:" + pushMsg);
        if (pushMsg == null) {
            return;
        }
        Vehicle vehicle = vehicleRepository.findByVin(vin);
        List<UserVehicleRelatived> uvr = userVehicleRelativedRepository.findByVid(vehicle);
        //一个车对应多个uid 报警消息都push过去
        if (uvr.size() > 0) {
            Iterator<UserVehicleRelatived> iterator = uvr.iterator();
            while (iterator.hasNext()) {
                UserVehicleRelatived userVehicleRelatived = iterator.next();
                if (userVehicleRelatived.getVflag() == 1) {
                    int uid = userVehicleRelatived.getUid().getId();
                    //int uid=iterator.next().getUid().getId();
                    _logger.info("[0x24][0x25][0x28][0x29]推送:" + uid + ":" + pushMsg);
                    try {
                        //this.mqService.pushToUser(uid, pushMsg);
    /*                    * @param sourceId    发送用户id<br>
                        * @param resourceFrom  数据来源,1 手机,2 车机<br>
                        * @param targetType 目标对象类型 ,1 user ,2 organize <br>
                        * @param targetId 目标对象id<br>
                        * @param resourceTo 目标类型 ,1 手机,2 车机,3 短信,4 手机,车机 ,短信<br>1
                        * @param funType 消息类型 ,1 ,即时通讯 ,2 ,推送消息<br>2
                        * @param pType 推送消息类型,1 远程控制,2 故障提醒,3 保养提醒,4 位置分享,5 智能寻车,6 位置共享,7 发送位置,8 气囊报警,9 防盗报警,10 拖车<br>289
                        * @param textContent 发送内容文本、经纬度<br>
                        * @param contentType 发送内容类型 ,1 文本,2 音乐,3 图片,41 位置信息,42 位置共享开始,43 位置共享结束<br>
                        * @param messageNums 信息条数 count
                        * @param cleanFlag 消息推送消除标志，仅针对 气囊报警、防盗报警,0报警,1消除
                        * @param file 上传的附件*/
                        pushMsg.put("targetType", 1);
                        pushMsg.put("targetId", uid);
                        pushMsg.put("resourceTo", 1);
                        pushMsg.put("funType", 2);
                        pushMsg.put("vin", vin);

                        httpRequestTool.doHttp(urlLink, pushMsg);

                    } catch (RuntimeException e) {
                        _logger.info(e.getMessage());
                    } catch (Exception e) {
                        _logger.info(e.getMessage());
                    }
                }
            }
        } else {
            _logger.info("[0x24][0x25][0x28][0x29]无法推送消息，没有找到vin:" + vin + "对应的用户");
        }
    }

    /**
     * 根据报警hex信息生成文本性质的报警提示
     *
     * @param vin      vin
     * @param msg      16进制报警信息
     * @param user     user
     * @param onefirst 气囊/碰撞/ATA 优先
     * @return 根据报警hex信息生成文本性质的报警提示
     */
    public Map<String, Object> getWarningMessageForPush(String vin, String msg, User user, int onefirst) {
        //报警数据保存
        _logger.info("[0x24]处理报警数据，看是否需要推送:" + msg);
        WarningMessageData wd = new WarningMessageData();
        WarningMessage bean = dataTool.decodeWarningMessage(msg);
        boolean isM85 = true;
        short vehicleModel = bean.getVehicleModel();//按照协议0628车型编号 0~255 0：默认值(M82)；1：M82；2：M85； 3：F60；4：F70； 5：F60电动车
        if (vehicleModel == (short) 2) {
            bean = dataTool.decodeWarningMessageM85(msg);
            WarningMessageM85 beanM85 = (WarningMessageM85) bean;
            if(beanM85.getTowWarning() != null){
                wd.setTowWarning(beanM85.getTowWarning().shortValue());
            }
        }
        wd.setVin(vin);
        wd.setImei(bean.getImei());
        wd.setApplicationId(bean.getApplicationID());
        wd.setMessageId(bean.getMessageID());
        wd.setSendingTime(dataTool.seconds2Date(bean.getSendingTime()));
        //分解IsIsLocation信息
        char[] location = dataTool.getBitsFromShort(bean.getIsLocation());
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
        RealTimeReportData rd = realTimeReportDataRespository.getLatestOneByVin(vin);
//        List<RealTimeReportData> rdList = realTimeReportDataRespository.findLatestOneByVin(vin);
//        RealTimeReportData rd=null;
//        if(rdList!=null&&rdList.size()>0) {
//            rd = rdList.get(0);
//        }

        //生成报警信息
        Map<String, Object> warningMessage = buildWarningString(wd, user, rd, onefirst);
        return warningMessage;
    }

    /**
     * 根据报警hex信息生成文本性质的报警短信
     *
     * @param vin   vin
     * @param msg   16进制报警信息
     * @param phone 接收短信号码
     * @return 根据报警hex信息生成文本性质的报警短信
     */
    public void sendWarningMessageSms(String vin, String msg, String phone, int oneFirst) {
        //报警数据保存
        _logger.info("[0x24]处理报警数据，看是否需要短信:" + msg);
        WarningMessage bean = dataTool.decodeWarningMessage(msg);
        WarningMessageData wd = new WarningMessageData();
        wd.setVin(vin);
        wd.setImei(bean.getImei());
        wd.setApplicationId(bean.getApplicationID());
        wd.setMessageId(bean.getMessageID());
        wd.setSendingTime(dataTool.seconds2Date(bean.getSendingTime()));
        //分解IsIsLocation信息
        char[] location = dataTool.getBitsFromShort(bean.getIsLocation());
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

        Short lastSrsWarning = 0;
        Short lastCrashWarning = 0;
        Short lastAtaWarning = 0;
//        WarningMessageData wmd = this.getWarningMessageData(vin);
//        if(wmd!=null){
//            lastSrsWarning=wmd.getSrsWarning();
//            lastCrashWarning=wmd.getCrashWarning();
//            lastAtaWarning=wmd.getAtaWarning();
//        }

        //判断条件改为通过message表中的上一条报警状态
        Message srsMsg = this.getWarningMessageData(vin, 8);
        Message crashMsg = this.getWarningMessageData(vin, 8);
        Message ataMsg = this.getWarningMessageData(vin, 9);
        if (srsMsg != null) {
            Integer cleanFlag = srsMsg.getCleanFlag();
            //上一条报警消息标志 0报警 1消除
            if (cleanFlag == 0) {
                lastSrsWarning = 1;
            }
        }

        if (crashMsg != null) {
            Integer cleanFlag = crashMsg.getCleanFlag();
            //上一条报警消息标志 0报警 1消除
            if (cleanFlag == 0) {
                lastCrashWarning = 1;
            }
        }

        if (ataMsg != null) {
            Integer cleanFlag = ataMsg.getCleanFlag();
            //上一条报警消息标志 0报警 1消除
            if (cleanFlag == 0) {
                lastAtaWarning = 1;
            }
        }

        _logger.info("[0x24]lastSrsWarning" + lastSrsWarning + "---lastCrashWarning" + lastCrashWarning + "---lastAtaWarning" + lastAtaWarning);
        _logger.info("[0x24]nowSrsWarning" + wd.getSrsWarning() + "---nowCrashWarning" + wd.getCrashWarning() + "---nowAtaWarning" + wd.getAtaWarning());

        if (oneFirst == SRSFIRST) {
            if (lastSrsWarning == (short) 0 && wd.getSrsWarning() == (short) 1) {
                GpsData gpsData = new GpsData();
                gpsData.setLatitude(wd.getLatitude());
                gpsData.setLongitude(wd.getLongitude());
                //GpsData baiduGpsData= gpsTool.getDataFromBaidu(gpsData);
                StringBuilder sb = new StringBuilder();
                StringBuilder longU = new StringBuilder();
                String srs = "";
                try {
                    srs = "[华晨汽车Bri-Air]尊敬的用户，您的爱车于" + DateUtil.formatDateByFormat(new Date(), "yyyy-MM-dd,HH:mm:ss") + "发生车辆碰撞，建议您尽快确认车辆状态。";
                    srs = java.net.URLEncoder.encode(srs, "UTF-8");
//                    srs = java.net.URLEncoder.encode(srs, "GB2312");
                } catch (Exception e) {
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
                _logger.info("[0x24]发送短信:" + phone + ":" + smsStr);
                //调用工具类发起 http请求
                smsHttpTool.doHttp(phone, smsStr);
            }
        } else if (oneFirst == CRASHFIRST) {
            if (lastCrashWarning == (short) 0 && wd.getCrashWarning() == (short) 1) {
                GpsData gpsData = new GpsData();
                gpsData.setLatitude(wd.getLatitude());
                gpsData.setLongitude(wd.getLongitude());
                //GpsData baiduGpsData= gpsTool.getDataFromBaidu(gpsData);
                StringBuilder sb = new StringBuilder();
                StringBuilder longU = new StringBuilder();
                String srs = "";
                try {
                    srs = "[华晨汽车Bri-Air]尊敬的用户，您的爱车于" + DateUtil.formatDateByFormat(new Date(), "yyyy-MM-dd,HH:mm:ss") + "发生车辆碰撞，建议您尽快确认车辆状态。";
                    srs = java.net.URLEncoder.encode(srs, "UTF-8");
//                    srs = java.net.URLEncoder.encode(srs, "GB2312");
                } catch (Exception e) {
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
                _logger.info("[0x24]发送短信:" + phone + ":" + smsStr);
                //调用工具类发起 http请求
                smsHttpTool.doHttp(phone, smsStr);
            }
        } else if (oneFirst == ATAFIRST) {
            if (lastAtaWarning == (short) 0 && wd.getAtaWarning() == (short) 1) {
                /*GpsData gpsData=new GpsData();
                gpsData.setLatitude(wd.getLatitude());
                gpsData.setLongitude(wd.getLongitude());
                GpsData baiduGpsData= gpsTool.getDataFromBaidu(gpsData);*/
                StringBuilder sb = new StringBuilder();
                StringBuilder longU = new StringBuilder();
                String srs = "";
                try {
                    srs = "[华晨汽车Bri-Air]尊敬的用户，您的爱车于" + DateUtil.formatDateByFormat(new Date(), "yyyy-MM-dd,HH:mm:ss") + "车门被异常开启，建议您尽快确认车辆状态。";
                    srs = java.net.URLEncoder.encode(srs, "UTF-8");
//                    srs = java.net.URLEncoder.encode(srs, "GB2312");
                } catch (Exception e) {
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
                _logger.info("[0x24]发送短信:" + phone + ":" + smsStr);
                //调用工具类发起 http请求
                smsHttpTool.doHttp(phone, smsStr);
            }

        }

    }

    /**
     * 根据报警hex信息生成文本性质的报警短信
     *
     * @param vin   vin
     * @param msg   16进制报警信息
     * @param phone 接收短信号码
     * @return 根据报警hex信息生成文本性质的报警短信
     */
    public void sendResendWarningMessageSms(String vin, String msg, String phone, int oneFirst) {
        //报警数据保存
        _logger.info("[0x25]处理补发报警数据，看是否需要短信:" + msg);
        DataResendWarningMes bean = dataTool.decodeResendWarningMessage(msg);
        WarningMessageData wd = new WarningMessageData();

/*        WarningMessage bean=dp.loadBean(WarningMessage.class);
          WarningMessageData wd=new WarningMessageData();*/
        wd.setVin(vin);
        wd.setImei(bean.getImei());
        wd.setApplicationId(bean.getApplicationID());
        wd.setMessageId(bean.getMessageID());
        wd.setSendingTime(dataTool.seconds2Date(bean.getSendingTime()));
        //分解IsIsLocation信息
        char[] location = dataTool.getBitsFromShort(bean.getIsLocation());
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


        Short lastSrsWarning = 0;
        Short lastAtaWarning = 0;
        Short lastCrashWarning = 0;
//        WarningMessageData wmd = this.getWarningMessageData(vin);
//        if(wmd!=null){
//            lastSrsWarning=wmd.getSrsWarning();
//            lastCrashWarning=wmd.getCrashWarning();
//            lastAtaWarning=wmd.getAtaWarning();
//        }

        //判断条件改为通过message表中的上一条报警状态
        Message srsMsg = this.getWarningMessageData(vin, 8);
        Message crashMsg = this.getWarningMessageData(vin, 8);
        Message ataMsg = this.getWarningMessageData(vin, 9);
        if (srsMsg != null) {
            Integer cleanFlag = srsMsg.getCleanFlag();
            //上一条报警消息标志 0报警 1消除
            if (cleanFlag == 0) {
                lastSrsWarning = 1;
            }
        }

        if (crashMsg != null) {
            Integer cleanFlag = crashMsg.getCleanFlag();
            //上一条报警消息标志 0报警 1消除
            if (cleanFlag == 0) {
                lastCrashWarning = 1;
            }
        }

        if (ataMsg != null) {
            Integer cleanFlag = ataMsg.getCleanFlag();
            //上一条报警消息标志 0报警 1消除
            if (cleanFlag == 0) {
                lastAtaWarning = 1;
            }
        }

        _logger.info("[0x25]lastSrsWarning" + lastSrsWarning + "---lastCrashWarning" + lastCrashWarning + "---lastAtaWarning" + lastAtaWarning);
        _logger.info("[0x25]nowSrsWarning" + wd.getSrsWarning() + "---nowCrashWarning" + wd.getCrashWarning() + "---nowAtaWarning" + wd.getAtaWarning());

        if (oneFirst == SRSFIRST) {
            if (lastSrsWarning == (short) 0 && wd.getSrsWarning() == (short) 1) {
              /*  GpsData gpsData=new GpsData();
                gpsData.setLatitude(wd.getLatitude());
                gpsData.setLongitude(wd.getLongitude());
                GpsData baiduGpsData= gpsTool.getDataFromBaidu(gpsData);*/
                StringBuilder sb = new StringBuilder();
                StringBuilder longU = new StringBuilder();
                String srs = "";
                try {
                    srs = "[华晨汽车Bri-Air]尊敬的用户，您的爱车于" + DateUtil.formatDateByFormat(new Date(), "yyyy-MM-dd,HH:mm:ss") + "发生车辆碰撞，建议您尽快确认车辆状态。";
                    srs = java.net.URLEncoder.encode(srs, "UTF-8");
//                    srs = java.net.URLEncoder.encode(srs, "GB2312");
                } catch (Exception e) {
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
                _logger.info("[0x25]发送短信:" + phone + ":" + smsStr);
                //调用工具类发起 http请求
                smsHttpTool.doHttp(phone, smsStr);
            }
        } else if (oneFirst == CRASHFIRST) {
            if (lastCrashWarning == (short) 0 && wd.getCrashWarning() == (short) 1) {
              /*  GpsData gpsData=new GpsData();
                gpsData.setLatitude(wd.getLatitude());
                gpsData.setLongitude(wd.getLongitude());
                GpsData baiduGpsData= gpsTool.getDataFromBaidu(gpsData);*/
                StringBuilder sb = new StringBuilder();
                StringBuilder longU = new StringBuilder();
                String srs = "";
                try {
                    srs = "[华晨汽车Bri-Air]尊敬的用户，您的爱车于" + DateUtil.formatDateByFormat(new Date(), "yyyy-MM-dd,HH:mm:ss") + "发生车辆碰撞，建议您尽快确认车辆状态。";
                    srs = java.net.URLEncoder.encode(srs, "UTF-8");
//                    srs = java.net.URLEncoder.encode(srs, "GB2312");
                } catch (Exception e) {
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
                _logger.info("[0x25]发送短信:" + phone + ":" + smsStr);
                //调用工具类发起 http请求
                smsHttpTool.doHttp(phone, smsStr);
            }
        } else if (oneFirst == ATAFIRST) {
            if (lastAtaWarning == (short) 0 && wd.getAtaWarning() == (short) 1) {
            /*    GpsData gpsData=new GpsData();
                gpsData.setLatitude(wd.getLatitude());
                gpsData.setLongitude(wd.getLongitude());
                GpsData baiduGpsData= gpsTool.getDataFromBaidu(gpsData);*/
                StringBuilder sb = new StringBuilder();
                StringBuilder longU = new StringBuilder();
                String srs = "";
                try {
                    srs = "[华晨汽车Bri-Air]尊敬的用户，您的爱车于" + DateUtil.formatDateByFormat(new Date(), "yyyy-MM-dd,HH:mm:ss") + "车门被异常开启，建议您尽快确认车辆状态。";
                    srs = java.net.URLEncoder.encode(srs, "UTF-8");
//                    srs = java.net.URLEncoder.encode(srs, "GB2312");
                } catch (Exception e) {
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
                _logger.info("[0x25]发送短信:" + phone + ":" + smsStr);
                //调用工具类发起 http请求
                smsHttpTool.doHttp(phone, smsStr);
            }

        }

    }

    /**
     * 根据补发报警hex信息生成文本性质的报警提示
     *
     * @param vin      vin
     * @param msg      16进制报警信息
     * @param user     user
     * @param oneFirst 气囊/碰撞/防盗优先
     * @return 根据报警hex信息生成文本性质的报警提示
     */
    public Map<String, Object> getResendWarningMessageForPush(String vin, String msg, User user, int oneFirst) {
        //报警数据保存
        _logger.info("[0x25]处理补发报警数据，看是否需要推送:" + msg);
        DataResendWarningMes bean = dataTool.decodeResendWarningMessage(msg);
        WarningMessageData wd = new WarningMessageData();
        wd.setVin(vin);
        wd.setImei(bean.getImei());
        wd.setApplicationId(bean.getApplicationID());
        wd.setMessageId(bean.getMessageID());
        wd.setSendingTime(dataTool.seconds2Date(bean.getSendingTime()));
        //分解IsIsLocation信息
        char[] location = dataTool.getBitsFromShort(bean.getIsLocation());
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

        RealTimeReportData rd = realTimeReportDataRespository.getLatestOneByVin(vin);
//        List<RealTimeReportData> rdList = realTimeReportDataRespository.findLatestOneByVin(vin);
//        RealTimeReportData rd=null;
//        if(rdList!=null&&rdList.size()>0) {
//            rd = rdList.get(0);
//        }
        //生成报警信息
        Map<String, Object> warningMessage = buildWarningString(wd, user, rd, oneFirst);
        return warningMessage;
    }

    /**
     * 根据故障hex信息生成文本性质的报警提示
     *
     * @param vin vin
     * @param msg 16进制报警信息
     * @return 根据故障hex信息生成文本性质的报警提示
     */
    public Map<String, Object> getFailureMessageForPush(String vin, String msg) {
        //故障数据
        _logger.info("[0x28][0x29]生成报警推送消息:" + msg);
        ByteBuffer bb = PackageEntityManager.getByteBuffer(msg);
        DataPackage dp = conversionTBox.generate(bb);
        FailureMessage bean = dp.loadBean(FailureMessage.class);
        FailureMessageData wd = new FailureMessageData();
        wd.setVin(vin);
        wd.setImei(bean.getImei());
        wd.setApplicationId(bean.getApplicationID());
        wd.setMessageId(bean.getMessageID());
        wd.setSendingTime(dataTool.seconds2Date(bean.getSendingTime()));
        //分解IsIsLocation信息
        char[] location = dataTool.getBitsFromShort(bean.getIsLocation());
        wd.setIsLocation(location[7] == '0' ? (short) 0 : (short) 1);//bit0 0有效定位 1无效定位
        wd.setNorthSouth(location[6] == '0' ? "N" : "S");//bit1 0北纬 1南纬
        wd.setEastWest(location[5] == '0' ? "E" : "W");//bit2 0东经 1西经
        wd.setLatitude(dataTool.getTrueLatAndLon(bean.getLatitude()));
        wd.setLongitude(dataTool.getTrueLatAndLon(bean.getLongitude()));
        wd.setSpeed(dataTool.getTrueSpeed(bean.getSpeed()));
        wd.setHeading(bean.getHeading());

        wd.setInfo(dataTool.getFailureMesId(bean));

        RealTimeReportData rd = realTimeReportDataRespository.getLatestOneByVin(vin);
//        List<RealTimeReportData> rdList = realTimeReportDataRespository.findLatestOneByVin(vin);
//        RealTimeReportData rd=null;
//        if(rdList!=null&&rdList.size()>0) {
//            rd = rdList.get(0);
//        }
        //RealTimeDataShow realTimeDataShow = vehicleDataService.getRealTimeData(vin);
        //生成故障信息
        Map<String, Object> failureString = buildFailureString(wd, rd, bean.getVehicleModel());
        return failureString;
    }

    /**
     * 根据补发故障hex信息生成文本性质的报警提示
     *
     * @param vin vin
     * @param msg 16进制故障信息
     * @return 根据故障hex信息生成文本性质的故障提示
     */
    public Map<String, Object> getResendFailureMessageForPush(String vin, String msg) {
        //报警数据保存
        _logger.info("[0x29]生成补发报警推送消息:" + msg);
        ByteBuffer bb = PackageEntityManager.getByteBuffer(msg);
        DataPackage dp = conversionTBox.generate(bb);
        DataResendFailureData bean = dp.loadBean(DataResendFailureData.class);
        FailureMessageData wd = new FailureMessageData();
        wd.setVin(vin);
        wd.setImei(bean.getImei());
        wd.setApplicationId(bean.getApplicationID());
        wd.setMessageId(bean.getMessageID());
        wd.setSendingTime(dataTool.seconds2Date(bean.getSendingTime()));
        //分解IsIsLocation信息
        char[] location = dataTool.getBitsFromShort(bean.getIsLocation());
        wd.setIsLocation(location[7] == '0' ? (short) 0 : (short) 1);//bit0 0有效定位 1无效定位
        wd.setNorthSouth(location[6] == '0' ? "N" : "S");//bit1 0北纬 1南纬
        wd.setEastWest(location[5] == '0' ? "E" : "W");//bit2 0东经 1西经
        wd.setLatitude(dataTool.getTrueLatAndLon(bean.getLatitude()));
        wd.setLongitude(dataTool.getTrueLatAndLon(bean.getLongitude()));
        wd.setSpeed(dataTool.getTrueSpeed(bean.getSpeed()));
        wd.setHeading(bean.getHeading());

        wd.setInfo(dataTool.getDataResendFailureMesId(bean));

        RealTimeReportData rd = realTimeReportDataRespository.getLatestOneByVin(vin);
//        List<RealTimeReportData> rdList = realTimeReportDataRespository.findLatestOneByVin(vin);
//        RealTimeReportData rd=null;
//        if(rdList!=null&&rdList.size()>0) {
//            rd = rdList.get(0);
//        }
        //生成故障信息
        Map<String, Object> failureString = buildFailureString(wd, rd, bean.getVehicleModel());
        return failureString;
    }

    /**
     * 根据报警消息类生成报警消息
     *
     * @param wd                 报警消息实体类
     * @param user               user
     * @param realTimeReportData 实时数据实体类
     * @param oneFirst           气囊优先
     * @return 便于阅读的报警消息
     */
    public Map<String, Object> buildWarningString(WarningMessageData wd, User user, RealTimeReportData realTimeReportData, int oneFirst) {
        Map<String, Object> dataMap = new HashMap<String, Object>();
        // StringBuilder sb=new StringBuilder() ;
        Map<String, Object> jsonMap = new HashMap<String, Object>();
        Map<String, String> positionMap = new HashMap<String, String>();

        //获取上一条报警消息
        String vin = wd.getVin();
        Short lastSrsWarning = 0;
        Short lastCrashWarning = 0;
        Short lastAtaWarning = 0;
//        WarningMessageData wmd = this.getWarningMessageData(vin);
//        if(wmd!=null){
//            lastSrsWarning=wmd.getSrsWarning();
//            lastCrashWarning=wmd.getCrashWarning();
//            lastAtaWarning=wmd.getAtaWarning();
//        }
        //判断条件改为通过message表中的上一条报警状态
        Message srsMsg = this.getWarningMessageData(vin, 8);
        Message crashMsg = this.getWarningMessageData(vin, 8);
        Message ataMsg = this.getWarningMessageData(vin, 9);
        if (srsMsg != null) {
            Integer cleanFlag = srsMsg.getCleanFlag();
            //上一条报警消息标志 0报警 1消除
            if (cleanFlag == 0) {
                lastSrsWarning = 1;
            }
        }

        if (crashMsg != null) {
            Integer cleanFlag = crashMsg.getCleanFlag();
            //上一条报警消息标志 0报警 1消除
            if (cleanFlag == 0) {
                lastCrashWarning = 1;
            }
        }

        if (ataMsg != null) {
            Integer cleanFlag = ataMsg.getCleanFlag();
            //上一条报警消息标志 0报警 1消除
            if (cleanFlag == 0) {
                lastAtaWarning = 1;
            }
        }

        _logger.info("[0x24][0x25]lastSrsWarning" + lastSrsWarning + "---lastCrashWarning" + lastCrashWarning + "---lastAtaWarning" + lastAtaWarning);
        _logger.info("[0x24][0x25]nowSrsWarning" + wd.getSrsWarning() + "---nowCrashWarning" + wd.getCrashWarning() + "---nowAtaWarning" + wd.getAtaWarning());
        //sb.append("车辆报警信息: ");
        if (wd.getIsLocation() == (short) 0) {
            //0有效 1无效
/*          sb.append("当前位置:");
            sb.append("经度:").append(wd.getLongitude()).append(wd.getEastWest()).append(",");
            sb.append("纬度").append(wd.getLatitude()).append(wd.getNorthSouth()).append(";");
            sb.append("速度:").append(wd.getSpeed()).append("km/h;");
            sb.append("方向:").append(wd.getHeading()).append(";");*/
            positionMap.put("longitude", new StringBuilder().append(wd.getLongitude()).toString());
            positionMap.put("latitude", new StringBuilder().append(wd.getLatitude()).toString());
            positionMap.put("speed", new StringBuilder().append(wd.getSpeed()).toString());
            positionMap.put("heading", new StringBuilder().append(wd.getHeading()).toString());

        } else {
            positionMap.put("longitude", "");
            positionMap.put("latitude", "");
            positionMap.put("speed", "");
            positionMap.put("heading", "");
        }
        if (oneFirst == SRSFIRST) {
            _logger.info("[0x24][0x25]仅处理气囊");
            //仅处理气囊
            if (wd.getSrsWarning() == (short) 1) {
                //安全气囊报警 0未触发 1触发
/*            sb.append("安全气囊报警触发,");
            sb.append("背扣安全带数量:").append(wd.getSafetyBeltCount()).append(",");
            sb.append("碰撞速度:").append(wd.getVehicleHitSpeed()).append("km/h;");*/
                jsonMap.put("srs_warning", "1");
                jsonMap.put("safety_belt_count", wd.getSafetyBeltCount());
                jsonMap.put("vehicle_hit_speed", new StringBuilder().append(wd.getVehicleHitSpeed()).toString());
                dataMap.put("pType", 8);
                dataMap.put("cleanFlag", "0");

                String contactsPhone = "";
                if (user != null) {
                    contactsPhone = user.getContactsPhone();
                }
                jsonMap.put("contacts_phone", contactsPhone);
            }
            if (wd.getSrsWarning() == (short) 0 && lastSrsWarning == 1) {//srs1--0 碰撞解除
                //推srs解除
                jsonMap.put("srs_warning", "0");
                jsonMap.put("safety_belt_count", wd.getSafetyBeltCount());
                jsonMap.put("vehicle_hit_speed", new StringBuilder().append(wd.getVehicleHitSpeed()).toString());
                dataMap.put("pType", 8);
                dataMap.put("cleanFlag", "1");

                String contactsPhone = "";
                if (user != null) {
                    contactsPhone = user.getContactsPhone();
                }
                jsonMap.put("contacts_phone", contactsPhone);
            }
            if (wd.getSrsWarning() == (short) 0 && lastSrsWarning == 0) {//碰撞一直没有
                return null;
            }
        } else if (oneFirst == CRASHFIRST) {
            _logger.info("[0x24][0x25]仅处理碰撞");
            //仅处理气囊
            if (wd.getCrashWarning() == (short) 1) {
                //安全气囊报警 0未触发 1触发
/*            sb.append("安全气囊报警触发,");
            sb.append("背扣安全带数量:").append(wd.getSafetyBeltCount()).append(",");
            sb.append("碰撞速度:").append(wd.getVehicleHitSpeed()).append("km/h;");*/
                jsonMap.put("srs_warning", "1");
                jsonMap.put("safety_belt_count", wd.getSafetyBeltCount());
                jsonMap.put("vehicle_hit_speed", new StringBuilder().append(wd.getVehicleHitSpeed()).toString());
                dataMap.put("pType", 8);
                dataMap.put("cleanFlag", "0");

                String contactsPhone = "";
                if (user != null) {
                    contactsPhone = user.getContactsPhone();
                }
                jsonMap.put("contacts_phone", contactsPhone);
            }
            if (wd.getCrashWarning() == (short) 0 && lastCrashWarning == 1) {//srs1--0 碰撞解除
                //推srs解除
                jsonMap.put("srs_warning", "0");
                jsonMap.put("safety_belt_count", wd.getSafetyBeltCount());
                jsonMap.put("vehicle_hit_speed", new StringBuilder().append(wd.getVehicleHitSpeed()).toString());
                dataMap.put("pType", 8);
                dataMap.put("cleanFlag", "1");

                String contactsPhone = "";
                if (user != null) {
                    contactsPhone = user.getContactsPhone();
                }
                jsonMap.put("contacts_phone", contactsPhone);
            }
            if (wd.getCrashWarning() == (short) 0 && lastCrashWarning == 0) {//碰撞一直没有
                return null;
            }
        } else if (oneFirst == ATAFIRST) {
            _logger.info("[0x24][0x25]仅处理防盗");
            //仅处理防盗
            if (wd.getAtaWarning() == (short) 1) {
                //防盗报警 0未触发 1触发
                // sb.append("车辆防盗报警触发");
                jsonMap.put("ata_warning", "1");
                dataMap.put("pType", 9);
                dataMap.put("cleanFlag", "0");
                String leftFrontDoorInformation = "1";
                String leftRearDoorInformation = "1";
                String rightFrontDoorInformation = "1";
                String rightRearDoorInformation = "1";
                String engineCoverState = "1";
                String trunkLidState = "1";
                if (realTimeReportData != null) {
                    if (realTimeReportData.getLeftFrontDoorInformation().equals("1")) {
                        leftFrontDoorInformation = "0";
                    }
                    if (realTimeReportData.getLeftRearDoorInformation().equals("1")) {
                        leftRearDoorInformation = "0";
                    }
                    if (realTimeReportData.getRightFrontDoorInformation().equals("1")) {
                        rightFrontDoorInformation = "0";
                    }
                    if (realTimeReportData.getRightRearDoorInformation().equals("1")) {
                        rightRearDoorInformation = "0";
                    }
                    if (realTimeReportData.getEngineCoverState().equals("1")) {
                        engineCoverState = "0";
                    }
                    if (realTimeReportData.getTrunkLidState().equals("1")) {
                        trunkLidState = "0";
                    }
                }
                jsonMap.put("leftFrontDoorInformation", leftFrontDoorInformation);
                jsonMap.put("leftRearDoorInformation", leftRearDoorInformation);
                jsonMap.put("rightFrontDoorInformation", rightFrontDoorInformation);
                jsonMap.put("rightRearDoorInformation", rightRearDoorInformation);
                jsonMap.put("engineCoverState", engineCoverState);
                jsonMap.put("trunkLidState", trunkLidState);
            }
            if (wd.getAtaWarning() == (short) 0 && lastAtaWarning == 1) {//atr1---0 防盗解除
                //推ata解除
                jsonMap.put("ata_warning", "0");
                dataMap.put("pType", 9);
                dataMap.put("cleanFlag", "1");
                String leftFrontDoorInformation = "1";
                String leftRearDoorInformation = "1";
                String rightFrontDoorInformation = "1";
                String rightRearDoorInformation = "1";
                String engineCoverState = "1";
                String trunkLidState = "1";
                if (realTimeReportData != null) {
                    if (realTimeReportData.getLeftFrontDoorInformation().equals("1")) {
                        leftFrontDoorInformation = "0";
                    }
                    if (realTimeReportData.getLeftRearDoorInformation().equals("1")) {
                        leftRearDoorInformation = "0";
                    }
                    if (realTimeReportData.getRightFrontDoorInformation().equals("1")) {
                        rightFrontDoorInformation = "0";
                    }
                    if (realTimeReportData.getRightRearDoorInformation().equals("1")) {
                        rightRearDoorInformation = "0";
                    }
                    if (realTimeReportData.getEngineCoverState().equals("1")) {
                        engineCoverState = "0";
                    }
                    if (realTimeReportData.getTrunkLidState().equals("1")) {
                        trunkLidState = "0";
                    }
                }
                jsonMap.put("leftFrontDoorInformation", leftFrontDoorInformation);
                jsonMap.put("leftRearDoorInformation", leftRearDoorInformation);
                jsonMap.put("rightFrontDoorInformation", rightFrontDoorInformation);
                jsonMap.put("rightRearDoorInformation", rightRearDoorInformation);
                jsonMap.put("engineCoverState", engineCoverState);
                jsonMap.put("trunkLidState", trunkLidState);
            }
            if (wd.getAtaWarning() == (short) 0 && lastAtaWarning == 0) {//atr1---0 防盗一直没有
                return null;
            }
        }else if(oneFirst == TOWRST){
            //TODO 徐卫超
            //this.mqService.pushToUser(uid, pushMsg);
    /*                    * @param sourceId    发送用户id<br>
                        * @param resourceFrom  数据来源,1 手机,2 车机<br>
                        * @param targetType 目标对象类型 ,1 user ,2 organize <br>
                        * @param targetId 目标对象id<br>
                        * @param resourceTo 目标类型 ,1 手机,2 车机,3 短信,4 手机,车机 ,短信<br>1
                        * @param funType 消息类型 ,1 ,即时通讯 ,2 ,推送消息<br>2
                        * @param pType 推送消息类型,1 远程控制,2 故障提醒,3 保养提醒,4 位置分享,5 智能寻车,6 位置共享,7 发送位置,8 气囊报警,9 防盗报警,10 拖车<br>289
                        * @param textContent 发送内容文本、经纬度<br>
                        * @param contentType 发送内容类型 ,1 文本,2 音乐,3 图片,41 位置信息,42 位置共享开始,43 位置共享结束<br>
                        * @param messageNums 信息条数 count
                        * @param cleanFlag 消息推送消除标志，仅针对 气囊报警、防盗报警,0报警,1消除
                        * @param file 上传的附件*/
            if (wd.getTowWarning() == (short) 1) {
                //防盗报警 0未触发 1触发
                jsonMap.put("ata_warning", "1");
                dataMap.put("pType", 10);
                dataMap.put("cleanFlag", "0");
                dataMap.put("funType",2);
            }
            if (wd.getTowWarning() == (short) 0) {//atr1---0 防盗解除
                //推ata解除
                jsonMap.put("ata_warning", "0");
                dataMap.put("pType", 10);
                dataMap.put("cleanFlag", "1");
                dataMap.put("funType",2);
            }
        }

        if (jsonMap.size() == 0) {
            return null;
        }
        jsonMap.put("position", positionMap);
        String contextJson = JSON.toJSONString(jsonMap);

        dataMap.put("textContent", contextJson);
        return dataMap;
    }

    public WarningMessageData getWarningMessageData(String vin) {
        List<WarningMessageData> list = warningMessageDataRespository.findTop1ByVinOrderByReceiveTimeDesc(vin);
        WarningMessageData wmd = null;
        if (list != null && list.size() > 0) {
            wmd = list.get(0);
        }
        return wmd;
    }

    public Message getWarningMessageData(String vin, Integer pType) {
        Message msg = messageRepository.getLatestOneByVin(vin, pType);
        return msg;
    }

    /**
     * 根据故障消息类生成故障告警消息
     *
     * @param wd                 故障消息实体类
     * @param realTimeReportData 实时数据实体类
     * @return 便于阅读的消息
     */
    public Map<String, Object> buildFailureString(FailureMessageData wd, RealTimeReportData realTimeReportData, Short model) {
        Map<String, Object> dataMap = new HashMap<String, Object>();

        Map<String, Object> jsonMap = new HashMap<String, Object>();
        // Map<String,String> positionMap = new HashMap<String,String>();
        HashSet<String> failInfo = new HashSet<String>();

        int drivingRange = 0;
        int drivingTime = 0;
        if (realTimeReportData != null) {
            drivingRange = realTimeReportData.getDrivingRange();
            drivingTime = realTimeReportData.getDrivingTime();
        }
        jsonMap.put("driving_range", drivingRange);
        jsonMap.put("driving_time", dataTool.getCurrentDate());

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
        List<WarningMessageConversion> allList = warningMessageConversionRepository.findAll();
        String[] failureId = wd.getIdArray();//包含故障信息ID的数组

        for (int i = 0; i < failureId.length; i++) {
            for (int j = 0; j < allList.size(); j++) {
                WarningMessageConversion warningMessageConversion = allList.get(j);
                if (warningMessageConversion.getMessageId().equals(failureId[i])) {
                    if (model == 3 && warningMessageConversion.getType() == 2) {//F60
                        failInfo.add(warningMessageConversion.getGroupMessage());
                    } else if (model != 3 && warningMessageConversion.getType() == 1) {
                        failInfo.add(warningMessageConversion.getGroupMessage());
                    }
                }
            }
        }
        //jsonMap.put("position",positionMap);
        jsonMap.put("failure_info", failInfo);
        jsonMap.put("failure_num", failInfo.size());
        String contextJson = JSON.toJSONString(jsonMap);

        dataMap.put("messageNums", failInfo.size());
        dataMap.put("textContent", contextJson);
        dataMap.put("pType", 2);

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
     *
     * @param vin     vin
     * @param eventId eventId
     * @return 封装远程控制参数的RemoteControl对象
     */
    public RemoteControl getRemoteCmdValueFromDb(String vin, long eventId) {
        String sessionId = String.valueOf(eventId);
        RemoteControl rc = remoteControlRepository.findTopByVinAndSessionIdOrderBySendingTimeDesc(vin, sessionId);
        if (rc == null) {
            _logger.info("[0x31]No RemoteControl found in db,vin:" + vin + "|eventId:" + eventId);
            return null;
        } else {
            return rc;
        }
        //取出暂存redis控制参数
    }

    /**
     * 更新远程控制记录的eventId
     *
     * @param rc 远程控制对象
     * @return 封装远程控制参数的RemoteControl对象
     */
    public RemoteControl modifyRemoteControl(RemoteControl rc) {
        String newSessionId = String.valueOf(dataTool.getCurrentSeconds() - 1500);//避免重复的eventId
        rc.setRefId(-2l);
        rc.setSessionId(newSessionId);
        RemoteControl retRc = remoteControlRepository.save(rc);
        return retRc;

    }

    /**
     * 更新远程控制记录的eventId
     *
     * @param rc 远程控制对象
     * @return 封装远程控制参数的RemoteControl对象
     */
    public RemoteControl modifyOrignalRemoteControl(RemoteControl rc) {
        RemoteControl retRc = remoteControlRepository.save(rc);
        return rc;

    }


    /**
     * 生成一个简单remoteControl 用于生成启动发动机命令,无需数据库存储
     *
     * @param vin vin
     * @return 封装远程控制参数的RemoteControl对象
     */
    public RemoteControl getStartEngineRemoteControl(int uid, String vin, long eventId, long refId, short isAnnounce) {
        RemoteControl remoteControl = new RemoteControl();
        String sessionId = String.valueOf(eventId);
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
        remoteControl.setAutoMode((short) 0);
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
        remoteControl.setIsAnnounce(isAnnounce);
        remoteControlRepository.save(remoteControl);
        return remoteControl;
    }

    /**
     * 从数据库取出暂存的远程控制参数
     *
     * @param id id
     * @return 封装远程控制参数的RemoteControl对象
     */
    public RemoteControl getRemoteCmdValueFromDb(long id) {
        RemoteControl rc = remoteControlRepository.findOne(id);
        if (rc == null) {
            _logger.info("[0x31]没有在数据库找到远程控制记录,id:" + id);
            return null;
        } else {
            return rc;
        }
    }


    /**
     * 处理远程控制Ack上行（持久化 push）
     *
     * @param vin     vin
     * @param eventId eventId
     */
    public void handleRemoteControlPreconditionResp(String vin, long eventId, String message, String messageEn, boolean push) {
        String sessionId = String.valueOf(eventId);
        RemoteControl rc = remoteControlRepository.findTopByVinAndSessionIdOrderBySendingTimeDesc(vin, sessionId);
        if (rc == null) {
            _logger.info("[0x31]没有在数据库找到远程控制记录,vin:" + vin + "|eventId:" + eventId);
        } else {
            //持久化远程控制记录状态，push to sender
            _logger.info("[0x31]远程控制Precondition响应处理开始");
            //返回无效才更新db记录
            rc.setRemark(message);
            rc.setRemarkEn(messageEn);
            rc.setStatus((short) 0);
            remoteControlRepository.save(rc);
            String pushMsg = message;
            _logger.info("[0x31]远程控制Precondition响应推送消息>:" + pushMsg);
            if (push) {
                try {
                    this.pushRemoteControlResult(rc.getId(), rc.getVin(), String.valueOf(eventId), 0, pushMsg);
                } catch (RuntimeException e) {
                    _logger.info(e.getMessage());
                }
            }
            _logger.info("[0x31]远程控制Precondition响应处理结束");
        }
    }

    /**
     * 处理远程控制Ack上行（持久化 push）
     *
     * @param vin     vin
     * @param eventId eventId
     * @param result  Ack响应结果  0：无效 1：命令已接收
     */
    public void handleRemoteControlAck(String vin, long eventId, Short result, boolean push) {
        String sessionId = String.valueOf(eventId);
        //  Rst 0：无效 1：命令已接收
        RemoteControl rc = remoteControlRepository.findTopByVinAndSessionIdOrderBySendingTimeDesc(vin, sessionId);
        if (rc == null) {
            _logger.info("[0x31]没有在数据库找到远程控制记录,vin:" + vin + "|eventId:" + eventId + "|result:" + result);
        } else {
            //持久化远程控制记录状态，push to sender
            if (result == (short) 0) {
                _logger.info("[0x31]远程控制应答处理开始");
                //返回无效才更新db记录 不阻塞
                rc.setRemark("TBOX提示命令无效");
                rc.setRemarkEn("TBOX prompt invalid command");
                rc.setStatus((short) 0);
                remoteControlRepository.save(rc);
                String pushMsg = "TBOX提示命令无效";
                if (push) {
                    try {
                        this.pushRemoteControlResult(rc.getId(), rc.getVin(), String.valueOf(eventId), 0, pushMsg);
                    } catch (RuntimeException e) {
                        _logger.info(e.getMessage());
                    }
                }
                _logger.info("[0x31]远程控制应答处理结束");
            }

        }
    }

    /**
     * 处理远程控制Ack上行（持久化 push）本方法只有引用远程启动的情况才会被调用
     *
     * @param id     vin
     * @param result Ack响应结果  0：无效 1：命令已接收
     */
    public void handleRefRemoteControlAck(long id, Short result) {
        RemoteControl rc = remoteControlRepository.findOne(id);
        if (rc == null) {
            _logger.info("[0x31]没有在数据库找到远程控制记录， id:" + id + "  " + "result:" + result);
        } else {
            //持久化远程控制记录状态，push to sender
            if (result == (short) 0) {//Rst 0：无效 1：命令已接收
                _logger.info("[0x31]关联远程控制应答处理开始");
                //返回无效才更新db记录 不阻塞
//                rc.setRemark("命令执行失败,依赖的远程启动发动机命令执行未能成功:TBOX提示命令无效");
                rc.setRemark("命令执行失败:TBOX提示命令无效");
                rc.setRemarkEn("Command execution failed, dependent remote start engine command execution failed: TBOX prompt command is invalid");
                rc.setStatus((short) 0);
                remoteControlRepository.save(rc);
//                String pushMsg="命令执行失败,依赖的远程启动发动机命令执行未能成功:TBOX提示命令无效";
                String pushMsg = "命令执行失败:TBOX提示命令无效";
                try {
                    this.pushRemoteControlResult(rc.getId(), rc.getVin(), rc.getSessionId(), 0, pushMsg);
                } catch (RuntimeException e) {
                    _logger.info(e.getMessage());
                }
                _logger.info("[0x31]关联远程控制应答处理结束");
            }

        }
    }


    /**
     * 返回远程控制记录
     *
     * @param vin     vin
     * @param eventId eventId
     */
    public RemoteControl getRemoteControlRecord(String vin, long eventId) {
        String sessionId = String.valueOf(eventId);
        RemoteControl rc = remoteControlRepository.findTopByVinAndSessionIdOrderBySendingTimeDesc(vin, sessionId);
        return rc;
    }


    /**
     * 返回远程控制记录
     *
     * @param id
     * @return
     */
    public RemoteControl getRemoteControlRecord(long id) {
        RemoteControl rc = remoteControlRepository.findOne(id);
        return rc;
    }


    /**
     * 处理远程控制结果上行（持久化 push）
     *
     * @param vin               vin
     * @param eventId           eventId
     * @param controlType       控制类别 0启动发动机
     * @param result            Rst响应结果 0：成功 1：失败
     * @param remoteControlTime 次数 参见协议0x05
     * @param push              是否推送
     */
    public void handleRemoteControlRst(String vin, long eventId, Short controlType, Short result, Short remoteControlTime, short isAnnounce, boolean push) {
        String sessionId = String.valueOf(eventId);
        Short dbResult = 0;//参考建表sql  0失败1成功   ,  Rst 0：成功 1：失败
        if (result == (short) 0) {
            dbResult = 1;
        }
        _logger.info("[0x31]远程控制结果,vin:" + vin + "|eventId:" + eventId + "|result:" + result + "|remoteControlTime:" + remoteControlTime);
        RemoteControl rc = remoteControlRepository.findTopByVinAndSessionIdOrderBySendingTimeDesc(vin, sessionId);
        if (rc == null) {
            _logger.info("[0x31]没有在数据库找到远程控制记录,vin:" + vin + "|eventId:" + eventId + "|result:" + result + "|remoteControlTime:" + remoteControlTime);
        } else {
            //持久化远程控制记录状态，push to sender
            _logger.info("[0x31]远程控制结果处理开始.id=" + rc.getId());
            rc.setStatus(dbResult);
            Vehicle vehicle = vehicleRepository.findByVin(vin);
            if (vehicle != null) {
                int currentCount = 0;
                if (vehicle.getRemoteCount() != null) {
                    currentCount = vehicle.getRemoteCount();
                }
                vehicle.setRemoteCount(currentCount + 1);
                vehicleRepository.save(vehicle);
            }

            String pushMsg = "";//参考PDF0621 page55
            String pushMsgEn = "";

            if (result == (short) 0) {
                pushMsg = "远程命令执行成功。";
                pushMsgEn = "Remote command execution success";
                if (controlType == 0) {
                    //启动发动机
                    //1）第一次启动成功，手机APP提示内容：“发动机启动成功，15分钟后将自动关闭”
                    //2）第一次启动成功关闭后，第二次启动成功，手机APP提示内容：“发动机第二次启动成功，15分钟后将自动关闭”。
                    //4）已经完成两次启动，进行第三次启动，手机APP提示内容：“发动机启动已超出允许启动次数2次”
                    if (isAnnounce == 0) {//对于fc 不需要处理此消息
                        remoteControlTime = (rc.getRemoteStartedCount() == null) ? 0 : rc.getRemoteStartedCount();
                        _logger.info("发动机启动成功,之前已经启动次数：" + remoteControlTime);
                        if (remoteControlTime == 0) {
                            pushMsg = "发动机启动成功，15分钟后将自动关闭。";
                            pushMsgEn = "The engine has been started successfully and will be shut down in 15 minutes";
                        } else if (remoteControlTime == 1) {
//                            pushMsg = "发动机第二次启动成功，15分钟后将自动关闭";
                            pushMsg = "发动机启动成功，15分钟后将自动关闭。";
                            pushMsgEn = "The engine has been started successfully and will be shut down in 15 minutes";
                        }
                    }
                } else if (controlType == 3) {
                    pushMsg = "解锁成功，车辆将在规定的时间内自动上锁。";
                    pushMsgEn = "Unlock successfully, the vehicle will be locked automatically after 20 seconds";
                } else if (controlType == 5) {
                    pushMsg = "空调已经关闭，发动机仍在运行中。";
                    pushMsgEn = "Unlock successfully, the vehicle will be locked automatically after 20 seconds";
                }
            } else if (result == (short) 1) {
                pushMsg = "远程命令执行失败。";
                pushMsgEn = "Remote command execution failed";
            } else if (result == (short) 0x20) {
                pushMsg = "远程指令未执行，请求未完成。";
                pushMsgEn = "remote command not implemented, request not completed";
            } else if (result == (short) 0x21) {
                pushMsg = "远程指令未执行，请求的CRC错误。";
                pushMsgEn = "remote command not implemented, requested CRC error";
            } else if (result == (short) 0x22) {
                pushMsg = "远程指令未执行，请求的身份验证错误。";
                pushMsgEn = "remote command not implemented, requested authentication error";
            } else if (result == (short) 0x23) {
                pushMsg = "远程指令未执行，请求无效。";
                pushMsgEn = "remote command not implemented, request message order error";
            } else if (result == (short) 0x24) {
                pushMsg = "远程指令未执行，请求消息顺序错误。";
                pushMsgEn = "remote command not implemented, request messages  received not in order";
            } else if (result == (short) 0x30) {
                pushMsg = "远程指令未执行，请求不能执行。";
                pushMsgEn = "remote command not implemented, request cannot be executed";
            } else if (result == (short) 0x31) {
                pushMsg = "远程指令未执行，请求先决条件无效。";
                pushMsgEn = "remote command not implemented, request prerequisites are invalid";
            } else if (result == (short) 0x40) {
                pushMsg = "远程指令未执行，本地用户终止请求。";
                pushMsgEn = "remote command not implemented, local user termination request";
            } else if (result == (short) 0x50) {
                pushMsg = "远程指令未执行，请求超时失效。";
                pushMsgEn = "remote command not implemented, request timeout";
            } else if (result == (short) 0x51) {
//                pushMsg="远程指令未执行，重复请求。";
//                pushMsgEn="remote command not implemented, request blocked by repetition block";
                pushMsg = "远程指令未执行，由于发动机启动已超出允许次数2次。";
                pushMsgEn = "remote command not implemented, due to engine start number exceeded 2 times.";
            } else if (result == (short) 0x60) {
                pushMsg = "远程指令未执行，功能无效，请求被忽略。";
                pushMsgEn = "remote command not implemented, function is not valid, the request is ignored.";
            } else if (result == (short) 0x80) {
                pushMsg = "远程指令未执行，等待响应中，指定时间后再请求。";
                pushMsgEn = "remote command not implemented, wait for the response, after the specified time to request";
            } else if (result == (short) 0x81) {
                pushMsg = "远程指令未执行，响应等待下次车辆启动。";
                pushMsgEn = "remote command not implemented, in response to waiting for the next vehicle to start";
            }
            if (controlType == 0 && remoteControlTime > 2) {
//                pushMsg="发动机启动次数已超出2次，启动请求无效";
//                pushMsgEn="Engine start is more than allowed times 2,invalid start request";
                pushMsg = "远程指令未执行,由于发动机启动已超出允许启动次数2次。";
                pushMsgEn = "remote command not implemented, engine start is more than allowed times 2";
            }
            if (controlType == 11) {//车窗结果

            }
            if (controlType == 12) {//天窗结果

            }
            String _dbReMark = pushMsg;
            rc.setRemark(_dbReMark);
            rc.setRemarkEn(pushMsgEn);
            remoteControlRepository.save(rc);
            if (push) {
                try {
                    this.pushRemoteControlResult(rc.getId(), rc.getVin(), String.valueOf(eventId), rc.getStatus(), pushMsg);
                } catch (RuntimeException e) {
                    _logger.info(e.getMessage());
                }
            }
            _logger.info("[0x31]远程控制结果处理结束");
        }
    }

    /**
     * 处理远程控制结果上行（持久化 push） 仅仅只处理失败的引用情况
     *
     * @param id                vin
     * @param result            Rst响应结果 0：成功 1：失败  ...
     * @param remoteControlTime 次数 参见协议0x05
     */
    public void handleRefRemoteControlRst(long id, Short controlType, Short result, Short remoteControlTime) {
        RemoteControl rc = remoteControlRepository.findOne(id);
        if (rc == null) {
            _logger.info("[0x31]没有在数据库找到远程控制记录， id:" + id + "  " + "result:" + result + "remoteControlTime:" + remoteControlTime);
        } else {
            //持久化远程控制记录状态，push to sender
            _logger.info("[0x31]关联远程控制结果处理开始");
            Vehicle vehicle = vehicleRepository.findByVin(rc.getVin());
            if (vehicle != null) {
                int currentCount = 0;
                if (vehicle.getRemoteCount() != null) {
                    currentCount = vehicle.getRemoteCount();
                }
                vehicle.setRemoteCount(currentCount + 1);
                vehicleRepository.save(vehicle);
            }
            String pushMsg = "";//参考PDF0621 page55
            String pushMsgEn = "";
            if (result == (short) 0) {
                pushMsg = "远程命令执行成功";
                pushMsgEn = "Remote command execution success";
            } else if (result == (short) 1) {
                pushMsg = "远程命令执行失败";
                pushMsgEn = "Remote command execution failed";
            } else if (result == (short) 0x20) {
                pushMsg = "远程指令未执行，请求未完成。";
                pushMsgEn = "remote command not implemented, request not completed";
            } else if (result == (short) 0x21) {
                pushMsg = "远程指令未执行，请求的CRC错误。";
                pushMsgEn = "remote command not implemented, requested CRC error";
            } else if (result == (short) 0x22) {
                pushMsg = "远程指令未执行，请求的身份验证错误。";
                pushMsgEn = "remote command not implemented, requested authentication error";
            } else if (result == (short) 0x23) {
                pushMsg = "远程指令未执行，请求无效。";
                pushMsgEn = "remote command not implemented, request message order error";
            } else if (result == (short) 0x24) {
                pushMsg = "远程指令未执行，请求消息顺序错误。";
                pushMsgEn = "remote command not implemented, request messages  received not in order";
            } else if (result == (short) 0x30) {
                pushMsg = "远程指令未执行，请求不能执行。";
                pushMsgEn = "remote command not implemented, request cannot be executed";
            } else if (result == (short) 0x31) {
                pushMsg = "远程指令未执行，请求先决条件无效。";
                pushMsgEn = "remote command not implemented, request prerequisites are invalid";
            } else if (result == (short) 0x40) {
                pushMsg = "远程指令未执行，本地用户终止请求。";
                pushMsgEn = "remote command not implemented, local user termination request";
            } else if (result == (short) 0x50) {
                pushMsg = "远程指令未执行，请求超时失效。";
                pushMsgEn = "remote command not implemented, request timeout";
            } else if (result == (short) 0x51) {
//                pushMsg="远程指令未执行，重复请求。";
//                pushMsgEn="remote command not implemented, request blocked by repetition block";
                pushMsg = "远程指令未执行，由于发动机启动已超出允许次数2次。";
                pushMsgEn = "remote command not implemented, due to engine start number exceeded 2 times.";
            } else if (result == (short) 0x60) {
                pushMsg = "远程指令未执行，功能无效，请求被忽略。";
                pushMsgEn = "remote command not implemented, function is not valid, the request is ignored.";
            } else if (result == (short) 0x80) {
                pushMsg = "远程指令未执行，等待响应中，指定时间后再请求。";
                pushMsgEn = "remote command not implemented, wait for the response, after the specified time to request";
            } else if (result == (short) 0x81) {
                pushMsg = "远程指令未执行，响应等待下次车辆启动。";
                pushMsgEn = "remote command not implemented, in response to waiting for the next vehicle to start";
            }
            if (controlType == 0 && remoteControlTime > 2) {
//                pushMsg="发动机启动次数已超出2次，启动请求无效";
//                pushMsgEn="Engine start is more than allowed times 2,invalid start request";
                pushMsg = "远程指令未执行,请求未完成";
                pushMsgEn = "remote command not implemented, request not completed";
            }
//            String _dbReMark="命令执行失败,依赖的远程启动发动机命令执行未能成功:"+pushMsg;
            String _dbReMark = "命令执行失败:" + pushMsg;
            rc.setRemark(_dbReMark);
            rc.setRemarkEn(pushMsgEn);
            if (result == (short) 0) {
                rc.setStatus((short) 1);
            } else {
                rc.setStatus((short) 0);
            }
            remoteControlRepository.save(rc);
            pushMsg = _dbReMark;
            try {
                this.pushRemoteControlResult(rc.getId(), rc.getVin(), rc.getSessionId(), rc.getStatus(), pushMsg);
            } catch (RuntimeException e) {
                _logger.info(e.getMessage());
            }
            _logger.info("[0x31]关联远程控制结果处理结束");
        }
    }

    /**
     * update RefController Remark
     *
     * @param id id
     */
    public void updateRefRemoteControlRst(long id, String remark, String remaerEn) {
        RemoteControl rc = remoteControlRepository.findOne(id);
        rc.setRemark(remark);
        rc.setRemarkEn(remaerEn);
        rc.setStatus((short) 0);
        remoteControlRepository.save(rc);
        String pushMsg = remark;
        try {
            this.pushRemoteControlResult(rc.getId(), rc.getVin(), rc.getSessionId(), rc.getStatus(), pushMsg);
        } catch (RuntimeException e) {
            _logger.info(e.getMessage());
        }
        _logger.info("[0x31]更新远程控制结果成功");
    }


    public void saveCmdToRedis(String serverId, String vin, String hexStr) {
        socketRedis.saveSetString(serverId + "-" + dataTool.out_cmd_preStr + vin, hexStr, -1);//代发命令的TTL为-1 由处理程序取出
        //保存到redis


    }

    /**
     * 处理下发失败的数据，更新数据库状态
     *
     * @param vin
     * @param eventId
     * @param applicationId
     * @param messageId
     */
    public void handleFailedData(String vin, String eventId, String applicationId, String messageId) {
        _logger.info("[0x31]报文下发失败，同步数据库状态>" + vin + "-" + eventId + "-" + applicationId + "-" + messageId);
        RemoteControl rc = remoteControlRepository.findTopByVinAndSessionIdOrderBySendingTimeDesc(vin, eventId);
        if (rc != null) {
            String _dbReMark = "";
            String _dbReMarkEn = "";
            if (applicationId.equals("49")) {
                if (messageId.equals("1")) {
                    _dbReMark = "Precondition重发3次后仍没有收到回复，远程控制操作失败。";
                    _dbReMarkEn = "Precondition repeat 3 times still did not receive a reply, the remote control operation failed.";
                } else if (messageId.equals("3")) {
                    _dbReMark = "远程控制命令重发3次后仍没有收到回复，远程控制操作失败。";
                    _dbReMarkEn = "Remote control commands repeat 3 times still did not receive a reply, the remote control operation failed.";
                }
                rc.setRemark(_dbReMark);
                rc.setRemarkEn(_dbReMarkEn);
                remoteControlRepository.save(rc);
                _logger.info("[0x31]报文下发失败，同步数据库信息>" + _dbReMark);
            } else {
                _logger.info("[0x31]报文下发失败，同步数据库信息>尚未定义");
            }

        } else {
            _logger.info("[0x31]同步数据库状态失败，没有找到记录>" + vin + "-" + eventId);
        }
    }

    /**
     * 获取最近一条故障信息
     *
     * @param vin
     * @return
     */
    public FailureMessageData getLatestFailureMessage(String vin) {
        FailureMessageData lastData = failureMessageDataRespository.findTopByVinOrderByReceiveTimeDescIdDesc(vin);
        return lastData;
    }


}
