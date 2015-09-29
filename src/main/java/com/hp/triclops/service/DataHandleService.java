package com.hp.triclops.service;

import com.hp.triclops.acquire.DataTool;
import com.hp.triclops.entity.GpsData;
import com.hp.triclops.repository.GpsDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by luj on 2015/9/28.
 */
@Component
public class DataHandleService {
    @Autowired
    GpsDataRepository gpsDataRepository;




    @Autowired
    DataTool dataTool;
    public void saveMessage(String msg){
       //数据保存，传入原始消息数据hex
       //分析消息类型，然后调用具体的保存方法
       saveRealTimeReportMes();
    }

    public void saveRealTimeReportMes(){
        GpsData gd=new GpsData();
        gd.setImei("123456789012345");
        gd.setVin("12345678912345678");
        gd.setSerialNumber("12345678919991");
        gd.setApplicationId(11);
        gd.setMessageId(1);
        gd.setHeading(230);
        gd.setIsLocation((short) 1);
        gd.setLatitude(114256398l);
        gd.setLongitude(111l);
        gd.setSpeed(123);
        gd.setSendingTime(dataTool.getCurrentSeconds());
        gpsDataRepository.save(gd);
        //实时数据保存
    }


    public void saveRegularReportMes(){
        //额定数据保存

    }
    public void saveDataResendMes(){
        //补发数据保存

    }
    public void saveWarningMessage(){
        //报警数据保存

    }
}
