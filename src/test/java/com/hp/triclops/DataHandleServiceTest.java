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
@SpringApplicationConfiguration(classes = Application.class,locations = "classpath:spring-config.xml")
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
    public void test_saveRegularReportMes(){
        //测试固定数据保存
        String byteString="23 23 00 3C 01 56 04 F6 8E 21 01 31 32 33 34 35 36 37 38 39 30 31 32 33 34 35 00 00 00 00 00 00 00 0A 00 0A 0A 00 3C 00 3C 01 00 0C 96 31 2E 30 00 00 31 2E 30 00 00 00 3C 00 00 C0 A8 01 01 23 28 C9";
        dataHandleService.saveMessage("12345678919991234",byteString);
    }
    @Test
    @Transactional
    @Rollback
    public void test_saveRealTimeMessage(){
        //测试实时数据保存
        String byteString="23 23 00 4C 01 56 16 29 6F 22 01 31 32 33 34 35 36 37 38 39 30 31 32 33 34 35 01 00 01 01 00 00 01 01 CB A5 C0 06 CD 62 C0 00 7B 00 E6 0A 00 0B 0F 0D 90 38 00 FB 00 FC 00 FD 00 FE AA 41 43 AA 36 B0 C8 3A 98 36 B0 AA 0F A0 00 C8 01 2C 00 1E 0A 30";
        dataHandleService.saveMessage("12345678919991234",byteString);
    }
    @Test
    public void test_saveDataResendMes(){
        //测试补发数据保存
        String byteString="23 23 00 53 01 56 18 B2 D2 23 01 31 32 33 34 35 36 37 38 39 30 31 32 33 34 35 01 00 01 01 00 00 01 01 CB A5 C0 06 CD 62 C0 00 7B 00 E6 0A 00 7B 0F 0D 90 38 00 FB 00 FC 00 FD 00 FE AA 41 43 AA 36 B0 C8 3A 98 36 B0 AA 0F A0 00 C8 01 2C 00 1E 0A AA AA AA AA AA AA AA DC";
        dataHandleService.saveMessage("12345678919991234",byteString);
    }
    @Test
    @Transactional
    @Rollback
    public void test_saveWarningMessage(){
        //测试报警数据保存
        String byteString="23 23 00 2F 01 56 16 14 A9 24 01 31 32 33 34 35 36 37 38 39 30 31 32 33 34 35 01 00 01 01 00 00 01 01 CB A5 C0 06 CD 62 C0 00 7B 00 E6 AA AA AA AA AA AA AA 37";
        dataHandleService.saveMessage("12345678919991234",byteString);
    }



}
