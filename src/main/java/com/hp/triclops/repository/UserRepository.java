package com.hp.triclops.repository;

import java.util.List;

import com.hp.triclops.entity.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
    List<User> findByName(String name);
}