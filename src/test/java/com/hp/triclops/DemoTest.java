package com.hp.triclops;

import com.hp.triclops.entity.User;
import com.hp.triclops.repository.UserRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

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
        userRepository.save(new User("Sam1",1,"张三1","1.jpg"));
        userRepository.save(new User("Sam2",0,"张三2","2.jpg"));
        userRepository.save(new User("Sam3",1,"张三3","3.jpg"));
        for(User user : userRepository.findAll()){
            System.out.println(user.getName());
        }
    }
}