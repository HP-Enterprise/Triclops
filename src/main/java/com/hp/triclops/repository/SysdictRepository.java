package com.hp.triclops.repository;

import com.hp.triclops.entity.Sysdict;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.CrudRepository;

@EnableJpaRepositories
public interface SysdictRepository extends CrudRepository<Sysdict, Long> {

    Sysdict findByDictid(int dictid);

}