package com.hp.triclops.repository;

import com.hp.triclops.entity.FtpSetting;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.CrudRepository;

@Configurable
@EnableJpaRepositories
public interface FtpSettingRepository extends CrudRepository<FtpSetting, Integer> {

    FtpSetting findById(int id);
}
