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
        RemoteControlAck hr=new RemoteControlAck();
        hr.setTestFlag((short) 1);
        hr.setSendingTime((long) dataTool.getCurrentSeconds());
        hr.setMessageID((short) 2);//>>>
        hr.setEventID(1438573144l);
        //hr.setVin("12345678919991234");
        hr.setApplicationID((short) 49);//>>>
        hr.setProtocolVersionNumber((short) 1);
        hr.setTripID((short) 1);

        hr.setVehicleID(1);
        hr.setImei("123456789012345");
        hr.setLength(32);
        hr.setReserved(0);
        hr.setHead(8995);
        hr.setRemoteControlAck((short) 0);
        hr.setReserved(0);



        DataPackage dpw=new DataPackage("8995_49_2");//>>>
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
        RemoteControlAck bean=dp.loadBean(RemoteControlAck.class);
        PackageEntityManager.printEntity(bean);

        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
    }
}
