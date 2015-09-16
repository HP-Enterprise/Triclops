package com.hp.triclops.repository;

import com.hp.triclops.entity.Organization;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;


@EnableJpaRepositories
public interface SysdictRepository extends CrudRepository<Organization, Long> {
    
    Organization findById(int id);

    Set<Organization> findByTypeKey(int typekey);
}