package com.hp.triclops;

import com.hp.triclops.entity.Org6S;
import com.hp.triclops.entity.Vehicle6S;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.transaction.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class Org6STest {
    @Autowired
    private ApplicationContext appContext;


    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testAddVehivle() {
      //  Org6S org6S=new Org6S(1);
   //     org6S.setAppCtxAndInit(appContext);
        Vehicle6S vehicle6S=new Vehicle6S(3);
        vehicle6S.setAppCtxAndInit(appContext);
    }

    /**
     * 车辆从组织解除绑定
     * @throws Exception
     */
    @Test
    @Transactional
    public void testUnbindVehicle() throws Exception{
        Org6S org6S = new Org6S(1);
        org6S.setAppCtxAndInit(appContext);
        Vehicle6S vehicle6S = new Vehicle6S(1);
        org6S.addVehicle(vehicle6S);
        org6S.deleteVehicle(vehicle6S);

    }
}
