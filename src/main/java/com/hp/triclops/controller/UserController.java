package com.hp.triclops.controller;

import com.hp.triclops.entity.User;
import com.hp.triclops.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping("/user")
    public Iterable<User> greeting(@RequestParam(value="name", defaultValue="World") String name) {

        return userService.findAllUsers();

    }
}