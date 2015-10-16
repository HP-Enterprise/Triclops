package com.hp.triclops.repository;

import com.hp.triclops.entity.Vehicle;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by Administrator on 2015/8/25.
 */

@EnableJpaRepositories
public interface VehicleRepository extends CrudRepository<Vehicle, String> {

    Vehicle findByVin(String vin);

    Vehicle findById(int id);

    Vehicle findByTboxsn(String tboxsn);

    @Query("select Ve from Vehicle Ve where Ve.vin = ?1 and Ve.tboxsn = ?2")
    Vehicle findByVinAndTbox(String vin, String tboxsn);

}
