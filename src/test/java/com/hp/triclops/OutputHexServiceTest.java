package com.hp.triclops;

import com.hp.triclops.acquire.DataTool;
import com.hp.triclops.entity.DiagnosticData;
import com.hp.triclops.entity.RemoteControl;
import com.hp.triclops.entity.TBoxParmSet;
import com.hp.triclops.entity.User;
import com.hp.triclops.service.OutputHexService;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.transaction.Transactional;
import java.util.Date;

/**
 * Created by luj on 2015/10/12.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class OutputHexServiceTest {
    @Autowired
    OutputHexService outputHexService;
    @Autowired
    DataTool dataTool;

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }
    @Test
    @Transactional
    @Rollback
    public void test_getRemoteControlPreHex(){
        RemoteControl remoteControl=new RemoteControl();
        remoteControl.setSendingTime(new Date());
        remoteControl.setControlType((short) 13);
        remoteControl.setAcTemperature(23.0);
        remoteControl.setUid(1);
        remoteControl.setVin("12345678919991234");
        System.out.println(">>>>>>>"+outputHexService.getRemoteControlPreHex(1444812349l));
    }

    @Test
    @Transactional
    @Rollback
    public void test_getRemoteControlCmdHex_ac(){
        RemoteControl remoteControl=new RemoteControl();
        remoteControl.setSendingTime(new Date());
        remoteControl.setControlType((short) 10);
        remoteControl.setAcTemperature(21.0);
        remoteControl.setMode((short) 3);
        remoteControl.setRecirMode((short) 1);
        remoteControl.setAcMode((short) 0);
        remoteControl.setFan((short) 6);
        remoteControl.setAcMode((short) 0);
        remoteControl.setAcMode((short) 0);
        remoteControl.setLightNum((short)1);
        remoteControl.setHornNum((short) 0);
        remoteControl.setActTime(0.2);
        remoteControl.setDeActive((short)1);


        remoteControl.setUid(1);
        remoteControl.setVin("12345678919991234");
        System.out.println(">>>>>>>"+outputHexService.getRemoteControlCmdHex(remoteControl, (long) 1444812349));
        //value 15 29 03 06
    }

    @Test
    @Transactional
    @Rollback
    public void test_getRemoteControlCmdHex_findCar(){
        RemoteControl remoteControl=new RemoteControl();
        remoteControl.setSendingTime(new Date());
        remoteControl.setControlType((short) 10);
        remoteControl.setLightNum((short) 15);
        remoteControl.setHornNum((short) 20);
        remoteControl.setActTime(0.4);
        remoteControl.setDeActive((short)0);

        remoteControl.setUid(1);
        remoteControl.setVin("12345678919991234");
        System.out.println(">>>>>>>"+outputHexService.getRemoteControlCmdHex(remoteControl, (long) 1444812349));
        //value 0F 14 19 00
    }

    @Test
    @Transactional
    @Rollback
    public void test_getParmSetCmdHex(){
        //测试
        TBoxParmSet tBoxParmSet=new TBoxParmSet();
        tBoxParmSet.setSendingTime(new Date());

        tBoxParmSet.setVin("12345678919991234");
        int eventId=dataTool.getCurrentSeconds();
        tBoxParmSet.setEventId((long) eventId);

        tBoxParmSet.setFrequencySaveLocalMedia(1000);
        tBoxParmSet.setFrequencyForReport(1000);
        tBoxParmSet.setFrequencyForWarningReport(1000);
        tBoxParmSet.setFrequencyHeartbeat((short) 10);
        tBoxParmSet.setTimeOutForTerminalSearch(10);
        tBoxParmSet.setTimeOutForServerSearch(10);
        tBoxParmSet.setLicensePlate("京A12345");
        tBoxParmSet.setUploadType((short) 1);
        tBoxParmSet.setEnterpriseBroadcastAddress1("192.168.1.1");
        tBoxParmSet.setEnterpriseBroadcastPort1(9000);
        tBoxParmSet.setEnterpriseBroadcastAddress2("192.168.1.2");
        tBoxParmSet.setEnterpriseBroadcastPort2(9000);
        tBoxParmSet.setEnterpriseDomainName("www.baidu.com");
        tBoxParmSet.setEnterpriseDomainNameSize((short) 13);

        System.out.println(">>>>>>>"+outputHexService.getParmSetCmdHex(tBoxParmSet));
    }
    @Test
    @Transactional
    @Rollback
    public void test_sendParmSetAfterRegister(){
        String vin="12345678919991234";
        outputHexService.sendParmSetAfterRegister(vin);

    }

   /* @Test
    public void test_getWarningMessageForPush() {
        String vin="12345678919991234";
        String byteString="23 23 01 58 00 56 04 BF DA 24 01 31 32 33 34 35 36 37 38 39 30 31 32 33 34 35 01 00 00 00 01 00 00 00 00 00 1E 41 00 00 72 1F 06 1F 00 EA 01 00 02 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 B9 ";
        User user = new User();
        user.setId(1);
        user.setContactsPhone("123456");
        System.out.println(outputHexService.getWarningMessageForPush(vin, byteString,user,true));

    }*/
  /*  @Test
    public void test_getResendWarningMessageForPush() {
        String vin="12345678919991234";
        String byteString="23 23 01 58 00 56 04 BF DA 25 01 31 32 33 34 35 36 37 38 39 30 31 32 33 34 35 01 00 00 00 01 00 00 00 00 00 1E 41 00 00 72 1F 06 1F 00 EA 00 01 02 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 B9 ";
        User user = new User();
        user.setId(1);
        user.setContactsPhone("123456");
        System.out.println(outputHexService.getResendWarningMessageForPush(vin, byteString,user,true));

    }*/

    @Test
    public void test_getFailureMessageForPush() {
        String vin="12345678919991234";
        String byteString="23 23 00 3C 00 56 04 BF DA 28 01 31 32 33 34 35 36 37 38 39 30 31 32 33 34 35 01 00 00 00 01 00 00 00 00 00 1E 41 00 00 72 1F 06 1F 00 EA 34 00 35 36 00 00 00 37 00 38 39 00 3A 3B 3C 00 3D 3E 3F D3 ";
        System.out.println(outputHexService.getFailureMessageForPush(vin, byteString));

    }
    @Test
    public void test_getResendFailureMessageForPush() {
        String vin="12345678919991234";
        String byteString="23 23 00 3C 00 56 04 BF DA 29 01 31 32 33 34 35 36 37 38 39 30 31 32 33 34 35 01 00 00 00 01 00 00 00 00 00 1E 41 00 00 72 1F 06 1F 00 EA 34 00 35 36 00 00 00 37 00 38 39 00 3A 3B 3C 00 3D 3E 3F D2 ";
        System.out.println(outputHexService.getResendFailureMessageForPush(vin, byteString));

    }

  /*  @Ignore("Not suitable for travis-ci")
    @Test
    public void test_getWarningMessageAndPush() {
        String vin="12345678919991234";
        String byteString="23 23 01 58 00 56 04 BF DA 24 01 31 32 33 34 35 36 37 38 39 30 31 32 33 34 35 01 00 00 00 01 00 00 00 00 00 1E 41 00 00 72 1F 06 1F 00 EA 01 01 02 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 B8 ";
       outputHexService.getWarningMessageAndPush(vin, byteString,true);

    }*/

    @Test
    public void test_getDiagCmdHex() {
        String vin="12345678919991234";
        DiagnosticData diagnosticData=new DiagnosticData();
        diagnosticData.setEventId(1444812349l);
        System.out.println(outputHexService.getDiagCmdHex(diagnosticData));

    }

}
