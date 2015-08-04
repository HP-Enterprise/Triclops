package com.hp.triclops;

import com.hp.triclops.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application implements CommandLineRunner {
    @Autowired
    UserRepository userRepository;
    @Override
    public void run(String... args) throws Exception {
        System.out.println("---------------初始化开始----------------");
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(new Object[]{com.hp.triclops.Application.class, "classpath:spring-config.xml"}, args);
    }
}
