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
        PramSetupAck hr=new PramSetupAck();
        hr.setTestFlag((short) 1);
        hr.setTestFlag((short) 1);
        hr.setSendingTime((long) dataTool.getCurrentSeconds());
        hr.setApplicationID((short) 82);//>>>
        hr.setMessageID((short) 2);//>>>
        hr.setImei("123456789012345");
        hr.setProtocolVersionNumber((short) 1);
        hr.setVehicleID(1);
        hr.setTripID((short) 1);
        hr.setReserved(0);

        hr.setEventID((long) 1444812349);
        hr.setPramSetAck((short) 13);
        hr.setPramSetID(new byte[]{(byte) 1, (byte) 2, (byte) 3, (byte) 4, (byte) 5, (byte) 6, (byte) 7, (byte) 8, (byte) 9, (byte) 10, (byte) 11, (byte) 12, (byte) 13});
        hr.setPramValue(new byte[]{(byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0});


        DataPackage dpw=new DataPackage("8995_82_2");//>>>
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
        PramSetupAck bean=dp.loadBean(PramSetupAck.class);
        PackageEntityManager.printEntity(bean);
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
    }
}
