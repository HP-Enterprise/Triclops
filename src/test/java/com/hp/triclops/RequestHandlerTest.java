package com.hp.triclops;

import com.hp.triclops.acquire.DataTool;
import com.hp.triclops.acquire.RequestHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by luj on 2015/9/25.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@ComponentScan(basePackages = { "com.hp.triclops.acquire","com.hp.data.core","com.hp.data.util" })
public class RequestHandlerTest {

    @Autowired
    RequestHandler requestHandler;
    @Autowired
    DataTool dataTool;

    Logger _logger = LoggerFactory.getLogger(RequestHandlerTest.class);

    @Before
    public void setUp() {}
    @After
    public void tearDown() {}



   @Test
    public void test_getActiveHandle() {
        //测试激活请求 激活结果
        String byteString="23 23 00 3E 01 56 04 B7 1E 12 01 31 32 33 34 35 36 37 38 39 30 31 32 33 34 35 00 00 00 00 00 00 55 BE E2 58 31 32 33 34 35 36 37 38 39 31 39 39 39 31 31 32 33 34 35 36 37 38 39 31 39 39 39 31 32 33 34 83 ";
        System.out.println("request.:"+byteString);
        System.out.println("response:"+requestHandler.getActiveHandle(byteString));

        String resultString="23 23 00 20 01 56 04 BF DA 12 03 31 32 33 34 35 36 37 38 39 30 31 32 33 34 35 00 00 00 00 00 00 55 BE E2 58 00 66 ";
        System.out.println("request.:"+resultString);
        System.out.println("response:"+requestHandler.getActiveHandle(resultString));
    }
    @Test
    public void test_getRemoteWakeUpResp() {
        //远程唤醒测试
        String byteString="23 23 00 4C 01 56 04 AD 8C 14 01 31 32 33 34 35 36 37 38 39 30 31 32 33 34 35 00 00 00 00 00 00 00 55 BE E2 58 31 32 33 34 35 36 37 38 39 31 39 39 31 32 33 34 35 36 37 38 39 31 39 39 39 31 32 33 34 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 47 ";
        System.out.println("request.:"+byteString);
        System.out.println("response:"+requestHandler.getRemoteWakeUpResp(byteString));

    }
    @Test
    public void test_getDiagResp() {
        //电检测试
        String byteString="23 23 00 3A 01 56 03 AE 2A 11 01 31 32 33 34 35 36 37 38 39 30 31 32 33 34 35 0C 00 01 01 00 01 55 BE E2 58 14 31 32 33 34 35 36 37 38 39 31 39 39 39 31 56 03 AE 2A 00 00 00 00 00 00 00 00 5B ";
        System.out.println("request.:"+byteString);
        System.out.println("response:"+requestHandler.getDiagResp(byteString));

    }

    @Test
    public void test_getHeartbeatResp() {
        //心跳测试
        String byteString="23 23 00 20 01 55 D2 0F E7 26 01 00 00 00 00 00 00 00 00 00 00 11 00 00 00 00 00 00 00 00 00 00 00 55 BE E2 58 29 ";
        System.out.println("request.:"+byteString);
        System.out.println("response:"+requestHandler.getHeartbeatResp(byteString));

    }

    @Test
    public void test_getSleepResp() {
        //休眠测试
        String byteString="23 23 00 20 01 56 04 BF DA 27 01 31 32 33 34 35 36 37 38 39 30 31 32 33 34 35 01 00 00 00 01 00 00 56 1E 16 3D 63 ";
        System.out.println("request.:"+byteString);
        System.out.println("response:"+requestHandler.getSleepResp(byteString));

    }


    @Test
      public void test_getRegisterResp() {
        //注册测试
        String byteString="23 23 00 4D 01 55 D2 0F E7 13 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 55 BE E2 58 31 32 33 34 35 36 37 38 39 31 39 39 39 31 31 32 33 34 35 36 37 38 39 31 39 39 39 31 32 33 34 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 55 ";
        System.out.println("request.:"+byteString);
        System.out.println("response:"+requestHandler.getRegisterResp(byteString, true));

    }

    @Test
    public void test_getApplicationIdAndMessageIdFromDownBytes() {
        //测试
        String byteString="23 23 00 0D 01 56 05 10 02 31 01 55 BE E2 58 00 01 01 2C ";
        System.out.println("request.:"+byteString);
        System.out.println("response:"+dataTool.getApplicationIdAndMessageIdFromDownBytes(byteString));

    }


    @Test
    public void test_handleRemoteControlPreconditionResp() {
        //测试
        String byteString="23 23 00 41 00 56 04 BF DA 31 02 31 32 33 34 35 36 37 38 39 30 31 32 33 34 35 01 00 00 00 01 00 00 56 1E 16 3D 56 04 BF DA 3C 03 E8 41 01 2C 01 5E 50 50 AA AA AA 23 28 AA AA AA AA AA AA AA AA AA AA AA AA AA 05 CB ";
        requestHandler.handleRemoteControlRequest(byteString, "12345678919991234");

    }
    @Test
    @Transactional
    @Rollback
    public void test_handleRemoteControlAck() {
        //测试
        String byteString="23 23 00 21 00 56 04 BF DA 31 04 31 32 33 34 35 36 37 38 39 30 31 32 33 34 35 01 00 00 00 01 00 00 56 1E 16 3D 01 71 ";
        requestHandler.handleRemoteControlRequest(byteString, "12345678919991234");
    }

    @Ignore("Not suitable for travis-ci")
    @Test
    @Transactional
    @Rollback
    public void test_handleRemoteControlRst() {
        //测试
        String byteString="23 23 00 22 00 56 04 BF DA 31 05 31 32 33 34 35 36 37 38 39 30 31 32 33 34 35 01 00 00 00 01 00 00 56 1E 16 3D 00 02 70 ";
        requestHandler.handleRemoteControlRequest(byteString, "12345678919991234");
    }

    @Test
    @Transactional
    @Rollback
    public void test_handleParmSetAck(){
        String byteString="23 23 00 3B 01 56 24 99 71 52 02 31 32 33 34 35 36 37 38 39 30 31 32 33 34 35 01 00 00 00 01 00 00 56 1E 16 3D 0D 01 02 03 04 05 06 07 08 09 0A 0B 0C 0D 00 00 00 00 00 00 00 00 00 00 00 00 00 AF ";
        String vin="12345678919991234";
        requestHandler.handleParmSetAck(byteString, vin);

    }

    @Test
    public void test_handleDiagnosticAck(){
        String byteStr="23 23 4D 40 00 55 D2 0F E7 42 02 31 32 33 34 35 36 37 38 39 30 31 32 33 34 35 00 00 00 00 00 00 00 55 BE E2 58 01 03 01 61 62 63 64 65 66 02 61 03 61 62 63 64 65 5D ";
        String vin="12345678919991234";
        requestHandler.handleDiagnosticAck(byteStr, vin);

    }
}
