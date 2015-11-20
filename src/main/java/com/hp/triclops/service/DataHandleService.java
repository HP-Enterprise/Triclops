package com.hp.triclops.service;

import com.hp.data.bean.tbox.*;
import com.hp.data.core.Conversion;
import com.hp.data.core.DataPackage;
import com.hp.data.util.PackageEntityManager;
import com.hp.triclops.acquire.DataTool;
import com.hp.triclops.entity.*;
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
    FailureMessageDataRespository failureMessageDataRespository;


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
                saveRealTimeReportMes(vin, msg);
                break;
            case 0x23://补发实时数据
                saveDataResendRealTimeMes(vin, msg);
                break;
            case 0x24://报警数据
                saveWarningMessage(vin, msg);
                break;
            case 0x25://补发报警数据
                saveDataResendWarningMessage(vin, msg);
                break;
            case 0x28://故障数据
                saveFailureMessage(vin, msg);
                break;
            case 0x29://补发故障数据
                saveDataResendFailureMessage(vin, msg);
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

        rd.setFuelOil(bean.getFuelOil() / 2f);
        rd.setAvgOilA(dataTool.getTrueAvgOil(bean.getAvgOilA()));
        rd.setAvgOilB(dataTool.getTrueAvgOil(bean.getAvgOilB()));
        rd.setServiceIntervall(bean.getServiceIntervall());

        rd.setLeftFrontTirePressure(bean.getLeftFrontTirePressure()*2.8f);
        rd.setLeftRearTirePressure(bean.getLeftRearTirePressure()*2.8f);
        rd.setRightFrontTirePressure(bean.getRightFrontTirePressure()*2.8f);
        rd.setRightRearTirePressure(bean.getRightRearTirePressure()*2.8f);
        char[] windows=dataTool.getBitsFromShort(bean.getWindowInformation());//
        rd.setLeftFrontWindowInformation(dataTool.getWindowStatus(String.valueOf(windows[6]) + String.valueOf(windows[7])));
        rd.setRightFrontWindowInformation(dataTool.getWindowStatus(String.valueOf(windows[4]) + String.valueOf(windows[5])));
        rd.setLeftRearWindowInformation(dataTool.getWindowStatus(String.valueOf(windows[2]) + String.valueOf(windows[3])));
        rd.setRightRearWindowInformation(dataTool.getWindowStatus(String.valueOf(windows[0]) + String.valueOf(windows[1])));

        rd.setVehicleTemperature(dataTool.getTrueTmp(bean.getVehicleTemperature()));//温度按照上报数值-40
        rd.setVehicleOuterTemperature(dataTool.getTrueTmp(bean.getVehicleOuterTemperature()));
        char[] doors=dataTool.getBitsFromShort(bean.getDoorInformation());//门 1开0关  bit 大端传输

        rd.setLeftFrontDoorInformation(dataTool.getDoorStatus(String.valueOf(doors[6])+String.valueOf(doors[7])));
        rd.setRightFrontDoorInformation(dataTool.getDoorStatus(String.valueOf(doors[4])+String.valueOf(doors[5])));
        rd.setLeftRearDoorInformation(dataTool.getDoorStatus(String.valueOf(doors[2])+String.valueOf(doors[3])));
        rd.setRightRearDoorInformation(dataTool.getDoorStatus(String.valueOf(doors[0])+String.valueOf(doors[1])));

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
        gd.setIsLocation(location[7] == '0' ? (short) 0 : (short) 1);//bit0 0有效定位 1无效定位
        gd.setNorthSouth(location[6]=='0'?"N":"S");//bit1 0北纬 1南纬
        gd.setEastWest(location[5]=='0'?"E":"W");//bit2 0东经 1西经
        gd.setLatitude(dataTool.getTrueLatAndLon(bean.getLatitude()));
        gd.setLongitude(dataTool.getTrueLatAndLon(bean.getLongitude()));
        gd.setSpeed(dataTool.getTrueSpeed(bean.getSpeed()));
        gd.setHeading(bean.getHeading());
        gpsDataRepository.save(gd);
    }

    public void saveDataResendRealTimeMes(String vin,String msg){
        //补发数据保存
        _logger.info(">>save DataResend RealTime Mes:"+msg);
        ByteBuffer bb= PackageEntityManager.getByteBuffer(msg);
        DataPackage dp=conversionTBox.generate(bb);
        DataResendRealTimeMes bean=dp.loadBean(DataResendRealTimeMes.class);
        // 0608协议调整后实时数据和报警数据分别补发
        RealTimeReportData rd=new RealTimeReportData();
        rd.setVin(vin);
        rd.setImei(bean.getImei());
        rd.setApplicationId(bean.getApplicationID());
        rd.setMessageId(bean.getMessageID());
        rd.setSendingTime(dataTool.seconds2Date(bean.getSendingTime()));
        rd.setFuelOil(bean.getFuelOil() / 2f);
        rd.setAvgOilA(dataTool.getTrueAvgOil(bean.getAvgOilA()));
        rd.setAvgOilB(dataTool.getTrueAvgOil(bean.getAvgOilB()));
        rd.setServiceIntervall(bean.getServiceIntervall());

        rd.setLeftFrontTirePressure(bean.getLeftFrontTirePressure()*2.8f);
        rd.setLeftRearTirePressure(bean.getLeftRearTirePressure()*2.8f);
        rd.setRightFrontTirePressure(bean.getRightFrontTirePressure()*2.8f);
        rd.setRightRearTirePressure(bean.getRightRearTirePressure()*2.8f);
        char[] windows=dataTool.getBitsFromShort(bean.getWindowInformation());//
        rd.setLeftFrontWindowInformation(dataTool.getWindowStatus(String.valueOf(windows[6]) + String.valueOf(windows[7])));
        rd.setRightFrontWindowInformation(dataTool.getWindowStatus(String.valueOf(windows[4]) + String.valueOf(windows[5])));
        rd.setLeftRearWindowInformation(dataTool.getWindowStatus(String.valueOf(windows[2]) + String.valueOf(windows[3])));
        rd.setRightRearWindowInformation(dataTool.getWindowStatus(String.valueOf(windows[0]) + String.valueOf(windows[1])));

        rd.setVehicleTemperature(dataTool.getTrueTmp(bean.getVehicleTemperature()));//温度按照上报数值-40
        rd.setVehicleOuterTemperature(dataTool.getTrueTmp(bean.getVehicleOuterTemperature()));
        char[] doors=dataTool.getBitsFromShort(bean.getDoorInformation());//门 bit位置按照大端传输原则
        //
        rd.setLeftFrontDoorInformation(dataTool.getDoorStatus(String.valueOf(doors[6])+String.valueOf(doors[7])));
        rd.setRightFrontDoorInformation(dataTool.getDoorStatus(String.valueOf(doors[4])+String.valueOf(doors[5])));
        rd.setLeftRearDoorInformation(dataTool.getDoorStatus(String.valueOf(doors[2])+String.valueOf(doors[3])));
        rd.setRightRearDoorInformation(dataTool.getDoorStatus(String.valueOf(doors[0])+String.valueOf(doors[1])));

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
        gd.setIsLocation(location[7] == '0' ? (short) 0 : (short) 1);//bit0 0有效定位 1无效定位
        gd.setNorthSouth(location[6]=='0'?"N":"S");//bit1 0北纬 1南纬
        gd.setEastWest(location[5]=='0'?"E":"W");//bit2 0东经 1西经
        gd.setLatitude(dataTool.getTrueLatAndLon(bean.getLatitude()));
        gd.setLongitude(dataTool.getTrueLatAndLon(bean.getLongitude()));
        gd.setSpeed(dataTool.getTrueSpeed(bean.getSpeed()));
        gd.setHeading(bean.getHeading());
        gpsDataRepository.save(gd);
    }
    public void saveWarningMessage(String vin,String msg){
        //报警数据保存
        _logger.info(">>save WarningMessage:" + msg);
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

        warningMessageDataRespository.save(wd);
    }

    public void saveDataResendWarningMessage(String vin,String msg){
        //报警数据保存
        _logger.info(">>save DataResend WarningMessage:"+msg);
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

        warningMessageDataRespository.save(wd);
    }

    public void saveFailureMessage(String vin,String msg){
        //故障数据保存
        _logger.info(">>save FailureMessage:"+msg);
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
        failureMessageDataRespository.save(wd);
    }

    public void saveDataResendFailureMessage(String vin,String msg){
        //补发故障数据保存
        _logger.info(">>save DataResend FailureMessage:"+msg);
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

        wd.setInfo1((short)(bean.getInfo1().shortValue()&0xFF));
        wd.setInfo2((short)(bean.getInfo2().shortValue()&0xFF));
        wd.setInfo3((short)(bean.getInfo3().shortValue()&0xFF));
        wd.setInfo4((short)(bean.getInfo4().shortValue()&0xFF));
        wd.setInfo5((short)(bean.getInfo5().shortValue()&0xFF));
        wd.setInfo6((short)(bean.getInfo6().shortValue()&0xFF));
        wd.setInfo7((short)(bean.getInfo7().shortValue()&0xFF));
        wd.setInfo8((short)(bean.getInfo8().shortValue()&0xFF));

        failureMessageDataRespository.save(wd);
    }


}
