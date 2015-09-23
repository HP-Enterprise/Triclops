package com.hp.triclops;

import com.hp.triclops.redis.Verifier;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Random;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@ComponentScan(basePackages = { "org.springframework.data.redis.core","org.springframework.data.redis.serializer" })
public class VerifierTest {
    @Autowired
    private Verifier verifier;

    private Logger _logger;

    @Before
    public void setUp() {
        this._logger = LoggerFactory.getLogger(VerifierTest.class);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testRedis() {
        Random rand = new Random();
        String target = "unitTest-" + String.valueOf(rand.nextInt());
        String code = verifier.generateCode(target, 60);
        this._logger.info("生成验证码{} -> {}", target, code);

        Assert.assertTrue(verifier.verifyCode(target, "123456") == 1);
        Assert.assertTrue(verifier.verifyCode(target, code) == 0);
    }
    
}