package com.hp.triclops.service.impl;

import com.hp.triclops.entity.User;
import com.hp.triclops.repostory.UserRepository;
import com.hp.triclops.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class UserServiceImpl implements UserService{
    @Autowired
    private UserRepository userRepository;
    public Iterable<User> findAllUsers(){
        return this.userRepository.findAll();
    }
}
