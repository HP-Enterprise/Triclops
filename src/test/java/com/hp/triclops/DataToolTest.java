package com.hp.triclops;

import com.hp.triclops.acquire.DataTool;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

/**
 * Created by luj on 2015/9/28.
 */


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class DataToolTest {
    @Autowired
    DataTool dataTool;
    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }
    @Test
    public void test_getMaxSendCount(){
        System.out.println(dataTool.getMaxSendCount("49","1"));
    }

    @Test
    public void test_getTimeOutSeconds(){
        System.out.println(dataTool.getTimeOutSeconds("49", "1"));
    }

    @Test
    public void test_getIp(){
        String hexStr="00 00 C0 A8 01 01";
        System.out.println(dataTool.getIp(dataTool.getBytesFromByteBuffer(dataTool.getByteBuffer(hexStr))));
    }
    @Test
    public void test_seconds2Date(){
        long a=new Date().getTime();
        System.out.println(a);
        int seconds=1444292975;
        System.out.println(dataTool.seconds2Date(seconds).toString());
    }
    @Test
    public void test_getTrueLatAndLon(){
        long a=114123456l;
        System.out.println(dataTool.getTrueLatAndLon(a));
    }
    @Test
    public void test_getTrueSpeed(){
        int a=123;
        System.out.println(dataTool.getTrueSpeed(a));
    }

    @Test
    public void test_getEngineConditionInfo(){
        short a=0;
        System.out.println(dataTool.getEngineConditionInfo(a));
        a=3;
        System.out.println(dataTool.getEngineConditionInfo(a));
    }

    @Test
    public void test_getTrueOilLife(){
        int a=8;
        System.out.println(dataTool.getTrueOilLife(a));
        a=80;
        System.out.println(dataTool.getTrueOilLife(a));
        a=235;
        System.out.println(dataTool.getTrueOilLife(a));
    }

    @Test
    public void test_getBitsFromShort(){
        char[] arr=dataTool.getBitsFromShort((short)1);
        for (int i = 0; i <arr.length ; i++) {
            System.out.println(arr[i]);

        }
    }
}
