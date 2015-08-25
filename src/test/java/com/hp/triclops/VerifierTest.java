package com.hp.triclops;

import com.hp.triclops.redis.Verifier;
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
public class VerifierTest {
    @Autowired
    private Verifier verifier;

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testRedis() {
        System.out.println(verifier.hashCode());
        String code=verifier.generateCode("aaaaa",60);
        System.out.println("生成验证码"+code);

        System.out.println("校验验证码"+verifier.verifyCode("aaaaa","123456"));
        System.out.println("校验验证码"+verifier.verifyCode("aaaaa",code));


    }
}