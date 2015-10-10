package com.hp.triclops.service;

import com.hp.data.bean.tbox.*;
import com.hp.data.core.Conversion;
import com.hp.data.core.DataPackage;
import com.hp.data.util.PackageEntityManager;
import com.hp.triclops.acquire.DataTool;
import com.hp.triclops.entity.GpsData;
import com.hp.triclops.entity.RealTimeReportData;
import com.hp.triclops.entity.RegularReportData;
import com.hp.triclops.entity.WarningMessageData;
import com.hp.triclops.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;

/**
 * Created by luj on 2015/9/28.
 */
@Component
public class DataHandleService {
    @Autowired
    GpsDataRepository gpsDataRepository;
    @Autowired
    RegularReportDataRespository regularReportDataRespository;
    @Autowired
    RealTimeReportDataRespository realTimeReportDataRespository;
    @Autowired
    WarningMessageDataRespository warningMessageDataRespository;


    @Autowired
    Conversion conversionTBox;
    private Logger _logger= LoggerFactory.getLogger(DataHandleService.class);

    @Autowired
    DataTool dataTool;
    public void saveMessage(String vin,String msg){
       //数据保存，传入原始消息数据hex
       //根据application id分析消息类型，然后调用具体的保存方法
        byte[] receiveData=dataTool.getBytesFromByteBuf(dataTool.getByteBuf(msg));
        byte dataType=dataTool.getApplicationType(receiveData);
        switch(dataType)
        {
            case 0x21://固定数据
                saveRegularReportMes(vin,msg);
                break;
            case 0x22://实时数据
                saveRealTimeReportMes(vin,msg);
                break;
            case 0x23://补发数据
                saveDataResendMes(vin,msg);
                break;
            case 0x24://报警数据
                saveWarningMessage(vin,msg);
                break;
            default:
                _logger.info(">>data is invalid,we will not save them");
                break;
        }
    }

    public void saveRegularReportMes(String vin,String msg){
        _logger.info(">>save RegularReportMes:"+msg);
        ByteBuffer bb= PackageEntityManager.getByteBuffer(msg);
        DataPackage dp=conversionTBox.generate(bb);
        RegularReportMes bean=dp.loadBean(RegularReportMes.class);
        RegularReportData rd=new RegularReportData();
        rd.setVin(vin);
        rd.setImei(bean.getImei());
        rd.setApplicationId(bean.getApplicationID());
        rd.setMessageId(bean.getMessageID());
        rd.setSendingTime(dataTool.seconds2Date(bean.getSendingTime()));
        rd.setFrequencyForRealTimeReport(bean.getFrequencyForRealTimeReport());
        rd.setFrequencyForWarningReport(bean.getFrequencyForWarningReport());
        rd.setFrequencyHeartbeat(bean.getFrequencyHeartbeat());
        rd.setTimeOutForTerminalSearch(bean.getTimeOutForTerminalSearch());
        rd.setTimeOutForServerSearch(bean.getTimeOutForServerSearch());
        rd.setVehicleType(bean.getVehicleType());
        rd.setVehicleModels(bean.getVehicleModels());
        rd.setMaxSpeed(bean.getMaxSpeed());
        rd.setHardwareVersion(bean.getHardwareVersion());
        rd.setSoftwareVersion(bean.getSoftwareVersion());
        rd.setFrequencySaveLocalMedia(bean.getFrequencySaveLocalMedia());
        //ip地址转换
        rd.setEnterpriseBroadcastAddress(dataTool.getIp(bean.getEnterpriseBroadcastAddress()));
        rd.setEnterpriseBroadcastPort(bean.getEnterpriseBroadcastPort());
        regularReportDataRespository.save(rd);
    }
    public void saveRealTimeReportMes(String vin,String msg){
        _logger.info(">>save RealTimeReportMes:"+msg);
        ByteBuffer bb= PackageEntityManager.getByteBuffer(msg);
        DataPackage dp=conversionTBox.generate(bb);
        RealTimeReportMes bean=dp.loadBean(RealTimeReportMes.class);
        RealTimeReportData rd=new RealTimeReportData();
        rd.setVin(vin);
        rd.setImei(bean.getImei());
        rd.setApplicationId(bean.getApplicationID());
        rd.setMessageId(bean.getMessageID());
        rd.setSendingTime(dataTool.seconds2Date(bean.getSendingTime()));
        rd.setFuelOil(bean.getFuelOil());
        rd.setAvgOil(dataTool.getTrueAvgOil(bean.getAvgOil()));
        rd.setOilLife(bean.getOilLife());
        String driveRangeHexStr=dataTool.bytes2hex(bean.getDriveRange());
        rd.setDriveRange(Integer.valueOf(driveRangeHexStr.replace(" ", ""), 16));//此处要处理3个byte为一个int
        rd.setLeftFrontTirePressure(bean.getLeftFrontTirePressure());
        rd.setLeftRearTirePressure(bean.getLeftRearTirePressure());
        rd.setRightFrontTirePressure(bean.getRightFrontTirePressure());
        rd.setRightRearTirePressure(bean.getRightRearTirePressure());
        char[] windows=dataTool.getBitsFromShort(bean.getWindowInformation());//窗 1开0关  bit0-3 分别左前 右前 左后 右后
        rd.setLeftFrontWindowInformation(windows[0] == '0' ? "0" : "1");
        rd.setRightFrontWindowInformation(windows[1] == '0' ? "0" : "1");
        rd.setLeftRearWindowInformation(windows[2] == '0' ? "0" : "1");
        rd.setRightRearWindowInformation(windows[3] == '0' ? "0" : "1");
        rd.setVehicleTemperature(dataTool.getTrueTmp(bean.getVehicleTemperature()));//温度按照上报数值-40
        rd.setVehicleOuterTemperature(dataTool.getTrueTmp(bean.getVehicleOuterTemperature()));
        char[] doors=dataTool.getBitsFromShort(bean.getWindowInformation());//门 1开0关  bit0-5 分别左前 左后 右前  右后 后备箱 前舱盖
        //此处千万注意!注意门和窗的顺序不一样，编协议的要么不是同一个人 要么人格分裂。
        rd.setLeftFrontDoorInformation(doors[0] == '0' ? "0" : "1");
        rd.setLeftRearDoorInformation(doors[1] == '0' ? "0" : "1");
        rd.setRightFrontDoorInformation(doors[2] == '0' ? "0" : "1");
        rd.setRightRearDoorInformation(doors[3] == '0' ? "0" : "1");
        rd.setTrunkDoorInformation(doors[4] == '0' ? "0" : "1");
        rd.setEngineDoorInformation(doors[5] == '0' ? "0" : "1");
        rd.setSingleBatteryVoltage(dataTool.getTrueBatteryVoltage(bean.getSingleBatteryVoltage()));
        rd.setMaximumVoltagePowerBatteryPack(bean.getMaximumVoltagePowerBatteryPack());
        rd.setMaximumBatteryVoltage(dataTool.getTrueBatteryVoltage(bean.getMaximumBatteryVoltage()));
        rd.setBatteryMonomerMinimumVoltage(dataTool.getTrueBatteryVoltage(bean.getBatteryMonomerMinimumVoltage()));
        rd.setEngineCondition(dataTool.getEngineConditionInfo(bean.getEngineCondition()));
        rd.setEngineSpeed(bean.getEngineSpeed());
        rd.setRapidAcceleration(bean.getRapidAcceleration());
        rd.setRapidDeceleration(bean.getRapidDeceleration());
        rd.setSpeeding(bean.getSpeeding());
        rd.setSignalStrength(bean.getSignalStrength());
        realTimeReportDataRespository.save(rd);
        //普通实时数据和位置数据分表存储
        GpsData gd=new GpsData();
        gd.setVin(vin);
        gd.setImei(bean.getImei());
        gd.setApplicationId(bean.getApplicationID());
        gd.setMessageId(bean.getMessageID());
        gd.setSendingTime(dataTool.seconds2Date(bean.getSendingTime()));
        //分解IsIsLocation信息
        char[] location=dataTool.getBitsFromShort(bean.getIsLocation());
        gd.setIsLocation(location[0] == '0' ? (short) 0 : (short) 1);//bit0 0有效定位 1无效定位
        gd.setNorthSouth(location[1]=='0'?"N":"S");//bit1 0北纬 1南纬
        gd.setEastWest(location[2]=='0'?"E":"W");//bit2 0东经 1西经
        gd.setLatitude(dataTool.getTrueLatAndLon(bean.getLatitude()));
        gd.setLongitude(dataTool.getTrueLatAndLon(bean.getLongitude()));
        gd.setSpeed(dataTool.getTrueSpeed(bean.getSpeed()));
        gd.setHeading(bean.getHeading());
        gpsDataRepository.save(gd);
    }

    public void saveDataResendMes(String vin,String msg){
        //补发数据保存
        _logger.info(">>save DataResendMes:"+msg);
        ByteBuffer bb= PackageEntityManager.getByteBuffer(msg);
        DataPackage dp=conversionTBox.generate(bb);
        DataResendMes bean=dp.loadBean(DataResendMes.class);
        //包含实时数据和报警数据
        RealTimeReportData rd=new RealTimeReportData();
        rd.setVin(vin);
        rd.setImei(bean.getImei());
        rd.setApplicationId(bean.getApplicationID());
        rd.setMessageId(bean.getMessageID());
        rd.setSendingTime(dataTool.seconds2Date(bean.getSendingTime()));
        rd.setFuelOil(bean.getFuelOil());
        rd.setAvgOil(dataTool.getTrueAvgOil(bean.getAvgOil()));
        rd.setOilLife(bean.getOilLife());
        String driveRangeHexStr=dataTool.bytes2hex(bean.getDriveRange());
        rd.setDriveRange(Integer.valueOf(driveRangeHexStr.replace(" ", ""), 16));//此处要处理3个byte为一个int
        rd.setLeftFrontTirePressure(bean.getLeftFrontTirePressure());
        rd.setLeftRearTirePressure(bean.getLeftRearTirePressure());
        rd.setRightFrontTirePressure(bean.getRightFrontTirePressure());
        rd.setRightRearTirePressure(bean.getRightRearTirePressure());
        char[] windows=dataTool.getBitsFromShort(bean.getWindowInformation());//窗 1开0关  bit0-3 分别左前 右前 左后 右后
        rd.setLeftFrontWindowInformation(windows[0] == '0' ? "0" : "1");
        rd.setRightFrontWindowInformation(windows[1] == '0' ? "0" : "1");
        rd.setLeftRearWindowInformation(windows[2] == '0' ? "0" : "1");
        rd.setRightRearWindowInformation(windows[3] == '0' ? "0" : "1");
        rd.setVehicleTemperature(dataTool.getTrueTmp(bean.getVehicleTemperature()));//温度按照上报数值-40
        rd.setVehicleOuterTemperature(dataTool.getTrueTmp(bean.getVehicleOuterTemperature()));
        char[] doors=dataTool.getBitsFromShort(bean.getWindowInformation());//门 1开0关  bit0-5 分别左前 左后 右前  右后 后备箱 前舱盖
        //此处千万注意!注意门和窗的顺序不一样，编协议的要么不是同一个人 要么人格分裂。
        rd.setLeftFrontDoorInformation(doors[0] == '0' ? "0" : "1");
        rd.setLeftRearDoorInformation(doors[1] == '0' ? "0" : "1");
        rd.setRightFrontDoorInformation(doors[2] == '0' ? "0" : "1");
        rd.setRightRearDoorInformation(doors[3] == '0' ? "0" : "1");
        rd.setTrunkDoorInformation(doors[4] == '0' ? "0" : "1");
        rd.setEngineDoorInformation(doors[5] == '0' ? "0" : "1");
        rd.setSingleBatteryVoltage(dataTool.getTrueBatteryVoltage(bean.getSingleBatteryVoltage()));
        rd.setMaximumVoltagePowerBatteryPack(bean.getMaximumVoltagePowerBatteryPack());
        rd.setMaximumBatteryVoltage(dataTool.getTrueBatteryVoltage(bean.getMaximumBatteryVoltage()));
        rd.setBatteryMonomerMinimumVoltage(dataTool.getTrueBatteryVoltage(bean.getBatteryMonomerMinimumVoltage()));
        rd.setEngineCondition(dataTool.getEngineConditionInfo(bean.getEngineCondition()));
        rd.setEngineSpeed(bean.getEngineSpeed());
        rd.setRapidAcceleration(bean.getRapidAcceleration());
        rd.setRapidDeceleration(bean.getRapidDeceleration());
        rd.setSpeeding(bean.getSpeeding());
        rd.setSignalStrength(bean.getSignalStrength());
        realTimeReportDataRespository.save(rd);
        //普通实时数据和位置数据分表存储
        GpsData gd=new GpsData();
        gd.setVin(vin);
        gd.setImei(bean.getImei());
        gd.setApplicationId(bean.getApplicationID());
        gd.setMessageId(bean.getMessageID());
        gd.setSendingTime(dataTool.seconds2Date(bean.getSendingTime()));
        //分解IsIsLocation信息
        char[] location=dataTool.getBitsFromShort(bean.getIsLocation());
        gd.setIsLocation(location[0] == '0' ? (short) 0 : (short) 1);//bit0 0有效定位 1无效定位
        gd.setNorthSouth(location[1]=='0'?"N":"S");//bit1 0北纬 1南纬
        gd.setEastWest(location[2]=='0'?"E":"W");//bit2 0东经 1西经
        gd.setLatitude(dataTool.getTrueLatAndLon(bean.getLatitude()));
        gd.setLongitude(dataTool.getTrueLatAndLon(bean.getLongitude()));
        gd.setSpeed(dataTool.getTrueSpeed(bean.getSpeed()));
        gd.setHeading(bean.getHeading());
        gpsDataRepository.save(gd);

        //报警数据保存
        WarningMessageData wd=new WarningMessageData();
        wd.setVin(vin);
        wd.setImei(bean.getImei());
        wd.setApplicationId(bean.getApplicationID());
        wd.setMessageId(bean.getMessageID());
        wd.setSendingTime(dataTool.seconds2Date(bean.getSendingTime()));
        //分解IsIsLocation信息
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
        warningMessageDataRespository.save(wd);
    }
    public void saveWarningMessage(String vin,String msg){
        //报警数据保存
        _logger.info(">>save WarningMessage:"+msg);
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
        warningMessageDataRespository.save(wd);
    }


}
