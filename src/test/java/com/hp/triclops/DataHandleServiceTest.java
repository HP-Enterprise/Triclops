package com.hp.triclops;

import com.hp.triclops.service.DataHandleService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by luj on 2015/9/29.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class DataHandleServiceTest {
    @Autowired
    DataHandleService dataHandleService;
    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }
    @Test
    public void test_saveMessage(){
        //测试实事数据保存
        String byteString="";
        dataHandleService.saveMessage(byteString);
    }


}
