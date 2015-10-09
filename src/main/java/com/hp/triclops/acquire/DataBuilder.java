package com.hp.triclops.acquire;
import com.hp.data.bean.tbox.*;
import com.hp.data.core.Conversion;
import com.hp.data.core.DataPackage;
import com.hp.data.util.PackageEntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;

/**
 * Created by luj on 2015/9/23.
 */
@Component
public class DataBuilder {
    //用于生成、验证测试数据包的辅助类，完成后删除
    @Autowired
    Conversion conversionTBox;
    @Autowired
    DataTool dataTool;
    public String  buildStr(){
        //生成数据
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        DataResendMes hr=new DataResendMes();
        hr.setTestFlag((short) 1);
        hr.setSendingTime((long) dataTool.getCurrentSeconds());
        hr.setApplicationID((short) 35);//>>>
        hr.setMessageID((short) 1);//>>>
        hr.setImei("123456789012345");
        hr.setProtocolVersionNumber((short) 1);
        hr.setVehicleID(1);
        hr.setTripID((short) 1);
        hr.setReserved(0);

        hr.setIsLocation((short) 1);
        hr.setLatitude(30123456l);
        hr.setLongitude(114123456l);
        hr.setSpeed(123);
        hr.setHeading(230);
        hr.setFuelOil((short) 10);
        hr.setAvgOil(11);
        hr.setOilLife((short) 15);
        hr.setDriveRange(new byte[]{(byte) 13, (byte) 144, (byte) 56});
        hr.setLeftFrontTirePressure(251);
        hr.setLeftRearTirePressure(252);
        hr.setRightFrontTirePressure(253);
        hr.setRightRearTirePressure(254);
        hr.setWindowInformation((short) 170);
        hr.setVehicleTemperature((short) 65);
        hr.setVehicleOuterTemperature((short) 67);
        hr.setDoorInformation((short) 170);
        hr.setSingleBatteryVoltage(14000);
        hr.setMaximumVoltagePowerBatteryPack((short) 200);
        hr.setMaximumBatteryVoltage(15000);
        hr.setBatteryMonomerMinimumVoltage(14000);
        hr.setEngineCondition((short) 170);
        hr.setEngineSpeed(4000);
        hr.setRapidAcceleration(200);
        hr.setRapidDeceleration(300);
        hr.setSpeeding(30);
        hr.setSignalStrength((short) 10);

        hr.setBcm1((byte)170);
        hr.setEms((byte)170);
        hr.setTcu((byte)170);
        hr.setIc((byte)170);
        hr.setAbs((byte)170);
        hr.setPdc((byte)170);
        hr.setBcm2((byte)170);

        DataPackage dpw=new DataPackage("8995_35_1");//>>>
        dpw.fillBean(hr);
        ByteBuffer bbw=conversionTBox.generate(dpw);
        String byteStr=PackageEntityManager.getByteString(bbw);
        System.out.println(byteStr);
        return byteStr;

    }
    public void print(String str){
        String byteString=str;
        ByteBuffer bb=PackageEntityManager.getByteBuffer(byteString);
        DataPackage dp=conversionTBox.generate(bb);
        DataResendMes bean=dp.loadBean(DataResendMes.class);
        PackageEntityManager.printEntity(bean);

        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
    }
}
