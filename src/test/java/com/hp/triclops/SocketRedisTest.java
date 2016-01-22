package com.hp.triclops;

import com.hp.triclops.acquire.DataTool;
import com.hp.triclops.redis.SessionRedis;
import com.hp.triclops.redis.SocketRedis;
import com.hp.triclops.repository.UserRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@ComponentScan(basePackages = { "org.springframework.data.redis.core","org.springframework.data.redis.serializer" })
public class SocketRedisTest {
    @Autowired
    private SocketRedis socketRedis;
    @Autowired
    DataTool dataTool;

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testRedis() {
        String key="key123456";
        String value="abcdefgh789";
        System.out.println("k>>:"+key+"|v>>:"+value);

        socketRedis.saveValueString(key, value, -1);
        value=socketRedis.getValueString(key);
        System.out.println("k>>:"+key+"|v>>:"+value);

        socketRedis.saveValueString(key, "qwertyuiop", -1);
        value=socketRedis.getValueString(key);
        System.out.println("k>>:" + key + "|v>>:" + value);

        socketRedis.delValueString(key);
        value=socketRedis.getValueString(key);
        System.out.println("k>>:" + key + "|v>>:" + value);

        key="ashdgkasg";
        value=socketRedis.getValueString(key);
        System.out.println("k>>:" + key + "|v>>:" + value);

    }

    @Test
    public void saveRealTimeDataToredis(){
        int count=10000;
        System.out.println(new Date().getTime()+">>>>>>>>>>>>>write 10000 start");
        for(int i=1;i<=count;i++){
            Long vin=10000000000000000l+i;
            String inputKey="input"+ dataTool.getRandomRealTimeDataSuffix()+":"+vin;
            String dataStr = "23 23 00 39 00 56 04 BF DA 22 01 31 32 33 34 35 36 37 38 39 30 31 32 33 34 35 01 00 00 00 01 00 00 00 00 00 1E 41 00 00 72 1F 06 1F 00 EA 00 63 00 7B 00 86 04 D2 64 65 66 67 0F 41 43 0F 96 ";
            socketRedis.saveSetString(inputKey, dataStr,-1);
        }
        System.out.println(new Date().getTime()+">>>>>>>>>>>>>write 10000 end");

    }
}