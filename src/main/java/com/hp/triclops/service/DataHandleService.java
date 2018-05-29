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
import org.springframework.transaction.annotation.Transactional;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    DrivingBehaviorDataRepository drivingBehaviorDataRepository;
    @Autowired
    DrivingBehaviorOriginalDataRepository drivingBehaviorOriginalDataRepository;
    @Autowired
    OutputHexService outputHexService;


    @Autowired
    Conversion conversionTBox;
    private Logger _logger= LoggerFactory.getLogger(DataHandleService.class);

    @Autowired
    DataTool dataTool;
    public void saveMessage(byte dataType, List<String> msgList){
       //数据保存，传入原始消息数据hex
       //根据application id分析消息类型，然后调用具体的保存方法
//        byte[] receiveData = dataTool.getBytesFromByteBuf(dataTool.getByteBuf(msg));
//        byte modelType = dataTool.getModelType(receiveData);
        switch(dataType)
        {
            case 0x21://固定数据
                saveRegularReportMes(msgList);
                break;
            case 0x22://实时数据
                List<String> carM82MessageList = new ArrayList<>();
                List<String> carMessageList = new ArrayList<>();
                for (String message : msgList) {
                    String msg = message.split(":")[1];
                    byte[] receiveData = dataTool.getBytesFromByteBuf(dataTool.getByteBuf(msg));
                    byte modelType = dataTool.getModelType(receiveData);
                    if(modelType == 0 || modelType == 1) {//M82车型
                        carM82MessageList.add(message);
                    } else {
                        carMessageList.add(message);
                    }

                }
                saveRealTimeReportMesM82(carM82MessageList);
                saveRealTimeReportMes(carMessageList);
                break;
            case 0x23://补发实时数据
                List<String> carM82MessageList2 = new ArrayList<>();
                List<String> carMessageList2 = new ArrayList<>();
                for (String message : msgList) {
                    String msg = message.split(":")[1];
                    byte[] receiveData = dataTool.getBytesFromByteBuf(dataTool.getByteBuf(msg));
                    byte modelType = dataTool.getModelType(receiveData);
                    if(modelType == 0 || modelType == 1) {//M82车型
                        carM82MessageList2.add(message);
                    } else {
                        carMessageList2.add(message);
                    }

                }
                saveDataResendRealTimeMesM82(carM82MessageList2);
                saveDataResendRealTimeMes(carMessageList2);
                break;
            case 0x24://报警数据
                for (String message : msgList) {
                    String[] tmp = message.split(":");
                    String vin = tmp[0];
                    String msg = tmp[1];
                    outputHexService.getWarningMessage(vin, msg, 1);
                    outputHexService.getWarningMessage(vin, msg, 2);
                    outputHexService.getWarningMessage(vin, msg, 3);
                }
//                outputHexService.getWarningMessageAndPush(vin, msg,1);
//                outputHexService.getWarningMessageAndPush(vin, msg, 2);
//                outputHexService.getWarningMessageAndPush(vin, msg, 3);
//                outputHexService.getWarningMessageAndSms(vin, msg, 1);
//                outputHexService.getWarningMessageAndSms(vin, msg, 2);
//                outputHexService.getWarningMessageAndSms(vin, msg, 3);
                saveWarningMessage(msgList);
                break;
            case 0x25://补发报警数据
                for (String message : msgList) {
                    String[] tmp = message.split(":");
                    String vin = tmp[0];
                    String msg = tmp[1];
                    outputHexService.getResendWarningMessage(vin, msg, 1);
                    outputHexService.getResendWarningMessage(vin, msg, 2);
                    outputHexService.getResendWarningMessage(vin, msg, 3);
                }
//                outputHexService.getResendWarningMessageAndPush(vin, msg,1);
//                outputHexService.getResendWarningMessageAndPush(vin, msg,2);
//                outputHexService.getResendWarningMessageAndPush(vin, msg,3);
//                outputHexService.getResendWarningMessageAndSms(vin, msg, 1);
//                outputHexService.getResendWarningMessageAndSms(vin, msg,2);
//                outputHexService.getResendWarningMessageAndSms(vin, msg,3);
                saveDataResendWarningMessage(msgList);
                break;
            case 0x28://故障数据
                for (String message : msgList) {
                    String[] tmp = message.split(":");
                    String vin = tmp[0];
                    String msg = tmp[1];
                    boolean checkDuplicate = isDuplicateFailureMessage(vin, msg);
                    if (!checkDuplicate) {
                        saveFailureMessage(vin, msg);
                        outputHexService.getFailureMessageAndPush(vin, msg);
                    } else {
                        _logger.info("[0x28]>>重复的故障数据，不处理");
                    }
                }
                break;
            case 0x29://补发故障数据
                for (String message : msgList) {
                    String[] tmp = message.split(":");
                    String vin = tmp[0];
                    String msg = tmp[1];
                    boolean checkDuplicate = isDuplicateResendFailureMessage(vin, msg);
                    if (!checkDuplicate) {
                        saveDataResendFailureMessage(vin, msg);
                        outputHexService.getResendFailureMessageAndPush(vin, msg);
                    } else {
                        _logger.info("[0x29]>>重复的补发故障数据，不处理");
                    }
                }
                break;
            case 0x2A://驾驶行为数据上报
                saveDrivingBehaviorData(msgList);
                break;
            default:
                _logger.info(">>data is invalid,we will not save them");
                break;
        }
    }

    //@Transactional
    public void saveRegularReportMes(List<String> msgList) {
        for (String message : msgList) {
            String[] tmp = message.split(":");
            String vin = tmp[0];
            String msg = tmp[1];
            _logger.info("[0x21]>>保存上报的固定数据:"+msg);
            long startTime = System.currentTimeMillis();
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
            long middleTime = System.currentTimeMillis();
            regularReportDataRespository.save(rd);
            long endTime = System.currentTimeMillis();
            if (endTime - startTime > 100) {
                _logger.info("saveRegularReportMes Analysis data time" + (middleTime - startTime));
                _logger.info("saveRegularReportMes First time save time" + (endTime - middleTime));
            }
        }
    }

    //@Transactional
    public void saveDrivingBehaviorData(List<String> msgList) {
        for (String message : msgList) {
            String[] tmp = message.split(":");
            String vin = tmp[0];
            String msg = tmp[1];
            _logger.info("[0x21]>>保存上报的驾驶行为数据:" + msg);
            long startTime = System.currentTimeMillis();
            DrivingBehaviorMes bean = dataTool.decodeDrivingBehaviorMes(msg);
            boolean isM8X = true;
            short vehicleModel = bean.getVehicleModel();//按照协议0628车型编号 0~255 0：默认值(M82)；1：M82；2：M85； 3：F60；4：F70； 5：F60电动车
            if (vehicleModel > (short) 2) {
                isM8X = false;
            }
            DrivingBehaviorData dd = new DrivingBehaviorData();
            dd.setVin(vin);
            dd.setImei(bean.getImei());
            dd.setApplicationId(bean.getApplicationID());
            dd.setMessageId(bean.getMessageID());
            dd.setTripId(bean.getTripID());
            dd.setSendingTime(dataTool.seconds2Date(bean.getSendingTime()));
            dd.setReceiveTime(new Date());
            dd.setSpeedUp((short) dataTool.calcSpeed(bean.getDriveAcceleration(), 1));//通过行驶方向加速度判断是否存在急加速
            dd.setSpeedDown((short) dataTool.calcSpeed(bean.getDriveAcceleration(), 2));//通过行驶方向加速度判断是否存在急减速
            dd.setSpeedTurn((short) dataTool.calcSpeed(bean.getLateralAcceleration(), 3));//通横向加速度判断是否存在急转弯
            long _tripA = bean.getTripA();
            long _tripB = bean.getTripB();
            if (isM8X) {//M8X 无效值0x1ffff
                dd.setTripA(_tripA == 0x1ffff ? -200 : (_tripA * 0.1f));
                dd.setTripB(_tripB == 0x1ffff ? -200 : (_tripB * 0.1f));
            } else {//F60上报范围0-10000 参考v0630
                dd.setTripA(_tripA > 10000 ? -200 : (_tripA * 0.1f));
                dd.setTripB(_tripB > 10000 ? -200 : (_tripB * 0.1f));
            }
            char[] seatBeltInfo = dataTool.getBitsFromInteger(bean.getSafeBelt());//安全带状况 大端传输
            if (isM8X) {
                dd.setSeatbeltFl(dataTool.getSeatBeltStatus4M8X(String.valueOf(seatBeltInfo[14]) + String.valueOf(seatBeltInfo[15])));
                dd.setSeatbeltFr(dataTool.getSeatBeltStatus4M8X(String.valueOf(seatBeltInfo[12]) + String.valueOf(seatBeltInfo[13])));
                dd.setSeatbeltRl(dataTool.getSeatBeltStatus4M8X(String.valueOf(seatBeltInfo[10]) + String.valueOf(seatBeltInfo[11])));
                dd.setSeatbeltRm(dataTool.getSeatBeltStatus4M8X(String.valueOf(seatBeltInfo[8]) + String.valueOf(seatBeltInfo[9])));
                dd.setSeatbeltRr(dataTool.getSeatBeltStatus4M8X(String.valueOf(seatBeltInfo[6]) + String.valueOf(seatBeltInfo[7])));
            } else {
                dd.setSeatbeltFl(dataTool.getSeatBeltStatus4F60(String.valueOf(seatBeltInfo[14]) + String.valueOf(seatBeltInfo[15])));
                dd.setSeatbeltFr(dataTool.getSeatBeltStatus4F60(String.valueOf(seatBeltInfo[12]) + String.valueOf(seatBeltInfo[13])));
                dd.setSeatbeltRl(dataTool.getSeatBeltStatus4F60(String.valueOf(seatBeltInfo[10]) + String.valueOf(seatBeltInfo[11])));
                dd.setSeatbeltRm(dataTool.getSeatBeltStatus4F60(String.valueOf(seatBeltInfo[8]) + String.valueOf(seatBeltInfo[9])));
                dd.setSeatbeltRr(dataTool.getSeatBeltStatus4F60(String.valueOf(seatBeltInfo[6]) + String.valueOf(seatBeltInfo[7])));
            }
            dd.setDrivingRange(bean.getKilometerMileage() == 0xffffff ? 0 : bean.getFuelOil());//行驶里程在bean中已经是int数据
            dd.setFuelOil(bean.getFuelOil() == 0xff ? -200 : bean.getFuelOil() * 1f);//0xff无效值
            dd.setAvgOilA(dataTool.getTrueAvgOil(bean.getAvgOilA()));
            dd.setAvgOilB(dataTool.getTrueAvgOil(bean.getAvgOilB()));
            dd.setSpeed_1_count((short) dataTool.calcSpeedRang(bean.getSpeed(), 1));//计算车速范围
            dd.setSpeed_1_45_count((short) dataTool.calcSpeedRang(bean.getSpeed(), 2));//计算车速范围
            dd.setSpeed_45_90_count((short) dataTool.calcSpeedRang(bean.getSpeed(), 3));//计算车速范围
            dd.setSpeed_90_count((short) dataTool.calcSpeedRang(bean.getSpeed(), 4));//计算车速范围
            dd.setSpeed_up_count((short) dataTool.calcSpeedRang(bean.getSpeed(), 5));//超速时间
            dd.setMax_speed((short) dataTool.calcSpeedRang(bean.getSpeed(), 6));//最高车速

            long middleTime = System.currentTimeMillis();
            drivingBehaviorDataRepository.save(dd);
            long middleTime2 = System.currentTimeMillis();
            //---保存原始驾驶行为报文数据--
            DrivingBehavioOriginalData drivingBehavioOriginalData = new DrivingBehavioOriginalData();
            drivingBehavioOriginalData.setImei(bean.getImei());
            drivingBehavioOriginalData.setVin(vin);
            drivingBehavioOriginalData.setHexString(msg);
            drivingBehavioOriginalData.setReceiveTime(new Date());
            long middleTime3 = System.currentTimeMillis();
            drivingBehaviorOriginalDataRepository.save(drivingBehavioOriginalData);
            long endTime = System.currentTimeMillis();
            if (endTime - startTime > 100) {
                _logger.info("saveDrivingBehaviorData Analysis data time" + (middleTime - startTime));
                _logger.info("saveDrivingBehaviorData First time save time" + (middleTime2 - middleTime));
                _logger.info("saveDrivingBehaviorData Second time save time" + (endTime - middleTime3));
            }
        }
    }

    /**
     * 保存非M82车型实时数据
     */
   // @Transactional
    public void saveRealTimeReportMes(List<String> msgList){
        for (String message : msgList) {
            String[] tmp = message.split(":");
            String vin = tmp[0];
            String msg = tmp[1];
            _logger.info("[0x22]>>保存上报的实时数据:" + msg);
            long startTime = System.currentTimeMillis();
            ByteBuffer bb = PackageEntityManager.getByteBuffer(msg);
            DataPackage dp = conversionTBox.generate(bb);
            RealTimeReportMes bean = dp.loadBean(RealTimeReportMes.class);
            short vehicleModel = bean.getVehicleModel();//按照协议0628车型编号 0~255 0：默认值(M82)；1：M82；2：M85； 3：F60；4：F70； 5：F60电动车
            boolean isM8X = true;
            if (vehicleModel > (short) 2) {
                isM8X = false;
            }
            RealTimeReportData rd = new RealTimeReportData();
            rd.setVin(vin);
            rd.setImei(bean.getImei());
            rd.setApplicationId(bean.getApplicationID());
            rd.setMessageId(bean.getMessageID());
            Date receiveDate = new Date();
            rd.setSendingTime(receiveDate);//服务器时间
            rd.setTripId(bean.getTripID());

//        rd.setFuelOil(bean.getFuelOil()==0xff?-200:bean.getFuelOil()* 1f);//0xff无效值
            rd.setAvgOilA(dataTool.getTrueAvgOil(bean.getAvgOilA()));
            rd.setAvgOilB(dataTool.getTrueAvgOil(bean.getAvgOilB()));
            rd.setServiceIntervall(-200);//在协议0628已经删除此项数据
            Short fuelOil = bean.getFuelOil() == 0xff ? -200 : bean.getFuelOil();
            float val = 0f;
            if (vehicleModel == 0 || vehicleModel == 1 || vehicleModel == 2) {
                if (fuelOil > 0 && fuelOil < 56) {
                    val = (float) Math.round(((float) fuelOil / 56f) * 100f);
                } else if (fuelOil >= 56) {
                    val = 100f;
                } else if (fuelOil == -200) {
                    val = -200f;
                }
                rd.setLeftFrontTirePressure(dataTool.getTrueTirePressure(bean.getLeftFrontTirePressure()));//有效值0-125
                rd.setLeftRearTirePressure(dataTool.getTrueTirePressure(bean.getLeftRearTirePressure()));
                rd.setRightFrontTirePressure(dataTool.getTrueTirePressure(bean.getRightFrontTirePressure()));
                rd.setRightRearTirePressure(dataTool.getTrueTirePressure(bean.getRightRearTirePressure()));
            } else if (vehicleModel == 3 || vehicleModel == 5) {//todo 在协议0628中F60无此数据 预留
                if (fuelOil > 0 && fuelOil < 52) {
                    val = (float) Math.round(((float) fuelOil / 52f) * 100f);
                } else if (fuelOil >= 52) {
                    val = 100f;
                } else if (fuelOil == -200) {
                    val = -200f;
                }
                rd.setLeftFrontTirePressure(0.0f);
                rd.setLeftRearTirePressure(0.0f);
                rd.setRightFrontTirePressure(0.0f);
                rd.setRightRearTirePressure(0.0f);
            } else if (vehicleModel == 4) {//F70
                if (fuelOil > 0 && fuelOil < 46) {
                    val = (float) Math.round(((float) fuelOil / 46f) * 100f);
                } else if (fuelOil >= 46) {
                    val = 100f;
                } else if (fuelOil == -200) {
                    val = -200f;
                }
                rd.setLeftFrontTirePressure(0.0f);
                rd.setLeftRearTirePressure(0.0f);
                rd.setRightFrontTirePressure(0.0f);
                rd.setRightRearTirePressure(0.0f);
            }
            rd.setFuelOil(val);
            char[] windows = dataTool.getBitsFromInteger(bean.getWindowInformation());//
            if (isM8X) {
                rd.setLeftFrontWindowInformation(dataTool.getWindowStatus(String.valueOf(windows[14]) + String.valueOf(windows[15])));
                rd.setRightFrontWindowInformation(dataTool.getWindowStatus(String.valueOf(windows[12]) + String.valueOf(windows[13])));
                rd.setLeftRearWindowInformation(dataTool.getWindowStatus(String.valueOf(windows[10]) + String.valueOf(windows[11])));
                rd.setRightRearWindowInformation(dataTool.getWindowStatus(String.valueOf(windows[8]) + String.valueOf(windows[9])));
            } else {//
                //车窗信息 0开1半开2关3信号异常
                rd.setLeftFrontWindowInformation(dataTool.getWindowStatusFrom3Bits(String.valueOf(windows[13]) + String.valueOf(windows[14]) + String.valueOf(windows[15])));
                rd.setRightFrontWindowInformation(dataTool.getWindowStatusFrom3Bits(String.valueOf(windows[10]) + String.valueOf(windows[11]) + String.valueOf(windows[12])));
                rd.setLeftRearWindowInformation(dataTool.getWindowStatusFrom3Bits(String.valueOf(windows[7]) + String.valueOf(windows[8]) + String.valueOf(windows[9])));
                rd.setRightRearWindowInformation(dataTool.getWindowStatusFrom3Bits(String.valueOf(windows[4]) + String.valueOf(windows[5]) + String.valueOf(windows[6])));
            }
            rd.setVehicleTemperature(dataTool.getInternTrueTmp(bean.getVehicleTemperature()));//
            rd.setVehicleOuterTemperature(dataTool.getOuterTrueTmp(bean.getVehicleOuterTemperature()));
            char[] doors = dataTool.getBitsFromShort(bean.getDoorInformation());//门 1开0关  bit 大端传输

            rd.setLeftFrontDoorInformation(dataTool.getDoorStatus(String.valueOf(doors[6]) + String.valueOf(doors[7])));
            rd.setRightFrontDoorInformation(dataTool.getDoorStatus(String.valueOf(doors[4]) + String.valueOf(doors[5])));
            rd.setLeftRearDoorInformation(dataTool.getDoorStatus(String.valueOf(doors[2]) + String.valueOf(doors[3])));
            rd.setRightRearDoorInformation(dataTool.getDoorStatus(String.valueOf(doors[0]) + String.valueOf(doors[1])));


            // waiting for protocol after 6.1.7
            rd.setDrivingTime(0);
            rd.setOilLife((short) 0);
            rd.setDrivingRange(dataTool.getDriveRangeFrom3Bytes(bean.getKilometerMileage()));//行驶里程
//        rd.setMileageRange(bean.getDrivingRange()==0xfff?-200:bean.getDrivingRange());//续航里程
            if (bean.getDrivingRange() == 0xfff || bean.getDrivingRange() < 50) {
                rd.setMileageRange(-200);
            } else {
                rd.setMileageRange(bean.getDrivingRange());
            }
            char[] bonnetAndTrunk = dataTool.getBitsFromShort(bean.getBonnetAndTrunk());
            rd.setEngineCoverState(dataTool.getDoorStatus(String.valueOf(bonnetAndTrunk[6]) + String.valueOf(bonnetAndTrunk[7])));
            rd.setTrunkLidState(dataTool.getDoorStatus(String.valueOf(bonnetAndTrunk[4]) + String.valueOf(bonnetAndTrunk[5])));
            char[] statWindow = dataTool.getBitsFromShort(bean.getStatWindow());
            rd.setSkylightState(dataTool.getSkyWindowStatus(String.valueOf(statWindow[5]) + String.valueOf(statWindow[6]) + String.valueOf(statWindow[7])));
            rd.setParkingState("0");
            char[] vBytes = dataTool.getBitsFromInteger(bean.getVoltage());
            if (isM8X) {
                //长度： 14bit
                int value = dataTool.getValueFromBytes(vBytes, 14);
                rd.setVoltage(value == 0x3fff ? -200 : (value * 0.0009765625f + 3.0f));//pdf 0628 part5.4 No24
            } else {
                //F60 长度： 8bit
                int value = dataTool.getValueFromBytes(vBytes, 8);
                rd.setVoltage(value * 0.1f);
            }
            rd.setAverageSpeedA(dataTool.getTrueAvgSpeed(bean.getAverageSpeedA()));
            rd.setAverageSpeedB(dataTool.getTrueAvgSpeed(bean.getAverageSpeedB()));
            if (isM8X) {
                rd.setMtGearPostion("-200");
            } else {
                rd.setMtGearPostion(dataTool.getMtGearPostion(bean.getMt_gear_position()));
            }
            rd.setEngineState(-200);
            rd.setLfLockState(-200);
            rd.setLrLockState(-200);
            rd.setRfLockState(-200);
            rd.setRrLockState(-200);
            rd.setBlow(-200);
            rd.setAcState(-200);
            long middleTime = System.currentTimeMillis();
            realTimeReportDataRespository.save(rd);
            long middleTime2 = System.currentTimeMillis();
            //普通实时数据和位置数据分表存储
            GpsData gd = new GpsData();
            gd.setVin(vin);
            gd.setImei(bean.getImei());
            gd.setApplicationId(bean.getApplicationID());
            gd.setMessageId(bean.getMessageID());
            gd.setSendingTime(receiveDate);//服务器时间
            //分解IsIsLocation信息
            char[] location = dataTool.getBitsFromShort(bean.getIsLocation());
            gd.setIsLocation(location[7] == '0' ? (short) 0 : (short) 1);//bit0 0有效定位 1无效定位
            gd.setNorthSouth(location[6] == '0' ? "N" : "S");//bit1 0北纬 1南纬
            gd.setEastWest(location[5] == '0' ? "E" : "W");//bit2 0东经 1西经
            gd.setLatitude(dataTool.getTrueLatAndLon(bean.getLatitude()));
            gd.setLongitude(dataTool.getTrueLatAndLon(bean.getLongitude()));
            gd.setSpeed(dataTool.getTrueSpeed(bean.getSpeed()));
            gd.setHeading(bean.getHeading());
            long middleTime3 = System.currentTimeMillis();
            gpsDataRepository.save(gd);
            long endTime = System.currentTimeMillis();
            if (endTime - startTime > 100) {
                _logger.info("saveRealTimeReportMes Analysis data time" + (middleTime - startTime));
                _logger.info("saveRealTimeReportMes First time save time" + (middleTime2 - middleTime));
                _logger.info("saveRealTimeReportMes Second time save time" + (endTime - middleTime3));
            }
        }
    }

    /**
     * 保存M82车型实时数据
     * 协议0638
     */
    //@Transactional
    public void saveRealTimeReportMesM82(List<String> msgList){
        for (String message : msgList) {
            String[] tmp = message.split(":");
            String vin = tmp[0];
            String msg = tmp[1];
            _logger.info("[0x22]>>保存上报的实时数据:" + msg);
            long startTime = System.currentTimeMillis();
            RealTimeReportMesM82 realTimeReportMesM82 = new RealTimeReportMesM82();
            RealTimeReportMesM82 bean = realTimeReportMesM82.decode(dataTool.getByteBuf(msg));
            short vehicleModel = bean.getVehicleModel();//按照协议0628车型编号 0~255 0：默认值(M82)；1：M82；2：M85； 3：F60；4：F70； 5：F60电动车
            boolean isM8X = true;
            if (vehicleModel > (short) 2) {
                isM8X = false;
            }
            RealTimeReportData rd = new RealTimeReportData();
            rd.setVin(vin);
            rd.setImei(bean.getImei());
            rd.setApplicationId(bean.getApplicationID());
            rd.setMessageId(bean.getMessageID());
            Date receiveDate = new Date();
            rd.setSendingTime(receiveDate);//服务器时间
            rd.setTripId(bean.getTripID());

//        rd.setFuelOil(bean.getFuelOil()==0xff?-200:bean.getFuelOil()* 1f);//0xff无效值
            rd.setAvgOilA(dataTool.getTrueAvgOil(bean.getAvgOilA()));
            rd.setAvgOilB(dataTool.getTrueAvgOil(bean.getAvgOilB()));
            rd.setServiceIntervall(-200);//在协议0628已经删除此项数据
            Short fuelOil = bean.getFuelOil() == 0xff ? -200 : bean.getFuelOil();
            float val = 0f;
            if (vehicleModel == 0 || vehicleModel == 1 || vehicleModel == 2) {
                if (fuelOil > 0 && fuelOil < 56) {
                    val = (float) Math.round(((float) fuelOil / 56f) * 100f);
                } else if (fuelOil >= 56) {
                    val = 100f;
                } else if (fuelOil == -200) {
                    val = -200f;
                }
                rd.setLeftFrontTirePressure(dataTool.getTrueTirePressure(bean.getLeftFrontTirePressure()));//有效值0-125
                rd.setLeftRearTirePressure(dataTool.getTrueTirePressure(bean.getLeftRearTirePressure()));
                rd.setRightFrontTirePressure(dataTool.getTrueTirePressure(bean.getRightFrontTirePressure()));
                rd.setRightRearTirePressure(dataTool.getTrueTirePressure(bean.getRightRearTirePressure()));
            } else if (vehicleModel == 3 || vehicleModel == 5) {//todo 在协议0628中F60无此数据 预留
                if (fuelOil > 0 && fuelOil < 52) {
                    val = (float) Math.round(((float) fuelOil / 52f) * 100f);
                } else if (fuelOil >= 52) {
                    val = 100f;
                } else if (fuelOil == -200) {
                    val = -200f;
                }
                rd.setLeftFrontTirePressure(0.0f);
                rd.setLeftRearTirePressure(0.0f);
                rd.setRightFrontTirePressure(0.0f);
                rd.setRightRearTirePressure(0.0f);
            } else if (vehicleModel == 4) {//F70
                if (fuelOil > 0 && fuelOil < 46) {
                    val = (float) Math.round(((float) fuelOil / 46f) * 100f);
                } else if (fuelOil >= 46) {
                    val = 100f;
                } else if (fuelOil == -200) {
                    val = -200f;
                }
                rd.setLeftFrontTirePressure(0.0f);
                rd.setLeftRearTirePressure(0.0f);
                rd.setRightFrontTirePressure(0.0f);
                rd.setRightRearTirePressure(0.0f);
            }
            rd.setFuelOil(val);
            char[] windows = dataTool.getBitsFromInteger(bean.getWindowInformation());//
            if (isM8X) {//Bit0~2：STAT_WindowFL Bit3~5：STAT_WindowFR Bit6~8：STAT_WindowRL Bit9~11：STAT_WindowRR
                rd.setLeftFrontWindowInformation(dataTool.getM82WindowStatus(String.valueOf(windows[13]) + String.valueOf(windows[14]) + String.valueOf(windows[15])));
                rd.setRightFrontWindowInformation(dataTool.getM82WindowStatus(String.valueOf(windows[10]) + String.valueOf(windows[11]) + String.valueOf(windows[12])));
                rd.setLeftRearWindowInformation(dataTool.getM82WindowStatus(String.valueOf(windows[7]) + String.valueOf(windows[8]) + String.valueOf(windows[9])));
                rd.setRightRearWindowInformation(dataTool.getM82WindowStatus(String.valueOf(windows[4]) + String.valueOf(windows[5]) + String.valueOf(windows[6])));
            } else {//
                //车窗信息 0开1半开2关3信号异常
                rd.setLeftFrontWindowInformation(dataTool.getWindowStatusFrom3Bits(String.valueOf(windows[13]) + String.valueOf(windows[14]) + String.valueOf(windows[15])));
                rd.setRightFrontWindowInformation(dataTool.getWindowStatusFrom3Bits(String.valueOf(windows[10]) + String.valueOf(windows[11]) + String.valueOf(windows[12])));
                rd.setLeftRearWindowInformation(dataTool.getWindowStatusFrom3Bits(String.valueOf(windows[7]) + String.valueOf(windows[8]) + String.valueOf(windows[9])));
                rd.setRightRearWindowInformation(dataTool.getWindowStatusFrom3Bits(String.valueOf(windows[4]) + String.valueOf(windows[5]) + String.valueOf(windows[6])));
            }
            rd.setVehicleTemperature(dataTool.getInternTrueTmp(bean.getVehicleTemperature()));//
            rd.setVehicleOuterTemperature(dataTool.getOuterTrueTmp(bean.getVehicleOuterTemperature()));
            char[] doors = dataTool.getBitsFromShort(bean.getDoorInformation());//门 1开0关  bit 大端传输

            rd.setLeftFrontDoorInformation(dataTool.getDoorStatus(String.valueOf(doors[6]) + String.valueOf(doors[7])));
            rd.setRightFrontDoorInformation(dataTool.getDoorStatus(String.valueOf(doors[4]) + String.valueOf(doors[5])));
            rd.setLeftRearDoorInformation(dataTool.getDoorStatus(String.valueOf(doors[2]) + String.valueOf(doors[3])));
            rd.setRightRearDoorInformation(dataTool.getDoorStatus(String.valueOf(doors[0]) + String.valueOf(doors[1])));


            // waiting for protocol after 6.1.7
            rd.setDrivingTime(0);
            rd.setOilLife((short) 0);
            rd.setDrivingRange(dataTool.getDriveRangeFrom3Bytes(bean.getKilometerMileage()));//行驶里程
//        rd.setMileageRange(bean.getDrivingRange()==0xfff?-200:bean.getDrivingRange());//续航里程
            if (bean.getDrivingRange() == 0xfff || bean.getDrivingRange() < 50) {
                rd.setMileageRange(-200);
            } else {
                rd.setMileageRange(bean.getDrivingRange());
            }
            char[] bonnetAndTrunk = dataTool.getBitsFromShort(bean.getBonnetAndTrunk());
            rd.setEngineCoverState(dataTool.getDoorStatus(String.valueOf(bonnetAndTrunk[6]) + String.valueOf(bonnetAndTrunk[7])));
            rd.setTrunkLidState(dataTool.getDoorStatus(String.valueOf(bonnetAndTrunk[4]) + String.valueOf(bonnetAndTrunk[5])));
            char[] statWindow = dataTool.getBitsFromShort(bean.getStatWindow());
            rd.setSkylightState(dataTool.getSkyWindowStatus(String.valueOf(statWindow[5]) + String.valueOf(statWindow[6]) + String.valueOf(statWindow[7])));
            rd.setParkingState("0");
            char[] vBytes = dataTool.getBitsFromInteger(bean.getVoltage());
            if (isM8X) {
                //长度： 14bit
                int value = dataTool.getValueFromBytes(vBytes, 14);
                rd.setVoltage(value == 0x3fff ? -200 : (value * 0.0009765625f + 3.0f));//pdf 0628 part5.4 No24
            } else {
                //F60 长度： 8bit
                int value = dataTool.getValueFromBytes(vBytes, 8);
                rd.setVoltage(value * 0.1f);
            }
            rd.setAverageSpeedA(dataTool.getTrueAvgSpeed(bean.getAverageSpeedA()));
            rd.setAverageSpeedB(dataTool.getTrueAvgSpeed(bean.getAverageSpeedB()));
            if (isM8X) {
                rd.setMtGearPostion("-200");
            } else {
                rd.setMtGearPostion(dataTool.getMtGearPostion(bean.getMt_gear_position()));
            }

            //发动机状态
            Short engineState = bean.getEngineState();
            if (engineState == 0x80) {
                rd.setEngineState(0);
            } else if (engineState == 0x82) {
                rd.setEngineState(1);
            } else {
                rd.setEngineState(2);
            }
            //车锁状态
            Byte lockState = bean.getDoorLockState();
            char[] lockBytes = dataTool.getBitsFromByte(lockState);
            if (lockBytes[6] == '0' && lockBytes[7] == '0') {
                rd.setLfLockState(0);
            } else if (lockBytes[6] == '0' && lockBytes[7] == '1') {
                rd.setLfLockState(1);
            } else if (lockBytes[6] == '1' && lockBytes[7] == '0') {
                rd.setLfLockState(2);
            } else if (lockBytes[6] == '1' && lockBytes[7] == '1') {
                rd.setLfLockState(3);
            }
            if (lockBytes[5] == '0') {
                rd.setLrLockState(0);
            } else if (lockBytes[5] == '1') {
                rd.setLrLockState(1);
            } else {
                rd.setLrLockState(3);
            }
            if (lockBytes[4] == '0') {
                rd.setRfLockState(0);
            } else if (lockBytes[4] == '1') {
                rd.setRfLockState(1);
            } else {
                rd.setRfLockState(3);
            }
            if (lockBytes[3] == '0') {
                rd.setRrLockState(0);
            } else if (lockBytes[3] == '1') {
                rd.setRrLockState(1);
            } else {
                rd.setRrLockState(3);
            }
            //空调风量状态
            int blow = bean.getBlowState();
            rd.setBlow(blow);
            //空调ac状态
            int acState = bean.getAcState();
            rd.setAcState(acState);
            long middleTime = System.currentTimeMillis();
            realTimeReportDataRespository.save(rd);
            long middleTime2 = System.currentTimeMillis();
            //普通实时数据和位置数据分表存储
            GpsData gd = new GpsData();
            gd.setVin(vin);
            gd.setImei(bean.getImei());
            gd.setApplicationId(bean.getApplicationID());
            gd.setMessageId(bean.getMessageID());
            gd.setSendingTime(receiveDate);//服务器时间
            //分解IsIsLocation信息
            char[] location = dataTool.getBitsFromShort(bean.getIsLocation());
            gd.setIsLocation(location[7] == '0' ? (short) 0 : (short) 1);//bit0 0有效定位 1无效定位
            gd.setNorthSouth(location[6] == '0' ? "N" : "S");//bit1 0北纬 1南纬
            gd.setEastWest(location[5] == '0' ? "E" : "W");//bit2 0东经 1西经
            gd.setLatitude(dataTool.getTrueLatAndLon(bean.getLatitude()));
            gd.setLongitude(dataTool.getTrueLatAndLon(bean.getLongitude()));
            gd.setSpeed(dataTool.getTrueSpeed(bean.getSpeed()));
            gd.setHeading(bean.getHeading());
            long middleTime3 = System.currentTimeMillis();
            gpsDataRepository.save(gd);
            long endTime = System.currentTimeMillis();
            if (endTime - startTime > 100) {
                _logger.info("saveRealTimeReportMesM82 Analysis data time" + (middleTime - startTime));
                _logger.info("saveRealTimeReportMesM82 First time save time" + (middleTime2 - middleTime));
                _logger.info("saveRealTimeReportMesM82 Second time save time" + (endTime - middleTime3));
            }
        }
    }

    /**
     * 保存非M82车型补传实时数据
     */
    //@Transactional
    public void saveDataResendRealTimeMes(List<String> msgList){
        for (String message : msgList) {
            String[] tmp = message.split(":");
            String vin = tmp[0];
            String msg = tmp[1];
            //补发数据保存
            _logger.info("[0x23]>>保存上报的补发实时数据:" + msg);
            long startTime = System.currentTimeMillis();
            ByteBuffer bb = PackageEntityManager.getByteBuffer(msg);
            DataPackage dp = conversionTBox.generate(bb);
            DataResendRealTimeMes bean = dp.loadBean(DataResendRealTimeMes.class);
            short vehicleModel = bean.getVehicleModel();//按照协议0628车型编号 0~255 0：默认值(M82)；1：M82；2：M85； 3：F60；4：F70； 5：F60电动车
            boolean isM8X = true;
            if (vehicleModel > (short) 2) {
                isM8X = false;
            }
            RealTimeReportData rd = new RealTimeReportData();
            rd.setVin(vin);
            rd.setImei(bean.getImei());
            rd.setApplicationId(bean.getApplicationID());
            rd.setMessageId(bean.getMessageID());
            Date receiveDate = new Date();
            rd.setSendingTime(receiveDate);//服务器时间
            rd.setTripId(bean.getTripID());

//        rd.setFuelOil(bean.getFuelOil()==0xff?-200:bean.getFuelOil()* 1f);//0xff无效值
            rd.setAvgOilA(dataTool.getTrueAvgOil(bean.getAvgOilA()));
            rd.setAvgOilB(dataTool.getTrueAvgOil(bean.getAvgOilB()));
            rd.setServiceIntervall(0);//在协议0628已经删除此项数据
            Short fuelOil = bean.getFuelOil() == 0xff ? -200 : bean.getFuelOil();
            float val = 0f;
            if (vehicleModel == 0 || vehicleModel == 1 || vehicleModel == 2) {
                if (fuelOil > 0 && fuelOil < 56) {
                    val = (float) Math.round(((float) fuelOil / 56f) * 100f);
                } else if (fuelOil >= 56) {
                    val = 100f;
                } else if (fuelOil == -200) {
                    val = -200f;
                }
                rd.setLeftFrontTirePressure(dataTool.getTrueTirePressure(bean.getLeftFrontTirePressure()));//有效值0-125
                rd.setLeftRearTirePressure(dataTool.getTrueTirePressure(bean.getLeftRearTirePressure()));
                rd.setRightFrontTirePressure(dataTool.getTrueTirePressure(bean.getRightFrontTirePressure()));
                rd.setRightRearTirePressure(dataTool.getTrueTirePressure(bean.getRightRearTirePressure()));
            } else if (vehicleModel == 3 || vehicleModel == 5) {//在协议0628中F60无此数据 预留
                if (fuelOil > 0 && fuelOil < 52) {
                    val = (float) Math.round(((float) fuelOil / 52f) * 100f);
                } else if (fuelOil >= 52) {
                    val = 100f;
                } else if (fuelOil == -200) {
                    val = -200f;
                }
                rd.setLeftFrontTirePressure(0.0f);
                rd.setLeftRearTirePressure(0.0f);
                rd.setRightFrontTirePressure(0.0f);
                rd.setRightRearTirePressure(0.0f);
            } else if (vehicleModel == 4) {//F70
                if (fuelOil > 0 && fuelOil < 46) {
                    val = (float) Math.round(((float) fuelOil / 46f) * 100f);
                } else if (fuelOil >= 46) {
                    val = 100f;
                } else if (fuelOil == -200) {
                    val = -200f;
                }
                rd.setLeftFrontTirePressure(0.0f);
                rd.setLeftRearTirePressure(0.0f);
                rd.setRightFrontTirePressure(0.0f);
                rd.setRightRearTirePressure(0.0f);
            }
            rd.setFuelOil(val);
            char[] windows = dataTool.getBitsFromInteger(bean.getWindowInformation());//
            if (isM8X) {
                rd.setLeftFrontWindowInformation(dataTool.getF60WindowStatus(String.valueOf(windows[13]) + String.valueOf(windows[14]) + String.valueOf(windows[15])));
                rd.setRightFrontWindowInformation(dataTool.getF60WindowStatus(String.valueOf(windows[10]) + String.valueOf(windows[11]) + String.valueOf(windows[12])));
                rd.setLeftRearWindowInformation(dataTool.getF60WindowStatus(String.valueOf(windows[5]) + String.valueOf(windows[6]) + String.valueOf(windows[7])));
                rd.setRightRearWindowInformation(dataTool.getF60WindowStatus(String.valueOf(windows[2]) + String.valueOf(windows[3]) + String.valueOf(windows[4])));
            } else {//在协议0628中F60无此数据 预留
                rd.setLeftFrontWindowInformation(dataTool.getWindowStatus(String.valueOf(windows[14]) + String.valueOf(windows[15])));
                rd.setRightFrontWindowInformation(dataTool.getWindowStatus(String.valueOf(windows[12]) + String.valueOf(windows[13])));
                rd.setLeftRearWindowInformation(dataTool.getWindowStatus(String.valueOf(windows[10]) + String.valueOf(windows[11])));
                rd.setRightRearWindowInformation(dataTool.getWindowStatus(String.valueOf(windows[8]) + String.valueOf(windows[9])));
            }
            rd.setVehicleTemperature(dataTool.getInternTrueTmp(bean.getVehicleTemperature()));//
            rd.setVehicleOuterTemperature(dataTool.getOuterTrueTmp(bean.getVehicleOuterTemperature()));
            char[] doors = dataTool.getBitsFromShort(bean.getDoorInformation());//门 1开0关  bit 大端传输

            rd.setLeftFrontDoorInformation(dataTool.getDoorStatus(String.valueOf(doors[6]) + String.valueOf(doors[7])));
            rd.setRightFrontDoorInformation(dataTool.getDoorStatus(String.valueOf(doors[4]) + String.valueOf(doors[5])));
            rd.setLeftRearDoorInformation(dataTool.getDoorStatus(String.valueOf(doors[2]) + String.valueOf(doors[3])));
            rd.setRightRearDoorInformation(dataTool.getDoorStatus(String.valueOf(doors[0]) + String.valueOf(doors[1])));


            // waiting for protocol after 6.1.7
            rd.setDrivingTime(0);
            rd.setOilLife((short) 0);
            rd.setDrivingRange(dataTool.getDriveRangeFrom3Bytes(bean.getKilometerMileage()));//行驶里程
//        rd.setMileageRange(bean.getDrivingRange()==0xfff?-200:bean.getDrivingRange());//续航里程
            if (bean.getDrivingRange() == 0xfff || bean.getDrivingRange() < 50) {
                rd.setMileageRange(-200);
            } else {
                rd.setMileageRange(bean.getDrivingRange());
            }
            char[] bonnetAndTrunk = dataTool.getBitsFromShort(bean.getBonnetAndTrunk());
            rd.setEngineCoverState(dataTool.getDoorStatus(String.valueOf(bonnetAndTrunk[6]) + String.valueOf(bonnetAndTrunk[7])));
            rd.setTrunkLidState(dataTool.getDoorStatus(String.valueOf(bonnetAndTrunk[4]) + String.valueOf(bonnetAndTrunk[5])));
            char[] statWindow = dataTool.getBitsFromShort(bean.getStatWindow());
            if (isM8X) {
                rd.setSkylightState(dataTool.getSkyWindowStatus(String.valueOf(statWindow[5]) + String.valueOf(statWindow[6]) + String.valueOf(statWindow[7])));
            } else {
                //F60
                rd.setSkylightState(dataTool.getSkyWindowStatus(String.valueOf(statWindow[5]) + String.valueOf(statWindow[6]) + String.valueOf(statWindow[7])));
            }
            rd.setParkingState("0");
            char[] vBytes = dataTool.getBitsFromInteger(bean.getVoltage());
            if (isM8X) {
                //长度： 14bit
                int value = dataTool.getValueFromBytes(vBytes, 14);
                rd.setVoltage(value == 0x3ff ? -200 : (value * 0.0009765625f + 3.0f));//pdf 0628 part5.4 No24
            } else {
                //F60 长度： 8bit
                int value = dataTool.getValueFromBytes(vBytes, 8);
                rd.setVoltage(value == 0xff ? -200 : (value * 0.1f));
            }
            rd.setAverageSpeedA(dataTool.getTrueAvgSpeed(bean.getAverageSpeedA()));
            rd.setAverageSpeedB(dataTool.getTrueAvgSpeed(bean.getAverageSpeedB()));
            if (isM8X) {
                rd.setMtGearPostion("-200");
            } else {
                rd.setMtGearPostion(dataTool.getMtGearPostion(bean.getMt_gear_position()));
            }
            rd.setEngineState(-200);
            rd.setLfLockState(-200);
            rd.setLrLockState(-200);
            rd.setRfLockState(-200);
            rd.setRrLockState(-200);
            rd.setBlow(-200);
            rd.setAcState(-200);
            long middleTime = System.currentTimeMillis();
            realTimeReportDataRespository.save(rd);
            long middleTime2 = System.currentTimeMillis();
            //普通实时数据和位置数据分表存储
            GpsData gd = new GpsData();
            gd.setVin(vin);
            gd.setImei(bean.getImei());
            gd.setApplicationId(bean.getApplicationID());
            gd.setMessageId(bean.getMessageID());
            gd.setSendingTime(receiveDate);//服务器时间
            //分解IsIsLocation信息
            char[] location = dataTool.getBitsFromShort(bean.getIsLocation());
            gd.setIsLocation(location[7] == '0' ? (short) 0 : (short) 1);//bit0 0有效定位 1无效定位
            gd.setNorthSouth(location[6] == '0' ? "N" : "S");//bit1 0北纬 1南纬
            gd.setEastWest(location[5] == '0' ? "E" : "W");//bit2 0东经 1西经
            gd.setLatitude(dataTool.getTrueLatAndLon(bean.getLatitude()));
            gd.setLongitude(dataTool.getTrueLatAndLon(bean.getLongitude()));
            gd.setSpeed(dataTool.getTrueSpeed(bean.getSpeed()));
            gd.setHeading(bean.getHeading());
            long middleTime3 = System.currentTimeMillis();
            gpsDataRepository.save(gd);
            long endTime = System.currentTimeMillis();
            if (endTime - startTime > 100) {
                _logger.info("saveDataResendRealTimeMes Analysis data time" + (middleTime - startTime));
                _logger.info("saveDataResendRealTimeMes First time save time" + (middleTime2 - middleTime));
                _logger.info("saveDataResendRealTimeMes Second time save time" + (endTime - middleTime3));
            }
        }
    }

    /**
     * 保存M82车型补传实时数据
     */
    //@Transactional
    public void saveDataResendRealTimeMesM82(List<String> msgList){
        for (String message : msgList) {
            String[] tmp = message.split(":");
            String vin = tmp[0];
            String msg = tmp[1];
            //补发数据保存
            _logger.info("[0x23]>>保存上报的补发实时数据:" + msg);
            long startTime = System.currentTimeMillis();
//        ByteBuffer bb= PackageEntityManager.getByteBuffer(msg);
//        DataPackage dp = conversionTBox.generate(bb);
//        DataResendRealTimeMesM82 bean = dp.loadBean(DataResendRealTimeMesM82.class);
            DataResendRealTimeMesM82 dataResendRealTimeMesM82 = new DataResendRealTimeMesM82();
            DataResendRealTimeMesM82 bean = dataResendRealTimeMesM82.decode(dataTool.getByteBuf(msg));
            short vehicleModel = bean.getVehicleModel();//按照协议0628车型编号 0~255 0：默认值(M82)；1：M82；2：M85； 3：F60；4：F70； 5：F60电动车
            boolean isM8X = true;
            if (vehicleModel > (short) 2) {
                isM8X = false;
            }
            RealTimeReportData rd = new RealTimeReportData();
            rd.setVin(vin);
            rd.setImei(bean.getImei());
            rd.setApplicationId(bean.getApplicationID());
            rd.setMessageId(bean.getMessageID());
            Date receiveDate = new Date();
            rd.setSendingTime(receiveDate);//服务器时间
            rd.setTripId(bean.getTripID());

//        rd.setFuelOil(bean.getFuelOil()==0xff?-200:bean.getFuelOil()* 1f);//0xff无效值
            rd.setAvgOilA(dataTool.getTrueAvgOil(bean.getAvgOilA()));
            rd.setAvgOilB(dataTool.getTrueAvgOil(bean.getAvgOilB()));
            rd.setServiceIntervall(0);//在协议0628已经删除此项数据
            Short fuelOil = bean.getFuelOil() == 0xff ? -200 : bean.getFuelOil();
            float val = 0f;
            if (vehicleModel == 0 || vehicleModel == 1 || vehicleModel == 2) {
                if (fuelOil > 0 && fuelOil < 56) {
                    val = (float) Math.round(((float) fuelOil / 56f) * 100f);
                } else if (fuelOil >= 56) {
                    val = 100f;
                } else if (fuelOil == -200) {
                    val = -200f;
                }
                rd.setLeftFrontTirePressure(dataTool.getTrueTirePressure(bean.getLeftFrontTirePressure()));//有效值0-125
                rd.setLeftRearTirePressure(dataTool.getTrueTirePressure(bean.getLeftRearTirePressure()));
                rd.setRightFrontTirePressure(dataTool.getTrueTirePressure(bean.getRightFrontTirePressure()));
                rd.setRightRearTirePressure(dataTool.getTrueTirePressure(bean.getRightRearTirePressure()));
            } else if (vehicleModel == 3 || vehicleModel == 5) {//在协议0628中F60无此数据 预留
                if (fuelOil > 0 && fuelOil < 52) {
                    val = (float) Math.round(((float) fuelOil / 52f) * 100f);
                } else if (fuelOil >= 52) {
                    val = 100f;
                } else if (fuelOil == -200) {
                    val = -200f;
                }
                rd.setLeftFrontTirePressure(0.0f);
                rd.setLeftRearTirePressure(0.0f);
                rd.setRightFrontTirePressure(0.0f);
                rd.setRightRearTirePressure(0.0f);
            } else if (vehicleModel == 4) {//F70
                if (fuelOil > 0 && fuelOil < 46) {
                    val = (float) Math.round(((float) fuelOil / 46f) * 100f);
                } else if (fuelOil >= 46) {
                    val = 100f;
                } else if (fuelOil == -200) {
                    val = -200f;
                }
                rd.setLeftFrontTirePressure(0.0f);
                rd.setLeftRearTirePressure(0.0f);
                rd.setRightFrontTirePressure(0.0f);
                rd.setRightRearTirePressure(0.0f);
            }
            rd.setFuelOil(val);
            char[] windows = dataTool.getBitsFromInteger(bean.getWindowInformation());//
            if (isM8X) {
                rd.setLeftFrontWindowInformation(dataTool.getM82WindowStatus(String.valueOf(windows[13]) + String.valueOf(windows[14]) + String.valueOf(windows[15])));
                rd.setRightFrontWindowInformation(dataTool.getM82WindowStatus(String.valueOf(windows[10]) + String.valueOf(windows[11]) + String.valueOf(windows[12])));
                rd.setLeftRearWindowInformation(dataTool.getM82WindowStatus(String.valueOf(windows[7]) + String.valueOf(windows[8]) + String.valueOf(windows[9])));
                rd.setRightRearWindowInformation(dataTool.getM82WindowStatus(String.valueOf(windows[4]) + String.valueOf(windows[5]) + String.valueOf(windows[6])));
            } else {//在协议0628中F60无此数据 预留
                rd.setLeftFrontWindowInformation(dataTool.getWindowStatusFrom3Bits(String.valueOf(windows[13]) + String.valueOf(windows[14]) + String.valueOf(windows[15])));
                rd.setRightFrontWindowInformation(dataTool.getWindowStatusFrom3Bits(String.valueOf(windows[10]) + String.valueOf(windows[11]) + String.valueOf(windows[12])));
                rd.setLeftRearWindowInformation(dataTool.getWindowStatusFrom3Bits(String.valueOf(windows[7]) + String.valueOf(windows[8]) + String.valueOf(windows[9])));
                rd.setRightRearWindowInformation(dataTool.getWindowStatusFrom3Bits(String.valueOf(windows[4]) + String.valueOf(windows[5]) + String.valueOf(windows[6])));
            }
            rd.setVehicleTemperature(dataTool.getInternTrueTmp(bean.getVehicleTemperature()));//
            rd.setVehicleOuterTemperature(dataTool.getOuterTrueTmp(bean.getVehicleOuterTemperature()));
            char[] doors = dataTool.getBitsFromShort(bean.getDoorInformation());//门 1开0关  bit 大端传输

            rd.setLeftFrontDoorInformation(dataTool.getDoorStatus(String.valueOf(doors[6]) + String.valueOf(doors[7])));
            rd.setRightFrontDoorInformation(dataTool.getDoorStatus(String.valueOf(doors[4]) + String.valueOf(doors[5])));
            rd.setLeftRearDoorInformation(dataTool.getDoorStatus(String.valueOf(doors[2]) + String.valueOf(doors[3])));
            rd.setRightRearDoorInformation(dataTool.getDoorStatus(String.valueOf(doors[0]) + String.valueOf(doors[1])));


            // waiting for protocol after 6.1.7
            rd.setDrivingTime(0);
            rd.setOilLife((short) 0);
            rd.setDrivingRange(dataTool.getDriveRangeFrom3Bytes(bean.getKilometerMileage()));//行驶里程
//        rd.setMileageRange(bean.getDrivingRange()==0xfff?-200:bean.getDrivingRange());//续航里程
            if (bean.getDrivingRange() == 0xfff || bean.getDrivingRange() < 50) {
                rd.setMileageRange(-200);
            } else {
                rd.setMileageRange(bean.getDrivingRange());
            }
            char[] bonnetAndTrunk = dataTool.getBitsFromShort(bean.getBonnetAndTrunk());
            rd.setEngineCoverState(dataTool.getDoorStatus(String.valueOf(bonnetAndTrunk[6]) + String.valueOf(bonnetAndTrunk[7])));
            rd.setTrunkLidState(dataTool.getDoorStatus(String.valueOf(bonnetAndTrunk[4]) + String.valueOf(bonnetAndTrunk[5])));
            char[] statWindow = dataTool.getBitsFromShort(bean.getStatWindow());
            if (isM8X) {
                rd.setSkylightState(dataTool.getSkyWindowStatus(String.valueOf(statWindow[5]) + String.valueOf(statWindow[6]) + String.valueOf(statWindow[7])));
            } else {
                //F60
                rd.setSkylightState(dataTool.getSkyWindowStatus(String.valueOf(statWindow[5]) + String.valueOf(statWindow[6]) + String.valueOf(statWindow[7])));
            }
            rd.setParkingState("0");
            char[] vBytes = dataTool.getBitsFromInteger(bean.getVoltage());
            if (isM8X) {
                //长度： 14bit
                int value = dataTool.getValueFromBytes(vBytes, 14);
                rd.setVoltage(value == 0x3ff ? -200 : (value * 0.0009765625f + 3.0f));//pdf 0628 part5.4 No24
            } else {
                //F60 长度： 8bit
                int value = dataTool.getValueFromBytes(vBytes, 8);
                rd.setVoltage(value == 0xff ? -200 : (value * 0.1f));
            }
            rd.setAverageSpeedA(dataTool.getTrueAvgSpeed(bean.getAverageSpeedA()));
            rd.setAverageSpeedB(dataTool.getTrueAvgSpeed(bean.getAverageSpeedB()));
            if (isM8X) {
                rd.setMtGearPostion("-200");
            } else {
                rd.setMtGearPostion(dataTool.getMtGearPostion(bean.getMt_gear_position()));
            }

            //发动机状态
            Short engineState = bean.getEngineState();
            if (engineState == 0x80) {
                rd.setEngineState(0);
            } else if (engineState == 0x82) {
                rd.setEngineState(1);
            } else {
                rd.setEngineState(2);
            }
            //车锁状态
            Byte lockState = bean.getDoorLockState();
            char[] lockBytes = dataTool.getBitsFromByte(lockState);
            if (lockBytes[6] == '0' && lockBytes[7] == '0') {
                rd.setLfLockState(0);
            } else if (lockBytes[6] == '0' && lockBytes[7] == '1') {
                rd.setLfLockState(1);
            } else if (lockBytes[6] == '1' && lockBytes[7] == '0') {
                rd.setLfLockState(2);
            } else if (lockBytes[6] == '1' && lockBytes[7] == '1') {
                rd.setLfLockState(3);
            }
            if (lockBytes[5] == '0') {
                rd.setLrLockState(0);
            } else if (lockBytes[5] == '1') {
                rd.setLrLockState(1);
            } else {
                rd.setLrLockState(3);
            }
            if (lockBytes[4] == '0') {
                rd.setRfLockState(0);
            } else if (lockBytes[4] == '1') {
                rd.setRfLockState(1);
            } else {
                rd.setRfLockState(3);
            }
            if (lockBytes[3] == '0') {
                rd.setRrLockState(0);
            } else if (lockBytes[3] == '1') {
                rd.setRrLockState(1);
            } else {
                rd.setRrLockState(3);
            }
            //空调风量状态
            int blow = bean.getBlowState();
            rd.setBlow(blow);
            //空调ac状态
            int acState = bean.getAcState();
            rd.setAcState(acState);
            long middleTime = System.currentTimeMillis();
            realTimeReportDataRespository.save(rd);
            long middleTime2 = System.currentTimeMillis();
            //普通实时数据和位置数据分表存储
            GpsData gd = new GpsData();
            gd.setVin(vin);
            gd.setImei(bean.getImei());
            gd.setApplicationId(bean.getApplicationID());
            gd.setMessageId(bean.getMessageID());
            gd.setSendingTime(receiveDate);//服务器时间
            //分解IsIsLocation信息
            char[] location = dataTool.getBitsFromShort(bean.getIsLocation());
            gd.setIsLocation(location[7] == '0' ? (short) 0 : (short) 1);//bit0 0有效定位 1无效定位
            gd.setNorthSouth(location[6] == '0' ? "N" : "S");//bit1 0北纬 1南纬
            gd.setEastWest(location[5] == '0' ? "E" : "W");//bit2 0东经 1西经
            gd.setLatitude(dataTool.getTrueLatAndLon(bean.getLatitude()));
            gd.setLongitude(dataTool.getTrueLatAndLon(bean.getLongitude()));
            gd.setSpeed(dataTool.getTrueSpeed(bean.getSpeed()));
            gd.setHeading(bean.getHeading());
            long middleTime3 = System.currentTimeMillis();
            gpsDataRepository.save(gd);
            long endTime = System.currentTimeMillis();
            if (endTime - startTime > 100) {
                _logger.info("saveDataResendRealTimeMesM82 Analysis data time" + (middleTime - startTime));
                _logger.info("saveDataResendRealTimeMesM82 First time save time" + (middleTime2 - middleTime));
                _logger.info("saveDataResendRealTimeMesM82 Second time save time" + (endTime - middleTime3));
            }
        }
    }

    //@Transactional
    public void saveWarningMessage(List<String> msgList){
        for (String message : msgList) {
            long startTime = System.currentTimeMillis();
            String[] tmp = message.split(":");
            String vin = tmp[0];
            String msg = tmp[1];
            //报警数据保存
            _logger.info("[0x24]>>保存上报的报警数据:" + msg);
            WarningMessage bean = dataTool.decodeWarningMessage(msg);
            WarningMessageData wd = new WarningMessageData();
            wd.setVin(vin);
            wd.setImei(bean.getImei());
            wd.setApplicationId(bean.getApplicationID());
            wd.setMessageId(bean.getMessageID());
            wd.setSendingTime(dataTool.seconds2Date(bean.getSendingTime()));
            wd.setReceiveTime(new Date());
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
            long middleTime = System.currentTimeMillis();
            warningMessageDataRespository.save(wd);
            long endTime = System.currentTimeMillis();
            if (endTime - startTime > 100) {
                _logger.info("saveWarningMessage Analysis data time" + (middleTime - startTime));
                _logger.info("saveWarningMessage First time save time" + (endTime - middleTime));
            }
        }
    }

    //@Transactional
    public void saveDataResendWarningMessage(List<String> msgList){
        for (String message : msgList) {
            long startTime = System.currentTimeMillis();
            String[] tmp = message.split(":");
            String vin = tmp[0];
            String msg = tmp[1];
            //报警数据保存
            _logger.info("[0x25]>>保存上报的补发报警数据:" + msg);
            DataResendWarningMes bean = dataTool.decodeResendWarningMessage(msg);
            WarningMessageData wd = new WarningMessageData();
            wd.setVin(vin);
            wd.setImei(bean.getImei());
            wd.setApplicationId(bean.getApplicationID());
            wd.setMessageId(bean.getMessageID());
            wd.setSendingTime(dataTool.seconds2Date(bean.getSendingTime()));
            wd.setReceiveTime(new Date());
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

            long middleTime = System.currentTimeMillis();
            warningMessageDataRespository.save(wd);
            long endTime = System.currentTimeMillis();
            if (endTime - startTime > 100) {
                _logger.info("saveDataResendWarningMessage Analysis data time" + (middleTime - startTime));
                _logger.info("saveDataResendWarningMessage First time save time" + (endTime - middleTime));
            }
        }
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
