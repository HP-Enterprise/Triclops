package com.hp.triclops;

import com.hp.data.core.Conversion;
import com.hp.triclops.service.DataHandleService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by luj on 2015/9/29.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class DataHandleServiceTest {
    @Autowired
    DataHandleService dataHandleService;
    @Autowired
    Conversion conversionTBox;
    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }
    @Test
    @Transactional
    @Rollback
    public void test_saveRegularReportMes(){
        //测试固定数据保存
        String byteString="23 23 00 3D 01 56 04 BF DA 21 01 31 32 33 34 35 36 37 38 39 30 31 32 33 34 35 01 00 00 00 01 00 00 00 0A 00 0A 0A 00 0A 00 0A 01 00 01 64 31 32 33 34 35 31 32 33 34 35 00 0A 00 00 C0 A8 01 0B 23 28 16 ";
        dataHandleService.saveMessage("12345678919991234",byteString);
    }
    @Test
    @Transactional
    @Rollback
    public void test_saveRealTimeMessage(){
        //测试实时数据保存
        String byteString="23 23 00 39 00 56 04 BF DA 22 01 31 32 33 34 35 36 37 38 39 30 31 32 33 34 35 01 00 00 00 01 00 00 00 01 CD AD 0E 06 CD 62 C0 06 1F 00 EA 00 63 00 7B 00 86 04 D2 64 65 66 67 0F 41 43 0F A2";
        dataHandleService.saveMessage("12345678919991234",byteString);
    }
    @Test
    @Transactional
    @Rollback
    public void test_saveDataResendRealTimeMes(){
        //测试补发数据保存
        String byteString="23 23 00 39 00 56 04 BF DA 23 01 31 32 33 34 35 36 37 38 39 30 31 32 33 34 35 01 00 00 00 01 00 00 00 01 CD AD 0E 06 CD 62 C0 06 1F 00 EA 00 63 00 7B 00 86 04 D2 64 65 66 67 0F 41 43 0F A3 ";
        dataHandleService.saveMessage("12345678919991234",byteString);
    }
    @Test
    @Transactional
    @Rollback
    public void test_saveWarningMessage(){
        //测试报警数据保存
        String byteString="23 23 01 58 00 56 04 BF DA 24 01 31 32 33 34 35 36 37 38 39 30 31 32 33 34 35 01 00 00 00 01 00 00 00 00 00 1E 41 00 00 72 1F 06 1F 00 EA 01 01 02 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 B8 ";
        dataHandleService.saveMessage("12345678919991234",byteString);
    }

    @Test
    @Transactional
    @Rollback
    public void test_saveDataResendWarningMessage(){
        //测试补发报警数据保存
        String byteString="23 23 01 58 00 56 04 BF DA 25 01 31 32 33 34 35 36 37 38 39 30 31 32 33 34 35 01 00 00 00 01 00 00 00 00 00 1E 41 00 00 72 1F 06 1F 00 EA 01 01 02 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 00 64 B9 ";
        dataHandleService.saveMessage("12345678919991234",byteString);
    }

    @Test
    @Transactional
    @Rollback
    public void test_saveFailureMessage(){
        //测试故障数据保存
        String byteString="23 23 00 31 00 56 04 BF DA 28 01 31 32 33 34 35 36 37 38 39 30 31 32 33 34 35 01 00 00 00 01 00 00 00 01 CD AD 0E 06 CD 62 C0 06 1F 00 EA 34 35 36 39 AA AA AA AA E4 ";
        dataHandleService.saveMessage("12345678919991234",byteString);
    }

    @Test
    @Transactional
    @Rollback
    public void test_saveDataResendFailureMessage(){
        //测试补发故障数据保存
        String byteString="23 23 00 31 00 56 04 BF DA 29 01 31 32 33 34 35 36 37 38 39 30 31 32 33 34 35 01 00 00 00 01 00 00 00 01 CD AD 0E 06 CD 62 C0 06 1F 00 EA AA AA AA AA 51 52 53 54 EF ";
        dataHandleService.saveMessage("12345678919991234",byteString);
    }



}
