package com.hp.triclops;

import com.hp.triclops.entity.TBox;
import com.hp.triclops.repository.TBoxRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


import java.util.Date;

/**
 * Created by luj on 2015/10/9.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class TBoxRepositoryTest {

    @Autowired
    TBoxRepository tBoxRepository;


    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void test_findByVinAndT_sn(){
        System.out.println(tBoxRepository.findByVinAndT_sn("12345678919991234","12345678919991"));
    }

    @Test
    public void test_ActivationTBox(){
        TBox tb=tBoxRepository.findByVinAndT_sn("12345678919991234","12345678919991");
        if(tb!=null){
            tb.setIs_activated(1);
            tb.setActivation_time(new Date());
            tBoxRepository.save(tb);
        }else{
            System.out.println("Activation failed,not record found!");
        }

    }
}
