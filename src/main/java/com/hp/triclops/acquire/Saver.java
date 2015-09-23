package com.hp.triclops.acquire;

import com.hp.data.bean.tbox.RegisterResp;
import com.hp.data.core.Conversion;
import com.hp.data.core.DataPackage;
import com.hp.data.protocol.ConversionTBox;
import com.hp.data.util.PackageEntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;

/**
 * Created by luj on 2015/9/23.
 */
@Component
@SpringApplicationConfiguration(locations = "classpath:spring-config.xml")
public class Saver {

    Conversion conversionTBox;
    public void print(){
        conversionTBox=new ConversionTBox();
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+conversionTBox);
        String byteString="23 23 00 4D 01 55 D2 0F E7 13 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 55 BE E2 58 00 00 00 00 00 00 00 00 00 00 00 00 00 00 31 32 33 34 35 36 37 38 39 31 39 39 39 31 32 33 34 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 5D ";
        System.out.println(byteString);
        RegisterResp resp=new RegisterResp();
        resp.setSendingTime(1234567l);
        resp.setApplicationID((short)19);
        resp.setMessageID((short)2);
        resp.setEventID(123456l);
        resp.setRegisterResult((short)(1));
        DataPackage dp=new DataPackage("8995_19_2");
        dp.fillBean(resp);
        ByteBuffer bb=conversionTBox.generate(dp);
        String str= PackageEntityManager.getByteString(bb);
        System.out.println(str);
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
    }
}
