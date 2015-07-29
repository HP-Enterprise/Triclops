package com.hp.triclops;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.SpringApplication;

@SpringBootApplication
public class Application implements CommandLineRunner {
    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }

    @Autowired
    UserRepository userRepository;

    @Override
    public void run(String... strings) throws Exception {
        for(User user : userRepository.findAll()){
            System.out.println(user);
        }
    }
}