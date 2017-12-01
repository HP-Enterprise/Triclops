package com.hp.triclops.repository;

import com.hp.triclops.entity.FtpData;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.CrudRepository;

@Configurable
@EnableJpaRepositories
public interface FtpDataRepository extends CrudRepository<FtpData, Integer> {

    FtpData findById(int id);
}
