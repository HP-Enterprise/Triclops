package com.hp.triclops;

import com.hp.triclops.entity.Organization;
import com.hp.triclops.repository.OrganizationRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class OrganizationTest {
    @Autowired
    private OrganizationRepository organizationRepository;

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testOrganization() {
        organizationRepository.save(new Organization("shebing","789",1,22,1));
    }
}