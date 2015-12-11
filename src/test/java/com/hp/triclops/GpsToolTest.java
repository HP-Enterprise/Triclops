package com.hp.triclops;

import com.alibaba.fastjson.JSONArray;
import com.hp.triclops.acquire.DataTool;
import com.hp.triclops.entity.GpsData;
import com.hp.triclops.utils.GpsTool;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;

/**
 * Created by luj on 2015/11/3.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class GpsToolTest {
    @Autowired
    GpsTool gpsTool;
    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }
    @Test
    public void test_convertToGCJ02(){
        GpsData gpsData=new GpsData();
        gpsData.setLatitude(36.6310687);
        gpsData.setLongitude(101.7540428);
        GpsData g=gpsTool.convertToGCJ02(gpsData);
        System.out.println(">>>>>>"+g.getLongitude()+","+g.getLatitude());
    }

    @Test
    public void test_convertTBaidu(){
        GpsData gpsData=new GpsData();
        gpsData.setLatitude(36.6310687);
        gpsData.setLongitude(111.7540428);
        GpsData g=gpsTool.convertToBaiDuGps(gpsData);
        System.out.println(">>>>>>"+g.getLongitude()+","+g.getLatitude());
    }
}
