package com.hp.triclops.service;

import com.hp.triclops.entity.User;

public interface UserService {
    public Iterable<User> findAllUsers();
}
