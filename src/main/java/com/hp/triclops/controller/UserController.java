package com.hp.triclops.controller;

import java.util.concurrent.atomic.AtomicLong;
import java.lang.Iterable;
import com.hp.triclops.User;
import com.hp.triclops.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @Autowired
    UserRepository userRepository;

    @RequestMapping("/user")
    public Iterable<User> greeting(@RequestParam(value="name", defaultValue="World") String name) {

        return userRepository.findAll();

    }
}