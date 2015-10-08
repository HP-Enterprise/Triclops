package com.hp.triclops.service;

import com.hp.data.bean.tbox.RealTimeReportMes;
import com.hp.data.bean.tbox.RegularReportMes;
import com.hp.data.bean.tbox.RemoteControlAck;
import com.hp.data.bean.tbox.WarningMessage;
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
    ResendMesDataRespository resendMesDataRespository;

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
        rd.setSendingTime(bean.getSendingTime());
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
        rd.setSendingTime(bean.getSendingTime());
        rd.setFuelOil(bean.getFuelOil());
        rd.setAvgOil(bean.getAvgOil());
        rd.setOilLife(bean.getOilLife());
        rd.setDriveRange(bean.getDriveRange().toString());//此处要处理3个byte为一个int

        rd.setLeftFrontTirePressure(bean.getLeftFrontTirePressure());
        rd.setLeftRearTirePressure(bean.getLeftRearTirePressure());
        rd.setRightFrontTirePressure(bean.getRightFrontTirePressure());
        rd.setRightRearTirePressure(bean.getRightRearTirePressure());
        rd.setWindowInformation(dataTool.getBinaryStrFromByte((byte) (short) bean.getWindowInformation()));
        rd.setVehicleTemperature(bean.getVehicleTemperature());
        rd.setVehicleOuterTemperature(bean.getVehicleOuterTemperature());
        rd.setDoorInformation(dataTool.getBinaryStrFromByte((byte) (short) bean.getDoorInformation()));
        rd.setSingleBatteryVoltage(bean.getSingleBatteryVoltage());
        rd.setMaximumVoltagePowerBatteryPack(bean.getMaximumVoltagePowerBatteryPack());
        rd.setMaximumBatteryVoltage(bean.getMaximumBatteryVoltage());
        rd.setBatteryMonomerMinimumVoltage(bean.getBatteryMonomerMinimumVoltage());
        rd.setEngineCondition(bean.getEngineCondition());
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
        gd.setSendingTime(bean.getSendingTime());
        gd.setIsLocation(bean.getIsLocation());
        gd.setLatitude(bean.getLatitude());
        gd.setLongitude(bean.getLongitude());
        gd.setSpeed(bean.getSpeed());
        gd.setHeading(bean.getHeading());
        gpsDataRepository.save(gd);
    }

    public void saveDataResendMes(String vin,String msg){
        //补发数据保存
        _logger.info(">>save DataResendMes:"+msg);

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
        wd.setSendingTime(bean.getSendingTime());
        wd.setIsLocation(bean.getIsLocation());
        wd.setLatitude(bean.getLatitude());
        wd.setLongitude(bean.getLongitude());
        wd.setSpeed(bean.getSpeed());
        wd.setHeading(bean.getHeading());
        wd.setBcm1(dataTool.getBinaryStrFromByte(bean.getBcm1()));
        wd.setEms(dataTool.getBinaryStrFromByte(bean.getEms()));
        wd.setTcu(dataTool.getBinaryStrFromByte(bean.getTcu()));
        wd.setIc(dataTool.getBinaryStrFromByte(bean.getIc()));
        wd.setAbs(dataTool.getBinaryStrFromByte(bean.getAbs()));
        wd.setPdc(dataTool.getBinaryStrFromByte(bean.getPdc()));
        wd.setBcm2(dataTool.getBinaryStrFromByte(bean.getBcm2()));
        warningMessageDataRespository.save(wd);
    }


}
