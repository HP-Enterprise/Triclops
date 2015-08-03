package com.hp.triclops;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.hp.triclops.entity.User;
import com.hp.triclops.repostory.UserRepository;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DemoTest {
    private static final EntityManagerFactory factory = Persistence.createEntityManagerFactory("triclops");
    private EntityManager em;
    private UserRepository userRepository;

    @Before
    public void setUp() {
        em = factory.createEntityManager();
        userRepository = new JpaRepositoryFactory(em).getRepository(UserRepository.class);

        em.getTransaction().begin();
    }

    @After
    public void tearDown() {
        em.getTransaction().rollback();
    }

    @Test
    public void testCustomMethod() {

        for(User user : userRepository.findAll()){
            System.out.println(user);
        }
    }
}