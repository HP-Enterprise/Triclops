package com.hp.triclops.service;

/**
 * Created by luj on 2015/10/12.
 */

import com.hp.data.bean.tbox.PramSetCmd;
import com.hp.data.bean.tbox.RemoteControlCmd;
import com.hp.data.bean.tbox.WarningMessage;
import com.hp.data.core.Conversion;
import com.hp.data.core.DataPackage;
import com.hp.data.util.PackageEntityManager;
import com.hp.triclops.acquire.DataTool;
import com.hp.triclops.entity.*;
import com.hp.triclops.redis.SocketRedis;
import com.hp.triclops.repository.TBoxParmSetRepository;
import com.hp.triclops.repository.UserVehicleRelativedRepository;
import com.hp.triclops.repository.VehicleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.List;

/**
 * 生成下行数据包hex 并写入redis
 */
@Service
public class OutputHexService {
    @Autowired
    Conversion conversionTBox;
    @Autowired
    DataTool dataTool;
    @Autowired
    SocketRedis socketRedis;
    @Autowired
    TBoxParmSetRepository tBoxParmSetRepository;
    @Autowired
    UserVehicleRelativedRepository userVehicleRelativedRepository;
    @Autowired
    VehicleRepository vehicleRepository;
    @Autowired
    MQService mqService;

    private Logger _logger = LoggerFactory.getLogger(OutputHexService.class);
    public String getRemoteControlHex(RemoteControl remoteControl,int eventId){
        //产生远程控制指令hex
        RemoteControlCmd  remoteControlCmd=new RemoteControlCmd();
        remoteControlCmd.setRemoteControlType(remoteControl.getControlType().intValue());
        remoteControlCmd.setAcTemperature(remoteControl.getAcTemperature());
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
        pramSetCmd.setSendingTime((long)dataTool.getCurrentSeconds());
        pramSetCmd.setPramSetNumber((short) 13);
        pramSetCmd.setPramSetID(new byte[]{(byte) 1, (byte) 2, (byte) 3, (byte) 4, (byte) 5, (byte) 6, (byte) 7, (byte) 8, (byte) 9, (byte) 10, (byte) 11, (byte) 12, (byte) 13});
        pramSetCmd.setFrequencySaveLocalMedia(tps.getFrequencySaveLocalMedia());
        pramSetCmd.setFrequencyForReport(tps.getFrequencyForReport());
        pramSetCmd.setFrequencyForWarningReport(tps.getFrequencyForWarningReport());
        pramSetCmd.setFrequencyHeartbeat(tps.getFrequencyHeartbeat());
        pramSetCmd.setTimeOutForTerminalSearch(tps.getTimeOutForTerminalSearch());
        pramSetCmd.setTimeOutForServerSearch(tps.getTimeOutForServerSearch());
        pramSetCmd.setUploadType(tps.getUploadType());
        pramSetCmd.setEnterpriseBroadcastAddress1(dataTool.getIpBytes(tps.getEnterpriseBroadcastAddress1()));
        pramSetCmd.setEnterpriseBroadcastPort1(tps.getEnterpriseBroadcastPort1());
        pramSetCmd.setEnterpriseBroadcastAddress2(dataTool.getIpBytes(tps.getEnterpriseBroadcastAddress2()));
        pramSetCmd.setEnterpriseBroadcastPort2(tps.getEnterpriseBroadcastPort2());
        pramSetCmd.setEnterpriseDomainNameSize(tps.getEnterpriseDomainNameSize());
        pramSetCmd.setEnterpriseDomainName(tps.getEnterpriseDomainName());

        DataPackage dpw=new DataPackage("8995_82_1");//>>>
        dpw.fillBean(pramSetCmd);
        ByteBuffer bbw=conversionTBox.generate(dpw);
        String byteStr= PackageEntityManager.getByteString(bbw);

        return byteStr;
    }

    /**
     * 根据报警hex信息生成文本性质的报警提示 并push到对应user
     * @param vin vin
     * @param msg 16进制报警信息
     */
    public void getWarningMessageAndPush(String vin,String msg){
        String pushMsg=getWarningMessageForPush(vin,msg);
        _logger.info("push message:"+pushMsg);
        Vehicle vehicle=vehicleRepository.findByVin(vin);
        List<UserVehicleRelatived> uvr=userVehicleRelativedRepository.findByVid(vehicle);
        //一个车对应多个uid 报警消息都push过去
        if(uvr.size()>0){
            Iterator<UserVehicleRelatived> iterator=uvr.iterator();
            while (iterator.hasNext()){
                int uid=iterator.next().getUid().getId();
                _logger.info("push to:"+uid+":"+pushMsg);
                this.mqService.pushToUser(uid, pushMsg);
            }
        }else{
            _logger.info("can not push warning message,because no user found for vin:"+vin);
        }
    }

    /**
     * 根据报警hex信息生成文本性质的报警提示
     * @param vin vin
     * @param msg 16进制报警信息
     * @return 根据报警hex信息生成文本性质的报警提示
     */
    public String getWarningMessageForPush(String vin,String msg){
        //报警数据保存
      /*  _logger.info(">>get WarningMessage For Push:"+msg);
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
        wd.setIsLocation(location[0] == '0' ? (short) 0 : (short) 1);//bit0 0有效定位 1无效定位
        wd.setNorthSouth(location[1] == '0' ? "N" : "S");//bit1 0北纬 1南纬
        wd.setEastWest(location[2] == '0' ? "E" : "W");//bit2 0东经 1西经
        wd.setLatitude(dataTool.getTrueLatAndLon(bean.getLatitude()));
        wd.setLongitude(dataTool.getTrueLatAndLon(bean.getLongitude()));
        wd.setSpeed(dataTool.getTrueSpeed(bean.getSpeed()));
        wd.setHeading(bean.getHeading());
        char[] bcm1=dataTool.getBitsFromByte(bean.getBcm1());
        wd.setBatteryVoltageTooHigh(bcm1[0] == '0' ? "0" : "1");
        wd.setBatteryVoltageTooLow(bcm1[1] == '0' ? "0" : "1");
        wd.setMediaAbnormal(bcm1[2] == '0' ? "0" : "1");
        wd.setFrozenLiquidShortage(bcm1[3] == '0' ? "0" : "1");
        wd.setLampFailure(bcm1[4] == '0' ? "0" : "1");
        char[] ems=dataTool.getBitsFromByte(bean.getEms());
        wd.setEngineAbnormal(ems[0] == '0' ? "0" : "1");
        wd.setWaterTemperatureTooHigh(ems[1] == '0' ? "0" : "1");
        char[] tcu=dataTool.getBitsFromByte(bean.getTcu());
        wd.setDangerousDrivingSystemFault(tcu[0] == '0' ? "0" : "1");
        wd.setWarningDrivingSystemFault(tcu[1] == '0' ? "0" : "1");
        wd.setDrivingSystemOverheated(tcu[2] == '0' ? "0" : "1");
        char[] ic=dataTool.getBitsFromByte(bean.getIc());
        wd.setAirbagAbnormal(ic[0] == '0' ? "0" : "1");
        wd.setAbsFault(ic[1] == '0' ? "0" : "1");
        wd.setOilPressureLow(ic[2] == '0' ? "0" : "1");
        char[] abs=dataTool.getBitsFromByte(bean.getAbs());
        wd.setBrakeFluidLevelLow(abs[0] == '0' ? "0" : "1");
        char[] pdc=dataTool.getBitsFromByte(bean.getPdc());
        wd.setPdcSystemFault(pdc[0] == '0' ? "0" : "1");
        char[] bcm2=dataTool.getBitsFromByte(bean.getBcm2());
        wd.setAirbagTriggered(bcm2[0] == '0' ? "0" : "1");

        StringBuilder sb=new StringBuilder() ;
        sb.append("车辆报警信息: ");
        if(wd.getIsLocation()==(short)0){
            //0有效 1无效
            sb.append("当前位置:");
            sb.append("经度:").append(wd.getLongitude()).append(wd.getEastWest()).append(",");
            sb.append("纬度").append(wd.getLatitude()).append(wd.getNorthSouth()).append(";");
            sb.append("速度:").append(wd.getSpeed()).append("km/h;");
            sb.append("方向:").append(wd.getHeading()).append(";");
        }
        if(wd.getBatteryVoltageTooHigh().equals("0")){
            sb.append("车辆电瓶过压;"); //车辆电瓶过压 0:故障发生 1:故障消除
        }
        if(wd.getBatteryVoltageTooLow().equals("0")){
            sb.append("车辆电瓶欠压;"); //车辆电瓶欠压 0:故障发生 1:故障消除
        }
        if(wd.getMediaAbnormal().equals("0")){
            sb.append("多媒体异常;"); //多媒体异常 0:故障发生 1:故障消除
        }
        if(wd.getFrozenLiquidShortage().equals("0")){
            sb.append("冷冻液不足;"); //冷冻液不足 0:故障发生 1:故障消除
        }
        if(wd.getLampFailure().equals("0")){
            sb.append("车灯系统故障;"); //车灯系统故障  0:故障发生 1:故障消除
        }
        if(wd.getEngineAbnormal().equals("0")){
            sb.append("发动机异常;"); //发动机异常 0:故障发生 1:故障消除
        }
        if(wd.getWaterTemperatureTooHigh().equals("0")){
            sb.append("水温过高;"); //水温过高 0:故障发生 1:故障消除
        }
        if(wd.getDangerousDrivingSystemFault().equals("0")){
            sb.append("危险传动系统故障;"); //危险传动系统故障 0:故障发生 1:故障消除
        }
        if(wd.getWarningDrivingSystemFault().equals("0")){
            sb.append("警告传动系统故障;"); //警告传动系统故障  0:故障发生 1:故障消除
        }
        if(wd.getDrivingSystemOverheated().equals("0")){
            sb.append("传动系统过热;"); //传动系统过热 0:故障发生 1:故障消除
        }
        if(wd.getAirbagAbnormal().equals("0")){
            sb.append("安全气囊异常;"); //安全气囊异常 0:故障发生 1:故障消除
        }
        if(wd.getAbsFault().equals("0")){
            sb.append("ABS故障;"); //ABS故障  0:故障发生 1:故障消除
        }
        if(wd.getOilPressureLow().equals("0")){
            sb.append("油压低;"); //油压低 0:故障发生 1:故障消除
        }
        if(wd.getBrakeFluidLevelLow().equals("0")){
            sb.append("刹车液位低;"); //刹车液位低  0:故障发生 1:故障消除
        }
        if(wd.getPdcSystemFault().equals("0")){
            sb.append("PDC系统故障;"); //PDC系统故障 0:故障发生 1:故障消除
        }
        if(wd.getAirbagTriggered().equals("0")){
            sb.append("安全气囊触发;"); //安全气囊触发 0: 触发 1:未触发
        }
        return sb.toString();*/
        return null;
    }

    public  void saveCmdToRedis(String vin,String hexStr){
        socketRedis.saveSetString(dataTool.out_cmd_preStr + vin, hexStr, -1);
        //保存到redis


    }

}
