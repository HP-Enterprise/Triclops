package com.hp.triclops;

import com.hp.triclops.acquire.DataTool;
import com.hp.triclops.entity.DiagnosticData;
import org.junit.After;
import org.junit.Assert;
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
        System.out.println(dataTool.getMaxSendCount("49", "1"));
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
        long a=7745l;
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
    public void test_getTrueBatteryVoltage(){
        int a=14123;
        System.out.println(dataTool.getTrueBatteryVoltage(a));
    }
    @Test
    public void test_getTrueTmp(){
        short a=110;
        System.out.println(dataTool.getTrueTmp(a));
    }
    @Test
    public void test_getTrueAvgOil(){
        int a=8;
        System.out.println(dataTool.getTrueAvgOil(a));
        a=80;
        System.out.println(dataTool.getTrueAvgOil(a));
        a=235;
        System.out.println(dataTool.getTrueAvgOil(a));
    }

    @Test
    public void test_getBitsFromShort(){
        char[] arr=dataTool.getBitsFromShort((short)1);
        for (int i = 0; i <arr.length ; i++) {
            System.out.println(arr[i]);

        }
    }

    @Test
    public void test_getIpBytes(){
        String ip="192.168.1.1";
        byte[] bytes=dataTool.getIpBytes(ip);

    }
    @Test
    public void test_getLengthString(){
        String str="好好学习";
        System.out.println(">>>>>|"+dataTool.getLengthString(str,8)+"|");
    }

    @Test
    public void test_getDatasFromDiagAckMsg(){
        String byteStr="23 23 00 31 00 55 D2 0F E7 42 02 31 32 33 34 35 36 37 38 39 30 31 32 33 34 35 00 00 00 00 00 00 00 56 1E 16 3D 01 03 01 61 62 63 64 65 66 02 61 03 61 62 63 64 65 48 ";
        DiagnosticData d= dataTool.getDatasFromDiagAckMsg(byteStr);
        System.out.println("m1:"+d.getMessage1()+"|m2:"+d.getMessage2()+"|m3:"+d.getMessage3()+"|m4:"+d.getMessage4());
        Assert.assertEquals(d.getMessage1(), "abcdef");
    }

    @Test
    public void test_getHitSpeedFromSpeeds(){
        float[] speeds = {100.0f, 95.9f, 85.8f, 75.7f, 65.6f, 55.5f, 45.4f, 35.3f, 25.1f, 10.0f, 1.1f, 0.0f};//12个数据,速度为递减的
//       float[] speeds = {100.f};
        float max = dataTool.getHitSpeedFromSpeeds(speeds);
        System.out.println("******************max=" + max);
    }
}
