package com.hp.triclops.repository;

import com.hp.triclops.entity.PhoneBook;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by Teemol on 2015/11/11.
 */
@EnableJpaRepositories
public interface PhoneBookRepository extends CrudRepository<PhoneBook, Long> {
}
