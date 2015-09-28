package com.hp.triclops;

import com.hp.triclops.acquire.DataTool;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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
}
