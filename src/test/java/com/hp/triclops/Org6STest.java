package com.hp.triclops;

import com.hp.triclops.Entity6S.Org6S;
import com.hp.triclops.entity.Vehicle;
import com.hp.triclops.Entity6S.Vehicle6S;
import com.hp.triclops.repository.VehicleRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import javax.transaction.Transactional;
import java.util.Date;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class Org6STest {
    @Autowired
    private ApplicationContext appContext;

    @Autowired
    VehicleRepository vehicleRepository;


    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test//组织绑定车辆
    @Transactional
    public void testAddVehivle() {

        Org6S org6S=new Org6S(1);
        org6S.setAppCtxAndInit(appContext);
        //新建车辆
        Vehicle vehicle=new Vehicle();
        vehicle.setVendor("BMW");
        vehicle.setModel("M3");
        vehicle.setDisplacement("6.0");
        vehicle.setVin("1G1BL52P7TR115520");
        vehicle.setTboxsn("TB11032DS31");
        vehicle.setLicense_plate("A12123");
        vehicle.setProduct_date(new Date());
        //保存车辆
        Vehicle testVehicle=vehicleRepository.save(vehicle);

        Vehicle6S vehicle6S=new Vehicle6S(testVehicle.getId());
        vehicle6S.setAppCtxAndInit(appContext);
        org6S.addVehicle(vehicle6S);
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
