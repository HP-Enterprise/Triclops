package com.hp.triclops;

import com.hp.triclops.acquire.DataTool;
import com.hp.triclops.entity.DiagnosticData;
import com.hp.triclops.entity.RemoteControl;
import com.hp.triclops.entity.TBoxParmSet;
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
        remoteControl.setAcTemperature((short) 23);
        remoteControl.setUid(1);
        remoteControl.setVin("12345678919991234");
        System.out.println(">>>>>>>"+outputHexService.getRemoteControlPreHex(remoteControl, (long) 1444812349));
    }

    @Test
    @Transactional
    @Rollback
    public void test_getRemoteControlCmdHex(){
        RemoteControl remoteControl=new RemoteControl();
        remoteControl.setSendingTime(new Date());
        remoteControl.setControlType((short) 13);
        remoteControl.setAcTemperature((short) 23);
        remoteControl.setUid(1);
        remoteControl.setVin("12345678919991234");
        System.out.println(">>>>>>>"+outputHexService.getRemoteControlCmdHex(remoteControl, (long) 1444812349));
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

    @Test
    public void test_getWarningMessageForPush() {
        String vin="12345678919991234";
        String byteString="23 23 00 31 01 56 04 BF DA 24 01 31 32 33 34 35 36 37 38 39 30 31 32 33 34 35 01 00 00 00 01 00 00 01 06 CF 6A 0E 00 00 00 6F 00 7B 00 E6 96 97 98 99 9A 9B 9C 9D 4C ";
        System.out.println(outputHexService.getWarningMessageForPush(vin, byteString));

    }
    @Test
    public void test_getResendWarningMessageForPush() {
        String vin="12345678919991234";
        String byteString="23 23 00 31 01 56 04 BF DA 25 01 31 32 33 34 35 36 37 38 39 30 31 32 33 34 35 01 00 00 00 01 00 00 01 06 CF 6A 0E 00 00 00 6F 00 7B 00 E6 AA AA AA AA AA AA AA AA 4D ";
        System.out.println(outputHexService.getResendWarningMessageForPush(vin, byteString));

    }

    @Ignore("Not suitable for travis-ci")
    @Test
    public void test_getWarningMessageAndPush() {
        String vin="12345678919991234";
        String byteString="23 23 00 30 01 56 04 BF DA 24 01 31 32 33 34 35 36 37 38 39 30 31 32 33 34 35 01 00 00 00 01 00 00 01 06 CF 6A 0E 00 00 00 6F 00 7B 00 E6 AA AA AA AA AA AA AA E7 ";
       outputHexService.getWarningMessageAndPush(vin, byteString);

    }

    @Test
    public void test_getDiagCmdHex() {
        String vin="12345678919991234";
        DiagnosticData diagnosticData=new DiagnosticData();
        diagnosticData.setDiaCmdDataSize((short) 17);
        diagnosticData.setDiaNumber((short) 17);
        diagnosticData.setDiaId((short) 0);
        diagnosticData.setEventId(1444812349l);
        System.out.println(outputHexService.getDiagCmdHex(diagnosticData));

    }

}
