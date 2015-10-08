package com.hp.triclops.service;

import com.hp.data.bean.tbox.RegularReportMes;
import com.hp.data.bean.tbox.RemoteControlAck;
import com.hp.data.core.Conversion;
import com.hp.data.core.DataPackage;
import com.hp.data.util.PackageEntityManager;
import com.hp.triclops.acquire.DataTool;
import com.hp.triclops.entity.GpsData;
import com.hp.triclops.entity.RegularReportData;
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
        System.out.println("EventID>>>>>>>>>>>>>>>>>>>>" + bean.getMessageID());
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

    }

    public void saveDataResendMes(String vin,String msg){
        //补发数据保存
        _logger.info(">>save DataResendMes:"+msg);

    }
    public void saveWarningMessage(String vin,String msg){
        //报警数据保存
        _logger.info(">>save WarningMessage:"+msg);
    }


   /* //GPS数据保存测试
    _logger.info(">>save RegularReportMes");
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
    gpsDataRepository.save(gd);*/
    //
}
