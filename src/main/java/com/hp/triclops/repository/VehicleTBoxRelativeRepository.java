package com.hp.triclops.repository;

import com.hp.triclops.entity.VehicleTBoxRelative;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by Administrator on 2015/8/25.
 */
@EnableJpaRepositories
public interface VehicleTBoxRelativeRepository extends CrudRepository<VehicleTBoxRelative, Integer> {

}
