package com.hp.triclops;

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

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@ComponentScan(basePackages = { "org.springframework.data.redis.core","org.springframework.data.redis.serializer" })
public class SocketRedisTest {
    @Autowired
    private SocketRedis socketRedis;


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
}