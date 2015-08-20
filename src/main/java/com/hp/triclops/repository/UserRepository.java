package com.hp.triclops.repository;

import java.util.List;

import com.hp.triclops.entity.User;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.CrudRepository;

@EnableJpaRepositories
public interface UserRepository extends CrudRepository<User, Long> {
    List<User> findByNick(String nick);
}