package com.hp.triclops;


import com.hp.triclops.entity.*;
import com.hp.triclops.repository.OrganizationRepository;
import com.hp.triclops.repository.SysdictRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by wang on 2015/10/13.
 */
public class Org6STest {

    @Autowired
    Org6S org6S;
    @Autowired
    SysdictRepository sysdictRepository;
    @Autowired
    OrganizationRepository organizationRepository;

    public void testOrg6S(){

    }

    @Test
    public void unbindTest() throws Exception{
        Org6S org6S = new Org6S();
        org6S.setOrganization(organizationRepository.findById(1));
        Vehicle6S vehicle6S = new Vehicle6S();
        org6S.addVehicle(vehicle6S);
        org6S.deleteVehicle(vehicle6S);
    }

}
