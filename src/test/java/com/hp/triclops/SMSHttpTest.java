package com.hp.triclops;


import com.hp.triclops.utils.SMSHttpTool;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by yuh on 2015/12/11.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class SMSHttpTest {
    @Autowired
    SMSHttpTool smsHttpTool;
    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }
    @Test
    public void test_getShortUrl(){
        String longUrl=  "http://127.0.0.1:8080/baiduMap.html?lon=114.13320540355&lat=30.257868000746";
        try{
        longUrl = java.net.URLEncoder.encode(longUrl, "UTF-8");
        }catch(Exception e){
          e.printStackTrace();
        }
        String shortUrl = smsHttpTool.getShortUrl(longUrl);
        System.out.println(">>>>>>"+shortUrl);
    }

}
