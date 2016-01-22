package com.hp.triclops.repository;

import com.hp.triclops.entity.VehicleEx;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by Teemol on 2016/1/22.
 */

@EnableJpaRepositories
public interface VehicleExRepository extends CrudRepository<VehicleEx, String> {

    @Query("select ve from VehicleEx ve " +
            "where (?1 is null or ve.vin=?1) " +
            "and ve.id in ?2 ")
    List<VehicleEx> findByIdIn(String vin,List<Integer> ids);
}
