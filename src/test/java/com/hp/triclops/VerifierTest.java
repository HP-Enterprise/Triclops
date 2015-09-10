package com.hp.triclops;

import com.hp.triclops.entity.UserVehicleRelatived;
import com.hp.triclops.entity.Vehicle;
import com.hp.triclops.redis.Verifier;
import com.hp.triclops.repository.UserRepository;
import com.hp.triclops.repository.UserVehicleRelativedRepository;
import com.hp.triclops.repository.VehicleRepository;
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
    @Autowired
    private VehicleRepository vehicleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserVehicleRelativedRepository userVehicleRelativedRepository;

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }


    public void testRedis() {
        System.out.println(verifier.hashCode());
        String code=verifier.generateCode("aaaaa",60);
        System.out.println("生成验证码"+code);

        System.out.println("校验验证码"+verifier.verifyCode("aaaaa","123456"));
        System.out.println("校验验证码"+verifier.verifyCode("aaaaa",code));

        int car= verifier.verifyCode("123",code);
        int car1= verifier.verifyCode("aaaaa","123457");
        int car2= verifier.verifyCode("aaaaa",code);
        System.out.println(car);
        System.out.println(car1);
        System.out.println(car2);
    }

    @Test
    public void testRelatived(){
        int uid = 2;
        String vin = "1";
        String tboxsn = "1";
        int vid = 1;

            /*Vehicle vehicle = vehicleRepository.findByVinAndTbox(vin,tboxsn);
            UserVehicleRelatived userVehicleRelatived = new UserVehicleRelatived(userRepository.findById(uid),vehicle,1,userRepository.findById(uid));*/

            Vehicle vehicle = vehicleRepository.findById(vid);
            UserVehicleRelatived userVehicleRelatived = new UserVehicleRelatived(userRepository.findById(uid),vehicle,0,userVehicleRelativedRepository.findByVin(vehicle).get(0).getParentuser());
    }
}