package com.hp.triclops;

import com.hp.triclops.entity.User;
import com.hp.triclops.redis.SessionRedis;
import com.hp.triclops.repository.UserRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@ComponentScan(basePackages = { "org.springframework.data.redis.core","org.springframework.data.redis.serializer" })
public class RedisTest {
    @Autowired
    private SessionRedis sessionRedis;

    @Autowired
    private UserRepository userRepository;

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testRedis() {

        List<User> userList = this.userRepository.findByNick("xq");

        sessionRedis.saveSessionOfList("user001", userList.get(0));

        User user1 = (User) sessionRedis.getSessionOfList("user001");
        System.out.println(user1.getName());

        //System.out.print(sessionRedis.delSessionAllOfList("user001"));

        //sessionRedis.saveSessionOfVal("key1", "value3");
        //out.println(sessionRedis.getSessionOfVal("key1"));
        //System.out.println(sessionRedis.updateSessionOfVal("key1","value2"));
        //System.out.println(sessionRedis.getSessionOfVal("key1"));
        //sessionRedis.delSessionOfVal("key1");
    }
}