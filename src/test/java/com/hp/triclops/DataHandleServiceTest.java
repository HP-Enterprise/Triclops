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
    public void test_saveRealTimeMessage(){
        //测试实时数据保存
        String byteString="23 23 00 4D 01 55 D2 10 6D 22 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 FE 00 00 00 01 00 03 5A 84 00 00 00 03 00 00 00 00 00 00 00 00 3C 00 32 00 46 00 5A 00 28 28 06 00 00 00 00 00 00 00 00 FE FF 00 00 00 00 00 00 0A AB";
        dataHandleService.saveMessage("12345678919991234",byteString);
    }
    @Test
    public void test_saveDataResendReadTimeMes(){
        //测试补发数据保存
        String byteString="23 23 00 54 01 56 04 BF DA 23 01 31 32 33 34 35 36 37 38 39 30 31 32 33 34 35 01 00 00 00 01 00 00 01 06 CF 6A 0E 00 00 00 6F 00 7B 00 E6 0A 00 0B 0F 63 63 5A 00 FB 00 FC 00 FD 00 FE AA 41 43 AA 36 B0 C8 3A 98 36 B0 AA 0F A0 00 C8 01 2C 00 1E 0A AA AA AA AA AA AA AA 48";
        dataHandleService.saveMessage("12345678919991234",byteString);
    }
    @Test
    public void test_saveWarningMessage(){
        //测试报警数据保存
        String byteString="23 23 00 31 01 56 04 BF DA 24 01 31 32 33 34 35 36 37 38 39 30 31 32 33 34 35 01 00 00 00 01 00 00 01 06 CF 6A 0E 00 00 00 6F 00 7B 00 E6 96 97 98 99 9A 9B 9C 9D 4C ";
        dataHandleService.saveMessage("12345678919991234",byteString);
    }

    @Test
    @Transactional
    @Rollback
    public void test_saveDataResendWarningMessage(){
        //测试补发报警数据保存
        String byteString="23 23 00 31 01 56 04 BF DA 25 01 31 32 33 34 35 36 37 38 39 30 31 32 33 34 35 01 00 00 00 01 00 00 01 06 CF 6A 0E 00 00 00 6F 00 7B 00 E6 A1 A2 A3 A4 A5 A6 A7 A8 45 ";
        dataHandleService.saveMessage("12345678919991234",byteString);
    }



}
