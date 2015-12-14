package com.hp.triclops;

import com.hp.data.core.Conversion;
import com.hp.triclops.entity.GpsData;
import com.hp.triclops.entity.RealTimeReportData;
import com.hp.triclops.repository.GpsDataRepository;
import com.hp.triclops.repository.RealTimeReportDataRespository;
import com.hp.triclops.service.DataHandleService;
import com.hp.triclops.utils.DateUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * Created by luj on 2015/9/29.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class DataHandleServiceTest {
    @Autowired
    DataHandleService dataHandleService;
    @Autowired
    Conversion conversionTBox;
    @Autowired
    RealTimeReportDataRespository realTimeReportDataRespository;
    @Autowired
    GpsDataRepository gpsDataRepository;



    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }
    @Test
    @Transactional
    @Rollback
    public void test_saveRegularReportMes(){
        //测试固定数据保存
        String byteString="23 23 00 3D 01 56 04 BF DA 21 01 31 32 33 34 35 36 37 38 39 30 31 32 33 34 35 01 00 00 00 01 00 00 00 0A 00 0A 0A 00 0A 00 0A 01 00 01 64 31 32 33 34 35 31 32 33 34 35 00 0A 00 00 C0 A8 01 0B 23 28 16 ";
        dataHandleService.saveMessage("12345678919991234",byteString);
    }
    @Test
    @Transactional
    @Rollback
    public void test_saveRealTimeMessage(){
        //测试实时数据保存
        String byteString="23 23 00 39 00 56 04 BF DA 22 01 31 32 33 34 35 36 37 38 39 30 31 32 33 34 35 01 00 00 00 01 00 00 00 01 CD AD 0E 06 CD 62 C0 06 1F 00 EA 00 63 00 7B 00 86 04 D2 64 65 66 67 0F 41 43 0F A2";
        dataHandleService.saveMessage("12345678919991234",byteString);
    }
    @Test
    @Transactional
    @Rollback
    public void test_saveDataResendRealTimeMes(){
        //测试补发数据保存
        String byteString="23 23 00 39 00 56 04 BF DA 23 01 31 32 33 34 35 36 37 38 39 30 31 32 33 34 35 01 00 00 00 01 00 00 00 01 CD AD 0E 06 CD 62 C0 06 1F 00 EA 00 63 00 7B 00 86 04 D2 64 65 66 67 0F 41 43 0F A3 ";
        dataHandleService.saveMessage("12345678919991234",byteString);
    }
    @Test
    @Transactional
    @Rollback
    public void test_saveWarningMessage(){
        //测试报警数据保存
        String byteString="23 23 01 58 00 56 04 BF DA 24 01 31 32 33 34 35 36 37 38 39 30 31 32 33 34 35 01 00 00 00 01 00 00 00 00 00 1E 41 00 00 72 1F 06 1F 00 EA 01 01 02 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 B8 ";
        dataHandleService.saveMessage("12345678919991234",byteString);
    }

    @Test
    @Transactional
    @Rollback
    public void test_saveDataResendWarningMessage(){
        //测试补发报警数据保存
        String byteString="23 23 01 58 00 56 04 BF DA 25 01 31 32 33 34 35 36 37 38 39 30 31 32 33 34 35 01 00 00 00 01 00 00 00 00 00 1E 41 00 00 72 1F 06 1F 00 EA 01 01 02 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 B9 ";
        dataHandleService.saveMessage("12345678919991234",byteString);
    }

    @Test
    @Transactional
    @Rollback
    public void test_saveFailureMessage(){
        //测试故障数据保存
        String byteString="23 23 00 31 00 56 04 BF DA 28 01 31 32 33 34 35 36 37 38 39 30 31 32 33 34 35 01 00 00 00 01 00 00 00 01 CD AD 0E 06 CD 62 C0 06 1F 00 EA 34 35 36 39 AA AA AA AA E4 ";
        dataHandleService.saveMessage("12345678919991234",byteString);
    }

    @Test
    @Transactional
    @Rollback
    public void test_saveDataResendFailureMessage(){
        //测试补发故障数据保存
        String byteString="23 23 00 31 00 56 04 BF DA 29 01 31 32 33 34 35 36 37 38 39 30 31 32 33 34 35 01 00 00 00 01 00 00 00 01 CD AD 0E 06 CD 62 C0 06 1F 00 EA AA AA AA AA 51 52 53 54 EF ";
        dataHandleService.saveMessage("12345678919991234",byteString);
    }

    @Test
    public void test_saveRealTime_Gps_data(){


        // int count =0;//计数
        int tripId = 100;
        int drivingTime =1;
        int drivingRange =1;
        Date sendingTime = DateUtil.parseDate("2015/01/01 00:00:00", "yyyy/MM/dd HH:mm:ss");
        for(int i=0;sendingTime.getTime()<= DateUtil.parseDate("2015/08/01 00:00:00", "yyyy/MM/dd HH:mm:ss").getTime();i++){
            System.out.println(i);
            RealTimeReportData rd=new RealTimeReportData();
            GpsData gd = new GpsData();

            if(i%540 ==0){
                tripId ++;
            }
            if(i%700 ==0){
                sendingTime=new Date(sendingTime.getTime()+60*1000*60*24-700*10*1000);
            }
            drivingTime+=10;
            drivingRange += (Math.random() * 20);
            sendingTime=new Date(sendingTime.getTime()+10000);

            rd.setVin("12345678919991234");
            rd.setImei("123456789012345");
            rd.setApplicationId(34);
            rd.setMessageId(1);
            rd.setFuelOil(49.5f);
            rd.setServiceIntervall(1234);
            rd.setLeftFrontTirePressure(280f);
            rd.setLeftRearTirePressure(282.8f);
            rd.setRightFrontTirePressure(285.6f);
            rd.setRightRearTirePressure(288.4f);
            rd.setLeftFrontWindowInformation("3");
            rd.setRightFrontWindowInformation("1");
            rd.setLeftRearWindowInformation("3");
            rd.setRightRearWindowInformation("1");
            rd.setVehicleTemperature((short) 25);//温度按照上报数值-40
            rd.setVehicleOuterTemperature((short) 27);
            rd.setLeftFrontDoorInformation("3");
            rd.setRightFrontDoorInformation("0");
            rd.setLeftRearDoorInformation("3");
            rd.setRightRearDoorInformation("0");
            rd.setOilLife((short) 1);
            rd.setMileageRange(1);
            rd.setEngineCoverState("0");
            rd.setTrunkLidState("0");
            rd.setSkylightState("0");
            rd.setParkingState("0");



            //要10秒一个
            rd.setSendingTime(sendingTime);
            //
            rd.setTripId(tripId);
            //5~15油耗
            rd.setAvgOilA((float)(Math.random() * 10)+5);
            //5~15油耗
            rd.setAvgOilB((float)(Math.random() * 10)+5);
            //
            rd.setDrivingTime(drivingTime);
            //
            rd.setDrivingRange(drivingRange);


            //0~120随机速度
            gd.setSpeed((float) (Math.random() * 120));
            //
            gd.setSendingTime(sendingTime);

            gd.setVin("12345678919991234");
            gd.setImei("123456789012345");
            gd.setApplicationId(34);
            gd.setMessageId(1);
            gd.setIsLocation((short) 0);
            gd.setNorthSouth("N");
            gd.setEastWest("W");
            gd.setLatitude(30.25390625d);
            gd.setLongitude(114.12109375d);
            gd.setHeading(234);

            realTimeReportDataRespository.save(rd);
            gpsDataRepository.save(gd);
        }
    }



}
