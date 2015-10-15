package com.hp.triclops;

import com.hp.data.core.Conversion;
import com.hp.triclops.acquire.DataTool;
import com.hp.triclops.entity.RemoteControl;
import com.hp.triclops.entity.TBoxParmSet;
import com.hp.triclops.service.OutputHexService;
import org.junit.After;
import org.junit.Before;
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
    public void test_getRemoteControlHex(){
        //测试固定数据保存
        RemoteControl remoteControl=new RemoteControl();
        remoteControl.setSendingTime(new Date());
        remoteControl.setControlType((short) 13);
        remoteControl.setAcTemperature((short) 23);
        remoteControl.setUid(1);
        remoteControl.setVin("12345678919991234");
        int eventId=dataTool.getCurrentSeconds();

        System.out.println(">>>>>>>"+outputHexService.getRemoteControlHex(remoteControl,eventId));
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
        tBoxParmSet.setEventId((long)eventId);

        tBoxParmSet.setFrequencySaveLocalMedia(1000);
        tBoxParmSet.setFrequencyForReport(1000);
        tBoxParmSet.setFrequencyForWarningReport(1000);
        tBoxParmSet.setFrequencyHeartbeat((short) 10);
        tBoxParmSet.setTimeOutForTerminalSearch(10);
        tBoxParmSet.setTimeOutForServerSearch(10);
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
}
