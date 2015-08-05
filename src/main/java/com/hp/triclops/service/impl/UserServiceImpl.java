package com.hp.triclops.service.impl;

import com.hp.triclops.entity.User;
import com.hp.triclops.repository.UserRepository;
import com.hp.triclops.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
public class UserServiceImpl implements UserService{
    @Autowired
    private UserRepository userRepository;
    @Transactional
    public Iterable<User> findAllUsers(){
        return this.userRepository.findAll();
    }
}
