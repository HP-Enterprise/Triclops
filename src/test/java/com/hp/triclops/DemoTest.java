package com.hp.triclops;

import com.hp.triclops.entity.User;
import com.hp.triclops.repository.UserRepository;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import javax.transaction.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class DemoTest {
    @Autowired
    private UserRepository userRepository;

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    @Transactional
    @Rollback(true)
    public void testSample() {
        userRepository.save(new User("Sam1",1,"张三1","1.jpg"));
        userRepository.save(new User("Sam2",0,"张三2","2.jpg"));
        userRepository.save(new User("Sam3",1,"张三3","3.jpg"));

        int count = 0;
        for(User user : userRepository.findAll()){
            count++;
        }
        Assert.assertTrue(count >= 3);
    }
}