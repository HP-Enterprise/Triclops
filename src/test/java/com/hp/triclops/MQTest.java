package com.hp.triclops;


import com.hp.triclops.entity.User;
import com.hp.triclops.entity.UserDevice;
import com.hp.triclops.mq.DeviceType;
import com.hp.triclops.repository.UserRepository;
import com.hp.triclops.service.MQService;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class MQTest {
    @Autowired
    private MQService mqService;
    @Autowired
    private UserRepository userRepository;
    Logger logger = LoggerFactory.getLogger(Application.class);

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Ignore("Not suitable for travis-ci")
    @Test
    public void testMQRegisterAndPush(){
        User user=userRepository.findAll().iterator().next();
        UserDevice ud=this.mqService.deviceRegister(user.getId(),"device1", DeviceType.ANDROID);
        System.out.println(ud.getId()+"注册完成");
        for(int i=0;i<10;i++) {
            this.mqService.pushToUser(ud.getId(), "推送消息"+i);
        }
    }
}
