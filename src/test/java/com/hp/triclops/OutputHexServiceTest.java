package com.hp.triclops;

import com.hp.data.core.Conversion;
import com.hp.triclops.acquire.DataTool;
import com.hp.triclops.entity.RemoteControl;
import com.hp.triclops.service.OutputHexService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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
}
