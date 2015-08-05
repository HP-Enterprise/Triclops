package com.hp.triclops;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.hp.triclops", "com.hp.briair"})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}