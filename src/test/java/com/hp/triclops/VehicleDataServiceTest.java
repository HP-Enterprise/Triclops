package com.hp.triclops;

import com.hp.triclops.service.VehicleDataService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by luj on 2015/10/12.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class VehicleDataServiceTest {
    @Autowired
    VehicleDataService vehicleDataService;
    private Logger _logger = LoggerFactory.getLogger(VehicleDataServiceTest.class);

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void test_remoteWakeUp() {
       vehicleDataService.remoteWakeUp("123456");
    }
}
