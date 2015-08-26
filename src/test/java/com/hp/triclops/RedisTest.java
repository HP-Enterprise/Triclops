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

        //List<User> userList = this.userRepository.findByNick("xq");
        /*User user = new User();
        user.setName("xiongqing");
        user.setNick("xq");
        user.setGender(1);
        user.setPhone("123456789");

        User user1 = new User();
        user1.setName("weisijie");
        user1.setNick("sisi");
        user1.setGender(0);
        user1.setPhone("987654321");

        sessionRedis.saveSessionOfList("user001", user);
        sessionRedis.saveSessionOfList("user002",user1);

        User user2 = (User) sessionRedis.getSessionOfList("user001");
        System.out.println(user2.getName());

        sessionRedis.updateSessionOfList("user001", user1);
        user2 = (User) sessionRedis.getSessionOfList("user001");
        System.out.println(user2.getName());*/
        //System.out.println(sessionRedis.getSessionOfList("user002"));

        /*List<Object> listUser = sessionRedis.getSessionOfList();
        for(int i = 0;i < listUser.size();i++){
            User user3 = (User) listUser.get(i);
            System.out.println(user3.getName());

        }*/

        //System.out.print(sessionRedis.delSessionAllOfList("user002"));

        /*sessionRedis.saveSessionOfVal("key3", "value3");
        System.out.println(sessionRedis.getSessionOfVal("key1"));
        sessionRedis.updateSessionOfVal("key3","value1");
        System.out.println(sessionRedis.getSessionOfVal("key3"));*/

        //sessionRedis.delSessionOfVal("key1");
    }
}