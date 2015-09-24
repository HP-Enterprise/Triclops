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
public class Saver {
    @Autowired
    Conversion conversionTBox;
    @Autowired
    DataTool dataTool;
    public String  buildStr(){
        //生成数据

        RemoteWakeUpReq hr=new RemoteWakeUpReq();
        hr.setImei("123456789012345");
        hr.setSerialNumber("12345678919991");
        hr.setLength(32);
        hr.setProtocolVersionNumber((short) 0);
        hr.setReserved(0);
        hr.setVehicleID(0);
        hr.setTripID((short) 0);
        hr.setHead(8995);
        hr.setTestFlag((short) 1);
        hr.setSendingTime((long) dataTool.getCurrentSeconds());
        hr.setApplicationID((short) 17);
        hr.setMessageID((short) 1);
        hr.setEventID(1438573144l);
        hr.setPramVersion("00000");
        hr.setVin("12345678919991234");
        hr.setSwVersion("00000");
        hr.setDbcVersion("00000");

        DataPackage dpw=new DataPackage("8995_20_1");
        dpw.fillBean(hr);
        ByteBuffer bbw=conversionTBox.generate(dpw);
        String byteStr=PackageEntityManager.getByteString(bbw);
        System.out.println(byteStr);

        return byteStr;

    }
    public void print(String str){
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        //String byteString="23 23 00 4D 01 56 03 BC D5 11 01 31 32 33 34 35 36 37 38 39 30 31 32 33 34 35 0C 00 01 01 00 01 55 BE E2 58 31 32 33 34 35 36 37 38 39 31 39 39 39 31 31 32 33 34 35 36 37 38 39 31 39 39 39 31 32 33 34 30 32 00 00 00 30 31 00 00 00 30 33 00 00 00 39 ";
        String byteString=str;
        System.out.println(byteString);
        ByteBuffer bb=PackageEntityManager.getByteBuffer(byteString);
        DataPackage dp=conversionTBox.generate(bb);
        RemoteWakeUpReq bean=dp.loadBean(RemoteWakeUpReq.class);
        String key = dp.getKey();
        PackageEntityManager.printEntity(bean);

      /*  HeartbeatResp hr=new HeartbeatResp();
        hr.setHead(bean.getHead());
        hr.setTestFlag(bean.getTestFlag());
        hr.setSendingTime((long)dataTool.getCurrentSeconds());
        hr.setApplicationID(bean.getApplicationID());
        hr.setMessageID((short) 2);
        hr.setEventID(bean.getEventID());
        //hr.setSendingTime((long) dataTool.getCurrentSeconds());
        bean.setMessageID((short)2);
        DataPackage dpw=new DataPackage("8995_38_2");
        dpw.fillBean(hr);
        ByteBuffer bbw=conversionTBox.generate(dpw);
        String byteStr=PackageEntityManager.getByteString(bbw);
        System.out.println(byteStr);*/

        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
    }
}
