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
import java.util.Date;

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
    OutputHexService outputHexService;


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
                outputHexService.getWarningMessageAndPush(vin, msg,1);
                outputHexService.getWarningMessageAndPush(vin, msg, 2);
                outputHexService.getWarningMessageAndPush(vin, msg, 3);
                outputHexService.getWarningMessageAndSms(vin, msg, 1);
                outputHexService.getWarningMessageAndSms(vin, msg, 2);
                outputHexService.getWarningMessageAndSms(vin, msg, 3);
                saveWarningMessage(vin, msg);
                break;
            case 0x25://补发报警数据
                outputHexService.getResendWarningMessageAndPush(vin, msg,1);
                outputHexService.getResendWarningMessageAndPush(vin, msg,2);
                outputHexService.getResendWarningMessageAndPush(vin, msg,3);
                outputHexService.getResendWarningMessageAndSms(vin, msg, 1);
                outputHexService.getResendWarningMessageAndSms(vin, msg,2);
                outputHexService.getResendWarningMessageAndSms(vin, msg,3);
                saveDataResendWarningMessage(vin, msg);
                break;
            case 0x28://故障数据
                boolean checkDuplicate=isDuplicateFailureMessage(vin,msg);
                if(!checkDuplicate){
                    saveFailureMessage(vin, msg);
                    outputHexService.getFailureMessageAndPush(vin, msg);
                }else{
                    _logger.info("[0x28]>>重复的故障数据，不处理");
                }
                break;
            case 0x29://补发故障数据
               checkDuplicate=isDuplicateResendFailureMessage(vin,msg);
               if(!checkDuplicate){
                    saveDataResendFailureMessage(vin, msg);
                    outputHexService.getResendFailureMessageAndPush(vin, msg);
                }else{
                    _logger.info("[0x29]>>重复的补发故障数据，不处理");
                }
                break;
            default:
                _logger.info(">>data is invalid,we will not save them");
                break;
        }
    }

    public void saveRegularReportMes(String vin,String msg){
        _logger.info("[0x21]>>保存上报的固定数据:"+msg);
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
        _logger.info("[0x22]>>保存上报的实时数据:"+msg);
        ByteBuffer bb= PackageEntityManager.getByteBuffer(msg);
        DataPackage dp=conversionTBox.generate(bb);
        RealTimeReportMes bean=dp.loadBean(RealTimeReportMes.class);
        short vehicleModel=bean.getVehicleModel();//按照协议0628车型编号 0~255 0：默认值(M82)；1：M82；2：M85； 3：F60；4：F70； 5：F60电动车
        boolean isM8X=true;
        if(vehicleModel>(short)2){
            isM8X=false;
        }
        RealTimeReportData rd=new RealTimeReportData();
        rd.setVin(vin);
        rd.setImei(bean.getImei());
        rd.setApplicationId(bean.getApplicationID());
        rd.setMessageId(bean.getMessageID());
        Date receiveDate=new Date();
        rd.setSendingTime(receiveDate);//服务器时间
        rd.setTripId(bean.getTripID());

        rd.setFuelOil(bean.getFuelOil()==0xff?-200:bean.getFuelOil()* 1f);//0xff无效值
        rd.setAvgOilA(dataTool.getTrueAvgOil(bean.getAvgOilA()));
        rd.setAvgOilB(dataTool.getTrueAvgOil(bean.getAvgOilB()));
        rd.setServiceIntervall(-200);//在协议0628已经删除此项数据
        if(isM8X) {
            rd.setLeftFrontTirePressure(dataTool.getTrueTirePressure(bean.getLeftFrontTirePressure()));//有效值0-125
            rd.setLeftRearTirePressure(dataTool.getTrueTirePressure(bean.getLeftRearTirePressure()));
            rd.setRightFrontTirePressure(dataTool.getTrueTirePressure(bean.getRightFrontTirePressure()));
            rd.setRightRearTirePressure(dataTool.getTrueTirePressure(bean.getRightRearTirePressure()));
        }else{//在协议0628中F60无此数据 预留
            rd.setLeftFrontTirePressure(0.0f);
            rd.setLeftRearTirePressure(0.0f);
            rd.setRightFrontTirePressure(0.0f);
            rd.setRightRearTirePressure(0.0f);
        }
        char[] windows=dataTool.getBitsFromInteger(bean.getWindowInformation());//
        if(isM8X) {
            rd.setLeftFrontWindowInformation(dataTool.getWindowStatus(String.valueOf(windows[14]) + String.valueOf(windows[15])));
            rd.setRightFrontWindowInformation(dataTool.getWindowStatus(String.valueOf(windows[12]) + String.valueOf(windows[13])));
            rd.setLeftRearWindowInformation(dataTool.getWindowStatus(String.valueOf(windows[10]) + String.valueOf(windows[11])));
            rd.setRightRearWindowInformation(dataTool.getWindowStatus(String.valueOf(windows[8]) + String.valueOf(windows[9])));
        }else{//在协议0628中F60无此数据 预留
            rd.setLeftFrontWindowInformation("3");//车窗信息 0开1半开2关3信号异常
            rd.setRightFrontWindowInformation("3");
            rd.setLeftRearWindowInformation("3");
            rd.setRightRearWindowInformation("3");
        }
        rd.setVehicleTemperature(dataTool.getInternTrueTmp(bean.getVehicleTemperature()));//
        rd.setVehicleOuterTemperature(dataTool.getOuterTrueTmp(bean.getVehicleOuterTemperature()));
        char[] doors=dataTool.getBitsFromShort(bean.getDoorInformation());//门 1开0关  bit 大端传输

        rd.setLeftFrontDoorInformation(dataTool.getDoorStatus(String.valueOf(doors[6]) + String.valueOf(doors[7])));
        rd.setRightFrontDoorInformation(dataTool.getDoorStatus(String.valueOf(doors[4]) + String.valueOf(doors[5])));
        rd.setLeftRearDoorInformation(dataTool.getDoorStatus(String.valueOf(doors[2]) + String.valueOf(doors[3])));
        rd.setRightRearDoorInformation(dataTool.getDoorStatus(String.valueOf(doors[0]) + String.valueOf(doors[1])));


        // waiting for protocol after 6.1.7
        rd.setDrivingTime(0);
        rd.setOilLife((short) 0);
        rd.setDrivingRange(dataTool.getDriveRangeFrom3Bytes(bean.getKilometerMileage()));//行驶里程
        rd.setMileageRange(bean.getDrivingRange()==0xfff?-200:bean.getDrivingRange());//续航里程
        char[] bonnetAndTrunk=dataTool.getBitsFromShort(bean.getBonnetAndTrunk());
        rd.setEngineCoverState(dataTool.getDoorStatus(String.valueOf(bonnetAndTrunk[6]) + String.valueOf(bonnetAndTrunk[7])));
        rd.setTrunkLidState(dataTool.getDoorStatus(String.valueOf(bonnetAndTrunk[4]) + String.valueOf(bonnetAndTrunk[5])));
        char[] statWindow=dataTool.getBitsFromShort(bean.getStatWindow());
        if(isM8X) {
            rd.setSkylightState(dataTool.getSkyWindowStatus(String.valueOf(statWindow[6]) + String.valueOf(statWindow[7])));
        }else{
            //F60
            rd.setSkylightState(dataTool.getSkyWindowStatus(String.valueOf(statWindow[6]) + String.valueOf(statWindow[7])));
        }
        rd.setParkingState("0");
        char[] vBytes=dataTool.getBitsFromInteger(bean.getVoltage());
        if(isM8X) {
            //长度： 14bit
            int value=dataTool.getValueFromBytes(vBytes,14);
            rd.setVoltage(value==0x3ff?-200:(value * 0.0009765625f + 3.0f));//pdf 0628 part5.4 No24
        }else{
            //F60 长度： 8bit
            int value=dataTool.getValueFromBytes(vBytes,8);
            rd.setVoltage(value==0xff?-200:(value * 0.1f));
        }
        rd.setAverageSpeedA(dataTool.getTrueAvgSpeed(bean.getAverageSpeedA()));
        rd.setAverageSpeedB(dataTool.getTrueAvgSpeed(bean.getAverageSpeedB()));

        realTimeReportDataRespository.save(rd);
        //普通实时数据和位置数据分表存储
        GpsData gd=new GpsData();
        gd.setVin(vin);
        gd.setImei(bean.getImei());
        gd.setApplicationId(bean.getApplicationID());
        gd.setMessageId(bean.getMessageID());
        gd.setSendingTime(receiveDate);//服务器时间
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
        _logger.info("[0x23]>>保存上报的补发实时数据:"+msg);
        ByteBuffer bb= PackageEntityManager.getByteBuffer(msg);
        DataPackage dp=conversionTBox.generate(bb);
        DataResendRealTimeMes bean=dp.loadBean(DataResendRealTimeMes.class);
        short vehicleModel=bean.getVehicleModel();//按照协议0628车型编号 0~255 0：默认值(M82)；1：M82；2：M85； 3：F60；4：F70； 5：F60电动车
        boolean isM8X=true;
        if(vehicleModel>(short)2){
            isM8X=false;
        }
        RealTimeReportData rd=new RealTimeReportData();
        rd.setVin(vin);
        rd.setImei(bean.getImei());
        rd.setApplicationId(bean.getApplicationID());
        rd.setMessageId(bean.getMessageID());
        Date receiveDate=new Date();
        rd.setSendingTime(receiveDate);//服务器时间
        rd.setTripId(bean.getTripID());

        rd.setFuelOil(bean.getFuelOil()==0xff?-200:bean.getFuelOil()* 1f);//0xff无效值
        rd.setAvgOilA(dataTool.getTrueAvgOil(bean.getAvgOilA()));
        rd.setAvgOilB(dataTool.getTrueAvgOil(bean.getAvgOilB()));
        rd.setServiceIntervall(0);//在协议0628已经删除此项数据
        if(isM8X) {
            rd.setLeftFrontTirePressure(dataTool.getTrueTirePressure(bean.getLeftFrontTirePressure()));//有效值0-125
            rd.setLeftRearTirePressure(dataTool.getTrueTirePressure(bean.getLeftRearTirePressure()));
            rd.setRightFrontTirePressure(dataTool.getTrueTirePressure(bean.getRightFrontTirePressure()));
            rd.setRightRearTirePressure(dataTool.getTrueTirePressure(bean.getRightRearTirePressure()));
        }else{//在协议0628中F60无此数据 预留
            rd.setLeftFrontTirePressure(0.0f);
            rd.setLeftRearTirePressure(0.0f);
            rd.setRightFrontTirePressure(0.0f);
            rd.setRightRearTirePressure(0.0f);
        }
        char[] windows=dataTool.getBitsFromInteger(bean.getWindowInformation());//
        if(isM8X) {
            rd.setLeftFrontWindowInformation(dataTool.getF60WindowStatus(String.valueOf(windows[13]) +String.valueOf(windows[14]) + String.valueOf(windows[15])));
            rd.setRightFrontWindowInformation(dataTool.getF60WindowStatus(String.valueOf(windows[10]) +String.valueOf(windows[11]) + String.valueOf(windows[12])));
            rd.setLeftRearWindowInformation(dataTool.getF60WindowStatus(String.valueOf(windows[5]) +String.valueOf(windows[6]) + String.valueOf(windows[7])));
            rd.setRightRearWindowInformation(dataTool.getF60WindowStatus(String.valueOf(windows[2]) +String.valueOf(windows[3]) + String.valueOf(windows[4])));
        }else{//在协议0628中F60无此数据 预留
            rd.setLeftFrontWindowInformation(dataTool.getWindowStatus(String.valueOf(windows[14]) + String.valueOf(windows[15])));
            rd.setRightFrontWindowInformation(dataTool.getWindowStatus(String.valueOf(windows[12]) + String.valueOf(windows[13])));
            rd.setLeftRearWindowInformation(dataTool.getWindowStatus(String.valueOf(windows[10]) + String.valueOf(windows[11])));
            rd.setRightRearWindowInformation(dataTool.getWindowStatus(String.valueOf(windows[8]) + String.valueOf(windows[9])));
        }
        rd.setVehicleTemperature(dataTool.getInternTrueTmp(bean.getVehicleTemperature()));//
        rd.setVehicleOuterTemperature(dataTool.getOuterTrueTmp(bean.getVehicleOuterTemperature()));
        char[] doors=dataTool.getBitsFromShort(bean.getDoorInformation());//门 1开0关  bit 大端传输

        rd.setLeftFrontDoorInformation(dataTool.getDoorStatus(String.valueOf(doors[6]) + String.valueOf(doors[7])));
        rd.setRightFrontDoorInformation(dataTool.getDoorStatus(String.valueOf(doors[4]) + String.valueOf(doors[5])));
        rd.setLeftRearDoorInformation(dataTool.getDoorStatus(String.valueOf(doors[2]) + String.valueOf(doors[3])));
        rd.setRightRearDoorInformation(dataTool.getDoorStatus(String.valueOf(doors[0]) + String.valueOf(doors[1])));


        // waiting for protocol after 6.1.7
        rd.setDrivingTime(0);
        rd.setOilLife((short) 0);
        rd.setDrivingRange(dataTool.getDriveRangeFrom3Bytes(bean.getKilometerMileage()));//行驶里程
        rd.setMileageRange(bean.getDrivingRange()==0xfff?-200:bean.getDrivingRange());//续航里程
        char[] bonnetAndTrunk=dataTool.getBitsFromShort(bean.getBonnetAndTrunk());
        rd.setEngineCoverState(dataTool.getDoorStatus(String.valueOf(bonnetAndTrunk[6]) + String.valueOf(bonnetAndTrunk[7])));
        rd.setTrunkLidState(dataTool.getDoorStatus(String.valueOf(bonnetAndTrunk[4]) + String.valueOf(bonnetAndTrunk[5])));
        char[] statWindow=dataTool.getBitsFromShort(bean.getStatWindow());
        if(isM8X) {
            rd.setSkylightState(dataTool.getSkyWindowStatus(String.valueOf(statWindow[6]) + String.valueOf(statWindow[7])));
        }else{
            //F60
            rd.setSkylightState(dataTool.getSkyWindowStatus(String.valueOf(statWindow[6]) + String.valueOf(statWindow[7])));
        }
        rd.setParkingState("0");
        char[] vBytes=dataTool.getBitsFromInteger(bean.getVoltage());
        if(isM8X) {
            //长度： 14bit
            int value=dataTool.getValueFromBytes(vBytes,14);
            rd.setVoltage(value==0x3ff?-200:(value * 0.0009765625f + 3.0f));//pdf 0628 part5.4 No24
        }else{
            //F60 长度： 8bit
            int value=dataTool.getValueFromBytes(vBytes,8);
            rd.setVoltage(value==0xff?-200:(value * 0.1f));
        }
        rd.setAverageSpeedA(dataTool.getTrueAvgSpeed(bean.getAverageSpeedA()));
        rd.setAverageSpeedB(dataTool.getTrueAvgSpeed(bean.getAverageSpeedB()));

        realTimeReportDataRespository.save(rd);
        //普通实时数据和位置数据分表存储
        GpsData gd=new GpsData();
        gd.setVin(vin);
        gd.setImei(bean.getImei());
        gd.setApplicationId(bean.getApplicationID());
        gd.setMessageId(bean.getMessageID());
        gd.setSendingTime(receiveDate);//服务器时间
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
        _logger.info("[0x24]>>保存上报的报警数据:" + msg);
        WarningMessage bean=dataTool.decodeWarningMessage(msg);
        WarningMessageData wd=new WarningMessageData();
        wd.setVin(vin);
        wd.setImei(bean.getImei());
        wd.setApplicationId(bean.getApplicationID());
        wd.setMessageId(bean.getMessageID());
        wd.setSendingTime(dataTool.seconds2Date(bean.getSendingTime()));
        wd.setReceiveTime(new Date());
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
        wd.setCrashWarning(dataTool.getWarningInfoFromByte(bean.getCrashWarning()));
        wd.setAtaWarning(dataTool.getWarningInfoFromByte(bean.getAtaWarning()));
        wd.setSafetyBeltCount(bean.getSafetyBeltCount());
        wd.setVehicleHitSpeed(dataTool.getHitSpeed(bean.getVehicleSpeedLast()));

        warningMessageDataRespository.save(wd);
    }

    public void saveDataResendWarningMessage(String vin,String msg){
        //报警数据保存
        _logger.info("[0x25]>>保存上报的补发报警数据:"+msg);
        DataResendWarningMes bean=dataTool.decodeResendWarningMessage(msg);
        WarningMessageData wd=new WarningMessageData();
        wd.setVin(vin);
        wd.setImei(bean.getImei());
        wd.setApplicationId(bean.getApplicationID());
        wd.setMessageId(bean.getMessageID());
        wd.setSendingTime(dataTool.seconds2Date(bean.getSendingTime()));
        wd.setReceiveTime(new Date());
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
        wd.setCrashWarning(dataTool.getWarningInfoFromByte(bean.getCrashWarning()));
        wd.setAtaWarning(dataTool.getWarningInfoFromByte(bean.getAtaWarning()));
        wd.setSafetyBeltCount(bean.getSafetyBeltCount());
        wd.setVehicleHitSpeed(dataTool.getHitSpeed(bean.getVehicleSpeedLast()));

        warningMessageDataRespository.save(wd);
    }

    /**
     * 判断是否是和最近一条故障消息内容一样
     * @param vin
     * @param msg
     * @return
     */
    public boolean isDuplicateFailureMessage(String vin,String msg){
        boolean result=false;
        _logger.info("[0x28]>>检查是否是重复的故障数据");
        ByteBuffer bb= PackageEntityManager.getByteBuffer(msg);
        DataPackage dp=conversionTBox.generate(bb);
        FailureMessage bean=dp.loadBean(FailureMessage.class);
        String info=dataTool.getFailureMesId(bean);//当前故障消息
        FailureMessageData lastData= failureMessageDataRespository.findTopByVinOrderByReceiveTimeDescIdDesc(vin);
        if(lastData!=null){
            if(lastData.getInfo().equals(info)){
                result=true;
            }
        }
        return result;
    }

    public void saveFailureMessage(String vin,String msg){
        //故障数据保存
        _logger.info("[0x28]>>保存上报的故障数据:"+msg);
        ByteBuffer bb= PackageEntityManager.getByteBuffer(msg);
        DataPackage dp=conversionTBox.generate(bb);
        FailureMessage bean=dp.loadBean(FailureMessage.class);
        FailureMessageData wd=new FailureMessageData();
        wd.setVin(vin);
        wd.setImei(bean.getImei());
        wd.setApplicationId(bean.getApplicationID());
        wd.setMessageId(bean.getMessageID());
        wd.setSendingTime(dataTool.seconds2Date(bean.getSendingTime()));
        wd.setReceiveTime(new Date());
        //分解IsIsLocation信息
        char[] location=dataTool.getBitsFromShort(bean.getIsLocation());
        wd.setIsLocation(location[7] == '0' ? (short) 0 : (short) 1);//bit0 0有效定位 1无效定位
        wd.setNorthSouth(location[6] == '0' ? "N" : "S");//bit1 0北纬 1南纬
        wd.setEastWest(location[5] == '0' ? "E" : "W");//bit2 0东经 1西经
        wd.setLatitude(dataTool.getTrueLatAndLon(bean.getLatitude()));
        wd.setLongitude(dataTool.getTrueLatAndLon(bean.getLongitude()));
        wd.setSpeed(dataTool.getTrueSpeed(bean.getSpeed()));
        wd.setHeading(bean.getHeading());
        wd.setInfo(dataTool.getFailureMesId(bean));

        failureMessageDataRespository.save(wd);
    }

    /**
     * 判断是否是和最近一条故障消息内容一样
     * @param vin
     * @param msg
     * @return
     */
    public boolean isDuplicateResendFailureMessage(String vin,String msg){
        boolean result=false;
        _logger.info("[0x29]>>检查是否是重复的补发故障数据");
        ByteBuffer bb= PackageEntityManager.getByteBuffer(msg);
        DataPackage dp=conversionTBox.generate(bb);
        DataResendFailureData bean=dp.loadBean(DataResendFailureData.class);
        String info=dataTool.getDataResendFailureMesId(bean);//当前故障消息
        FailureMessageData lastData= failureMessageDataRespository.findTopByVinOrderByReceiveTimeDescIdDesc(vin);
        if(lastData!=null){
            if(lastData.getInfo().equals(info)){
                result=true;
            }
        }
        return result;
    }
    public void saveDataResendFailureMessage(String vin,String msg){
        //补发故障数据保存
        _logger.info("[0x29]>>保存上报的补发故障数据:"+msg);
        ByteBuffer bb= PackageEntityManager.getByteBuffer(msg);
        DataPackage dp=conversionTBox.generate(bb);
        DataResendFailureData bean=dp.loadBean(DataResendFailureData.class);
        FailureMessageData wd=new FailureMessageData();
        wd.setVin(vin);
        wd.setImei(bean.getImei());
        wd.setApplicationId(bean.getApplicationID());
        wd.setMessageId(bean.getMessageID());
        wd.setSendingTime(dataTool.seconds2Date(bean.getSendingTime()));
        wd.setReceiveTime(new Date());
        //分解IsIsLocation信息
        char[] location=dataTool.getBitsFromShort(bean.getIsLocation());
        wd.setIsLocation(location[7] == '0' ? (short) 0 : (short) 1);//bit0 0有效定位 1无效定位
        wd.setNorthSouth(location[6] == '0' ? "N" : "S");//bit1 0北纬 1南纬
        wd.setEastWest(location[5] == '0' ? "E" : "W");//bit2 0东经 1西经
        wd.setLatitude(dataTool.getTrueLatAndLon(bean.getLatitude()));
        wd.setLongitude(dataTool.getTrueLatAndLon(bean.getLongitude()));
        wd.setSpeed(dataTool.getTrueSpeed(bean.getSpeed()));
        wd.setHeading(bean.getHeading());

        wd.setInfo(dataTool.getDataResendFailureMesId(bean));


        failureMessageDataRespository.save(wd);
    }


}
