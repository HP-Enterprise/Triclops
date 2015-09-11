package com.hp.triclops;

import com.hp.triclops.entity.Organization;
import com.hp.triclops.entity.Vehicle;
import com.hp.triclops.repository.OrganizationRepository;
import com.hp.triclops.repository.VehicleRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.transaction.Transactional;
import java.util.Set;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class OrganizationTest {
    @Autowired
    private OrganizationRepository organizationRepository;
    @Autowired
    private VehicleRepository vehicleRepository;
    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testOrganization() {

//        Organization organization = organizationRepository.findById(1); //
//        Organization subOrganization = organizationRepository.findById(2);
//
//        Set<Organization> set = organization.getOrganizationSet();
//        set.add(subOrganization);
//        organizationRepository.save(organization);

       /* //添加一条记录
        organizationRepository.save(new Organization("shebing","789",1,22,1));*/
      /*  //创建一个组织并制定其子组织
        Organization organization=new Organization("asd","sa",2);
        organization.setSubOid(12);
        organization.setParOid(0);
        organizationRepository.save(organization);*/
        //测试一个组织下面的所有车
//        Organization org = organizationRepository.findById(1);
//        System.out.println(org.getVehicleSet().size());
        //测试一台车所属的组织
//        Vehicle vehicle = vehicleRepository.findByVin("INCAR00001");
//        System.out.println(vehicle.getOrganizationSet().size());
    }
}